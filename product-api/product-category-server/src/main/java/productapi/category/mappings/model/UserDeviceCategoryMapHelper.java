package productapi.category.mappings.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import productapi.category.mappings.dao.UserDeviceCategoryMapDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserDeviceCategoryMapHelper extends DeviceMapHelper {

    @Autowired
    private UserDeviceCategoryMapDAO userDeviceCategoryMapDao;

    @Override
    public void insertDeviceMapping(DeviceMapping deviceMapping) {
        userDeviceCategoryMapDao.insertDeviceMapping(dbContext, deviceMapping);
    }

    public List<DeviceCategoryPath> getDeviceCategoryList(int deviceId, String lang) {
        List<pcapi.jooq.common.db.tables.pojos.ProductCategoryUserDeviceMap> deviceCategoryList = userDeviceCategoryMapDao.getDeviceCategories(dbContext, deviceId);
        return  buildDeviceCategories(deviceCategoryList, lang);
    }

    private List<DeviceCategoryPath> buildDeviceCategories(List<pcapi.jooq.common.db.tables.pojos.ProductCategoryUserDeviceMap> deviceCategoryList, String lang) {
        List<DeviceCategoryPath> list = new ArrayList<>();
        deviceCategoryList
                .stream()
                .map(p -> list.add(getDeviceCategoryPath(p, lang))).collect(Collectors.toList());
        return list;
    }

    private DeviceCategoryPath getDeviceCategoryPath(pcapi.jooq.common.db.tables.pojos.ProductCategoryUserDeviceMap p, String lang) {
        return new DeviceCategoryPath(p.getAindex(), p.getDeviceModelId(), p.getProductCategoryId(), getAllDeviceCategories(p.getProductCategoryId(), lang), p.getValidfrom(), p.getValidto());
    }

    @Override
    public void updateDeviceMapping(DeviceMapping deviceMapping) {
        userDeviceCategoryMapDao.updateDeviceMapping(dbContext, deviceMapping);
    }
}
