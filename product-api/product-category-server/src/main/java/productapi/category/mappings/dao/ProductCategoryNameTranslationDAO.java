package productapi.category.mappings.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.currentTimestamp;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_NAME_TRANSLATION;

@Component
public class ProductCategoryNameTranslationDAO {
    private static final String FINNISH = "fi";

    public Map<String, String> getCategoryTranslation(DSLContext dbContext, int categoryId) {
        return dbContext.select()
                .from(PRODUCT_CATEGORY_NAME_TRANSLATION)
                .where(PRODUCT_CATEGORY_NAME_TRANSLATION.PRODUCT_CATEGORY_ID.eq(categoryId))
                .fetchMap(PRODUCT_CATEGORY_NAME_TRANSLATION.LANGUAGE, PRODUCT_CATEGORY_NAME_TRANSLATION.NAME) ;
    }

    public boolean nameHasBeenModified(DSLContext context, Integer categoryId, String name) {
        int finnishNameCount = context
            .select(count())
            .from(PRODUCT_CATEGORY_NAME_TRANSLATION)
            .where(PRODUCT_CATEGORY_NAME_TRANSLATION.PRODUCT_CATEGORY_ID.eq(categoryId))
            .and(PRODUCT_CATEGORY_NAME_TRANSLATION.LANGUAGE.eq(FINNISH))
            .and(PRODUCT_CATEGORY_NAME_TRANSLATION.NAME.eq(name))
            .fetchOne(0, Integer.class);
        return finnishNameCount == 0;
    }

    public Integer updateFinnishName(DSLContext context, Integer categoryId, String name) {
        return context
            .update(PRODUCT_CATEGORY_NAME_TRANSLATION)
            .set(PRODUCT_CATEGORY_NAME_TRANSLATION.NAME, name)
            .where(PRODUCT_CATEGORY_NAME_TRANSLATION.PRODUCT_CATEGORY_ID.eq(categoryId))
            .and(PRODUCT_CATEGORY_NAME_TRANSLATION.LANGUAGE.eq(FINNISH))
            .execute();
    }

    public void insertFinnishName(DSLContext context, Integer categoryId, String name) {
        context
            .insertInto(PRODUCT_CATEGORY_NAME_TRANSLATION)
            .set(PRODUCT_CATEGORY_NAME_TRANSLATION.PRODUCT_CATEGORY_ID, categoryId)
            .set(PRODUCT_CATEGORY_NAME_TRANSLATION.NAME, name)
            .set(PRODUCT_CATEGORY_NAME_TRANSLATION.LANGUAGE, FINNISH)
            .set(PRODUCT_CATEGORY_NAME_TRANSLATION.CREATED, currentTimestamp())
            .execute();
    }
}
