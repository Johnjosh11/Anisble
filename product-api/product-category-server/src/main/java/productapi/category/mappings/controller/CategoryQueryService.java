package productapi.category.mappings.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import productapi.category.mappings.model.DeviceType;
import productapi.category.mappings.model.CategoryPath;
import productapi.category.mappings.model.ProductMap;

@RestController
public class CategoryQueryService {

    static Logger logger = LogManager.getLogger(CategoryQueryService.class);

    @Autowired
    private CategoryMappingRepository categoryMappingRepository;

    @RequestMapping("/category/mappings")
    public Map<DeviceType, List<ProductMap>> productCategoryMappings(HttpServletResponse res) {
        logger.debug("mapping service called...");
        return categoryMappingRepository.fetchAllCategoryMappings();
    }

    @RequestMapping("/")
    public String greetings(HttpServletResponse res) {
        logger.debug("Welcome to Product-API.");
        return "Welcome to Product-API!";
    }

    @RequestMapping("/category/paths")
    public CategoryPath getAllCategoryPaths(@RequestParam(value = "categoryId") Integer categoryId,
                                            @RequestParam(value = "language", defaultValue = "fi") String lang) {
        return categoryMappingRepository.fetchAllCategoryPaths(categoryId, lang);
    }

    @RequestMapping("/category/parentmappings")
    public List<CategoryPath> getCategoryParents(@RequestParam(value = "categoryId") Integer categoryId,
                                                 @RequestParam(value = "language", defaultValue = "fi") String lang) {
        return categoryMappingRepository.fetchCategoryParentMappings(categoryId, lang);
    }
}
