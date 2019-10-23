package productapi.category.hierarchy.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import productapi.category.hierarchy.view.JsonView.Management;
import productapi.category.hierarchy.view.JsonView.WebShop;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryNode {

    public static final String FORWARD_SLASH = "/";

    @JsonProperty("nameTranslations")
    @JsonView(WebShop.class)
    private List<NameTranslation> nameTranslations;

    @JsonProperty("pathItem")
    @JsonView(WebShop.class)
    @JsonInclude(Include.NON_NULL)
    private String pathItem;

    @JsonProperty("path")
    @JsonView(WebShop.class)
    @JsonInclude(Include.NON_NULL)
    private String path;

    @JsonProperty("pathItemHistory")
    @JsonInclude(Include.NON_NULL)
    @JsonView(WebShop.class)
    private List<String> pathItemHistory;

    @JsonView(Management.class)
    private Integer weigth;

    @JsonProperty("type")
    @JsonView(WebShop.class)
    private String type;

    @JsonProperty("productCategoryId")
    @JsonView(WebShop.class)
    private Integer productCategoryId;

    @JsonView(Management.class)
    private Timestamp categoryValidFrom;

    @JsonView(Management.class)
    private Timestamp categoryValidTo;

    @JsonProperty("children")
    @JsonInclude(Include.NON_NULL)
    @JsonView(WebShop.class)
    private List<CategoryNode> children = null;

    @JsonView(Management.class)
    private Integer aindex;

    @JsonProperty("contentType")
    @JsonInclude(Include.NON_NULL)
    @JsonView(WebShop.class)
    private String contentType;

    @JsonProperty("iconName")
    @JsonInclude(Include.NON_NULL)
    @JsonView(WebShop.class)
    private String iconName;

    @JsonView(WebShop.class)
    private String codeName;

    @JsonInclude(Include.NON_NULL)
    @JsonView(WebShop.class)
    private String metaDescription;

    @JsonIgnore
    private boolean temporary = false;

    public List<CategoryNode> getChildren() {
        if (this.children != null) {
            this.children.sort(new CategoryWeightComparator());
            createPathsForChildren();
        }
        return children;
    }

    public List<CategoryNode> getUnsortedChildren() {
        if(this.children != null) {
            children = Collections.unmodifiableList(this.children);
        }
        return children;
    }

    public void setChildren(List<CategoryNode> children) {
        this.children = children;
    }

    public Integer getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public Timestamp getCategoryValidFrom() {
        return categoryValidFrom;
    }

    public void setCategoryValidFrom(Timestamp categoryValidFrom) {
        this.categoryValidFrom = categoryValidFrom;
    }

    public Timestamp getCategoryValidTo() {
        return categoryValidTo;
    }

    public void setCategoryValidTo(Timestamp categoryValidTo) {
        this.categoryValidTo = categoryValidTo;
    }

    public void addChild(CategoryNode child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    public List<NameTranslation> getNameTranslations() {
        return nameTranslations;
    }

    public void setNameTranslations(List<NameTranslation> nameTranslations) {
        this.nameTranslations = nameTranslations;
    }

    public void addNameTranslation(NameTranslation nameTranslation) {
        if (this.nameTranslations == null) {
            this.nameTranslations = new ArrayList<>();
        }
        this.nameTranslations.add(nameTranslation);
    }

    public Integer getWeigth() {
        return weigth;
    }

    public void setWeigth(Integer weigth) {
        this.weigth = weigth;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTemporary() {
        return temporary;
    }

    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }

    public String getPathItem() {
        return pathItem;
    }

    public void setPathItem(String pathItem) {
        this.pathItem = pathItem;
    }

    public List<String> getPathItemHistory() {
        return pathItemHistory;
    }

    public void addPathItemHistory(String pathItem) {
        if (this.pathItemHistory == null) {
            this.pathItemHistory = new ArrayList<>();
        }
        this.pathItemHistory.add(pathItem);
    }

    public void setPathItemHistory(List<String> pathItemHistory) {
        this.pathItemHistory = pathItemHistory;
    }

    public Integer getAindex() {
        return aindex;
    }

    public void setAindex(Integer aindex) {
        this.aindex = aindex;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCodeName() {
        return codeName;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getPath() {
        if (this.path == null) {
            this.path = getPathItemOrEmptyIfNull();
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void createPathsForChildren() {
        for (CategoryNode child : children) {
            child.createPath(this.getPath());
        }
    }

    private void createPath(String parentPath) {
        if (this.pathItem == null) {
            this.path = (new StringBuilder()).append(parentPath).toString();
        } else if (parentPath.isEmpty()) {
            this.path = getPathItemOrEmptyIfNull();
        } else {
            this.path = (new StringBuilder()).append(parentPath)
                    .append(CategoryNode.FORWARD_SLASH)
                    .append(getPathItemOrEmptyIfNull()).toString();
        }

    }

    private String getPathItemOrEmptyIfNull() {
        String result = "";
        if (this.pathItem != null) {
            result = this.pathItem;
        }
        return result;
    }

}
