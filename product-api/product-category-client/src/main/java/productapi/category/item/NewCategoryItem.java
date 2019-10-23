package productapi.category.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import productapi.category.mappings.model.CategoryPath;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewCategoryItem {

    @NotEmpty(message = "{codeName.notEmpty}")
    public final String name;

    @NotEmpty(message = "{codeName.notEmpty}")
    public final String codeName;

    @NotNull(message = "{weight.notNull}")
    public final Integer weight;

    @NotNull(message = "{type.notNull}")
    public final CategoryType type;

    public final Timestamp validFrom;

    public final Timestamp validTo;

    @NotNull(message = "{pathItem.notNull}")
    public final String pathItem;

    public final List<CategoryPath> categoryParentMappings;

    @JsonCreator
    public NewCategoryItem(@JsonProperty("name") String name,
                        @JsonProperty("codeName") String codeName,
                        @JsonProperty("weight") Integer weight,
                        @JsonProperty("type") CategoryType type,
                        @JsonProperty("validFrom") Timestamp validFrom,
                        @JsonProperty("validTo") Timestamp validTo,
                        @JsonProperty("pathItem") String pathItem,
                        @JsonProperty("categoryParentMappings") List<CategoryPath> categoryParentMappings) {
        this.name = name;
        this.codeName = codeName;
        this.weight = weight;
        this.type = type;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.pathItem = pathItem;
        this.categoryParentMappings = categoryParentMappings;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
