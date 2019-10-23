package productapi.category.item;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import pcapi.jooq.common.db.tables.records.ProductCategoryRecord;

import java.sql.Timestamp;
import java.util.Date;

import static pcapi.jooq.common.db.tables.ProductCategory.PRODUCT_CATEGORY;

@Component
public class ProductCategoryDAO {

    private static final String CONTENT_TYPE_DEVICES = "devices";

    public ProductCategoryRecord addCategoryItem(DSLContext dslContext, NewCategoryItem newCategoryItem) {
        Timestamp current = new Timestamp(new Date().getTime());
        return dslContext
            .insertInto(PRODUCT_CATEGORY)
            .columns(
                PRODUCT_CATEGORY.CODE_NAME,
                PRODUCT_CATEGORY.CONTENT_TYPE,
                PRODUCT_CATEGORY.CREATED,
                PRODUCT_CATEGORY.TYPE,
                PRODUCT_CATEGORY.VALIDFROM,
                PRODUCT_CATEGORY.VALIDTO,
                PRODUCT_CATEGORY.WEIGHT
            )
            .values(
                newCategoryItem.codeName,
                CONTENT_TYPE_DEVICES,
                current,
                newCategoryItem.type.name(),
                newCategoryItem.validFrom,
                newCategoryItem.validTo,
                newCategoryItem.weight
            )
            .returning()
            .fetchOne();
    }

    public int updateCategoryItem(DSLContext dslContext, CategoryItem categoryItem) {
        return dslContext
            .update(PRODUCT_CATEGORY)
            .set(PRODUCT_CATEGORY.CODE_NAME, categoryItem.codeName)
            .set(PRODUCT_CATEGORY.WEIGHT, categoryItem.weight)
            .set(PRODUCT_CATEGORY.TYPE, categoryItem.type.toString())
            .set(PRODUCT_CATEGORY.VALIDFROM, categoryItem.validFrom)
            .set(PRODUCT_CATEGORY.VALIDTO, categoryItem.validTo)
            .where(PRODUCT_CATEGORY.AINDEX.eq(categoryItem.categoryId))
            .execute();
    }

    public int updateCurrentPathItem(DSLContext dslContext, Integer categoryId, Integer pathItemId) {
        return dslContext
            .update(PRODUCT_CATEGORY)
            .set(PRODUCT_CATEGORY.PATH_ITEM_ID, pathItemId)
            .where(PRODUCT_CATEGORY.AINDEX.eq(categoryId))
            .execute();
    }
}
