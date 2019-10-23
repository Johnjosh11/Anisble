package productapi.mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import productapi.category.mappings.controller.DeviceMappingQueryService;
import productapi.category.mappings.controller.DeviceMappingRepository;
import productapi.category.mappings.model.DeviceCategoryPath;
import productapi.category.mappings.model.DeviceMapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DeviceMappingQueryService.class)
public class DeviceCategoryMappingTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DeviceMappingRepository deviceMappingRepository;
    private JacksonTester<List<DeviceMapping>> jsonTester;

    @Before
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void shouldCallServiceOnce() throws Exception {
        when(deviceMappingRepository.getDeviceCategoryDetails(Arrays.asList("1130"), "handset", "fi")).thenReturn(getMappings());
        mockMvc
                .perform(get("/category/deviceCategories?deviceId=1130&deviceType=handset&lang=\"fi\""))
                .andExpect(status().isOk());
        verify(deviceMappingRepository, times(1)).getDeviceCategoryDetails(Arrays.asList("1130"), "handset", "fi");
    }

    @Test
    public void shouldReturnDeviceCategoriesForCorrectRequest() throws Exception {
        when(deviceMappingRepository.getDeviceCategoryDetails(Arrays.asList("1130"), "handset", "fi")).thenReturn(getMappings());
        mockMvc.perform(get("/category/deviceCategories?deviceId=1130&deviceType=handset&lang=\"fi\""))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", Matchers.hasSize(1)))
            .andReturn();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturn400ForInvalidTypes() throws Exception {
        doThrow(new IllegalArgumentException()).when(deviceMappingRepository).getDeviceCategoryDetails(any(List.class), any(String.class), any(String.class));
        mockMvc.perform(get("/category/deviceCategories?deviceId=1130&deviceType=ABC"))
            .andExpect(status().isBadRequest());
    }

    private List<DeviceCategoryPath> getMappings() {
        List<DeviceCategoryPath> categoryPathList = new ArrayList<>();
        categoryPathList.add(new DeviceCategoryPath(1, 1130,31, "Webshop>Kotiverkon laitteet", new Timestamp(System.currentTimeMillis()), null));
        return categoryPathList;
    }
}
