package productapi.category.mappings.dao;

import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PRODUCT_MAP;

@Component
public class ProductCategoryMapDAO {

    public Map<Integer, List<Integer>> fetchProductCategoryMap(DSLContext dbContext) {
        Map<Integer, List<Integer>> handsetCategoryMap = dbContext.select()
                .from(PRODUCT_CATEGORY_PRODUCT_MAP)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY_PRODUCT_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY_PRODUCT_MAP.VALIDTO))
                .fetchGroups(PRODUCT_CATEGORY_PRODUCT_MAP.PRODUCT_ID, PRODUCT_CATEGORY_PRODUCT_MAP.PRODUCT_CATEGORY_ID);
        return handsetCategoryMap;
    }
}
