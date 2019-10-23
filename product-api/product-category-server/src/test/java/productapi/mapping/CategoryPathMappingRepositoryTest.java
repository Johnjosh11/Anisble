package productapi.mapping;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import productapi.category.hierarchy.controller.CategoryHierarchyBuilder;
import productapi.category.hierarchy.model.CategoryNode;
import productapi.category.hierarchy.model.NameTranslation;
import productapi.category.mappings.controller.CategoryPathMappingRepository;
import productapi.category.mappings.model.CategoryPath;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static productapi.category.hierarchy.controller.IncludeTimeRange.ACTIVE_AND_PAST_30_DAYS;
import static productapi.category.hierarchy.controller.IncludeTimeRange.ALL;

@RunWith(MockitoJUnitRunner.class)
public class CategoryPathMappingRepositoryTest {

    @Mock
    private CategoryHierarchyBuilder hierarchyBuilder;
    @InjectMocks
    private CategoryPathMappingRepository mappingRepository;

    private CategoryNode categoryNode;
    private static final String CATEGORY_NAME_PREFIX = "Category_Name_";
    private static final String LANGUAGE = "fi";
    private static final Integer ROOT_NODE_CATEGORY_ID = 9999;
    private static final Integer LVL1_NODE_A_CATEGORY_ID = 1;
    private static final Integer LVL1_NODE_B_CATEGORY_ID = 2;
    private static final Integer LVL1_NODE_C_CATEGORY_ID = 3;
    private static final Integer LVL2_NODE_A1_CATEGORY_ID = 11;
    private static final Integer LVL2_NODE_A2_CATEGORY_ID = 12;
    private static final Integer LVL2_NODE_B1_CATEGORY_ID = 21;
    private static final Integer LVL2_NODE_C1_CATEGORY_ID = 31;
    private static final Integer LVL3_NODE_CATEGORY_ID = 100;
    private static final Timestamp VALID_FROM =  new Timestamp(2000L);
    private static final Timestamp VALID_TO =  new Timestamp(2000L);

    @Before
    public void setUp() throws Exception {
        categoryNode = new CategoryNode();
        categoryNode.setProductCategoryId(ROOT_NODE_CATEGORY_ID);
        categoryNode.setNameTranslations(getTranslations(ROOT_NODE_CATEGORY_ID));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void shouldGetOneCategoryPathForCategoryIdWithOneChild() throws Exception {
        doReturn(getRootNodeWithOneChild()).when(hierarchyBuilder).buildHierarchyTreeForCategoryId(ROOT_NODE_CATEGORY_ID, ACTIVE_AND_PAST_30_DAYS);
        List<String> categoryPaths = mappingRepository.getAllProductCategoryPaths(ROOT_NODE_CATEGORY_ID, LANGUAGE);
        assertThat(categoryPaths.size(), equalTo(1));
        assertThat(categoryPaths.get(0), equalTo("Category_Name_1>Category_Name_9999"));
    }

    @Test
    public void shouldGetThreeCategoryPathsForCategoryId() throws Exception {
        doReturn(getRootNodeWithThreeChildren()).when(hierarchyBuilder).buildHierarchyTreeForCategoryId(ROOT_NODE_CATEGORY_ID, ACTIVE_AND_PAST_30_DAYS);
        List<String> categoryPaths = mappingRepository.getAllProductCategoryPaths(ROOT_NODE_CATEGORY_ID, LANGUAGE);
        assertThat("Three category paths are fetched", categoryPaths.size(), equalTo(3));
        assertTrue(categoryPaths.contains("Category_Name_1>Category_Name_9999"));
        assertTrue(categoryPaths.contains("Category_Name_2>Category_Name_9999"));
        assertTrue(categoryPaths.contains("Category_Name_3>Category_Name_9999"));
    }

    @Test
    public void shouldGetOneCategoryPathForCategoryIdWithNoChild() throws Exception {
        doReturn(categoryNode).when(hierarchyBuilder).buildHierarchyTreeForCategoryId(ROOT_NODE_CATEGORY_ID, ACTIVE_AND_PAST_30_DAYS);
        List<String> categoryPaths = mappingRepository.getAllProductCategoryPaths(ROOT_NODE_CATEGORY_ID, LANGUAGE);
        assertThat(categoryPaths.size(), equalTo(1));
        assertTrue(categoryPaths.contains("Category_Name_9999"));
    }

    @Test
    public void shouldGetAllCategoryPathsForCategoryId() throws Exception {
        doReturn(getRootNodeWithChildNodesHavingChildren()).when(hierarchyBuilder).buildHierarchyTreeForCategoryId(ROOT_NODE_CATEGORY_ID, ACTIVE_AND_PAST_30_DAYS);
        List<String> categoryPaths = mappingRepository.getAllProductCategoryPaths(ROOT_NODE_CATEGORY_ID, LANGUAGE);
        assertThat("Five category paths are fetched", categoryPaths.size(), equalTo(5));
        assertTrue(categoryPaths.contains("Category_Name_100>Category_Name_1>Category_Name_11>Category_Name_9999"));
        assertTrue(categoryPaths.contains("Category_Name_100>Category_Name_1>Category_Name_12>Category_Name_9999"));
        assertTrue(categoryPaths.contains("Category_Name_100>Category_Name_2>Category_Name_21>Category_Name_9999"));
        assertTrue(categoryPaths.contains("Category_Name_100>Category_Name_3>Category_Name_31>Category_Name_9999"));
        assertTrue(categoryPaths.contains("Category_Name_100>Category_Name_9999"));
    }

    @Test
    public void shouldGetParentMappingsForCategoryThatHasParentWithMultipleParents() {
        doReturn(getHierarchyWhereChildHasTwoParents()).when(hierarchyBuilder).buildHierarchyTreeForCategoryId(ROOT_NODE_CATEGORY_ID, ALL);
        List<CategoryPath> parentMappings = mappingRepository.getCategoryParentMappings(ROOT_NODE_CATEGORY_ID, LANGUAGE);
        assertThat(parentMappings.size(), equalTo(1));
        CategoryPath parentMapping = parentMappings.get(0);
        List<String> parentPaths = parentMapping.getCategories();
        assertTrue(parentPaths.contains("Category_Name_100>Category_Name_11"));
        assertTrue(parentPaths.contains("Category_Name_100>Category_Name_1>Category_Name_11"));
        assertThat(parentMapping.getValidFrom(), equalTo(VALID_FROM));
        assertThat(parentMapping.getValidTo(), equalTo(VALID_TO));
        assertThat(parentMapping.getCategoryId(), equalTo(LVL2_NODE_A1_CATEGORY_ID));
    }

    private CategoryNode getRootNodeWithOneChild() {
        addChildNodeToNode(categoryNode, LVL1_NODE_A_CATEGORY_ID);
        return categoryNode;
    }

    private CategoryNode getRootNodeWithThreeChildren() {
        addChildNodeToNode(categoryNode, LVL1_NODE_A_CATEGORY_ID);
        addChildNodeToNode(categoryNode, LVL1_NODE_B_CATEGORY_ID);
        addChildNodeToNode(categoryNode, LVL1_NODE_C_CATEGORY_ID);
        return categoryNode;
    }

    private CategoryNode getRootNodeWithChildNodesHavingChildren() {
        CategoryNode tempNode = addChildNodeToNode(categoryNode, LVL2_NODE_A1_CATEGORY_ID);
        tempNode = addChildNodeToNode(tempNode, LVL1_NODE_A_CATEGORY_ID);
        addChildNodeToNode(tempNode, LVL3_NODE_CATEGORY_ID);//path 100>1>11>9999

        tempNode = addChildNodeToNode(categoryNode, LVL2_NODE_A2_CATEGORY_ID);
        tempNode = addChildNodeToNode(tempNode, LVL1_NODE_A_CATEGORY_ID);
        addChildNodeToNode(tempNode, LVL3_NODE_CATEGORY_ID);//path 100>1>12>9999

        tempNode = addChildNodeToNode(categoryNode, LVL2_NODE_B1_CATEGORY_ID);
        tempNode = addChildNodeToNode(tempNode, LVL1_NODE_B_CATEGORY_ID);
        addChildNodeToNode(tempNode, LVL3_NODE_CATEGORY_ID);//path 100>1>21>9999

        tempNode = addChildNodeToNode(categoryNode, LVL2_NODE_C1_CATEGORY_ID);
        tempNode = addChildNodeToNode(tempNode, LVL1_NODE_C_CATEGORY_ID);
        addChildNodeToNode(tempNode, LVL3_NODE_CATEGORY_ID);//path 100>1>31>9999

        addChildNodeToNode(categoryNode, LVL3_NODE_CATEGORY_ID);//path 100>9999

        return categoryNode;
    }

    private CategoryNode getHierarchyWhereChildHasTwoParents() {
        CategoryNode tempNode = addChildNodeToNode(categoryNode, LVL2_NODE_A1_CATEGORY_ID);
        addChildNodeToNode(tempNode, LVL3_NODE_CATEGORY_ID);//path 100>11

        tempNode = addChildNodeToNode(categoryNode, LVL2_NODE_A1_CATEGORY_ID);
        tempNode = addChildNodeToNode(tempNode, LVL1_NODE_A_CATEGORY_ID);
        addChildNodeToNode(tempNode, LVL3_NODE_CATEGORY_ID);//path 100>1>11

        return categoryNode;
    }

    private List<NameTranslation> getTranslations(Integer categoryId) {
        NameTranslation translation = new NameTranslation(CATEGORY_NAME_PREFIX + categoryId, "fi");
        return Arrays.asList(translation);
    }

    private CategoryNode addChildNodeToNode(CategoryNode node, Integer childCategoryId) {
        CategoryNode childNode = new CategoryNode();
        childNode.setProductCategoryId(childCategoryId);
        childNode.setAindex(childCategoryId);
        childNode.setNameTranslations(getTranslations(childCategoryId));
        childNode.setCategoryValidFrom(VALID_FROM);
        childNode.setCategoryValidTo(VALID_TO);
        node.addChild(childNode);
        return childNode;
    }
}
