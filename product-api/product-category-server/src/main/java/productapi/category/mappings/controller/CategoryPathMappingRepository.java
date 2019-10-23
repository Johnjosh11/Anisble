package productapi.category.mappings.controller;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import productapi.category.hierarchy.controller.CategoryHierarchyBuilder;
import productapi.category.hierarchy.controller.IncludeTimeRange;
import productapi.category.hierarchy.model.CategoryNode;
import productapi.category.hierarchy.model.NameTranslation;
import productapi.category.mappings.model.CategoryPath;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryPathMappingRepository {
    @Autowired
    DSLContext dbContext;
    @Autowired
    private CategoryHierarchyBuilder productCategoryHierarchyBuilder;

    private static final String DELIMITER = ">";

    public List<String> getAllProductCategoryPaths(Integer categoryId, String lang) {
        CategoryNode rootCategoryNode = productCategoryHierarchyBuilder.buildHierarchyTreeForCategoryId(categoryId, IncludeTimeRange.ACTIVE_AND_PAST_30_DAYS);
        return getTranslatedCategoryPaths(buildCategoryPaths(rootCategoryNode), lang);
    }

    public List<CategoryPath> getCategoryParentMappings(Integer categoryId, String lang) {
        CategoryNode rootCategoryNode = productCategoryHierarchyBuilder.buildHierarchyTreeForCategoryId(categoryId, IncludeTimeRange.ALL);
        List<List<CategoryNode>> parentCategories = buildCategoryPaths(rootCategoryNode);
        List<CategoryPath> parentCategoryMappings = new ArrayList<>();

        for(List<CategoryNode> parentCategoryList : parentCategories) {
            if (parentCategoryList.size() > 1) {
                parentCategoryList.remove(0); // first item is category itself
                CategoryNode parentNode = parentCategoryList.get(0);
                CategoryPath parentCategoryMapping = getAlreadyAddedCategoryMapping(parentCategoryMappings, parentNode);
                String path = getTranslatedCategoryPath(parentCategoryList, lang);

                if (parentCategoryMapping == null) {
                    parentCategoryMapping = createCategoryPathFromCategoryNode(parentNode, path);
                    parentCategoryMappings.add(parentCategoryMapping);
                } else {
                    parentCategoryMapping.getCategories().add(path);
                }
            }
        }
        return parentCategoryMappings;
    }

    private CategoryPath getAlreadyAddedCategoryMapping(List<CategoryPath> parentCategoryMappings, CategoryNode parentNode) {
        return parentCategoryMappings.stream()
            .filter(existingCategory -> existingCategory.getAindex().equals(parentNode.getAindex()))
            .findAny()
            .orElse(null);
    }

    private CategoryPath createCategoryPathFromCategoryNode(CategoryNode parentNode, String path) {
        CategoryPath parentCategory = new CategoryPath();
        parentCategory.setCategoryId(parentNode.getProductCategoryId());
        parentCategory.setValidFrom(parentNode.getCategoryValidFrom());
        parentCategory.setValidTo(parentNode.getCategoryValidTo());
        parentCategory.setAindex(parentNode.getAindex());

        List<String> parentPaths = new ArrayList<>();
        parentPaths.add(path);
        parentCategory.setCategories(parentPaths);
        return parentCategory;
    }

    private String getTranslatedCategoryPath(List<CategoryNode> categoryPath, String lang) {
        Collections.reverse(categoryPath);
        return categoryPath.stream()
                .map(categoryNode -> getTranslatedCategoryName(categoryNode.getNameTranslations(), lang))
                .collect(Collectors.joining(DELIMITER));
    }

    private List<String> getTranslatedCategoryPaths(List<List<CategoryNode>> categoryNodePaths, String lang) {
        return categoryNodePaths.stream()
            .map(categoryPath -> getTranslatedCategoryPath(categoryPath, lang))
            .collect(Collectors.toList());
    }

    private String getTranslatedCategoryName(List<NameTranslation> translations, String lang) {
        String productCategoryName = translations.get(0).getDisplayName();
        for(NameTranslation translation: translations) {
            if (lang.equalsIgnoreCase(translation.getLanguage())) {
                productCategoryName = translation.getDisplayName();
                break;
            }
        }
        return productCategoryName;
    }

    private List<List<CategoryNode>> buildCategoryPaths(CategoryNode pos) {
        List<List<CategoryNode>> retLists = new ArrayList<>();

        if (CollectionUtils.isEmpty(pos.getUnsortedChildren())) {
            List<CategoryNode> leafList = new LinkedList<>();
            leafList.add(pos);
            retLists.add(leafList);
        } else {
            pos.getUnsortedChildren().stream().forEach(node -> buildCategoryPaths(node).stream().forEach(nodeList -> {
                nodeList.add(0, pos);
                retLists.add(nodeList);
            }));
        }
        return retLists;
    }
}
