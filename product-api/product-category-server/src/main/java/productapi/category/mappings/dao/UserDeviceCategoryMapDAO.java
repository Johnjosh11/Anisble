package productapi.category.mappings.dao;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep5;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.UpdateConditionStep;
import org.springframework.stereotype.Component;
import pcapi.jooq.common.db.tables.records.ProductCategoryUserDeviceMapRecord;
import productapi.category.mappings.model.DeviceMapping;
import productapi.category.mappings.util.DateUtil;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_USER_DEVICE_MAP;

@Component
public class UserDeviceCategoryMapDAO {

    public Map<Integer, List<Integer>> fetchUserDeviceCategoryMap(DSLContext dbContext) {

        Map<Integer, List<Integer>> userDeviceCategoryMap = dbContext.select()
                .from(PRODUCT_CATEGORY_USER_DEVICE_MAP)
                .where(JooqDateComparison.isNotNullAndBeforeNow(PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrAfterNow(PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDTO))
                .fetchGroups(PRODUCT_CATEGORY_USER_DEVICE_MAP.DEVICE_MODEL_ID, PRODUCT_CATEGORY_USER_DEVICE_MAP.PRODUCT_CATEGORY_ID);
        return userDeviceCategoryMap;
    }

    public void insertDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping) {
            Query[] queries = deviceMapping.getModelid()
                    .stream()
                    .map(deviceId -> addDeviceMapping(dbContext, deviceMapping, deviceId))
                    .toArray(Query[]::new);
            dbContext.batch(queries).execute();
    }

    private InsertValuesStep5<ProductCategoryUserDeviceMapRecord, Integer, Integer, Timestamp, Timestamp, Timestamp> addDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping, Integer deviceId) {
        return dbContext
                .insertInto(PRODUCT_CATEGORY_USER_DEVICE_MAP)
                .columns(PRODUCT_CATEGORY_USER_DEVICE_MAP.PRODUCT_CATEGORY_ID, PRODUCT_CATEGORY_USER_DEVICE_MAP.DEVICE_MODEL_ID,
                        PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDFROM, PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDTO,
                        PRODUCT_CATEGORY_USER_DEVICE_MAP.CREATED)
                .values(deviceMapping.getCategoryid(), deviceId, deviceMapping.getValidfrom(), deviceMapping.getValidto(),
                        DateUtil.getCurrentTime());
    }

    public List<pcapi.jooq.common.db.tables.pojos.ProductCategoryUserDeviceMap> getDeviceCategories(DSLContext dbContext, int deviceId) {
        return dbContext.select()
                .from(PRODUCT_CATEGORY_USER_DEVICE_MAP)
                .where(JooqDateComparison.isNotNull(PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDFROM))
                .and(JooqDateComparison.isNullOrClosedInPreviousMonth(PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDTO))
                .and(PRODUCT_CATEGORY_USER_DEVICE_MAP.DEVICE_MODEL_ID.eq(deviceId))
                .fetchInto(pcapi.jooq.common.db.tables.pojos.ProductCategoryUserDeviceMap.class);
    }

    public void updateDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping) {
        Query[] queries = deviceMapping.getModelid()
                .stream()
                .map(deviceId -> updateDeviceMapping(dbContext, deviceMapping, deviceId))
                .toArray(Query[]::new);
        dbContext.batch(queries).execute();
    }

    private UpdateConditionStep<ProductCategoryUserDeviceMapRecord> updateDeviceMapping(DSLContext dbContext, DeviceMapping deviceMapping, Integer deviceId) {
        Record record = dbContext.newRecord(PRODUCT_CATEGORY_USER_DEVICE_MAP);
        if (deviceMapping.getValidfrom() != null)
            record.set(PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDFROM, deviceMapping.getValidfrom());
        record.set(PRODUCT_CATEGORY_USER_DEVICE_MAP.VALIDTO, deviceMapping.getValidto());
        Integer aindex = deviceMapping.getMappingUid();

        return dbContext.update(PRODUCT_CATEGORY_USER_DEVICE_MAP)
                .set(record)
                .where(PRODUCT_CATEGORY_USER_DEVICE_MAP.PRODUCT_CATEGORY_ID.eq(deviceMapping.getCategoryid()))
                .and(PRODUCT_CATEGORY_USER_DEVICE_MAP.DEVICE_MODEL_ID.eq(deviceId))
                .and(PRODUCT_CATEGORY_USER_DEVICE_MAP.AINDEX.eq(aindex));
    }
}
