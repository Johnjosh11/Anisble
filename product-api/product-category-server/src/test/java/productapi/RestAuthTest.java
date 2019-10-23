package productapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import productapi.category.mappings.controller.DeviceMappingRepository;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static productapi.RequestAuthorization.createBasicAuthHeader;
import static productapi.RequestAuthorization.createValidAuthHeader;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "/unittest.properties")
@AutoConfigureMockMvc
public class RestAuthTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    DeviceMappingRepository deviceMappingRepository;

    @Test
    public void whenAnonymousUserRequestsHomePage_thenSuccess() throws Exception {
        this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Welcome to Product-API")));
    }

    @Test
    public void whenUserWithCorrectCredentialsPostsANewCategoryMapping_thenOkResponse() throws Exception {
        this.mockMvc.perform(postAddDevice(createValidAuthHeader())).andExpect(status().isOk());
    }

    @Test
    public void whenUserWithWrongCredentialsPostsANewCategoryMapping_thenUnauthorizedResponse() throws Exception {
        this.mockMvc.perform(postAddDevice(createBasicAuthHeader("wrong", "password")))
            .andExpect(status().isUnauthorized());
    }

    private MockHttpServletRequestBuilder postAddDevice(String basicAuthHeader) {
        return post("/category/addDeviceMapping")
            .header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
                .header("X-user", "test-user")
            .contentType("application/json")
            .content("[]");
    }
}
