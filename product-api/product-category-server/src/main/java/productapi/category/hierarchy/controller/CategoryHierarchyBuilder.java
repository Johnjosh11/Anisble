package productapi.category.hierarchy.controller;

import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_NAME_TRANSLATION;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PATH_ITEM;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PARENT_MAP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.util.CollectionUtils;
import pcapi.jooq.common.db.tables.pojos.ProductCategoryPathItem;
import pcapi.jooq.common.db.tables.pojos.ProductCategoryNameTranslation;
import pcapi.jooq.common.db.tables.records.ProductCategoryParentMapRecord;
import productapi.category.hierarchy.model.CategoryNode;
import productapi.category.hierarchy.model.CategoryWeightComparator;
import productapi.category.hierarchy.model.NameTranslation;

@Component
public class CategoryHierarchyBuilder {
    
    @Autowired
    CategoryAndParentMapFlatListFetcher categoryFlatListFetcher;

    public List<CategoryNode> buildHierarchyTreeForWebShop() {
        List<CategoryNode> rootNodes  = new ArrayList<>();
        Map<Integer,CategoryNode> handeledNodes = new HashMap<>();
        Result<Record> categories = categoryFlatListFetcher.fetchAllOpenProductCategories();
        Map<Integer, List<ProductCategoryParentMapRecord>> categoryParentMaps = categoryFlatListFetcher.fetchAllOpenProductCategoriesParentMaps().intoGroups(PRODUCT_CATEGORY_PARENT_MAP.PRODUCT_CATEGORY_ID, ProductCategoryParentMapRecord.class);
        return buildHierarchy(rootNodes, handeledNodes, categories, categoryParentMaps);
    }

    public List<CategoryNode> buildHierarchyTreeForAllCategories() {
        List<CategoryNode> rootNodes  = new ArrayList<>();
        Map<Integer,CategoryNode> handeledNodes = new HashMap<>();
        Result<Record> categories = categoryFlatListFetcher.fetchAllProductCategories();
        Map<Integer, List<ProductCategoryParentMapRecord>> categoryParentMaps = categoryFlatListFetcher.fetchAllProductCategoriesParentMaps().intoGroups(PRODUCT_CATEGORY_PARENT_MAP.PRODUCT_CATEGORY_ID, ProductCategoryParentMapRecord.class);
        return buildHierarchy(rootNodes, handeledNodes, categories, categoryParentMaps);
    }

    public CategoryNode buildHierarchyTreeForCategoryId(Integer categoryId, IncludeTimeRange timeRange) {
        Map<Integer, List<ProductCategoryParentMapRecord>> parentCategoryMap;

        if (IncludeTimeRange.ALL.equals(timeRange)) {
            parentCategoryMap = categoryFlatListFetcher.fetchAllProductCategoryParentMapsIntoCategoryIdGroups();
        } else {
            parentCategoryMap = categoryFlatListFetcher.fetchAllValidProductCategoryParentMaps();
        }

        Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations = categoryFlatListFetcher.fetchAllProductCategoryNames().intoGroups(PRODUCT_CATEGORY_NAME_TRANSLATION.PRODUCT_CATEGORY_ID, ProductCategoryNameTranslation.class);
        CategoryNode categoryNode = new CategoryNode();
        categoryNode.setProductCategoryId(categoryId);
        addNameTranslations(categoryNode, categoryNameTranslations);
        addParentCategoriesForCategoryId(categoryNode, parentCategoryMap, categoryNameTranslations);
        return categoryNode;
    }

    private List<CategoryNode> buildHierarchy(List<CategoryNode> rootNodes, Map<Integer, CategoryNode> handeledNodes,
            Result<Record> categories, Map<Integer, List<ProductCategoryParentMapRecord>> categoryParentMaps) {
        Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations = categoryFlatListFetcher.fetchAllProductCategoryNames().intoGroups(PRODUCT_CATEGORY_NAME_TRANSLATION.PRODUCT_CATEGORY_ID, ProductCategoryNameTranslation.class);
        Map<Integer, List<ProductCategoryPathItem>> categoryPathItems = categoryFlatListFetcher.fetchAllPathItems().intoGroups(PRODUCT_CATEGORY_PATH_ITEM.PRODUCT_CATEGORY_ID, ProductCategoryPathItem.class);
        buildHierarchy(categories, categoryParentMaps, handeledNodes, rootNodes, categoryNameTranslations, categoryPathItems);
        rootNodes.sort(new CategoryWeightComparator());
        return rootNodes;
    }

    private void buildHierarchy(Result<Record> categories,
            Map<Integer, List<ProductCategoryParentMapRecord>> categoryParentMaps, Map<Integer, 
            CategoryNode> handeledNodes, List<CategoryNode> rootNodes, 
            Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations, 
            Map<Integer, List<ProductCategoryPathItem>> categoryPathItems) {
        for (Record record : categories) {
            CategoryNode node = createNewNodeOrFillDetails(handeledNodes, record, categoryNameTranslations,
                categoryPathItems);
            addNodeToTree(categoryParentMaps, handeledNodes, node, rootNodes);
            markNodeAsHandled(handeledNodes, node);
        }
    }

    private void addNodeToTree(Map<Integer, List<ProductCategoryParentMapRecord>> categoryParentMaps,
            Map<Integer, CategoryNode> handeledNodes, CategoryNode node, List<CategoryNode> rootNodes) {
        if(categoryParentMaps.containsKey(node.getProductCategoryId())) {
            handleRelationships(categoryParentMaps, handeledNodes, node);
        } else {
            addToRootNodelist(node, rootNodes);
        }
    }

    private void handleRelationships(Map<Integer, List<ProductCategoryParentMapRecord>> categoryParentMaps,
            Map<Integer, CategoryNode> handeledNodes, CategoryNode node) {
        List<ProductCategoryParentMapRecord> parentRelationShips = categoryParentMaps.get(node.getProductCategoryId());
        for (ProductCategoryParentMapRecord relationShip : parentRelationShips) {
            addToParentOrCreateTemporaryParent(handeledNodes, node, relationShip);
        }
    }
    
    private void markNodeAsHandled(Map<Integer, CategoryNode> handeledNodes, CategoryNode node) {
        handeledNodes.put(node.getProductCategoryId(), node);
    }

    private void addToParentOrCreateTemporaryParent(Map<Integer, CategoryNode> nodeMap, CategoryNode node, ProductCategoryParentMapRecord relationShip) {
        Integer parentId = relationShip.getParentId();
        if(nodeMap.containsKey(parentId)) {
            addToParent(nodeMap, node, parentId);
        } else {
            createTemplateParentAndAddAsChild(nodeMap, node, parentId);
        }
    }

    private void createTemplateParentAndAddAsChild(Map<Integer, CategoryNode> nodeMap, CategoryNode node, Integer parentId) {
        CategoryNode temporaryNode = new CategoryNode();
        temporaryNode.setTemporary(true);
        temporaryNode.setProductCategoryId(parentId);
        temporaryNode.addChild(node);
        nodeMap.put(parentId, temporaryNode);
    }

    private void addToParent(Map<Integer, CategoryNode> nodeMap, CategoryNode node, Integer parentId) {
        CategoryNode parent = nodeMap.get(parentId);
        parent.addChild(node);
    }

    private void addToRootNodelist(CategoryNode node, List<CategoryNode> rootNodes) {
        rootNodes.add(node);
    }

    private CategoryNode createNewNodeOrFillDetails(Map<Integer, CategoryNode> handeledNodes, 
            Record record, Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations, 
            Map<Integer, List<ProductCategoryPathItem>> categoryPathItems) {
        CategoryNode node = null;
        node = findExistingNodeOrCreateNewOne(handeledNodes, record, categoryNameTranslations, categoryPathItems);
        ifTemporaryNodeFillInDetails(record, node, categoryNameTranslations, categoryPathItems);
        return node;
    }

    private void ifTemporaryNodeFillInDetails(Record record, CategoryNode node, 
            Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations, 
            Map<Integer, List<ProductCategoryPathItem>> categoryPathItems) {
        if(node.isTemporary()) {
            buildCategoryNode(record, node, categoryNameTranslations, categoryPathItems);
        }
    }

    private void buildCategoryNode(Record record, CategoryNode node, Map<Integer, 
            List<ProductCategoryNameTranslation>> categoryNameTranslation, 
            Map<Integer, List<ProductCategoryPathItem>> categoryPathItems) {
        addNameTranslations(record, node, categoryNameTranslation);
        addPathItems(record, node, categoryPathItems);
        node.setWeigth(record.getValue(PRODUCT_CATEGORY.WEIGHT));
        node.setType(record.getValue(PRODUCT_CATEGORY.TYPE));
        if(record.getValue(PRODUCT_CATEGORY.VALIDFROM) != null) {
            node.setCategoryValidFrom(record.getValue(PRODUCT_CATEGORY.VALIDFROM));
        }
        if(record.getValue(PRODUCT_CATEGORY.VALIDTO) != null) {
            node.setCategoryValidTo(record.getValue(PRODUCT_CATEGORY.VALIDTO));
        }
        node.setContentType(record.getValue(PRODUCT_CATEGORY.CONTENT_TYPE));
        node.setCodeName(record.getValue(PRODUCT_CATEGORY.CODE_NAME));
        node.setAindex(record.getValue(PRODUCT_CATEGORY.AINDEX));
        node.setMetaDescription(record.getValue(PRODUCT_CATEGORY.META_DESCRIPTION));
        node.setIconName(record.getValue(PRODUCT_CATEGORY.ICON_NAME));
    }

    private void addPathItems(Record record, CategoryNode node,
            Map<Integer, List<ProductCategoryPathItem>> categoryPathItems) {
        List<ProductCategoryPathItem> pathItems = categoryPathItems.get(record.getValue(PRODUCT_CATEGORY.AINDEX));
        Integer currentPathItemId = record.getValue(PRODUCT_CATEGORY.PATH_ITEM_ID);
        if(pathItems == null) {
            return;
        }
        for (ProductCategoryPathItem pathItem : pathItems) {
            if(pathItem.getAindex().equals(currentPathItemId)) {
                node.setPathItem(pathItem.getName());
            } else {
                node.addPathItemHistory(pathItem.getName());
            }
        }
    }

    private void addNameTranslations(Record record, CategoryNode node,
            Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslation) {
        List<ProductCategoryNameTranslation> translations = categoryNameTranslation.get(record.getValue(PRODUCT_CATEGORY.AINDEX));
        for (ProductCategoryNameTranslation nameTranslation : translations) {
            NameTranslation name = new NameTranslation(nameTranslation.getName(), nameTranslation.getLanguage());
            node.addNameTranslation(name);
        }
    }

    private void addNameTranslations(CategoryNode node,
                                     Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslation) {
        List<ProductCategoryNameTranslation> translations = categoryNameTranslation.get(node.getProductCategoryId());
        for (ProductCategoryNameTranslation nameTranslation : translations) {
            NameTranslation name = new NameTranslation(nameTranslation.getName(), nameTranslation.getLanguage());
            node.addNameTranslation(name);
        }
    }

    private CategoryNode findExistingNodeOrCreateNewOne(Map<Integer, CategoryNode> handeledNodes, 
            Record record, Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations, 
            Map<Integer, List<ProductCategoryPathItem>> categoryPathItems) {
        CategoryNode node;
        Integer productCategoryAindex = record.getValue(PRODUCT_CATEGORY.AINDEX);
        if(handeledNodes.containsKey(productCategoryAindex)) {
            node = handeledNodes.get(productCategoryAindex);
        } else {
            node = new CategoryNode();
            node.setProductCategoryId(productCategoryAindex);
            buildCategoryNode(record, node, categoryNameTranslations, categoryPathItems);
        }
        return node;
    }

    private void addParentCategoriesForCategoryId(CategoryNode categoryNode,
                                                  Map<Integer, List<ProductCategoryParentMapRecord>> parentCategoryMap,
                                                  Map<Integer, List<ProductCategoryNameTranslation>> categoryNameTranslations) {
        List<ProductCategoryParentMapRecord> parentCategoryMappings = getParentCategoriesForCategoryId(categoryNode.getProductCategoryId(), parentCategoryMap);
        if (!CollectionUtils.isEmpty(parentCategoryMappings)) {
            parentCategoryMappings.stream().forEach(parentCategoryMapping -> {
                CategoryNode childNode = createNodeFromParentMapping(parentCategoryMapping);
                addNameTranslations(childNode, categoryNameTranslations);
                categoryNode.addChild(childNode);
                addParentCategoriesForCategoryId(childNode, parentCategoryMap, categoryNameTranslations);
            });
        }
    }

    private CategoryNode createNodeFromParentMapping(ProductCategoryParentMapRecord parentCategoryMapping) {
        CategoryNode childNode = new CategoryNode();
        childNode.setAindex(parentCategoryMapping.getAindex());
        childNode.setProductCategoryId(parentCategoryMapping.getParentId());
        childNode.setCategoryValidFrom(parentCategoryMapping.getValidfrom());

        if (parentCategoryMapping.getValidto() != null) {
            childNode.setCategoryValidTo(parentCategoryMapping.getValidto());
        }

        return childNode;
    }

    private List<ProductCategoryParentMapRecord> getParentCategoriesForCategoryId(Integer categoryId, Map<Integer, List<ProductCategoryParentMapRecord>> parentCategoryMap) {
        return parentCategoryMap.getOrDefault(categoryId, null);
    }
}
