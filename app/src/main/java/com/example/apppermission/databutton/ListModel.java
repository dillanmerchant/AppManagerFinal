package com.example.apppermission.databutton;

import java.util.ArrayList;

public class ListModel {
    public String packageName;
    public ArrayList<String> personalPermissions;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public ArrayList<String> getPersonalPermissions() {
        return personalPermissions;
    }

    public void setPersonalPermissions(ArrayList<String> personalPermissions) {
        this.personalPermissions = personalPermissions;
    }
}
