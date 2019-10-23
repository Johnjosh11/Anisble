package productapi.category.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pcapi.jooq.common.db.tables.records.ProductCategoryPathItemRecord;
import pcapi.jooq.common.db.tables.records.ProductCategoryRecord;
import productapi.category.mappings.dao.ProductCategoryNameTranslationDAO;
import productapi.category.mappings.dao.ProductCategoryParentMapDAO;
import productapi.category.mappings.model.CategoryPath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static productapi.RequestAuthorization.createValidAuthHeader;
import static productapi.TestResource.readResource;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryItemController.class)
@TestPropertySource(locations = "/unittest.properties")
@Import({UpdateCategoryItemTestConfig.class})
@ActiveProfiles("categoryItemTestProfile")
public class AddCategoryItemTest {

    private static final String EXPECTED_NAME = "newCategory_name";
    private static final String EXPECTED_CODE_NAME = "newCategory_code_name";
    private static final Integer EXPECTED_WEIGHT = 50;
    private static final String EXPECTED_PATH = "newCategory_path_item";
    private static final long EXPECTED_VALID_FROM_MS = 1502446272000L;
    private static final Integer MOCK_NEW_PATH_AINDEX_AFTER_INSERT = 999;
    private static final Integer MOCK_NEW_CATEGORY_ID_AFTER_INSERT = 444;

    @Autowired
    private ProductCategoryDAO productCategoryDAO;

    @Autowired
    private ProductCategoryNameTranslationDAO productCategoryNameTranslationDAO;

    @Autowired
    private ProductCategoryPathItemDAO productCategoryPathItemDAO;

    @Autowired
    private ProductCategoryParentMapDAO productCategoryParentMapDAO;

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:mockJson/addCategoryItem.json")
    private Resource resourceFile;

    @Value("classpath:mockJson/addCategoryItem_missingValues.json")
    private Resource invalidResourceWithMissingValues;

    @Captor
    private ArgumentCaptor<NewCategoryItem> newCategoryItemCaptor;

    @Before
    public void setup() {
        mockNewCategoryRecordAfterInsert();
        mockNewPathItemAfterInsert();
    }

    @Test
    public void shouldAddNewCategoryInformation() throws Exception {
        String mockCategoryJson = readResource(resourceFile);
        MvcResult mvcResult = performPostCategoryItemContent(mockCategoryJson).andExpect(status().isOk()).andReturn();
        verifyCategoryDaoResponse(mvcResult.getResponse());
        verifyCategoryDaoInsert();
        verifyCategoryNameTranslationInsert();
        verifyCategoryPathItemStoring();
        verifyCategoryParentMappingAdded();
    }

    @Test
    public void whenRequiredValuesAreMissing_shouldReturnBadRequest() throws Exception {
        String invalidRequestBodyWithMissingValues = readResource(invalidResourceWithMissingValues);
        performPostCategoryItemContent(invalidRequestBodyWithMissingValues)
            .andExpect(status().isBadRequest());
    }

    private void mockNewPathItemAfterInsert() {
        ProductCategoryPathItemRecord mockNewPathItem = mock(ProductCategoryPathItemRecord.class);
        when(mockNewPathItem.getAindex()).thenReturn(MOCK_NEW_PATH_AINDEX_AFTER_INSERT);
        when(productCategoryPathItemDAO.addPathItem(any(DSLContext.class), eq(MOCK_NEW_CATEGORY_ID_AFTER_INSERT), eq(EXPECTED_PATH)))
            .thenReturn(mockNewPathItem);
    }

    private void mockNewCategoryRecordAfterInsert() {
        ProductCategoryRecord mockNewCategoryRecord = mock(ProductCategoryRecord.class);
        when(mockNewCategoryRecord.getAindex()).thenReturn(MOCK_NEW_CATEGORY_ID_AFTER_INSERT);
        when(productCategoryDAO.addCategoryItem(any(DSLContext.class), newCategoryItemCaptor.capture())).thenReturn(mockNewCategoryRecord);
    }

    private ResultActions performPostCategoryItemContent(String content) throws Exception {
        return mockMvc.perform(
            post("/category/item")
                .header(HttpHeaders.AUTHORIZATION, createValidAuthHeader())
                .header("X-user", "username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );
    }

    private void verifyCategoryDaoResponse(MockHttpServletResponse response) throws Exception {
        JsonNode responseNode = new ObjectMapper().readTree(response.getContentAsString());
        assertEquals(MOCK_NEW_CATEGORY_ID_AFTER_INSERT, (Integer)responseNode.get("categoryId").asInt());
    }

    private void verifyCategoryDaoInsert() {
        NewCategoryItem actualNewCategoryItem = newCategoryItemCaptor.getValue();
        assertEquals(EXPECTED_NAME, actualNewCategoryItem.name);
        assertEquals(EXPECTED_CODE_NAME, actualNewCategoryItem.codeName);
        assertEquals(EXPECTED_WEIGHT, actualNewCategoryItem.weight);
        assertEquals(EXPECTED_PATH, actualNewCategoryItem.pathItem);
        assertEquals(EXPECTED_VALID_FROM_MS, actualNewCategoryItem.validFrom.getTime());
        assertNull(actualNewCategoryItem.validTo);
    }

    private void verifyCategoryNameTranslationInsert() {
        verify(productCategoryNameTranslationDAO, times(1)).insertFinnishName(any(DSLContext.class),
            eq(MOCK_NEW_CATEGORY_ID_AFTER_INSERT), eq(EXPECTED_NAME));
    }

    private void verifyCategoryPathItemStoring() {
        verify(productCategoryPathItemDAO, times(1))
            .addPathItem(any(DSLContext.class), eq(MOCK_NEW_CATEGORY_ID_AFTER_INSERT), eq(EXPECTED_PATH));
        verify(productCategoryDAO, times(1)).updateCurrentPathItem(any(DSLContext.class),
            eq(MOCK_NEW_CATEGORY_ID_AFTER_INSERT), eq(MOCK_NEW_PATH_AINDEX_AFTER_INSERT));
    }

    private void verifyCategoryParentMappingAdded() {
        verify(productCategoryParentMapDAO, times(1))
                .insertParentMap(any(DSLContext.class), eq(MOCK_NEW_CATEGORY_ID_AFTER_INSERT), any(CategoryPath.class));
    }
}
