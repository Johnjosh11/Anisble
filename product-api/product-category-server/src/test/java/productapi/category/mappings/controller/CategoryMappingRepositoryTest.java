package productapi.category.mappings.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import productapi.category.mappings.model.CategoryPath;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.doReturn;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CategoryMappingRepositoryTest {
    @Mock
    private CategoryPathMappingRepository categoryPathMappingRepository;

    @InjectMocks
    private CategoryMappingRepository categoryMappingRepository;

    @Test
    public void TestThatFetchCategoryParentMappingsReturnsParentMappingsById() {
        List<CategoryPath> expectedList = new ArrayList<>();
        doReturn(expectedList).when(categoryPathMappingRepository).getCategoryParentMappings(123, "fi");
        List<CategoryPath> result = categoryMappingRepository.fetchCategoryParentMappings(123, "fi");
        assertThat(result, equalTo(expectedList));
    }
}
