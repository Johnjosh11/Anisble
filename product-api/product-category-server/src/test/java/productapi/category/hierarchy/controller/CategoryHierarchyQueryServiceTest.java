package productapi.category.hierarchy.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.exception.DataAccessException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import productapi.category.hierarchy.model.CategoryNode;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryHierarchyQueryService.class)
public class CategoryHierarchyQueryServiceTest {
    private static final String CATEGORY_HIERARCHY_FOR_SALES_PATH = "/category/hierarchyForSales";
    private static final String CATEGORY_HIERARCHY_FOR_MANAGEMENT_PATH = "/category/hierarchyForManagement";

    private static final int AINDEX = 345678;
    private static final String CODE_NAME = "a-code-name";
    private static final int WEIGHT = 100;
    private static final String PATH_ITEM = "category_path_item";

    private static final long VALID_FROM_MILLISECONDS = 1568630000000L;
    private static final String EXPECTED_VALID_FROM_TIMESTAMP = "2019-09-16T10:33:20.000+0000";

    private static final long VALID_TO_MILLISECONDS = 1568634120000L;
    private static final String EXPECTED_VALID_TO_TIMESTAMP = "2019-09-16T11:42:00.000+0000";

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CategoryHierarchyBuilder productCategoryHierarchyBuilder;

    @Test
    public void whenFetchingWebShopCategoriesShouldIncludeWebShopFieldsAndOmitManagementFields() throws Exception {
        specifyHierarchy(Collections.singletonList(getCategoryNodeWithValidityDates()));
        String responseBody = performGetExpectingOk(CATEGORY_HIERARCHY_FOR_SALES_PATH);
        JsonNode node = getFirstJsonNode(responseBody);
        assertNull(node.get("aindex"));
        assertNull(node.get("code_name"));
        assertNull(node.get("weight"));
        assertEquals(PATH_ITEM, node.get("pathItem").asText());
    }
    
    @Test
    public void whenFetchingCategoriesForManagementShouldIncludeBothManagementAndWebShopFields() throws Exception {
        specifyHierarchy(Collections.singletonList(getCategoryNodeWithValidityDates()));
        String responseBody = performGetExpectingOk(CATEGORY_HIERARCHY_FOR_MANAGEMENT_PATH);
        JsonNode node = getFirstJsonNode(responseBody);
        assertEquals(AINDEX, node.get("aindex").asInt());
        assertEquals(CODE_NAME, node.get("codeName").asText());
        assertEquals(WEIGHT, node.get("weigth").asInt());
        assertEquals(PATH_ITEM, node.get("pathItem").asText());
    }

    @Test
    public void testWebShopNOkResponse() throws Exception {
        doThrow(new DataAccessException("test")).when(productCategoryHierarchyBuilder).buildHierarchyTreeForWebShop();
        mockMvc.perform(get(CATEGORY_HIERARCHY_FOR_SALES_PATH))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void testManagementNOkResponse() throws Exception {
        doThrow(new DataAccessException("test")).when(productCategoryHierarchyBuilder).buildHierarchyTreeForWebShop();
        mockMvc.perform(get(CATEGORY_HIERARCHY_FOR_MANAGEMENT_PATH))
            .andExpect(status().isInternalServerError());
    }

    @Test
    public void whenFetchingManagementCategoriesThenShouldUseTimestampsWithUtcTimeZone() throws Exception {
        CategoryNode node = getCategoryNodeWithValidityDates();
        specifyHierarchy(Collections.singletonList(node));

        String responseBody = performGetExpectingOk(CATEGORY_HIERARCHY_FOR_MANAGEMENT_PATH);

        assertTimestampsWithTimeZone(responseBody);
    }

    @Test
    public void whenFetchingSalesCategoriesThenShouldReturnNullValidityDates() throws Exception {
        CategoryNode node = getCategoryNodeWithValidityDates();
        specifyHierarchy(Collections.singletonList(node));

        String responseBody = performGetExpectingOk(CATEGORY_HIERARCHY_FOR_SALES_PATH);

        assertEmptyValidityDates(responseBody);
    }

    private String performGetExpectingOk(String path) throws Exception {
        return mockMvc.perform(get(path))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    private void specifyHierarchy(List<CategoryNode> categoryHierarchy) {
        doReturn(categoryHierarchy).when(productCategoryHierarchyBuilder).buildHierarchyTreeForWebShop();
    }

    private CategoryNode getCategoryNodeWithValidityDates() {
        CategoryNode node = new CategoryNode();
        node.setAindex(AINDEX);
        node.setCodeName(CODE_NAME);
        node.setWeigth(WEIGHT);
        node.setPathItem(PATH_ITEM);
        Timestamp categoryValidFrom = new Timestamp(VALID_FROM_MILLISECONDS);
        node.setCategoryValidFrom(categoryValidFrom);
        Timestamp categoryValidTo = new Timestamp(VALID_TO_MILLISECONDS);
        node.setCategoryValidTo(categoryValidTo);
        return node;
    }

    private void assertTimestampsWithTimeZone(String jsonString) throws Exception {
        JsonNode firstJsonNode = getFirstJsonNode(jsonString);
        JsonNode categoryValidFromNode = firstJsonNode.get("categoryValidFrom");
        JsonNode categoryValidToNode = firstJsonNode.get("categoryValidTo");
        assertEquals(EXPECTED_VALID_FROM_TIMESTAMP, categoryValidFromNode.asText());
        assertEquals(EXPECTED_VALID_TO_TIMESTAMP, categoryValidToNode.asText());
    }

    private void assertEmptyValidityDates(String jsonString) throws Exception {
        JsonNode firstJsonNode = getFirstJsonNode(jsonString);
        JsonNode categoryValidFromNode = firstJsonNode.get("categoryValidFrom");
        JsonNode categoryValidToNode = firstJsonNode.get("categoryValidTo");
        assertNull(categoryValidFromNode);
        assertNull(categoryValidToNode);
    }

    private JsonNode getFirstJsonNode(String jsonString) throws Exception {
        return new ObjectMapper().readTree(jsonString).get(0);
    }
}
