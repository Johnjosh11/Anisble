package productapi.category.mappings.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryQueryService.class)
public class CategoryQueryServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryMappingRepository categoryMappingRepository;

    @Test
    public void testGetCategoryParentMappings() throws Exception {
        mockMvc.perform(get("/category/parentmappings").param("categoryId", "123")).andExpect(status().isOk());
        verify(categoryMappingRepository).fetchCategoryParentMappings(123, "fi");
    }

    @Test
    public void testGetCategoryPaths() throws Exception {
        mockMvc.perform(get("/category/paths").param("categoryId", "123")).andExpect(status().isOk());
        verify(categoryMappingRepository).fetchAllCategoryPaths(123, "fi");
    }

    @Test
    public void testGetAllCategoryMappings() throws Exception {
        mockMvc.perform(get("/category/mappings")).andExpect(status().isOk());
        verify(categoryMappingRepository).fetchAllCategoryMappings();
    }

    @Test
    public void testRootRoute() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome to Product-API!"));
    }
}
