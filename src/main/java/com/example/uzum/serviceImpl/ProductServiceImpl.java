package com.example.uzum.serviceImpl;

import com.example.uzum.dto.product.MaxAndMinPriceDTO;
import com.example.uzum.dto.product.ProductDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.entity.*;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.ProductService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;

import static com.example.uzum.helper.StringUtils.changeBracket;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private SellerRepo sellerRepo;
    @Autowired
    private BrandRepo brandRepo;
    @Autowired
    private AttachmentRepo attachmentRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PanelConnectMethodRepo panelConnectMethodRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private MainPanelRepo mainPanelRepo;


    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);


    @Override
    public Result<?> addProduct(ProductDTO productDto) {
        boolean existsByNameEn = productRepo.existsByNameEnAndCategoryIdAndIsActive(productDto.getNameEn(), productDto.getCategoryId(), true);
        boolean existsByNameUz = productRepo.existsByNameUzAndCategoryIdAndIsActive(productDto.getNameUz(), productDto.getCategoryId(), true);
        if (existsByNameEn || existsByNameUz)
            return new Result<>(false, Messages.THIS_PRODUCT_OF_THIS_SELLER_TO_THIS_CATEGORY_ALREADY_IS_ADDED);
        Product product = new Product();
        Result<?> result = duplicateCodeForAddAndEdit(product, productDto);
        if (!result.getSuccess()) return result;
        product = productRepo.save((Product) result.getData());
        logger.info("New product added. ID : {} ", product.getId());
        return new Result<>(true, Messages.PRODUCT_SAVED);
    }

    @Override
    public Result<?> getByFilter(String search, String categoryId, List<String> prices, List<String> brandIds, String order, String page) {
        if (search == null) return createQuery(null, categoryId, prices, brandIds, order, page);
        String condition = " (word_similarity('" + search + "', pro.name_uz)>0 OR word_similarity('" + search + "',  pro.name_en)>0) ";
        return createQuery(condition, categoryId, prices, brandIds, order, page);
    }

    @Override
    public Result<?> getByPanelId(Integer panelId, String categoryId, List<String> prices, List<String> brandIds, String order, String page) {
        Result<?> result = getConditionByPanelId(panelId);
        if (!result.getSuccess()) return result;
        String condition = result.getData().toString();
        return createQuery(condition, categoryId, prices, brandIds, order, page);
    }


    /*  Two main methods that getByFilter and getByMainPanelId are created using this method, so in first parameter of this method is come search condition for getByFilter method, main panel condition for getMainPanelId method.*/
    public Result<?> createQuery(String condition, String categoryId, List<String> prices, List<String> brandIds, String order, String page) {
        String query = "SELECT * FROM product AS pro WHERE pro.is_active=TRUE ";
        if (condition != null) {
            query = query.concat(" AND ").concat(condition);
        }
        if (categoryId != null) {
            int categoryIdInt = Integer.parseInt(categoryId);
            List<Integer> categoryIds = getDeepCategoryIDsByHigherCategoryId(new ArrayList<>(), categoryIdInt);
            query = query.concat(" AND ").concat("pro.category_id IN " + changeBracket(categoryIds));
        }
        if (prices != null) {
            if (prices.size() != 2) return new Result<>(false, Messages.PRODUCT_PRICE_LIST_SIZE_MUST_BE_2);
            int fromPrice = Integer.parseInt(prices.get(0));
            int toPrice = Integer.parseInt(prices.get(1));
            query = query.concat(" AND ").concat("pro.discounted_price>=" + fromPrice + " AND pro.discounted_price<=" + toPrice);
        }
        if (brandIds != null) {
            List<Integer> brandIdsInt = brandIds.stream().map(brn -> Integer.parseInt(brn)).toList();
            query = query.concat(" AND ").concat("pro.brand_id IN " + changeBracket(brandIdsInt));
        }
        switch (order) {
            case Filter.MOST_SOLD -> {
                query = query.concat(" ORDER BY pro.amount_of_sold_products DESC ");
            }
            case Filter.RATING_DESC -> {
                query = query.concat(" ORDER BY pro.rating DESC ");
            }
            case Filter.PRICE_ASC -> {
                query = query.concat(" ORDER BY pro.price ASC ");
            }
            case Filter.PRICE_DESC -> {
                query = query.concat(" ORDER BY pro.price DESC ");
            }
            case Filter.RECENTLY_ADDED -> {
                query = query.concat(" ORDER BY pro.created_date DESC ");
            }
        }
        int pageInt = Integer.parseInt(page);
        Query createQuery = entityManager.createNativeQuery(query, Product.class).setFirstResult(pageInt * 20).setMaxResults(20);
        logger.info("For Product filtering and Panel created query. Query : {} ", query);
        List<Product> products = createQuery.getResultList();
        return new Result<>(true, products);
    }


    @Override
    public Result<?> getBySimilarProducts(Integer id) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optional.get();
        Category category = product.getCategory();
        List<Product> similarProducts;
        Set<Integer> similarProductIdSet = new HashSet<>();
        List<Integer> allOfSimilarProductsInCategory = productRepo.getAmountOfProductsByCategoryId(id, category.getId());
        if (allOfSimilarProductsInCategory.size() <= 25) {
            similarProducts = productRepo.findAllByCategoryId(id, category.getId());
        } else {
            Random random = new Random();
            int randomNumber = random.nextInt(allOfSimilarProductsInCategory.size());
            while (similarProductIdSet.size() <= 25) {
                similarProductIdSet.add(allOfSimilarProductsInCategory.get(randomNumber));
                randomNumber = random.nextInt(allOfSimilarProductsInCategory.size());
            }
            similarProducts = productRepo.getProductsByProductIds(similarProductIdSet);
        }
        return new Result<>(true, similarProducts);
    }

    @Override
    public Result<?> getById(Integer id) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optional.get();
        return new Result<>(true, product);
    }

    @Override
    public Result<?> getPricesByFilter(String search, String categoryId, List<String> brandIds) {
        if (search == null) return createQueryForGetMaxAndMinPrice(null, categoryId, brandIds);
        String condition = " (word_similarity('" + search + "', pro.name_uz)>0 OR word_similarity('" + search + "',  pro.name_en)>0) ";
        return createQueryForGetMaxAndMinPrice(condition, categoryId, brandIds);
    }

    @Override
    public Result<?> getPricesByPanelId(Integer panelId, String categoryId, List<String> brandIds) {
        Result<?> result = getConditionByPanelId(panelId);
        if (!result.getSuccess()) return result;
        String condition = result.getData().toString();
        return createQueryForGetMaxAndMinPrice(condition, categoryId, brandIds);
    }

    private Result<?> createQueryForGetMaxAndMinPrice(String condition, String categoryId, List<String> brandIds) {
        String query = "SELECT MAX(pro.discounted_price), MIN(pro.discounted_price) FROM product AS pro WHERE pro.is_active=TRUE ";
        if (condition != null) {
            query = query.concat(" AND ").concat(condition);
        }
        if (categoryId != null) {
            int categoryIdInt = Integer.parseInt(categoryId);
            List<Integer> categoryIds = getDeepCategoryIDsByHigherCategoryId(new ArrayList<>(), categoryIdInt);
            query = query.concat(" AND ").concat("pro.category_id IN " + changeBracket(categoryIds));
        }
        if (brandIds != null) {
            List<Integer> brandIdsInt = brandIds.stream().map(br->Integer.parseInt(br)).toList();
            query = query.concat(" AND ").concat("pro.brand_id IN " + changeBracket(brandIdsInt));
        }
        Query createQuery = entityManager.createNativeQuery(query);
        logger.info("For getting max and min prices of product created query. Query : {}", query);
        List prices = createQuery.getResultList();
        Object[] obj = (Object[]) prices.get(0);
        if (obj[0] != null) {
            int maxPrice = (int) obj[0];
            int minPrice = (int) obj[1];
            MaxAndMinPriceDTO dto = MaxAndMinPriceDTO.builder()
                    .maxPrice(maxPrice)
                    .minPrice(minPrice)
                    .build();
        return new Result<>(true, dto);
        }else return new Result<>(true, new ArrayList<>());
    }


    public Result<?> getConditionByPanelId(Integer panelId) {
        Optional<MainPanel> optionalMainPanel = mainPanelRepo.findByIdAndIsActive(panelId, true);
        if (optionalMainPanel.isEmpty()) return new Result<>(false, Messages.SUCH_MAIN_PANEL_ID_NOT_EXIST);
        String condition;
        Optional<PanelConnectMethod> optional = panelConnectMethodRepo.findByMainPanelId(panelId);
        if (optional.isPresent()) {
            PanelConnectMethod connectMethod = optional.get();
            condition = directMethodNameToMethod(connectMethod.getMethodName());
            return new Result<>(true, condition);
        }
        Optional<Category> optionalCategory = categoryRepo.findByMainPanelIdAndIsActive(panelId, Boolean.TRUE);
        if (optionalCategory.isPresent()) {
            List<Integer> deepCategoryIDs = getDeepCategoryIDsByHigherCategoryId(new ArrayList<>(), optionalCategory.get().getId());
            condition = " pro.category_id IN " + deepCategoryIDs;
            return new Result<>(true, condition);
        }
        Optional<Seller> optionalSeller = sellerRepo.findByMainPanelIdAndIsActive(panelId, Boolean.TRUE);
        if (optionalSeller.isPresent()) {
            condition = " pro.seller_id=" + optionalSeller.get().getId();
            return new Result<>(true, condition);
        }
        List<Integer> productIds = productRepo.getProductIdsByMainPanelId(panelId);
        if (!productIds.isEmpty()) {
            if (productIds.size() == 1)
                return null; /*  DO NOTHING! because in this case only one product will be sent to UI. brand, prices, categories will be not sent. */
            else {
                condition = " pro.id IN " + productIds;
                return new Result<>(true, condition);
            }
        }
        return new Result<>(false, Messages.THIS_PANEL_HAS_NOT_BEEN_CONNECTED_WITH_ANYTHING);
    }

    @Override
    public String directMethodNameToMethod(String methodName) {
        return switch (methodName) {
            case Filter.PRODUCTS_COSTING_LESS_THAN_300 -> getProductsCostingLessThan300();
            case Filter.GET_PRODUCTS_TO_CREDIT -> getProductsToCredit();
            case Filter.DISCOUNTED_PRODUCTS -> getDiscountedProducts();
            default -> throw new IllegalStateException("Unexpected value: " + methodName);
        };
    }

    private String getDiscountedProducts() {
        return " pro.price<>pro.discounted_price "; // if product has not discount price, then its discount price will equal to price
    }

    public String getProductsCostingLessThan300() {
        return " pro.discounted_price<300 ";
    }

    public String getProductsToCredit() {
        return " pro.is_credit=TRUE ";
    }

    @Override
    public List<String> getMethodNamesForConnectToPanels() {
        List<String> methodNames = new ArrayList<>();
        methodNames.add(Filter.PRODUCTS_COSTING_LESS_THAN_300);
        methodNames.add(Filter.GET_PRODUCTS_TO_CREDIT);
        methodNames.add(Filter.DISCOUNTED_PRODUCTS);
        return methodNames;
    }


    private List<Integer> getDeepCategoryIDsByHigherCategoryId(List<Integer> newList, Integer id) {
        List<Category> subCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(id, true);
        List<Integer> nestedList = new ArrayList<>();
        for (Category category : subCategories) {
            newList.addAll(getDeepCategoryIDsByHigherCategoryId(nestedList, category.getId()));
        }
        if (subCategories.isEmpty()) newList.add(id);
        return newList;
    }


    @Override
    public Result<?> edit(Integer id, ProductDTO productDto) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        Product product = optional.get();
        boolean existsByNameEn = productRepo.existsByNameEnAndCategoryIdAndIsActiveAndIdNot(productDto.getNameEn(), productDto.getCategoryId(), true, id);
        boolean existsByNameUz = productRepo.existsByNameUzAndCategoryIdAndIsActiveAndIdNot(productDto.getNameUz(), productDto.getCategoryId(), true, id);
        if (existsByNameEn || existsByNameUz)
            return new Result<>(false, Messages.THIS_PRODUCT_OF_THIS_SELLER_TO_THIS_CATEGORY_ALREADY_IS_ADDED);
        Result<?> result = duplicateCodeForAddAndEdit(product, productDto);
        if (!result.getSuccess()) return result;
        productRepo.save((Product) result.getData());
        logger.info("Product updated. ID : {} ", id);
        return new Result<>(true, Messages.PRODUCT_UPDATED);
    }

    public Result<?> duplicateCodeForAddAndEdit(Product product, ProductDTO productDto) {
        Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(productDto.getCategoryId(), true);
        if (optionalCategory.isEmpty()) return new Result<>(false, Messages.NO_ID_CATEGORY);
        Category category = optionalCategory.get();
        Optional<Seller> optionalSeller = sellerRepo.findByIdAndIsActive(productDto.getSellerId(), true);
        if (optionalSeller.isEmpty()) return new Result<>(false, Messages.SUCH_SELLER_ID_NOT_EXIST);
        Seller seller = optionalSeller.get();
        if (productDto.getBrandId() != null) {
            Optional<Brand> optionalBrand = brandRepo.findByIdAndIsActive(productDto.getBrandId(), true);
            if (optionalBrand.isEmpty()) return new Result<>(false, Messages.SUCH_BRAND_NOT_EXIST);
            Brand brand = optionalBrand.get();
            product.setBrand(brand);
        } else product.setBrand(null);
        List<Attachment> attachments = attachmentRepo.findAllById(productDto.getAttachmentIds());
        if (attachments.size()<2) return new Result<>(false, Messages.PRODUCT_MUST_HAVE_2_AVAILABLE_ATTACHMENTS);
        List<Long> attachmentIdsThatAlreadyAdded = productRepo.getAttachmentIdsThatAlreadyAdded(product.getId(), attachments.stream().map(att->att.getId()).toList());
        if (attachmentIdsThatAlreadyAdded.size()>0) return new Result<>(false,String.format(Messages.THIS_ATTACHMENT_ALREADY_ADDED_TO_ANOTHER_PRODUCT,attachmentIdsThatAlreadyAdded));
        product.setAttachments(attachments);
        product.setNameEn(productDto.getNameEn());
        product.setNameUz(productDto.getNameUz());
        product.setCategory(category);
        product.setSeller(seller);
        product.setAmount(productDto.getAmount());
        product.setPrice(productDto.getPrice());
        if (productDto.getDiscountedPrice() == null)
            product.setDiscountedPrice(productDto.getPrice());
        else product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setInfo(productDto.getInfo());
        product.setDescribe(productDto.getDescribe());
        product.setDirectionToUse(productDto.getDirectionToUse());
        product.setSize(productDto.getSize());
        product.setMade(productDto.getMade());
        product.setDeliveryDate(productDto.getDeliveryDate());
        product.setIsCredit(productDto.getIsCredit());
        product.setHowMuchPerMonth(productDto.getHowMuchPerMonth());
        return new Result<>(true, product);
    }


    @Override
    public Result<?> delete(Integer id) {
        Optional<Product> optional = productRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_PRODUCT_ID_NOT_EXIST);
        commentRepo.deleteByProductId(id);
        logger.info("Comments deleted by product Id. Product ID : {}", id);
        productRepo.deleteById(id);
        logger.info("Product deleted. ID : {}", id);
        return new Result<>(true, Messages.PRODUCT_DELETED);
    }
}






