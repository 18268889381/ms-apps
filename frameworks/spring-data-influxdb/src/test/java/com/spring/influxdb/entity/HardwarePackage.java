package com.spring.influxdb.entity;


public class HardwarePackage extends PacketBase {
    private boolean algorithmTimeout;
    private boolean offlineCalc;
    private String endemicAreaId;

    public HardwarePackage() {
    }

    public boolean getAlgorithmTimeout() {
        return this.algorithmTimeout;
    }

    public void setAlgorithmTimeout(boolean algorithmTimeout) {
        this.algorithmTimeout = algorithmTimeout;
    }

    public boolean getOfflineCalc() {
        return this.offlineCalc;
    }

    public void setOfflineCalc(boolean offlineCalc) {
        this.offlineCalc = offlineCalc;
    }

    public String getEndemicAreaId() {
        return this.endemicAreaId;
    }

    public void setEndemicAreaId(String endemicAreaId) {
        this.endemicAreaId = endemicAreaId;
    }
}