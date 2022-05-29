package com.showcase.tabra.data.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.io.File;
import java.io.IOException;
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

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("price")
    @Expose
    private Number price;

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
    private boolean active;
    private boolean isSelected = false;

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

//    public Product() {
//
//    }

//    public Product(UUID id, String name, String description, Number price, String image) {
//        this.id = id;
//        this.name = name;
//        this.description = description;
//        this.price = price;
//        this.image = image;
//    }


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