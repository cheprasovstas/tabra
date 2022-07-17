package com.showcase.tabra.ui.product;

import com.showcase.tabra.data.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ProductFormFilter {
    private String name = null;
    private Category category = null;

    public Boolean getAvailable() {
        return available;
    }

    private Boolean available = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setAvailable(Boolean b) {
        this.available = b;
    }
}
