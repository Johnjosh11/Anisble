package productapi.category.item;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockExecuteContext;
import org.jooq.tools.jdbc.MockResult;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.PlatformTransactionManager;
import productapi.category.mappings.dao.ProductCategoryNameTranslationDAO;
import productapi.category.mappings.dao.ProductCategoryParentMapDAO;

import java.sql.SQLException;

@Configuration
@Profile("categoryItemTestProfile")
public class UpdateCategoryItemTestConfig {

    @MockBean
    ProductCategoryDAO productCategoryDAO;

    @MockBean
    ProductCategoryPathItemDAO productCategoryPathItemDAO;

    @MockBean
    ProductCategoryNameTranslationDAO productCategoryNameTranslationDAO;

    @MockBean
    PlatformTransactionManager platformTransactionManager;

    @MockBean
    ProductCategoryParentMapDAO productCategoryParentMapDAO;

    @Bean
    public ProductCategoryRepository productCategoryRepository() {
        return new ProductCategoryRepository();
    }

    @Bean
    public DSLContext dslContext() {
        MockDataProvider provider = new MockProvider();
        MockConnection connection = new MockConnection(provider);
        return DSL.using(connection, SQLDialect.MYSQL);
    }

    private class MockProvider implements MockDataProvider {
        @Override
        public MockResult[] execute(MockExecuteContext mockExecuteContext) throws SQLException {
            return new MockResult[0];
        }
    }
}
