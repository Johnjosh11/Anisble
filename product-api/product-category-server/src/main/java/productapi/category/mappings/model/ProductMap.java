package productapi.category.mappings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import java.util.List;

public class ProductMap {

    @JsonProperty("id")
    public final Integer productId;

    @JsonProperty("category")
    public final List<Integer> categoryList;

    public ProductMap(Integer productId, List<Integer> categoryList) {
        this.productId = productId;
        this.categoryList = categoryList;
    }
}
