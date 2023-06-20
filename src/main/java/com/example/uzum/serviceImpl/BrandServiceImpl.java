package com.example.uzum.serviceImpl;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.*;
import com.example.uzum.helper.Filter;
import com.example.uzum.helper.Messages;
import com.example.uzum.repository.*;
import com.example.uzum.service.BrandService;
import com.example.uzum.service.CategoryService;
import com.example.uzum.service.ProductService;
import lombok.SneakyThrows;
import org.apache.el.lang.ELArithmetic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Optional;

import static com.example.uzum.helper.StringUtils.changeBracket;

@Service
public class BrandServiceImpl implements BrandService {


    @Autowired
    private BrandRepo brandRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    private static final Logger logger = LogManager.getLogger(BrandServiceImpl.class);

    @Override
    public Result<?> add(Brand brand) {
        Optional<Brand> optional = brandRepo.findByNameAndIsActive(brand.getName(), true);
        if (optional.isPresent()) return new Result<>(false, Messages.BRAND_ALREADY_EXIST);
        brand = brandRepo.save(brand);
        logger.info("New brand added. ID: {} ", brand.getId());
        return new Result<>(true, Messages.BRAND_SAVED);
    }

    @Override
    public Result<?> getAll() {
        List<Brand> allBrands = brandRepo.getAllByIsActiveOrderByNameAsc(true);
        if (allBrands.isEmpty()) return new Result<>(true, Messages.BRAND_NOT_ADDED_YET);
        return new Result<>(true, allBrands);
    }

    @Override
    public Result<?> getByFilter(String search, String categoryId, List<String> prices) {
        if (search != null) {
            String condition = " (word_similarity('" + search + "', pro.name_uz)>0 OR word_similarity('" + search + "', pro.name_en)>0) ";
            return createQuery(condition, categoryId, prices);
        } else return createQuery(null, categoryId, prices);
    }


    @Override
    public Result<?> getByPanelId(Integer panelId, String categoryId, List<String> prices) {
        Result<?> result = productService.getConditionByPanelId(panelId);
        if (!result.getSuccess()) return result;
        String condition = result.getData().toString();
        return createQuery(condition, categoryId, prices);

    }

    private Result<?> createQuery(String condition, String categoryId, List<String> prices) {
        String query = "SELECT DISTINCT * FROM brand AS brn WHERE brn.id IN (SELECT pro.brand_id FROM product AS pro WHERE pro.is_active=TRUE ";
        if (condition != null)
            query = query.concat(" AND ").concat(condition);
        if (categoryId != null) {
            int categoryIdInt = Integer.parseInt(categoryId);
            if (categoryIdInt == 0) {
                /* DO NOTHING! */
            } else {
                Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(categoryIdInt, true);
                if (optionalCategory.isEmpty()) return new Result<>(false, Messages.NO_ID_CATEGORY);
                Category category = optionalCategory.get();
                List<Integer> deepCategoryIDs = categoryService.getDeepCategoryIDsByHigherCategoryId(new ArrayList<>(), category.getId());
                query = query.concat(" AND ").concat("pro.category_id IN " + changeBracket(deepCategoryIDs));
            }
        }
        if (prices != null) {
            if (prices.size() != 2) return new Result<>(false, Messages.PRODUCT_PRICE_LIST_SIZE_MUST_BE_2);
            int fromPrice = Integer.parseInt(prices.get(0));
            int toPrice = Integer.parseInt(prices.get(1));
            query = query.concat(" AND ").concat("pro.discounted_price>=" + fromPrice + " AND pro.discounted_price<=" + toPrice);
        }
        query = query.concat(" ) ORDER BY brn.name ASC ");
        Query createQuery = entityManager.createNativeQuery(query,Brand.class);
        List<Brand> brands = createQuery.getResultList();
        logger.info("For Product Filter or Panel getting brands. Query : {} ", query);
        return new Result<>(true, brands);
    }


    @Override
    public Result<?> editById(Brand newBrand, Integer id) {
        Optional<Brand> optional = brandRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_BRAND_NOT_EXIST);
        Brand brand = optional.get();
        boolean existsByNameAndIsActive = brandRepo.existsByNameAndIsActiveAndIdNot(newBrand.getName(), true, id);
        if (existsByNameAndIsActive) return new Result<>(false, Messages.BRAND_ALREADY_EXIST);
        brand.setName(newBrand.getName());
        brandRepo.save(brand);
        logger.info("Brand name updated. ID: {} ", id);
        return new Result<>(true, Messages.BRAND_UPDATED);
    }

    @Override
    public Result<?> deleteById(Integer id) {
        Optional<Brand> optional = brandRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.SUCH_BRAND_NOT_EXIST);
        Brand brand = optional.get();
        brand.setIsActive(false);
        brandRepo.save(brand);
        logger.info("Brand deactivated. ID : {}", id);
        return new Result<>(true, Messages.BRAND_DELETED);
    }


}
