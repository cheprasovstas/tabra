package com.showcase.tabra.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Showcase {


    @SerializedName("id")
    @Expose
    private UUID id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("contact_phone")
    @Expose
    private String contactPhone;

    @SerializedName("showcase_url")
    @Expose
    private String showcaseUrl;


    public String getShowcaseUrl() {
        return showcaseUrl;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactTelegram() {
        return contactTelegram;
    }

    public void setContactTelegram(String contactTelegram) {
        this.contactTelegram = contactTelegram;
    }

    @SerializedName("contact_telegram")
    @Expose
    private String contactTelegram;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
