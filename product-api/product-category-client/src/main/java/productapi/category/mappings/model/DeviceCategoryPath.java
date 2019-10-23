package productapi.category.mappings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DeviceCategoryPath {

    @JsonProperty("categoryPathUid")
    int categoryPathUid;
    @JsonProperty("deviceId")
    int deviceId;
    @JsonProperty("categoryId")
    int categoryId;
    @JsonProperty("categories")
    String categories;
    @JsonProperty("validFrom")
    Timestamp validFrom;
    @JsonProperty("validTo")
    Timestamp validTo;

    /**
     * No args constructor for use in serialization
     *
     */
    public DeviceCategoryPath() {}

    @JsonCreator
    public DeviceCategoryPath(int categoryPathUid, int deviceId, int categoryId, String categories, Timestamp validFrom,
        Timestamp validTo) {
        this.categoryPathUid = categoryPathUid;
        this.deviceId = deviceId;
        this.categoryId = categoryId;
        this.categories = categories;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    @JsonProperty("categoryPathUid")
    public int getCategoryPathUid() {
        return categoryPathUid;
    }

    @JsonProperty("categoryId")
    public int getCategoryId() {
        return categoryId;
    }

    @JsonProperty("categories")
    public String getCategories() {
        return categories;
    }

    @JsonProperty("validFrom")
    public Timestamp getValidFrom() {
        return validFrom;
    }

    @JsonProperty("validTo")
    public Timestamp getValidTo() {
        return validTo;
    }

    @JsonProperty("deviceId")
    public int getDeviceId() {
        return deviceId;
    }
}
