package productapi.category.hierarchy.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import productapi.category.hierarchy.view.JsonView.WebShop;

public class NameTranslation {

    @JsonView(WebShop.class)
    private String displayName;
    @JsonView(WebShop.class)
    private String language;

    /**
     * No args constructor for use in serialization
     *
     */
    public NameTranslation() {}

    @JsonCreator
    public NameTranslation(@JsonProperty("displayName") String displayName, @JsonProperty("language") String language) {
        this.displayName = displayName;
        this.language = language;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLanguage() {
        return language;
    }
}
