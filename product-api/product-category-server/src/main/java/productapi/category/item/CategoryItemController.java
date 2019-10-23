package productapi.category.item;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CategoryItemController {

    private final static Logger LOGGER = LogManager.getLogger(CategoryItemController.class);

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @RequestMapping(value = "/category/item", method = RequestMethod.POST, consumes = "application/json")
    public AddCategoryResponse addCategoryItem(@RequestBody @Valid NewCategoryItem newCategoryItem, @RequestHeader("X-user") String userName) {
        Integer newCategoryId = productCategoryRepository.addCategoryItem(newCategoryItem);
        LOGGER.info("Category added by user {} with data {}", userName, newCategoryItem);
        return new AddCategoryResponse(newCategoryId);
    }

    @RequestMapping(value = "/category/item", method = RequestMethod.PUT, consumes = "application/json")
    public void updateCategoryItem(@RequestBody @Valid CategoryItem categoryItem, @RequestHeader("X-user") String userName) {
        productCategoryRepository.updateCategoryItem(categoryItem);
        LOGGER.info("Category updated by user {} with data {}", userName, categoryItem);
    }
}
