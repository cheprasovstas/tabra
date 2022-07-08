package com.showcase.tabra.data.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.UUID;

/**
 * Product
 */
public class Product {

    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("name")
    @Expose
    private String name;

    public UUID getCategory_id() {
        return category_id;
    }

    public void setCategory_id(UUID category_id) {
        this.category_id = category_id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @SerializedName("category")
    @Expose
    private UUID category_id;

    @SerializedName("category_name")
    @Expose
    private String categoryName;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("price")
    @Expose
    private Number price;

    public Currency getPrice_currency() {
        return price_currency;
    }

    public void setPrice_currency(Currency price_currency) {
        this.price_currency = price_currency;
    }

    @SerializedName("price_currency")
    @Expose(serialize = false)
    private Currency price_currency;

    @SerializedName("unit_price")
    @Expose
    private String unitPrice;

    @SerializedName("image")
    @Expose(serialize = false)
    private String image;
    private File f;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @SerializedName("active")
    @Expose
    private boolean active = true;

    @SerializedName("product_url")
    @Expose
    private String productUrl;

    public String getProductUrl() {
        return productUrl;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public Number getPrice() {
        return price;
    }

    public Product setPrice(Number price) {
        this.price = price;
        return this;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public String getImage() {
        return image;
    }

    public Product setImage(String image) {
        this.image = image;
        return this;
    }

    public void setF(File f) {
        this.f=f;
    }

    public File getF() {
        return f;
    }
}