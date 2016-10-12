package com.knowledge_seek.phyctogram.domain;

/**
 * Created by shj on 2016-02-16.
 */
public class Wifi {

    private String ssid;
    private String capabilities;

    public Wifi(String ssid, String capabilities) {
        this.ssid = ssid;
        this.capabilities = capabilities;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
    public String getSsid() {
        return ssid;
    }
    public String getCapabilities() {
        return capabilities;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "ssid='" + ssid + '\'' +
                ", capabilities='" + capabilities + '\'' +
                '}';
    }
}
