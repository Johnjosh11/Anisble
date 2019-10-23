package productapi.category.item;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import productapi.category.mappings.model.CategoryPath;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryItem extends NewCategoryItem {

    @NotNull(message = "{categoryId.notNull}")
    public final Integer categoryId;

    @JsonCreator
    public CategoryItem(@JsonProperty("categoryId") Integer categoryId,
                        @JsonProperty("name") String name,
                        @JsonProperty("codeName") String codeName,
                        @JsonProperty("weight") Integer weight,
                        @JsonProperty("type") CategoryType type,
                        @JsonProperty("validFrom") Timestamp validFrom,
                        @JsonProperty("validTo") Timestamp validTo,
                        @JsonProperty("pathItem") String pathItem,
                        @JsonProperty("categoryParentMappings") List<CategoryPath> categoryParentMappings) {
        super(name, codeName, weight, type, validFrom, validTo, pathItem, categoryParentMappings);
        this.categoryId = categoryId;
    }
}
