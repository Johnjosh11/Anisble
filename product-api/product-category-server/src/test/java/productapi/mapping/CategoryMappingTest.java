package productapi.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.exception.DataAccessException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import productapi.category.mappings.controller.CategoryMappingRepository;
import productapi.category.mappings.controller.CategoryQueryService;
import productapi.category.mappings.model.DeviceType;
import productapi.category.mappings.model.CategoryPath;
import productapi.category.mappings.model.ProductMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryQueryService.class)
public class CategoryMappingTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final Integer CATEGORY_ID = 9999;
    @MockBean
    private CategoryMappingRepository categoryMappingRepository;

    @Before
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void shouldCallServiceOnce() throws Exception {
        when(categoryMappingRepository.fetchAllCategoryMappings()).thenReturn(getCategoryMappings());
        mockMvc
                .perform(get("/category/mappings"))
                .andExpect(status().isOk());
        verify(categoryMappingRepository, times(1)).fetchAllCategoryMappings();
    }

    @Test
    public void shouldReturnCorrectResponse() throws Exception {
        when(categoryMappingRepository.fetchAllCategoryMappings()).thenReturn(getCategoryMappings());
        mockMvc
                .perform(get("/category/mappings"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"HANDSET\":[{\"id\":2,\"category\":[3,4]}],\"PRODUCT\":[{\"id\":1,\"category\":[1,2]}]}"));
    }

    @Test(expected = Exception.class)
    public void shouldthrowExceptionForErrorCases() throws Exception {
        when(categoryMappingRepository.fetchAllCategoryMappings()).thenThrow(new DataAccessException("error"));
        mockMvc.perform(get("/category/mappings"));
    }

    @Test
    public void shouldCallCategoryPathsServiceOnce() throws Exception {
        when(categoryMappingRepository.fetchAllCategoryPaths(9999, "fi")).thenReturn(getCategoryPath());
        mockMvc
                .perform(get("/category/paths?categoryId=9999&language=fi"))
                .andExpect(status().isOk());
        verify(categoryMappingRepository, times(1)).fetchAllCategoryPaths(CATEGORY_ID, "fi");
    }

    @Test
    public void shouldReturnCategoryPathsCorrectResponse() throws Exception {
        when(categoryMappingRepository.fetchAllCategoryPaths(CATEGORY_ID, "fi")).thenReturn(getCategoryPath());
        mockMvc
                .perform(get("/category/paths?categoryId=9999&language=fi"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"categoryId\":9999,\"categories\":[\"Webshop>Random>Path>For>Category\",\"Webshop>Another>Path>ForThis>Category\"]}"));
    }

    @Test(expected = Exception.class)
    public void shouldThrowCategoryPathsExceptionForErrorCases() throws Exception {
        when(categoryMappingRepository.fetchAllCategoryPaths(CATEGORY_ID, "fi")).thenThrow(new DataAccessException("error"));
        mockMvc.perform(get("/category/paths?categoryId=9999&language=fi"));
    }

    private Map<DeviceType, List<ProductMap>> getCategoryMappings() {
        Map categoryMap = new HashMap<String, List<ProductMap>>();
        categoryMap.put(DeviceType.PRODUCT, Arrays.asList(new ProductMap(1, Arrays.asList(1, 2))));
        categoryMap.put(DeviceType.HANDSET, Arrays.asList(new ProductMap(2, Arrays.asList(3, 4))));
        return categoryMap;
    }

    private CategoryPath getCategoryPath() {
        List<String> categoryPaths = new ArrayList<>();
        categoryPaths.add("Webshop>Random>Path>For>Category");
        categoryPaths.add("Webshop>Another>Path>ForThis>Category");
        return new CategoryPath(CATEGORY_ID, categoryPaths);
    }
}
