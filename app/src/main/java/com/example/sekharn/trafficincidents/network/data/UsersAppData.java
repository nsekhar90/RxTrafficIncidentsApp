package com.example.sekharn.trafficincidents.network.data;

public class UsersAppData implements Comparable<Object> {

    private long lastUpdateTime;
    private String appName;
    private String appIcon;

    public UsersAppData(String name, String icon, long lastUpdateTime) {
        appName = name;
        appIcon = icon;
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public int compareTo(Object another) {
        UsersAppData f = (UsersAppData) another;
        return getAppName().compareTo(f.getAppName());
    }

    public String getAppName() {
        return appName;
    }

    public String getAppIcon() {
        return appIcon;
    }
}