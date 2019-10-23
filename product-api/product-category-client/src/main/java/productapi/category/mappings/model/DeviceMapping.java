package productapi.category.mappings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.List;

public class DeviceMapping {
    private int mappingUid;
    private int categoryid;
    private List<Integer> modelid;
    private Timestamp validto;
    private Timestamp validfrom;
    private DeviceType deviceType;

    /**
     * No args constructor for use in serialization
     *
     */
    public DeviceMapping() {}

    @JsonCreator
    public DeviceMapping(@JsonProperty("mappingUid") int mappingUid,
            @JsonProperty("categoryid") int categoryid,
            @JsonProperty("modelid") List<Integer> modelid,
            @JsonProperty("validto") Timestamp validto,
            @JsonProperty("validfrom") Timestamp validfrom,
            @JsonProperty("deviceType") DeviceType deviceType) {
        this.mappingUid = mappingUid;
        this.categoryid = categoryid;
        this.modelid = modelid;
        this.validto = validto;
        this.validfrom = validfrom;
        this.deviceType = deviceType;
    }

    public int getMappingUid() {
        return mappingUid;
    }

    public void setMappingUid(int mappingUid) {
        this.mappingUid = mappingUid;
    }

    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public List<Integer> getModelid() {
        return modelid;
    }

    public void setModelid(List<Integer> modelids) {
        this.modelid = modelids;
    }

    public Timestamp getValidto() {
        return validto;
    }

    public void setValidto(Timestamp validto) {
        this.validto = validto;
    }

    public Timestamp getValidfrom() {
        return validfrom;
    }

    public void setValidfrom(Timestamp validfrom) {
        this.validfrom = validfrom;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType type) {
        this.deviceType = type;
    }
}
