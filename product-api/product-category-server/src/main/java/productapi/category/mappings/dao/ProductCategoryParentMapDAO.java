package productapi.category.mappings.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import productapi.category.mappings.model.CategoryPath;

import static org.jooq.impl.DSL.currentTimestamp;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PARENT_MAP;

@Component
public class ProductCategoryParentMapDAO {

    public Integer getParentDeviceCategoryId(DSLContext dbContext, int categoryId) {
        return dbContext.select()
                .from(PRODUCT_CATEGORY_PARENT_MAP)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO))
                .and(PRODUCT_CATEGORY_PARENT_MAP.PRODUCT_CATEGORY_ID.eq(categoryId))
                .fetchAny(PRODUCT_CATEGORY_PARENT_MAP.PARENT_ID);
    }

    public void insertParentMap(DSLContext dbContext, Integer categoryId, CategoryPath parentMap) {
        dbContext
            .insertInto(PRODUCT_CATEGORY_PARENT_MAP)
            .set(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM, parentMap.getValidFrom())
            .set(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO, parentMap.getValidTo())
            .set(PRODUCT_CATEGORY_PARENT_MAP.PARENT_ID, parentMap.getCategoryId())
            .set(PRODUCT_CATEGORY_PARENT_MAP.PRODUCT_CATEGORY_ID, categoryId)
            .set(PRODUCT_CATEGORY_PARENT_MAP.CREATED, currentTimestamp())
            .execute();
    }

    public void updateParentMap(DSLContext dbContext, CategoryPath parentMap) {
        dbContext
            .update(PRODUCT_CATEGORY_PARENT_MAP)
            .set(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM, parentMap.getValidFrom())
            .set(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO, parentMap.getValidTo())
            .where(PRODUCT_CATEGORY_PARENT_MAP.AINDEX.eq(parentMap.getAindex()))
            .execute();
    }
}
