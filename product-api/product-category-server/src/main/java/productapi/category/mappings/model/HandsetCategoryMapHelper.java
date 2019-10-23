package productapi.category.mappings.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import productapi.category.mappings.dao.HandsetCategoryMapDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class HandsetCategoryMapHelper extends DeviceMapHelper {

    @Autowired
    private HandsetCategoryMapDAO handsetCategoryMapDao;

    @Override
    public void insertDeviceMapping(DeviceMapping deviceMapping) {
        handsetCategoryMapDao.insertDeviceMapping(dbContext, deviceMapping);
    }

    public List<DeviceCategoryPath> getDeviceCategoryList(int deviceId, String lang){
        List<pcapi.jooq.common.db.tables.pojos.ProductCategoryHandsetMap> deviceCategoryList = handsetCategoryMapDao.getDeviceCategories(dbContext, deviceId);
        return  buildDeviceCategories(deviceCategoryList, lang);
    }

    private List<DeviceCategoryPath> buildDeviceCategories(List<pcapi.jooq.common.db.tables.pojos.ProductCategoryHandsetMap> deviceCategoryList, String lang) {
        List<DeviceCategoryPath> list = new ArrayList<>();
        deviceCategoryList
                .stream()
                .map(p -> list.add(getDeviceCategoryPath(p, lang))).collect(Collectors.toList());
        return list;
    }

    private DeviceCategoryPath getDeviceCategoryPath(pcapi.jooq.common.db.tables.pojos.ProductCategoryHandsetMap p, String lang) {
        return new DeviceCategoryPath(p.getAindex(), p.getHandsetModelId(), p.getProductCategoryId(), getAllDeviceCategories(p.getProductCategoryId(), lang), p.getValidfrom(), p.getValidto());
    }

    @Override
    public void updateDeviceMapping(DeviceMapping deviceMapping) {
        handsetCategoryMapDao.updateDeviceMapping(dbContext, deviceMapping);
    }

}
