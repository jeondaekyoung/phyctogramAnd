package com.knowledge_seek.phyctogram.domain;

/**
 * Created by shj on 2016-02-16.
 */
public class Wifi {

    private String ssid;
    private String capabilities;
    private String signal;

    public Wifi(String ssid, String capabilities,String signal) {
        this.ssid = ssid;
        this.capabilities = capabilities;
        this.signal = signal;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
    public void setSignal(String signal) {
        this.signal = signal;
    }
    public String getSsid() {
        return ssid;
    }
    public String getCapabilities() {
        return capabilities;
    }
    public String getSignal() {return signal; }

    @Override
    public String toString() {
        return "Wifi{" +
                "ssid='" + ssid + '\'' +
                ", capabilities='" + capabilities + '\'' +
                "signal='" + signal + '\'' +
                '}';
    }
}
