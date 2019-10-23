package productapi.category.mappings.dao;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep5;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.UpdateConditionStep;
import org.springframework.stereotype.Component;
import pcapi.jooq.common.db.tables.records.ProductCategoryHandsetMapRecord;
import productapi.category.mappings.model.DeviceMapping;
import productapi.category.mappings.util.DateUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_HANDSET_MAP;

@Component
public class HandsetCategoryMapDAO {

    public Map<Integer, List<Integer>> fetchHandsetCategoryMap(DSLContext dbContext) {
        Map<Integer, List<Integer>> handsetCategoryMap = dbContext.select()
                .from(PRODUCT_CATEGORY_HANDSET_MAP)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY_HANDSET_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY_HANDSET_MAP.VALIDTO))
                .fetchGroups(PRODUCT_CATEGORY_HANDSET_MAP.HANDSET_MODEL_ID, PRODUCT_CATEGORY_HANDSET_MAP.PRODUCT_CATEGORY_ID);
        return handsetCategoryMap;
    }

    public void insertDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping) {
            Query[] queries = deviceMapping.getModelid()
                    .stream()
                    .map(deviceId -> addHandsetMapping(dbContext, deviceMapping, deviceId))
                    .toArray(Query[]::new);
            dbContext.batch(queries).execute();
    }


    private InsertValuesStep5<ProductCategoryHandsetMapRecord, Integer, Integer, Timestamp, Timestamp, Timestamp> addHandsetMapping(DSLContext dbContext, DeviceMapping deviceMapping, Integer deviceId) {
        return dbContext
                .insertInto(PRODUCT_CATEGORY_HANDSET_MAP)
                .columns(PRODUCT_CATEGORY_HANDSET_MAP.PRODUCT_CATEGORY_ID, PRODUCT_CATEGORY_HANDSET_MAP.HANDSET_MODEL_ID,
                        PRODUCT_CATEGORY_HANDSET_MAP.VALIDFROM, PRODUCT_CATEGORY_HANDSET_MAP.VALIDTO,
                        PRODUCT_CATEGORY_HANDSET_MAP.CREATED)
                .values(deviceMapping.getCategoryid(), deviceId, deviceMapping.getValidfrom(), deviceMapping.getValidto(),
                        DateUtil.getCurrentTime());
    }

    public List<pcapi.jooq.common.db.tables.pojos.ProductCategoryHandsetMap> getDeviceCategories(DSLContext dbContext, int deviceId) {
        return dbContext.select()
                .from(PRODUCT_CATEGORY_HANDSET_MAP)
                .where(JooqDateComparison.isNotNull(PRODUCT_CATEGORY_HANDSET_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrClosedInPreviousMonth(PRODUCT_CATEGORY_HANDSET_MAP.VALIDTO))
                .and(PRODUCT_CATEGORY_HANDSET_MAP.HANDSET_MODEL_ID.eq(deviceId))
                .fetchInto(pcapi.jooq.common.db.tables.pojos.ProductCategoryHandsetMap.class);
    }

    public void updateDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping) {
        Query[] queries = deviceMapping.getModelid()
                .stream()
                .map(deviceId -> updateDeviceMapping(dbContext, deviceMapping, deviceId))
                .toArray(Query[]::new);
        dbContext.batch(queries).execute();
    }

    private UpdateConditionStep<ProductCategoryHandsetMapRecord> updateDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping, Integer deviceId) {
        Record record = dbContext.newRecord(PRODUCT_CATEGORY_HANDSET_MAP);
        if (deviceMapping.getValidfrom() != null)
            record.set(PRODUCT_CATEGORY_HANDSET_MAP.VALIDFROM, deviceMapping.getValidfrom());
        record.set(PRODUCT_CATEGORY_HANDSET_MAP.VALIDTO, deviceMapping.getValidto());
        Integer aindex = deviceMapping.getMappingUid();

        return dbContext.update(PRODUCT_CATEGORY_HANDSET_MAP)
                .set(record)
                .where(PRODUCT_CATEGORY_HANDSET_MAP.PRODUCT_CATEGORY_ID.eq(deviceMapping.getCategoryid()))
                .and(PRODUCT_CATEGORY_HANDSET_MAP.HANDSET_MODEL_ID.eq(deviceId))
                .and(PRODUCT_CATEGORY_HANDSET_MAP.AINDEX.eq(aindex));
    }
}
