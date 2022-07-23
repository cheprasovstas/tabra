package com.showcase.tabra.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigInteger;

public class Step {

    @SerializedName("id")
    @Expose
    private BigInteger id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("color")
    @Expose
    private String color;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

