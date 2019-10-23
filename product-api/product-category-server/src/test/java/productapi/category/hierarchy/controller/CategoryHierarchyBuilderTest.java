package productapi.category.hierarchy.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_NAME_TRANSLATION;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PARENT_MAP;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PATH_ITEM;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pcapi.jooq.common.db.tables.records.ProductCategoryNameTranslationRecord;
import pcapi.jooq.common.db.tables.records.ProductCategoryParentMapRecord;
import pcapi.jooq.common.db.tables.records.ProductCategoryPathItemRecord;
import pcapi.jooq.common.db.tables.records.ProductCategoryRecord;
import productapi.category.hierarchy.model.CategoryNode;
import productapi.category.hierarchy.model.NameTranslation;

@RunWith(MockitoJUnitRunner.class)
public class CategoryHierarchyBuilderTest {
    
    private static final NameTranslation SUBB_NAME_FI = new NameTranslation("subB", "fi");
    private static final NameTranslation SUBB_NAME_SV = new NameTranslation("sobB", "sv");
    private static final NameTranslation ROOTB_NAME_FI = new NameTranslation("rootB", "fi");
    private static final NameTranslation ROOTA_NAME_FI = new NameTranslation("rootA", "fi");
    private static final NameTranslation SUBA_NAME_FI = new NameTranslation("subA", "fi");
    private static final NameTranslation SUBC_NAME_FI = new NameTranslation("subC", "fi");
    private static final NameTranslation SUBD_NAME_FI = new NameTranslation("subD", "fi");
    private static final NameTranslation SUBE_NAME_FI = new NameTranslation("subE", "fi");
    private static final Integer ONE_CHILD = 1;
    private static final Integer TWO_CHILDREN = 2;
    private static final Integer BIGGER_WEIGTH = 10;
    private static final Integer SMALLER_WEIGTH = 5;
    private static final Integer ROOTA_AINDEX = 1;
    private static final Integer SUBA_AINDEX = 2;
    private static final Integer SUBB_AINDEX = 3;
    private static final Integer SUBC_AINDEX = 4;
    private static final Integer SUBD_AINDEX = 5;
    private static final Integer SUBE_AINDEX = 6;
    private static final Integer ROOTB_AINDEX = 30;
    private static final Timestamp VALID_FROM = new Timestamp(2000L);
    private static final Timestamp VALID_TO = new Timestamp(2000L);
    private static final Timestamp VALID_TO_MISSING = null;
    private static final Timestamp VALID_FROM_MISSING = null;
    private static final String ROOTA_PATH_ITEM = "roota";
    private static final String NO_PATH_ITEM = null;
    private static final String SUBA_PATH_ITEM_NULL = null;
    private static final String SUBA_PATH_ITEM = "suba";
    private static final String SUBB_PATH_ITEM = "subb";
    private static final String[] NO_HISTORY = null;
    private static final String[] SUBB_PATH_ITEM_HISTORY = {"subbOlder", "subbOldest"};
    private static final boolean WEBSHOP = true;
    private static final boolean MANAGEMENT = false;
    private static final String PATH_IS_EMPTY = "";

    @Mock private CategoryAndParentMapFlatListFetcher categoryFlatListFetcher;
    
    @InjectMocks
    private CategoryHierarchyBuilder hierarchyBuilder;
    
    private List<ProductCategoryRecord> productCategoryRecords;
    private List<ProductCategoryParentMapRecord> productCategoryParentMapRecords;
    private List<ProductCategoryNameTranslationRecord> productCategoryNameTranslations;
    private List<ProductCategoryPathItemRecord> productCategoryPathItems;

    @Before
    public void setUp() throws Exception {
        productCategoryRecords = new ArrayList<ProductCategoryRecord>();
        productCategoryParentMapRecords = new ArrayList<ProductCategoryParentMapRecord>();
        productCategoryNameTranslations = new ArrayList<ProductCategoryNameTranslationRecord>();
        productCategoryPathItems = new ArrayList<ProductCategoryPathItemRecord>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testRootWithOneSubCategoryWebShop() throws SQLException {
        NameTranslation[] rootNames = specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        NameTranslation[] subNames = specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBA_NAME_FI);
        specifyProductCategories(WEBSHOP);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX);
        specifyProductCategoriParentMaps(WEBSHOP);
        List<CategoryNode> rootNodes = hierarchyBuilder.buildHierarchyTreeForWebShop();
        assertThat(rootNodes.size(), equalTo(ONE_CHILD));
        CategoryNode child = verifyNodeHasOneChild(rootNodes.get(0), ROOTA_AINDEX, rootNames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(child, SUBA_AINDEX, subNames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
    }

    @Test
    public void testMultipParentSubCategoryManagement() throws SQLException {
        NameTranslation[] rootNames = specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO_MISSING, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        NameTranslation[] subANames = specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBA_NAME_FI);
        NameTranslation[] subBNames = specifyProductCategory(SUBB_AINDEX, BIGGER_WEIGTH, VALID_FROM_MISSING, VALID_TO_MISSING, SUBB_PATH_ITEM, SUBB_PATH_ITEM_HISTORY, SUBB_NAME_FI, SUBB_NAME_SV);
        specifyProductCategories(MANAGEMENT);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX);
        specifyProductCategoryParentMap(SUBB_AINDEX, ROOTA_AINDEX);
        specifyProductCategoryParentMap(SUBB_AINDEX, SUBA_AINDEX);
        specifyProductCategoriParentMaps(MANAGEMENT);
        List<CategoryNode> rootNodes = hierarchyBuilder.buildHierarchyTreeForAllCategories();
        assertThat(rootNodes.size(), equalTo(ONE_CHILD));
        CategoryNode rootNode = rootNodes.get(0);
        List<CategoryNode> children = verifyNodeAndGetChildren(rootNode, ROOTA_AINDEX, TWO_CHILDREN, rootNames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(children.get(0), SUBB_AINDEX, subBNames, createPath(ROOTA_PATH_ITEM, SUBB_PATH_ITEM), SUBB_PATH_ITEM_HISTORY);
        children = verifyNodeAndGetChildren(children.get(1), SUBA_AINDEX, ONE_CHILD, subANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(children.get(0), SUBB_AINDEX, subBNames, createPath(ROOTA_PATH_ITEM,SUBB_PATH_ITEM), SUBB_PATH_ITEM_HISTORY);
    }
    
    @Test
    public void testMultipParentSubCategoryInReverseOrderWebShop() throws SQLException {
        NameTranslation[] subBNames = specifyProductCategory(SUBB_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBB_PATH_ITEM, NO_HISTORY, SUBB_NAME_FI, SUBB_NAME_SV);
        NameTranslation[] subANames = specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM, NO_HISTORY, SUBA_NAME_FI);
        NameTranslation[] rootNames = specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO_MISSING, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        specifyProductCategories(WEBSHOP);
        specifyProductCategoryParentMap(SUBB_AINDEX, SUBA_AINDEX);
        specifyProductCategoryParentMap(SUBB_AINDEX, ROOTA_AINDEX);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX);
        specifyProductCategoriParentMaps(WEBSHOP);
        List<CategoryNode> rootNodes = hierarchyBuilder.buildHierarchyTreeForWebShop();
        assertThat(rootNodes.size(), equalTo(ONE_CHILD));
        CategoryNode rootNode = rootNodes.get(0);
        List<CategoryNode> children = verifyNodeAndGetChildren(rootNode, ROOTA_AINDEX, TWO_CHILDREN, rootNames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(children.get(0), SUBB_AINDEX, subBNames, createPath(ROOTA_PATH_ITEM, SUBB_PATH_ITEM) , NO_HISTORY);
        children = verifyNodeAndGetChildren(children.get(1), SUBA_AINDEX, ONE_CHILD, subANames, createPath(ROOTA_PATH_ITEM, SUBA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(children.get(0), SUBB_AINDEX, subBNames, createPath(ROOTA_PATH_ITEM, SUBA_PATH_ITEM, SUBB_PATH_ITEM), NO_HISTORY);
    }
    
    @Test
    public void testTwoRootsWithSameSubCategoryWithReverseOrderManagement() throws SQLException {
        NameTranslation[] subANames = specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBA_NAME_FI);
        NameTranslation[] rootANames = specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        NameTranslation[] rootBNames = specifyProductCategory(ROOTB_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, NO_PATH_ITEM, NO_HISTORY, ROOTB_NAME_FI);
        specifyProductCategories(MANAGEMENT);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTB_AINDEX);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX);
        specifyProductCategoriParentMaps(MANAGEMENT);
        List<CategoryNode> rootNodes = hierarchyBuilder.buildHierarchyTreeForAllCategories();
        assertThat(rootNodes.size(), equalTo(TWO_CHILDREN));
        CategoryNode child = verifyNodeHasOneChild(rootNodes.get(0), ROOTA_AINDEX, rootANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(child, SUBA_AINDEX, subANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        child = verifyNodeHasOneChild(rootNodes.get(1), ROOTB_AINDEX, rootBNames, PATH_IS_EMPTY, NO_HISTORY);
        verifyNodeAndNoChildren(child, SUBA_AINDEX, subANames, PATH_IS_EMPTY, NO_HISTORY);
    }
    
    @Test
    public void testOpenNodeButParentMapMissingWebShop() throws SQLException {
        NameTranslation[] rootANames = specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO_MISSING, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        NameTranslation[] subANames = specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBA_NAME_FI);
        specifyProductCategory(SUBB_AINDEX, BIGGER_WEIGTH, VALID_FROM_MISSING, VALID_TO_MISSING, SUBB_PATH_ITEM, SUBB_PATH_ITEM_HISTORY, SUBB_NAME_FI, SUBB_NAME_SV);
        specifyProductCategories(WEBSHOP);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX);
        specifyProductCategoriParentMaps(WEBSHOP);
        List<CategoryNode> rootNodes = hierarchyBuilder.buildHierarchyTreeForWebShop();
        assertThat(rootNodes.size(), equalTo(TWO_CHILDREN));
        CategoryNode rootNode = rootNodes.get(0);
        CategoryNode child = verifyNodeHasOneChild(rootNode, ROOTA_AINDEX, rootANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(child, SUBA_AINDEX, subANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
    }
    
    @Test
    public void testOpenNodeButClosedParentMapManagement() throws SQLException {
        NameTranslation[] rootANames = specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO_MISSING, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        NameTranslation[] subANames = specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBA_NAME_FI);
        specifyProductCategories(MANAGEMENT);
        specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX);
        specifyProductCategoryParentMap(SUBB_AINDEX, ROOTA_AINDEX);
        specifyProductCategoriParentMaps(MANAGEMENT);
        List<CategoryNode> rootNodes = hierarchyBuilder.buildHierarchyTreeForAllCategories();
        assertThat(rootNodes.size(), equalTo(ONE_CHILD));
        CategoryNode rootNode = rootNodes.get(0);
        CategoryNode child = verifyNodeHasOneChild(rootNode, ROOTA_AINDEX, rootANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
        verifyNodeAndNoChildren(child, SUBA_AINDEX, subANames, createPath(ROOTA_PATH_ITEM), NO_HISTORY);
    }

    @Test
    public void testCategoryPathsHierarchyTreeForCategoryId() throws SQLException {
        specifyProductCategoryAndParentMapForAllPaths();
        specifyProductCategories(WEBSHOP);
        CategoryNode rootNode = hierarchyBuilder.buildHierarchyTreeForCategoryId(SUBC_AINDEX, IncludeTimeRange.ACTIVE_AND_PAST_30_DAYS);
        List<String> categoryIds = getListOfCategoryIdsFromCategoryNodeList(rootNode.getUnsortedChildren());
        List<CategoryNode> children = rootNode.getUnsortedChildren();
        assertThat(rootNode.getProductCategoryId(), equalTo(SUBC_AINDEX));
        assertThat("Root node has two child nodes ", children.size(), equalTo(TWO_CHILDREN));
        assertTrue(categoryIds.contains(SUBB_AINDEX.toString()));
        assertTrue(categoryIds.contains(SUBE_AINDEX.toString()));

        assertParentMappingDetails(children.get(0));
    }

    private void assertParentMappingDetails(CategoryNode parentMapping) {
        assertThat(parentMapping.getCategoryValidFrom(), equalTo(VALID_FROM));
        assertThat(parentMapping.getCategoryValidFrom(), equalTo(VALID_TO));
    }

    private List<String> getListOfCategoryIdsFromCategoryNodeList (List<CategoryNode> categories) {
        return categories.stream().map(categoryNode -> categoryNode.getProductCategoryId().toString()).collect(Collectors.toList());
    }
    private String createPath(String ... pathItems) {
        return String.join(CategoryNode.FORWARD_SLASH, pathItems);
    }

    private CategoryNode verifyNodeHasOneChild(CategoryNode node, Integer nodeAindex, 
            NameTranslation[] expectedNameTranslations, String rootaPathItem, String[] noHistory) {
        List<CategoryNode> children = verifyNodeAndGetChildren(node, nodeAindex, ONE_CHILD, 
            expectedNameTranslations, rootaPathItem, noHistory);
        return children.get(0);
    }
    
    private List<CategoryNode> verifyNodeAndGetChildren(CategoryNode node, Integer productCategoryId, 
            Integer childrenSize, NameTranslation[] expectedNameTranslations, 
            String expectedPathItem, String[] expectedPathItemHistory) {
        verifyNode(node, productCategoryId, expectedNameTranslations, expectedPathItem, expectedPathItemHistory);
        List<CategoryNode> children = node.getChildren();
        assertThat("Children amount ", children.size(), equalTo(childrenSize));
        return children;
    }
    
    private void verifyPathItemHistroy(CategoryNode node, String[] expectedPathItemHistory) {
        List<String> history = node.getPathItemHistory();
        boolean allOk = false;
        if(expectedPathItemHistory == null || history == null) {
            return;
        }
        for (String pathItem : history) {
            allOk = isMatch(pathItem, expectedPathItemHistory);
        }
        assertThat("Path item history ", allOk, equalTo(true));
    }

    private boolean isMatch(String pathItem, String[] expectedPathItemHistory) {
        for (String expectedPathItem : expectedPathItemHistory) {
            if(pathItem.equals(expectedPathItem)) {
                return true;
            }
        }
        return false;
    }

    private void verifyNameTranslations(CategoryNode node, NameTranslation[] expectedNameTranslations) {
        boolean allOk = true;
        List<NameTranslation> nameTranslations = node.getNameTranslations();
        assertThat("Translation amount ", nameTranslations.size(), equalTo(expectedNameTranslations.length));
        for (NameTranslation nameTranslation : nameTranslations) {
            allOk = findMatch(nameTranslation, expectedNameTranslations);
        }
        assertThat("All translations found ", allOk, equalTo(true));
    }

    private boolean findMatch(NameTranslation nameTranslation, NameTranslation[] expectedNameTranslations) {
        boolean found = false;
        for (NameTranslation expectedTranslation : expectedNameTranslations) {
            if(expectedTranslation.getDisplayName().equals(nameTranslation.getDisplayName()) &&
                    expectedTranslation.getLanguage().equals(nameTranslation.getLanguage())) {
                found = true;
                break;
            }
        }
        return found;
    }

    private void verifyNodeAndNoChildren(CategoryNode node, Integer aindex,
            NameTranslation[] expectedNameTranslations, String expectedPath,
            String[] expectedPathItemHistory) {
        verifyNode(node, aindex, expectedNameTranslations, expectedPath, expectedPathItemHistory);
        assertNull("There should not be any children ",node.getChildren());
    }

    private void verifyNode(CategoryNode node, Integer productCategoryId, NameTranslation[] expectedNameTranslations,
            String expectedPath, String[] expectedPathItemHistory) {
        assertThat("ProductCategoryId for child ", node.getProductCategoryId(), equalTo(productCategoryId));
        assertThat("Path item ", node.getPath(), equalTo(expectedPath));
        verifyPathItemHistroy(node, expectedPathItemHistory);
        verifyNameTranslations(node, expectedNameTranslations);
    }

    private NameTranslation[] specifyProductCategory(Integer productCategoryAindex, Integer weigth,
            Timestamp validFrom, Timestamp validTo, String pathItem, 
            String[] pathItemHistory, NameTranslation... nameTranslations) {
        ProductCategoryRecord category = new ProductCategoryRecord();
        category.setWeight(weigth);
        category.setAindex(productCategoryAindex);
        category.setValidfrom(validFrom);
        category.setValidto(validTo);
        this.productCategoryRecords.add(category);
        specifyNameTranslations(productCategoryAindex, nameTranslations);
        int pathItemAindexBase = productCategoryAindex+10;
        if(pathItem != null) {
            category.setPathItemId(pathItemAindexBase);
            specifyPathItem(productCategoryAindex, pathItem, pathItemAindexBase++);
        }
        specifyPathItemHistory(productCategoryAindex, pathItem, pathItemHistory, pathItemAindexBase);
        return nameTranslations;
    }

    private void specifyPathItemHistory(Integer productCategoryAindex, String pathItem, String[] pathItemHistory, Integer pathItemAindexBase) {
        if(pathItemHistory == null) {
            return;
        }
        for (String historyPathItem : pathItemHistory) {
            specifyPathItem(productCategoryAindex, historyPathItem, pathItemAindexBase++);
        }
    }

    private void specifyPathItem(Integer productCategoryAindex, String pathItem, Integer aindex) {
        ProductCategoryPathItemRecord item = new ProductCategoryPathItemRecord();
        item.setProductCategoryId(productCategoryAindex);
        item.setName(pathItem);
        item.setAindex(aindex);
        this.productCategoryPathItems.add(item);
    }

    private void specifyNameTranslations(Integer productCategoryAindex, NameTranslation[] nameTranslations) {
        for (NameTranslation nameTranslation : nameTranslations) {
            ProductCategoryNameTranslationRecord translation = new ProductCategoryNameTranslationRecord();
            translation.setProductCategoryId(productCategoryAindex);
            translation.setName(nameTranslation.getDisplayName());
            translation.setLanguage(nameTranslation.getLanguage());
            this.productCategoryNameTranslations.add(translation);
        }
    }

    private void specifyProductCategories(boolean webShop) {
        DSLContext create = DSL.using(SQLDialect.MYSQL);
        Result<ProductCategoryRecord> categories = create.newResult(PRODUCT_CATEGORY);
        categories.addAll(this.productCategoryRecords);
        if(webShop) {
            doReturn(categories).when(categoryFlatListFetcher).fetchAllOpenProductCategories();
        } else {
            doReturn(categories).when(categoryFlatListFetcher).fetchAllProductCategories();
        }
        specifyNameTranslations(create);
        specifyPathItems(create);
    }

    private void specifyPathItems(DSLContext create) {
        Result<ProductCategoryPathItemRecord> pathItemns = create.newResult(PRODUCT_CATEGORY_PATH_ITEM);
        pathItemns.addAll(this.productCategoryPathItems);
        doReturn(pathItemns).when(categoryFlatListFetcher).fetchAllPathItems();
    }

    private void specifyNameTranslations(DSLContext create) {
        Result<ProductCategoryNameTranslationRecord> nameTranslations = create.newResult(PRODUCT_CATEGORY_NAME_TRANSLATION);
        nameTranslations.addAll(this.productCategoryNameTranslations);
        doReturn(nameTranslations).when(categoryFlatListFetcher).fetchAllProductCategoryNames();
    }
    
    private ProductCategoryParentMapRecord specifyProductCategoryParentMap(Integer category, Integer parentCategory) {
        ProductCategoryParentMapRecord parentMap = new ProductCategoryParentMapRecord();
        parentMap.setProductCategoryId(category);
        parentMap.setParentId(parentCategory);
        parentMap.setValidfrom(VALID_FROM);
        parentMap.setValidto(VALID_TO);
        this.productCategoryParentMapRecords.add(parentMap);
        return parentMap;
    }
    
    private void specifyProductCategoriParentMaps(boolean webShop) {
        DSLContext create = DSL.using(SQLDialect.MYSQL);
        Result<ProductCategoryParentMapRecord> parentMap = create.newResult(PRODUCT_CATEGORY_PARENT_MAP);
        parentMap.addAll(this.productCategoryParentMapRecords);
        if(webShop) {
            doReturn(parentMap).when(categoryFlatListFetcher).fetchAllOpenProductCategoriesParentMaps();
        } else {
            doReturn(parentMap).when(categoryFlatListFetcher).fetchAllProductCategoriesParentMaps();
        }
    }

    private void specifyProductCategoryAndParentMapForAllPaths() {
        specifyProductCategory(ROOTA_AINDEX, BIGGER_WEIGTH, VALID_FROM, VALID_TO_MISSING, ROOTA_PATH_ITEM, NO_HISTORY, ROOTA_NAME_FI);
        specifyProductCategory(SUBA_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBA_NAME_FI);
        specifyProductCategory(SUBB_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBB_NAME_FI);
        specifyProductCategory(SUBD_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBD_NAME_FI);
        specifyProductCategory(SUBE_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBE_NAME_FI);
        specifyProductCategory(SUBC_AINDEX, SMALLER_WEIGTH, VALID_FROM, VALID_TO_MISSING, SUBA_PATH_ITEM_NULL, NO_HISTORY, SUBC_NAME_FI);

        Map<Integer, List<ProductCategoryParentMapRecord>> parentCategoryMap = new HashMap<>();
        parentCategoryMap.put(SUBC_AINDEX, Arrays.asList(specifyProductCategoryParentMap(SUBC_AINDEX, SUBE_AINDEX), specifyProductCategoryParentMap(SUBC_AINDEX, SUBB_AINDEX)));
        parentCategoryMap.put(SUBB_AINDEX, Arrays.asList(specifyProductCategoryParentMap(SUBB_AINDEX, SUBA_AINDEX)));
        parentCategoryMap.put(SUBA_AINDEX, Arrays.asList(specifyProductCategoryParentMap(SUBA_AINDEX, ROOTA_AINDEX)));
        parentCategoryMap.put(SUBE_AINDEX, Arrays.asList(specifyProductCategoryParentMap(SUBE_AINDEX, SUBD_AINDEX)));
        parentCategoryMap.put(SUBD_AINDEX, Arrays.asList(specifyProductCategoryParentMap(SUBD_AINDEX, ROOTA_AINDEX)));
        specifyProductCategoriParentMaps(WEBSHOP);

        doReturn(parentCategoryMap).when(categoryFlatListFetcher).fetchAllValidProductCategoryParentMaps();
    }
}
