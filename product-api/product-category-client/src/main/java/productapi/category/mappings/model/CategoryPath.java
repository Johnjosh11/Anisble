package productapi.category.mappings.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.sql.Timestamp;
import java.util.List;

public class CategoryPath {

    private int categoryId;
    private List<String> categories;
    private Timestamp validTo;
    private Timestamp validFrom;
    private Integer aindex;

    /**
     * No args constructor for use in serialization
     *
     */
    public CategoryPath() {}

    @JsonCreator
    public CategoryPath(@JsonProperty("categoryId") int categoryId,
                        @JsonProperty("categories") List<String> categories) {
        this.categoryId = categoryId;
        this.categories = categories;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public List<String> getCategories() {
        return categories;
    }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public Timestamp getValidTo() { return validTo; }
    public void setValidTo(Timestamp validTo) { this.validTo = validTo; }

    public Timestamp getValidFrom() { return validFrom; }
    public void setValidFrom(Timestamp validFrom) { this.validFrom = validFrom; }

    public Integer getAindex() { return aindex; }
    public void setAindex(Integer aindex) { this.aindex = aindex; }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
