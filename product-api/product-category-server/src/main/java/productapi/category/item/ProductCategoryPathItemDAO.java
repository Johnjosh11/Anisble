package productapi.category.item;


import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import pcapi.jooq.common.db.tables.records.ProductCategoryPathItemRecord;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.currentTimestamp;
import static pcapi.jooq.common.db.tables.ProductCategoryPathItem.PRODUCT_CATEGORY_PATH_ITEM;

@Component
public class ProductCategoryPathItemDAO {

    public ProductCategoryPathItemRecord addPathItem(DSLContext dslContext, Integer categoryId, String path) {
        return dslContext.insertInto(PRODUCT_CATEGORY_PATH_ITEM)
            .set(PRODUCT_CATEGORY_PATH_ITEM.PRODUCT_CATEGORY_ID, categoryId)
            .set(PRODUCT_CATEGORY_PATH_ITEM.NAME, path)
            .set(PRODUCT_CATEGORY_PATH_ITEM.CREATED, currentTimestamp())
            .returning()
            .fetchOne();
    }

    public int closeOpenPathItems(DSLContext dslContext, Integer categoryId) {
        return dslContext
            .update(PRODUCT_CATEGORY_PATH_ITEM)
            .set(PRODUCT_CATEGORY_PATH_ITEM.CLOSED, currentTimestamp())
            .where(PRODUCT_CATEGORY_PATH_ITEM.PRODUCT_CATEGORY_ID.eq(categoryId))
            .and(PRODUCT_CATEGORY_PATH_ITEM.CLOSED.isNull())
            .execute();
    }

    public boolean pathItemHasBeenModified(DSLContext dslContext, Integer categoryId, String pathItem) {
        int amountOfOpenPaths = dslContext
            .select(count())
            .from(PRODUCT_CATEGORY_PATH_ITEM)
            .where(PRODUCT_CATEGORY_PATH_ITEM.PRODUCT_CATEGORY_ID.eq(categoryId))
            .and(PRODUCT_CATEGORY_PATH_ITEM.NAME.eq(pathItem))
            .and(PRODUCT_CATEGORY_PATH_ITEM.CLOSED.isNull())
            .fetchOne(0, Integer.class);
        return amountOfOpenPaths == 0;
    }
}
