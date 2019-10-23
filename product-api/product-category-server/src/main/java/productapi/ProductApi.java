package productapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class ProductApi {
    static Logger logger = LogManager.getLogger(ProductApi.class);
    public static void main(String[] args) {
        SpringApplication.run(ProductApi.class, args);
        logger.info("Product-api started...");
    }
    
}
