package productapi.mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import productapi.category.mappings.controller.DeviceMappingQueryService;
import productapi.category.mappings.controller.DeviceMappingRepository;
import productapi.category.mappings.model.DeviceMapping;
import productapi.category.mappings.model.DeviceType;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static productapi.RequestAuthorization.createValidAuthHeader;

@RunWith(SpringRunner.class)
@WebMvcTest(DeviceMappingQueryService.class)
@TestPropertySource(locations = "/unittest.properties")
public class DeviceMappingTest {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    private static final String ADD_MAPPING_PATH = "/category/addDeviceMapping";
    private static final String UPDATE_MAPPING_PATH = "/category/updateDeviceMapping";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    private DeviceMapping deviceMapping;
    private DeviceMapping editDeviceMapping;

    @MockBean
    private DeviceMappingRepository deviceMappingRepository;
    private JacksonTester<List<DeviceMapping>> jsonTester;

    @Before
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
        deviceMapping = new DeviceMapping(1, 12, Arrays.asList(3, 2), new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), DeviceType.USERDEVICE);
        editDeviceMapping = new DeviceMapping(1, 12, Arrays.asList(3), new Timestamp(System.currentTimeMillis()),
                new Timestamp(System.currentTimeMillis()), DeviceType.USERDEVICE);
    }

    @Test
    public void testResponseIsOkForCorrectRequest() throws Exception {
        final String deviceMappingJSON = jsonTester.write(Arrays.asList(deviceMapping)).getJson();
        mockMvc.perform(postMapping(ADD_MAPPING_PATH, deviceMappingJSON)).andExpect(status().isOk());
        verify(deviceMappingRepository).addDeviceMapping(any(DeviceMapping.class));
    }

    @Test
    public void testResponseForEmptyRequest() throws Exception {
        MvcResult error = mockMvc.perform(postMapping(ADD_MAPPING_PATH, ""))
            .andExpect(status().is4xxClientError()).andReturn();
        assertTrue("", error.getResolvedException().toString().contains("Required request body is missing"));
    }

    @Test
    public void testResponseIsOkIfEditRequestIsCorrect() throws Exception {
        final String deviceMappingJSON = jsonTester.write(Arrays.asList(editDeviceMapping)).getJson();
        mockMvc.perform(postMapping(UPDATE_MAPPING_PATH, deviceMappingJSON))
            .andExpect(status().isOk());
        verify(deviceMappingRepository).updateDeviceMapping(any(DeviceMapping.class));
    }

    @Test
    public void testResponseForEmptyEditRequest() throws Exception {
        MvcResult error = mockMvc.perform(postMapping(UPDATE_MAPPING_PATH, ""))
            .andExpect(status().is4xxClientError()).andReturn();
        assertTrue("",error.getResolvedException().toString().contains("Required request body is missing"));
    }

    private RequestBuilder postMapping(String path, String content) {
        return post(path)
            .contentType(APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, createValidAuthHeader())
            .header("X-user", "test user")
            .content(content);
    }
}
