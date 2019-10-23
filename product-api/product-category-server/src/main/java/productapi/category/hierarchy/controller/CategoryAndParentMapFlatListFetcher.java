package productapi.category.hierarchy.controller;

import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_NAME_TRANSLATION;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PARENT_MAP;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PATH_ITEM;

import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import productapi.category.mappings.dao.JooqDateComparison;
import pcapi.jooq.common.db.tables.records.ProductCategoryParentMapRecord;

@Component
public class CategoryAndParentMapFlatListFetcher {

    @Autowired
    DSLContext create;

    public Result<Record> fetchAllOpenProductCategories() {
        return create.select()
                .from(PRODUCT_CATEGORY)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY.VALIDFROM).and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY.VALIDTO)))
                .fetch();
    }

    public Result<Record> fetchAllOpenProductCategoriesParentMaps() {
        return create.select()
                .from(PRODUCT_CATEGORY_PARENT_MAP)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM).and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO)))
                .fetch();
    }

    public Result<Record> fetchAllProductCategories() {
        return create.select()
                .from(PRODUCT_CATEGORY)
                .fetch();
    }

    public Result<Record> fetchAllProductCategoriesParentMaps() {
        return create.select()
                .from(PRODUCT_CATEGORY_PARENT_MAP)
                .fetch();
    }

    public Result<Record> fetchAllProductCategoryNames() {
        return create.select()
                .from(PRODUCT_CATEGORY_NAME_TRANSLATION)
                .fetch();
    }

    public Result<Record> fetchAllPathItems() {
        return create.select()
                .from(PRODUCT_CATEGORY_PATH_ITEM)
                .fetch();
    }

    public Map<Integer, List<ProductCategoryParentMapRecord>> fetchAllValidProductCategoryParentMaps() {
        return create.select()
                .from(PRODUCT_CATEGORY_PARENT_MAP)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO).or(JooqDateComparison.isNotNullAndWithin30Days(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO)))
                .fetch()
                .intoGroups(PRODUCT_CATEGORY_PARENT_MAP.PRODUCT_CATEGORY_ID, ProductCategoryParentMapRecord.class);
    }

    public Map<Integer, List<ProductCategoryParentMapRecord>> fetchAllProductCategoryParentMapsIntoCategoryIdGroups() {
        return fetchAllProductCategoriesParentMaps().intoGroups(PRODUCT_CATEGORY_PARENT_MAP.PRODUCT_CATEGORY_ID, ProductCategoryParentMapRecord.class);
    }
}
