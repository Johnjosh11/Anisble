package productapi.category.hierarchy.model;

import java.util.Comparator;

public class CategoryWeightComparator implements Comparator<CategoryNode> {

    public int compare(CategoryNode a, CategoryNode b) {
        return b.getWeigth() - a.getWeigth();
    }

}
