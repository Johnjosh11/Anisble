package productapi.category.hierarchy.controller;

import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_PARENT_MAP;
import static pcapi.jooq.common.db.Tables.PRODUCT_CATEGORY_NAME_TRANSLATION;
import static org.jooq.impl.DSL.currentTimestamp;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.jooq.DSLContext;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CategoryAndParentMapFlatListFetcherTest {

    @Mock DSLContext create;
    @Mock SelectSelectStep select;
    @Mock SelectJoinStep join;
    @Mock SelectConditionStep condition;
    
    @InjectMocks
    CategoryAndParentMapFlatListFetcher categoryAndParentMapFlatListFetcher;

    @Test
    public void testFetchAllOpenProductCategories() {
        doReturn(select).when(create).select();
        doReturn(join).when(select).from(PRODUCT_CATEGORY);
        doReturn(condition).when(join).where(PRODUCT_CATEGORY.VALIDFROM.isNotNull().and(PRODUCT_CATEGORY.VALIDFROM.lessOrEqual(currentTimestamp())).and(PRODUCT_CATEGORY.VALIDTO.isNull().or(PRODUCT_CATEGORY.VALIDTO.greaterOrEqual(currentTimestamp()))));
        categoryAndParentMapFlatListFetcher.fetchAllOpenProductCategories();
        verify(condition).fetch();
    }
    
    @Test
    public void testFetchAllOpenProductCategoriesParentMaps() {
        doReturn(select).when(create).select();
        doReturn(join).when(select).from(PRODUCT_CATEGORY_PARENT_MAP);
        doReturn(condition).when(join).where(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM.isNotNull().and(PRODUCT_CATEGORY_PARENT_MAP.VALIDFROM.lessOrEqual(currentTimestamp())).and(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO.isNull().or(PRODUCT_CATEGORY_PARENT_MAP.VALIDTO.greaterOrEqual(currentTimestamp()))));
        categoryAndParentMapFlatListFetcher.fetchAllOpenProductCategoriesParentMaps();
        verify(condition).fetch();
    }
    
    @Test
    public void testFetchAllProductCategories() {
        doReturn(select).when(create).select();
        doReturn(join).when(select).from(PRODUCT_CATEGORY);
        categoryAndParentMapFlatListFetcher.fetchAllProductCategories();
        verify(join).fetch();
    }
    
    @Test
    public void testFetchAllProductCategoriesParentMaps() {
        doReturn(select).when(create).select();
        doReturn(join).when(select).from(PRODUCT_CATEGORY_PARENT_MAP);
        categoryAndParentMapFlatListFetcher.fetchAllProductCategoriesParentMaps();
        verify(join).fetch();
    }
    
    @Test
    public void testFetchAllProductCategoryNames() {
        doReturn(select).when(create).select();
        doReturn(join).when(select).from(PRODUCT_CATEGORY_NAME_TRANSLATION);
        categoryAndParentMapFlatListFetcher.fetchAllProductCategoryNames();
        verify(join).fetch();
    }
    
}
