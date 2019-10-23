package productapi.category.mappings.model;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import productapi.category.mappings.dao.ProductCategoryNameTranslationDAO;
import productapi.category.mappings.dao.ProductCategoryParentMapDAO;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public abstract class DeviceMapHelper {

    @Autowired
    private ProductCategoryParentMapDAO productCategoryParentMapDAO;

    @Autowired
    private ProductCategoryNameTranslationDAO productCategoryNameTranslationDAO;

    @Autowired
    DSLContext dbContext;

    public abstract List<DeviceCategoryPath> getDeviceCategoryList(int deviceId, String lang);

    public abstract void insertDeviceMapping(DeviceMapping deviceMapping);

    public abstract void updateDeviceMapping(DeviceMapping deviceMapping);


    public String getAllDeviceCategories(int categoryId, String lang) {
        List<Integer> allCategories = new ArrayList<>();
        Integer parentCategoryId;
        allCategories.add(categoryId);
        parentCategoryId = categoryId;
        do {
            parentCategoryId = productCategoryParentMapDAO.getParentDeviceCategoryId(dbContext, parentCategoryId);
            if (isValidCategory(parentCategoryId))
                addValidCategories(allCategories, parentCategoryId);
        } while (isValidCategory(parentCategoryId));
        return getCategoryTranslationPath(allCategories, lang);
    }

    private boolean isValidCategory(Integer parentCategoryId) {
        return parentCategoryId != null && parentCategoryId != 0;
    }

    private void addValidCategories(List<Integer> allCategories, Integer parentCategoryId) {
        if (!allCategories.contains(parentCategoryId)) {
            allCategories.add(parentCategoryId);
        } else {
            throw new IllegalArgumentException("Invalid Data Mapping");
        }
    }

    public String getCategoryTranslationPath(List<Integer> allCategories, String lang) {
        Collections.reverse(allCategories);
        List<String> categoryString = allCategories
                .stream()
                .map(categoryId -> getCategoryTranslation(categoryId, lang))
                .collect(toList());
        return getCategoryPath(categoryString);
    }

    private String getCategoryTranslation(Integer categoryId, String lang) {
        Map<String, String> langPathMap = productCategoryNameTranslationDAO.getCategoryTranslation(dbContext, categoryId);
        return langPathMap.get(lang) == null ? langPathMap.get("fi") :langPathMap.get(lang);
    }

    public String getCategoryPath(List<String> categoryString) {
        return categoryString
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(">"));
    }

}
