package productapi.category.hierarchy.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import productapi.category.hierarchy.model.CategoryNode;
import productapi.category.hierarchy.view.JsonView.Management;
import productapi.category.hierarchy.view.JsonView.WebShop;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@RestController
public class CategoryHierarchyQueryService {
    
    static Logger logger = LogManager.getLogger(CategoryHierarchyQueryService.class);
    
    @Autowired
    CategoryHierarchyBuilder productCategoryHierarchyBuilder;

    // FOR WEBSHOP
    @JsonView(WebShop.class)
    @RequestMapping("/category/hierarchyForSales")
    public List<CategoryNode> productCategoryHierarchyForWebShop(HttpServletResponse res) {
        try {
            return productCategoryHierarchyBuilder.buildHierarchyTreeForWebShop();
        } catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ArrayList<>();
        } 
    }

    // FOR TAGGING TOOL
    @JsonView(Management.class)
    @RequestMapping("/category/hierarchyForManagement")
    public List<CategoryNode> productCategoryHierarchyForManagement(HttpServletResponse res) {
        try {
            return productCategoryHierarchyBuilder.buildHierarchyTreeForWebShop();
        } catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ArrayList<>();
        } 
    }

    // FOR CATEGORY MANAGEMENT TOOL
    @JsonView(Management.class)
    @RequestMapping("/category/hierarchy/all")
    public List<CategoryNode> productCategoryAllHierarchyForManagement(HttpServletResponse res) {
        try {
            return productCategoryHierarchyBuilder.buildHierarchyTreeForAllCategories();
        } catch (DataAccessException e) {
            logger.error(e.getMessage(), e);
            res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return new ArrayList<>();
        }
    }
    
}
