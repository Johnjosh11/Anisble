package productapi.category.mappings.controller;

import static productapi.category.mappings.model.ProductMapMapper.populateMappingList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import productapi.category.mappings.dao.HandsetCategoryMapDAO;
import productapi.category.mappings.dao.ProductCategoryMapDAO;
import productapi.category.mappings.dao.UserDeviceCategoryMapDAO;
import productapi.category.mappings.model.CategoryPath;
import productapi.category.mappings.model.DeviceType;
import productapi.category.mappings.model.ProductMap;

@Component
public class CategoryMappingRepository {

    @Autowired
    DSLContext dbContext;
    @Autowired
    private UserDeviceCategoryMapDAO userDeviceCategoryMapDao;
    @Autowired
    private HandsetCategoryMapDAO handsetCategoryMapDao;
    @Autowired
    private ProductCategoryMapDAO productCategoryMapDao;
    @Autowired
    private CategoryPathMappingRepository categoryPathMappingRepository;

    public Map<DeviceType, List<ProductMap>> fetchAllCategoryMappings() {
        Map<DeviceType, List<ProductMap>> mappings = new HashMap<>();

        mappings.put(DeviceType.PRODUCT, populateMappingList(productCategoryMapDao.fetchProductCategoryMap(dbContext)));
        mappings.put(DeviceType.USERDEVICE, populateMappingList(userDeviceCategoryMapDao.fetchUserDeviceCategoryMap(dbContext)));
        mappings.put(DeviceType.HANDSET, populateMappingList(handsetCategoryMapDao.fetchHandsetCategoryMap(dbContext)));

        return mappings;
    }

    public CategoryPath fetchAllCategoryPaths(Integer categoryId, String language) {
        return new CategoryPath(categoryId, categoryPathMappingRepository.getAllProductCategoryPaths(categoryId, language));
    }

    public List<CategoryPath> fetchCategoryParentMappings(Integer categoryId, String language) {
        return categoryPathMappingRepository.getCategoryParentMappings(categoryId, language);
    }
}
