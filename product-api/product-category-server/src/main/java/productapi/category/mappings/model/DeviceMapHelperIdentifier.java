package productapi.category.mappings.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapHelperIdentifier {
    @Autowired
    private HandsetCategoryMapHelper handsetCategoryMapHelper;

    @Autowired
    private UserDeviceCategoryMapHelper userDeviceCategoryMapHelper;

    public DeviceMapHelper getDeviceMapHelper(String type){
        if ("handset".equalsIgnoreCase(type)) {
            return handsetCategoryMapHelper;
        } else if ("userdevice".equalsIgnoreCase(type)) {
            return userDeviceCategoryMapHelper;
        } else {
            throw new IllegalArgumentException("Invalid device type -- "+ type);
        }
    }

}
