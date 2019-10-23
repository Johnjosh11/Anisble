package productapi.category.mappings.model;

import java.util.Arrays;

public enum DeviceType {
    HANDSET("handset"),
    USERDEVICE("userdevice"),
    PRODUCT("product");

    private final String deviceTypeName;

    DeviceType(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public static boolean isValid(String value) {
        for (DeviceType type : values()) {
            if (type.deviceTypeName.equalsIgnoreCase(value)) {
                return true;
            }
        }
        throw new IllegalArgumentException(
                "Unknown enum type " + value + ", Allowed values are " + Arrays.toString(values()));
    }
}
