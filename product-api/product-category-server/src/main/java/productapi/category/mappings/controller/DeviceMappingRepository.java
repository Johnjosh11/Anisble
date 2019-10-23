package productapi.category.mappings.controller;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import productapi.category.mappings.model.DeviceMapping;
import productapi.category.mappings.model.DeviceCategoryPath;
import productapi.category.mappings.model.DeviceMapHelperIdentifier;
import productapi.category.mappings.model.DeviceType;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DeviceMappingRepository {
    @Autowired
    DSLContext dbContext;

    @Autowired
    private DeviceMapHelperIdentifier identifier;

    public void addDeviceMapping(DeviceMapping deviceMapping) {
        identifier.getDeviceMapHelper(deviceMapping.getDeviceType().getDeviceTypeName()).insertDeviceMapping(deviceMapping);

    }

    public List<DeviceCategoryPath> getDeviceCategoryDetails(List<String> deviceIdList, String deviceType, String lang) {
        return deviceIdList
                .stream()
                .map(deviceId -> getDeviceCategories(Integer.parseInt(deviceId), deviceType, lang))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    private List<DeviceCategoryPath> getDeviceCategories(int deviceId, String deviceType, String lang) {
        if(!DeviceType.isValid(deviceType)) throw new IllegalArgumentException(
                "Unknown device type " + deviceType);
       return identifier.getDeviceMapHelper(deviceType).getDeviceCategoryList(deviceId, lang);

    }

    public void updateDeviceMapping(DeviceMapping deviceMapping) {
        identifier.getDeviceMapHelper(deviceMapping.getDeviceType().getDeviceTypeName()).updateDeviceMapping(deviceMapping);
    }
}
