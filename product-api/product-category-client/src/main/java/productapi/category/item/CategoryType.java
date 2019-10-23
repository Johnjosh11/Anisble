package productapi.category.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CategoryType {
    @JsonProperty("marketing")
    MARKETING,

    @JsonProperty("reporting")
    REPORTING,

    @JsonProperty("sales")
    SALES;

    public String toString() {
        return this.name().toLowerCase();
    }
}
