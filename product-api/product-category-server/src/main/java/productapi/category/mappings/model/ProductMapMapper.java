package productapi.category.mappings.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductMapMapper {

    public static List<ProductMap> populateMappingList(Map<Integer, List<Integer>> categoryMap) {
        return categoryMap.entrySet()
                .stream()
                .map(entry -> new ProductMap(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
