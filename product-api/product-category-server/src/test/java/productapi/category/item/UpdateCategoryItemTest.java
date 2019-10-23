package productapi.category.item;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pcapi.jooq.common.db.tables.records.ProductCategoryPathItemRecord;
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
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static productapi.RequestAuthorization.createValidAuthHeader;
import static productapi.TestResource.readResource;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(CategoryItemController.class)
@TestPropertySource(locations = "/unittest.properties")
@Import({UpdateCategoryItemTestConfig.class})
@ActiveProfiles("categoryItemTestProfile")
public class UpdateCategoryItemTest {

    private static final Integer EXPECTED_ID = 1;
    private static final String EXPECTED_NAME = "name";
    private static final String EXPECTED_CODE_NAME = "code_name";
    private static final Integer EXPECTED_WEIGHT = 99;
    private static final String EXPECTED_PATH = "path_item";
    private static final long EXPECTED_VALID_FROM_MS = 1564661772000L;
    private static final Integer MOCK_NEW_PATH_AINDEX_AFTER_INSERT = 999;
    private static final int EXPECTED_ADDED_CATEGORY_PARENT_ID = 20;
    private static final long EXPECTED_ADDED_CATEGORY_PARENT_VALID_TO = 1628676672000L;
    private static final Integer EXPECTED_EDITED_CATEGORY_PARENT_AINDEX = 1;
    private static final long EXPECTED_EDITED_CATEGORY_PARENT_VALID_FROM = 1597140672000L;

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

    @Value("classpath:mockJson/updateCategoryItem.json")
    private Resource resourceFile;

    @Value("classpath:mockJson/updateCategoryItem_invalidCodeName.json")
    private Resource invalidCodeNameRequestJson;

    @Value("classpath:mockJson/updateCategoryItem_invalidCategoryId.json")
    private Resource invalidCategoryIdRequestJson;

    @Captor
    private ArgumentCaptor<CategoryItem> categoryItemCaptor;

    @Captor
    private ArgumentCaptor<CategoryPath> addCategoryMapCaptor;

    @Captor
    private ArgumentCaptor<CategoryPath> editCategoryMapCaptor;

    private String mockCategoryJson;

    @Before
    public void setup() throws Exception {
        mockCategoryJson = readResource(resourceFile);
        when(productCategoryDAO.updateCategoryItem(any(DSLContext.class), categoryItemCaptor.capture())).thenReturn(1);
        mockNewPathItemAfterInsert();
        captorParentMapParameters();
    }

    @Test
    public void shouldUpdateExistingCategoryInformation() throws Exception {
        mockNameTranslationBeforeUpdate(true, 1);
        when(productCategoryPathItemDAO.pathItemHasBeenModified(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_PATH))).thenReturn(true);

        performPutCategoryItem().andExpect(status().isOk());

        verifyCategoryDaoUpdate();

        verify(productCategoryNameTranslationDAO, times(1)).updateFinnishName(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_NAME));
        verify(productCategoryNameTranslationDAO, times(0)).insertFinnishName(any(DSLContext.class), any(Integer.class), any(String.class));

        verify(productCategoryPathItemDAO, times(1)).closeOpenPathItems(any(DSLContext.class), eq(EXPECTED_ID));
        verify(productCategoryPathItemDAO, times(1)).addPathItem(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_PATH));
        verify(productCategoryDAO, times(1)).updateCurrentPathItem(any(DSLContext.class), eq(EXPECTED_ID), eq(MOCK_NEW_PATH_AINDEX_AFTER_INSERT));
    }

    @Test
    public void whenThereIsNoExistingCategoryFoundToUpdate_shouldReturnBadRequest() throws Exception {
        when(productCategoryDAO.updateCategoryItem(any(DSLContext.class), categoryItemCaptor.capture())).thenReturn(0);

        performPutCategoryItem()
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Update error, category 1 does not exist"));
    }

    @Test
    public void whenThereIsNoFinnishNameToUpdate_shouldInsertANewNameEntry() throws Exception {
        mockNameTranslationBeforeUpdate(true, 0);

        performPutCategoryItem().andExpect(status().isOk());

        verify(productCategoryNameTranslationDAO, times(1)).insertFinnishName(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_NAME));
    }

    @Test
    public void whenNameHasNotChanged_shouldNeitherInsertNorUpdate() throws Exception {
        mockNameTranslationBeforeUpdate(false, 1);

        performPutCategoryItem().andExpect(status().isOk());

        verify(productCategoryNameTranslationDAO, times(0)).insertFinnishName(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_NAME));
        verify(productCategoryNameTranslationDAO, times(0)).updateFinnishName(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_NAME));
    }

    @Test
    public void whenPathHasNotChanged_shouldNotAddANewPath() throws Exception {
        when(productCategoryPathItemDAO.pathItemHasBeenModified(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_PATH))).thenReturn(false);

        performPutCategoryItem().andExpect(status().isOk());

        verify(productCategoryPathItemDAO, times(0)).closeOpenPathItems(any(DSLContext.class), any(Integer.class));
        verify(productCategoryPathItemDAO, times(0)).addPathItem(any(DSLContext.class), any(Integer.class), any(String.class));
        verify(productCategoryDAO, times(0)).updateCurrentPathItem(any(DSLContext.class), any(Integer.class), any(Integer.class));
    }

    @Test
    public void whenCodeNameIsNull_shouldReturnBadRequest() throws Exception {
        String inputJson = readResource(invalidCodeNameRequestJson);
        performPutCategoryItemContent(inputJson).andExpect(status().isBadRequest());
    }

    @Test
    public void whenCategoryIdIsNull_shouldReturnBadRequest() throws Exception {
        String inputJson = readResource(invalidCategoryIdRequestJson);
        performPutCategoryItemContent(inputJson).andExpect(status().isBadRequest());
    }

    private void captorParentMapParameters() {
        doNothing().when(productCategoryParentMapDAO).insertParentMap(any(DSLContext.class), any(Integer.class), addCategoryMapCaptor.capture());
        doNothing().when(productCategoryParentMapDAO).updateParentMap(any(DSLContext.class), editCategoryMapCaptor.capture());
    }

    private void mockNewPathItemAfterInsert() {
        ProductCategoryPathItemRecord mockNewPathItem = mock(ProductCategoryPathItemRecord.class);
        when(mockNewPathItem.getAindex()).thenReturn(MOCK_NEW_PATH_AINDEX_AFTER_INSERT);
        when(productCategoryPathItemDAO.addPathItem(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_PATH))).thenReturn(mockNewPathItem);
    }

    private void mockNameTranslationBeforeUpdate(boolean nameHasBeenModified, int previousFinnishEntriesCount) {
        when(productCategoryNameTranslationDAO.nameHasBeenModified(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_NAME))).thenReturn(nameHasBeenModified);
        when(productCategoryNameTranslationDAO.updateFinnishName(any(DSLContext.class), eq(EXPECTED_ID), eq(EXPECTED_NAME))).thenReturn(previousFinnishEntriesCount);
    }

    private ResultActions performPutCategoryItem() throws Exception {
        return performPutCategoryItemContent(mockCategoryJson);
    }

    private ResultActions performPutCategoryItemContent(String content) throws Exception {
        return mockMvc.perform(
            put("/category/item")
                .header(HttpHeaders.AUTHORIZATION, createValidAuthHeader())
                .header("X-user", "username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
        );
    }

    private void verifyCategoryDaoUpdate() {
        CategoryItem actualCategoryItem = categoryItemCaptor.getValue();
        assertEquals(EXPECTED_ID, actualCategoryItem.categoryId);
        assertEquals(EXPECTED_NAME, actualCategoryItem.name);
        assertEquals(EXPECTED_CODE_NAME, actualCategoryItem.codeName);
        assertEquals(EXPECTED_WEIGHT, actualCategoryItem.weight);
        assertEquals(EXPECTED_PATH, actualCategoryItem.pathItem);
        assertEquals(EXPECTED_VALID_FROM_MS, actualCategoryItem.validFrom.getTime());
        assertNull(actualCategoryItem.validTo);
    }

    private void verifyCategoryParentMappings() {
        verifyCategoryParentMappingAdded();
        verifyCategoryParentMappingEdited();
    }

    private void verifyCategoryParentMappingAdded() {
        verify(productCategoryParentMapDAO, times(1))
                .insertParentMap(any(DSLContext.class), eq(EXPECTED_ID), any(CategoryPath.class));

        CategoryPath actualCategoryPathParameter = addCategoryMapCaptor.getValue();
        assertEquals(EXPECTED_ADDED_CATEGORY_PARENT_ID, actualCategoryPathParameter.getCategoryId());
        assertEquals(EXPECTED_ADDED_CATEGORY_PARENT_VALID_TO, actualCategoryPathParameter.getValidTo().getTime());
        assertNull(actualCategoryPathParameter.getValidFrom());
    }

    private void verifyCategoryParentMappingEdited() {
        verify(productCategoryParentMapDAO, times(1))
                .updateParentMap(any(DSLContext.class), any(CategoryPath.class));

        CategoryPath actualCategoryPathParameter = editCategoryMapCaptor.getValue();
        assertEquals(EXPECTED_EDITED_CATEGORY_PARENT_AINDEX, actualCategoryPathParameter.getAindex());
        assertEquals(EXPECTED_EDITED_CATEGORY_PARENT_VALID_FROM, actualCategoryPathParameter.getValidFrom().getTime());
        assertNull(actualCategoryPathParameter.getValidTo());
    }
}
