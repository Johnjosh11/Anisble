package productapi.category.mappings.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class DeviceCategory {
    List<Integer> deviceIds;
    DeviceType deviceType;

    @JsonCreator
    public DeviceCategory(List<Integer> deviceIds, DeviceType deviceType) {
        this.deviceIds = deviceIds;
        this.deviceType = deviceType;
    }

    public List<Integer> getDeviceIds() {
        return deviceIds;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }
}
