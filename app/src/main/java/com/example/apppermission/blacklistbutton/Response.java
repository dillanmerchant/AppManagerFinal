package com.example.apppermission.blacklistbutton;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @SerializedName("appId")
    @Expose
    private Integer appId;
    @SerializedName("appName")
    @Expose
    private String appName;
    @SerializedName("packageName")
    @Expose
    private String packageName;
    @SerializedName("createdDate")
    @Expose
    private double createdDate;
    @SerializedName("updatedDate")
    @Expose
    private Object updatedDate;

    public Integer getAppId() {
        return appId;
    }

    public void setAppId(Integer appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Double getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(double createdDate) {
        this.createdDate = createdDate;
    }

    public Object getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Object updatedDate) {
        this.updatedDate = updatedDate;
    }

}
