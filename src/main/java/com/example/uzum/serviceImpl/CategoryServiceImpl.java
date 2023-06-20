package com.example.uzum.serviceImpl;

import com.example.uzum.dto.category.CategoryDTO;
import com.example.uzum.dto.category.CategoryDTOToGet;
import com.example.uzum.entity.MainPanel;
import com.example.uzum.entity.PanelConnectMethod;
import com.example.uzum.entity.Seller;
import com.example.uzum.helper.Messages;
import com.example.uzum.entity.Category;
import com.example.uzum.repository.*;
import com.example.uzum.dto.Result;
import com.example.uzum.service.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {


    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private CommentRepo commentRepo;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Override
    public Result<String> addCategory(CategoryDTO categoryDto) {
        boolean existsByNameAndParentId;
        Category parentCategory = null;
        /* Grand category ID is given as 0 from UI */
        if (categoryDto.getParentCategoryId() == 0)
            existsByNameAndParentId = categoryRepo.existsByNameEnAndParentCategoryAndIsActiveOrNameUzAndParentCategoryAndIsActive
                    (categoryDto.getNameEn(), null, true, categoryDto.getNameUz(), null, true);
        else {
            Optional<Category> optionalParent = categoryRepo.findByIdAndIsActive(categoryDto.getParentCategoryId(), true);
            if (optionalParent.isEmpty()) return new Result<>(false, Messages.SUCH_CATEGORY_DOES_NOT_EXIST);
            parentCategory = optionalParent.get();
            existsByNameAndParentId = categoryRepo.existsByNameEnAndParentCategoryAndIsActiveOrNameUzAndParentCategoryAndIsActive
                    (categoryDto.getNameEn(), parentCategory, true, categoryDto.getNameUz(), parentCategory, true);
        }
        if (existsByNameAndParentId) return new Result<>(false, Messages.CATEGORY_ALREADY_EXIST);
        Category newCategory;
        newCategory = new Category(categoryDto.getNameUz(), categoryDto.getNameEn(), parentCategory);
        newCategory = categoryRepo.save(newCategory);
        logger.info("New category added. ID : {}", newCategory.getId());
        return new Result<>(true, Messages.CATEGORY_SAVED);
    }


    @Override
    public Result<?> getByFilter(String search, String categoryId, List<String> prices, List<String> brandIds) {
        if (search == null) return createQuery(null, categoryId, prices, brandIds);
        String condition = " (word_similarity('" + search + "', pro.name_uz)>0 OR word_similarity('" + search + "', pro.name_en)>0) ";
        return createQuery(condition, categoryId, prices, brandIds);
    }


    private Result<?> createQuery(String condition, String categoryId, List<String> prices, List<String> brandIds) {
        String query = "SELECT DISTINCT * FROM category AS ctg WHERE ctg.is_active=TRUE AND ctg.id IN (SELECT pro.category_id FROM product AS pro WHERE pro.is_active=TRUE ";
        if (condition != null) {
            query = query.concat(" AND ").concat(condition);
        }
        if (categoryId != null) {
            int categoryIdInt = Integer.parseInt(categoryId);
            List<Integer> categoryIds;
            categoryIds = getDeepCategoryIDsByHigherCategoryId(new ArrayList<>(), categoryIdInt);
            query = query.concat(" AND ").concat("pro.category_id IN " + changeBracket(categoryIds));
        }
        if (prices != null) {
            if (prices.size() != 2) return new Result<>(false, Messages.PRODUCT_PRICE_LIST_SIZE_MUST_BE_2);
            int from = Integer.parseInt(prices.get(0));
            int to = Integer.parseInt(prices.get(1));
            query = query.concat(" AND ").concat("pro.discounted_price>=" + from + " AND pro.discounted_price<=" + to);
        }
        if (brandIds != null) {
            List<Integer> brandIdsInt = brandIds.stream().map(Integer::parseInt).toList();
            query = query.concat(" AND ").concat("pro.brand_id IN " + changeBracket(brandIdsInt));
        }
        query = query.concat(" ) ORDER BY ctg.parent_category_id ASC ");
        Query createQuery = entityManager.createNativeQuery(query, Category.class);
        logger.info("For Product Filter and Panel getting categories. Query : {} ", query);
        List<Category> deepCategories = createQuery.getResultList();
        if (deepCategories.size() == 1) {  // for getting brothers of deep category
            deepCategories = categoryRepo.getCategoriesByParentCategoryAndIsActive(deepCategories.get(0).getParentCategory(), true);
        }
        Set<Category> categorySet = new HashSet<>();
        Category requestedCategory;
        if (categoryId == null) requestedCategory = null;
        else {
            int categoryIdInt = Integer.parseInt(categoryId);
            Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(categoryIdInt, true);
            requestedCategory = optionalCategory.get();
        }
        for (Category deepCategory : deepCategories) {
            categorySet.add(getFamilyTreeOfCategories(deepCategory, requestedCategory));
        }
        if (categoryId == null) return new Result<>(true, categorySet);
        else {
            if (requestedCategory.getParentCategory() == null)
                return new Result<>(true, Set.of(Set.of(requestedCategory), categorySet));
            if (requestedCategory.getParentCategory().getParentCategory() == null)
                return new Result<>(true, Set.of(Set.of(requestedCategory.getParentCategory()), Set.of(requestedCategory), categorySet));
            if (requestedCategory.getParentCategory().getParentCategory().getParentCategory() == null)
                return new Result<>(true, Set.of(Set.of(requestedCategory.getParentCategory().getParentCategory()), Set.of(requestedCategory.getParentCategory()), Set.of(requestedCategory), categorySet));
        }
        return null;
    }


    private Category getFamilyTreeOfCategories(Category childCategory, Category higherCategory) {
        if (childCategory.getParentCategory() == higherCategory) return childCategory;
        else return getFamilyTreeOfCategories(childCategory.getParentCategory(), higherCategory);
    }

    public List<Integer> getDeepCategoryIDsByHigherCategoryId(List<Integer> newList, Integer id) {
        List<Category> subCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(id, true);
        for (Category category : subCategories) {
            getDeepCategoryIDsByHigherCategoryId(newList, category.getId());
        }
        if (subCategories.isEmpty()) newList.add(id);
        return newList;
    }

    @Override
    public Result<?> getByPanelId(Integer panelId, String categoryId, List<String> prices, List<String> brandIds) {
        Result<?> result = productService.getConditionByPanelId(panelId);
        if (!result.getSuccess()) return result;
        String condition = result.getData().toString();
        return createQuery(condition, categoryId, prices, brandIds);
    }

    @Override
    public Result<?> getGrandCategory() {
        List<Category> grandCategory = categoryRepo.getCategoriesByParentCategoryAndIsActive(null, true);
        if (grandCategory.size() == 0) return new Result<>(true, Messages.NO_ANY_CATEGORY);
        return new Result<>(true, grandCategory);
    }

    @Override
    public Result<?> getByGrandCategoryId(Integer id) {
        Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(id, true);
        if (optionalCategory.isEmpty()) return new Result<>(false, Messages.NO_ID_CATEGORY);
        Category category = optionalCategory.get();
        if (category.getParentCategory() != null)
            return new Result<>(false, Messages.THIS_CATEGORY_NOT_GRAND_CATEGORY_ETC);
        List<Category> secondStepCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(id, true);
        if (secondStepCategories.size() == 0)
            return new Result<>(true, Messages.NO_ANY_NESTED_CATEGORY);
        List<CategoryDTOToGet> categories = new ArrayList<>();
        CategoryDTOToGet categoryDTOToGet;
        List<Category> thirdStepCategories;
        for (Category secondStepCategory : secondStepCategories) {
            thirdStepCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(secondStepCategory.getId(), true);
            categoryDTOToGet = new CategoryDTOToGet(
                    secondStepCategory,
                    thirdStepCategories
            );
            categories.add(categoryDTOToGet);
        }
        return new Result<>(true, categories);
    }

    @Override
    public Result<?> getByParentCategoryId(Integer id) {
        Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(id, true);
        if (optionalCategory.isEmpty()) return new Result<>(false, Messages.SUCH_CATEGORY_DOES_NOT_EXIST);
        Category category = optionalCategory.get();
        if (category.getParentCategory() == null) {
            Category grandCategory = optionalCategory.get();
            List<Category> secondStepCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(id, true);
            List<List<Category>> dto = List.of(List.of(grandCategory), secondStepCategories);
            return new Result<>(true, dto);
        } else if (category.getParentCategory().getParentCategory() == null) {
            Category secondStepCategory = optionalCategory.get();
            Category grandCategory = secondStepCategory.getParentCategory();
            List<Category> thirdStepCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(id, true);
            List<List<Category>> dto = List.of(List.of(grandCategory), List.of(secondStepCategory), thirdStepCategories);
            return new Result<>(true, dto);
        } else {
            Category grandCategory = category.getParentCategory().getParentCategory();
            Category secondStepCategory = category.getParentCategory();
            List<Category> thirdStepCategories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(secondStepCategory.getId(), true);
            List<List<Category>> dto = List.of(List.of(grandCategory), List.of(secondStepCategory), thirdStepCategories);
            return new Result<>(true, dto);
        }
    }

    @Override
    public Result<?> editById(CategoryDTO categoryDto, Integer id) {
        Optional<Category> optionalCategory = categoryRepo.findByIdAndIsActive(id, true);
        if (optionalCategory.isEmpty()) return new Result<>(false, Messages.NO_ID_CATEGORY);
        Category category = optionalCategory.get();
        boolean existByNameAndParentId = categoryRepo.existsByNameEnAndParentCategoryAndIsActiveAndIdNotOrNameUzAndParentCategoryAndIsActiveAndIdNot(categoryDto.getNameEn(), category.getParentCategory(), true, id, categoryDto.getNameUz(), category.getParentCategory(), true, id);
        if (existByNameAndParentId) return new Result<>(false, Messages.CATEGORY_ALREADY_EXIST);
        category.setNameEn(categoryDto.getNameEn());
        category.setNameUz(categoryDto.getNameUz());
        categoryRepo.save(category);
        logger.info("Category updated. ID : {} ", id);
        return new Result<>(true, Messages.CATEGORY_EDITED);
    }

    @Override
    public Result<?> deleteById(Integer id) {
        Optional<Category> optional = categoryRepo.findByIdAndIsActive(id, true);
        if (optional.isEmpty()) return new Result<>(false, Messages.NO_ID_CATEGORY);
        Category category = optional.get();
        delete(category);
        logger.info("Category and its subCategories and their products deactivated. Category ID : {}", id);
        return new Result<>(true, Messages.CATEGORY_DELETED);
    }


    private void delete(Category category) {
        List<Category> categories = categoryRepo.getCategoriesByParentCategoryIdAndIsActive(category.getId(), true);
        for (Category subCategory : categories) {
            delete(subCategory);
        }
        if (categories.isEmpty()) {
            commentRepo.deleteByCategory(category.getId());
            productRepo.deleteByCategoryId(category.getId());
        }
        category.setIsActive(Boolean.FALSE);
        categoryRepo.save(category);
    }


}
