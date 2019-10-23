package productapi.category.item;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import productapi.category.ProductApiInvalidRequestException;
import productapi.category.mappings.dao.ProductCategoryNameTranslationDAO;
import pcapi.jooq.common.db.tables.records.ProductCategoryPathItemRecord;
import pcapi.jooq.common.db.tables.records.ProductCategoryRecord;
import productapi.category.mappings.dao.ProductCategoryParentMapDAO;
import productapi.category.mappings.model.CategoryPath;

import java.util.List;

@Component
public class ProductCategoryRepository {

    @Autowired
    DSLContext dslContext;

    @Autowired
    ProductCategoryDAO productCategoryDAO;

    @Autowired
    ProductCategoryPathItemDAO productCategoryPathItemDAO;

    @Autowired
    ProductCategoryNameTranslationDAO productCategoryNameTranslationDAO;

    @Autowired
    ProductCategoryParentMapDAO productCategoryParentMapDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCategoryItem(CategoryItem categoryItem) {
        updateProductCategory(categoryItem, dslContext);
        updateFinnishNameIfNameHasBeenModified(dslContext, categoryItem.categoryId, categoryItem.name);
        updatePathItemIfPathHasBeenModified(dslContext, categoryItem.categoryId, categoryItem.pathItem);
        updateOrInsertParentMappings(dslContext, categoryItem.categoryId, categoryItem.categoryParentMappings);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Integer addCategoryItem(NewCategoryItem newCategoryItem) {
        ProductCategoryRecord newCategoryRecord = productCategoryDAO.addCategoryItem(dslContext, newCategoryItem);
        Integer categoryRecordId = newCategoryRecord.getAindex();
        productCategoryNameTranslationDAO.insertFinnishName(dslContext, categoryRecordId, newCategoryItem.name);
        ProductCategoryPathItemRecord newPathItemRecord = productCategoryPathItemDAO.addPathItem(
            dslContext, categoryRecordId, newCategoryItem.pathItem);
        productCategoryDAO.updateCurrentPathItem(dslContext, categoryRecordId, newPathItemRecord.getAindex());
        updateOrInsertParentMappings(dslContext, categoryRecordId, newCategoryItem.categoryParentMappings);
        return categoryRecordId;
    }

    private void updateProductCategory(CategoryItem categoryItem, DSLContext context) {
        int updatedRows = productCategoryDAO.updateCategoryItem(context, categoryItem);
        if (updatedRows == 0) {
            String errorMessage = String.format("Update error, category %s does not exist", categoryItem.categoryId);
            throw new ProductApiInvalidRequestException(errorMessage);
        }
    }

    private void updateFinnishNameIfNameHasBeenModified(DSLContext context, Integer categoryId, String name) {
        boolean nameHasBeenModified = productCategoryNameTranslationDAO.nameHasBeenModified(context, categoryId, name);
        if (nameHasBeenModified) {
            Integer updatedCount = productCategoryNameTranslationDAO.updateFinnishName(context, categoryId, name);
            if (updatedCount == 0) {
                productCategoryNameTranslationDAO.insertFinnishName(context, categoryId, name);
            }
        }
    }

    private void updatePathItemIfPathHasBeenModified(DSLContext dslContext, Integer categoryId, String pathItem) {
        boolean pathHasBeenModified = productCategoryPathItemDAO.pathItemHasBeenModified(dslContext, categoryId, pathItem);
        if (pathHasBeenModified) {
            productCategoryPathItemDAO.closeOpenPathItems(dslContext, categoryId);
            ProductCategoryPathItemRecord newPathItem = productCategoryPathItemDAO.addPathItem(dslContext, categoryId, pathItem);
            productCategoryDAO.updateCurrentPathItem(dslContext, categoryId, newPathItem.getAindex());
        }
    }

    private void updateOrInsertParentMappings(DSLContext dslContext, Integer categoryId, List<CategoryPath> categoryParentMappings) {
        if (categoryParentMappings != null) {
            categoryParentMappings.forEach(parentMapping -> {
                if (parentMapping.getAindex() != null) {
                    productCategoryParentMapDAO.updateParentMap(dslContext, parentMapping);
                } else {
                    productCategoryParentMapDAO.insertParentMap(dslContext, categoryId, parentMapping);
                }
            });
        }
    }
}
