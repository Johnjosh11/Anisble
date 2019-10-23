package productapi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ProductApi.class}, webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductApiTest {
    
    @Autowired
    private TestRestTemplate template;
    
    @LocalServerPort
    private int productApiPort;
    
    private StringBuilder baseUrl;
    
    @Before
    public void setup() {
        this.baseUrl = new StringBuilder().append("http://localhost:").append(productApiPort).append("/");
    }

    @Test
    public void testProductApiIsUpAndRunning() {
        ResponseEntity<String> response = template.getForEntity(baseUrl.append("actuator/health").toString(),
            String.class);
        Assert.assertEquals("{\"status\":\"UP\"}", response.getBody());
    }
}
