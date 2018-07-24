package com.spring.influxdb.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PacketBase implements Serializable {
    private static final long serialVersionUID = 7247714666080610000L;
    private String deviceId;
    private String ip;
    private long dateTime;
    private String dateString;
    private String dateString2;
    private int packageSn;
    private short group;
    private long serverDate = (new Date()).getTime();
    private short heartRate;
    private boolean ecgNoise;
    private short respRate;
    private short plusRate;
    private String temperatureTime;
    private short temperature;
    private byte spo2;
    private byte ecgLeadState;
    private byte spo2LeadSate;
    private byte temperatureConnState;
    private byte bloodPressureConnState;
    private byte thermometerLostLowSwitch;
    private byte batteryLowLanpSwitch;
    private byte batteryLowShockSwitch;
    private byte thermometerLowLanpSwitch;
    private byte thermometerSwitch;
    private byte sop2Switch;
    private byte bloodSwitch;
    private byte spo2ConnState;
    private byte spo2Signal;
    private short battery;
    private short temperatureBattery;
    private short spo2Battery;
    private short outBattery;
    private short bloodPressureBattery;
    private short wifiSignal;
    private boolean realTime = true;
    private String gesture;
    private short orderNum;
    private List<Short> ecgList;
    private List<Short> xList;
    private List<Short> yList;
    private List<Short> zList;
    private List<Integer> respList;
    private List<Integer> abdominalList;
    private List<Integer> oldRespList;
    private List<Integer> oldAbdominalRespList;
    private List<Short> spo2List;
    private String userId;
    private Short step;
    private Double energy;
    private byte fall;
    private byte heartRateAlarm = 0;
    private byte respRateAlarm = 0;
    private byte plusRateAlarm = 0;
    private byte temperatureAlarm = 0;
    private byte spo2Alarm = 0;
    private byte outBatteryAlarm;
    private byte bluetoothTemperatureBatteryAlarm;
    private byte bluetoothSpoBatteryAlarm;
    private byte bluetoothSphygmomanometerBatteryAlarm;
    private String apMac;

    public PacketBase() {
    }

    public static long getSerialVersionUID() {
        return 7247714666080610000L;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getDateTime() {
        return this.dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateString() {
        return this.dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getDateString2() {
        return this.dateString2;
    }

    public void setDateString2(String dateString2) {
        this.dateString2 = dateString2;
    }

    public int getPackageSn() {
        return this.packageSn;
    }

    public void setPackageSn(int packageSn) {
        this.packageSn = packageSn;
    }

    public short getGroup() {
        return this.group;
    }

    public void setGroup(short group) {
        this.group = group;
    }

    public long getServerDate() {
        return this.serverDate;
    }

    public void setServerDate(long serverDate) {
        this.serverDate = serverDate;
    }

    public short getHeartRate() {
        return this.heartRate;
    }

    public void setHeartRate(short heartRate) {
        this.heartRate = heartRate;
    }

    public short getRespRate() {
        return this.respRate;
    }

    public void setRespRate(short respRate) {
        this.respRate = respRate;
    }

    public short getPlusRate() {
        return this.plusRate;
    }

    public void setPlusRate(short plusRate) {
        this.plusRate = plusRate;
    }

    public String getTemperatureTime() {
        return this.temperatureTime;
    }

    public void setTemperatureTime(String temperatureTime) {
        this.temperatureTime = temperatureTime;
    }

    public short getTemperature() {
        return this.temperature;
    }

    public void setTemperature(short temperature) {
        this.temperature = temperature;
    }

    public byte getSpo2() {
        return this.spo2;
    }

    public void setSpo2(byte spo2) {
        this.spo2 = spo2;
    }

    public byte getEcgLeadState() {
        return this.ecgLeadState;
    }

    public void setEcgLeadState(byte ecgLeadState) {
        this.ecgLeadState = ecgLeadState;
    }

    public byte getSpo2LeadSate() {
        return this.spo2LeadSate;
    }

    public void setSpo2LeadSate(byte spo2LeadSate) {
        this.spo2LeadSate = spo2LeadSate;
    }

    public byte getTemperatureConnState() {
        return this.temperatureConnState;
    }

    public void setTemperatureConnState(byte temperatureConnState) {
        this.temperatureConnState = temperatureConnState;
    }

    public byte getBloodPressureConnState() {
        return this.bloodPressureConnState;
    }

    public void setBloodPressureConnState(byte bloodPressureConnState) {
        this.bloodPressureConnState = bloodPressureConnState;
    }

    public byte getThermometerLostLowSwitch() {
        return this.thermometerLostLowSwitch;
    }

    public void setThermometerLostLowSwitch(byte thermometerLostLowSwitch) {
        this.thermometerLostLowSwitch = thermometerLostLowSwitch;
    }

    public byte getBatteryLowLanpSwitch() {
        return this.batteryLowLanpSwitch;
    }

    public void setBatteryLowLanpSwitch(byte batteryLowLanpSwitch) {
        this.batteryLowLanpSwitch = batteryLowLanpSwitch;
    }

    public byte getBatteryLowShockSwitch() {
        return this.batteryLowShockSwitch;
    }

    public void setBatteryLowShockSwitch(byte batteryLowShockSwitch) {
        this.batteryLowShockSwitch = batteryLowShockSwitch;
    }

    public byte getThermometerLowLanpSwitch() {
        return this.thermometerLowLanpSwitch;
    }

    public void setThermometerLowLanpSwitch(byte thermometerLowLanpSwitch) {
        this.thermometerLowLanpSwitch = thermometerLowLanpSwitch;
    }

    public byte getThermometerSwitch() {
        return this.thermometerSwitch;
    }

    public void setThermometerSwitch(byte thermometerSwitch) {
        this.thermometerSwitch = thermometerSwitch;
    }

    public byte getSop2Switch() {
        return this.sop2Switch;
    }

    public void setSop2Switch(byte sop2Switch) {
        this.sop2Switch = sop2Switch;
    }

    public byte getBloodSwitch() {
        return this.bloodSwitch;
    }

    public void setBloodSwitch(byte bloodSwitch) {
        this.bloodSwitch = bloodSwitch;
    }

    public byte getSpo2ConnState() {
        return this.spo2ConnState;
    }

    public void setSpo2ConnState(byte spo2ConnState) {
        this.spo2ConnState = spo2ConnState;
    }

    public byte getSpo2Signal() {
        return this.spo2Signal;
    }

    public void setSpo2Signal(byte spo2Signal) {
        this.spo2Signal = spo2Signal;
    }

    public short getBattery() {
        return this.battery;
    }

    public void setBattery(short battery) {
        this.battery = battery;
    }

    public short getTemperatureBattery() {
        return this.temperatureBattery;
    }

    public void setTemperatureBattery(short temperatureBattery) {
        this.temperatureBattery = temperatureBattery;
    }

    public short getSpo2Battery() {
        return this.spo2Battery;
    }

    public void setSpo2Battery(short spo2Battery) {
        this.spo2Battery = spo2Battery;
    }

    public short getOutBattery() {
        return this.outBattery;
    }

    public void setOutBattery(short outBattery) {
        this.outBattery = outBattery;
    }

    public short getBloodPressureBattery() {
        return this.bloodPressureBattery;
    }

    public void setBloodPressureBattery(short bloodPressureBattery) {
        this.bloodPressureBattery = bloodPressureBattery;
    }

    public short getWifiSignal() {
        return this.wifiSignal;
    }

    public void setWifiSignal(short wifiSignal) {
        this.wifiSignal = wifiSignal;
    }

    public boolean isRealTime() {
        return this.realTime;
    }

    public void setRealTime(boolean realTime) {
        this.realTime = realTime;
    }

    public String getGesture() {
        return this.gesture;
    }

    public void setGesture(String gesture) {
        this.gesture = gesture;
    }

    public short getOrderNum() {
        return this.orderNum;
    }

    public void setOrderNum(short orderNum) {
        this.orderNum = orderNum;
    }

    public List<Short> getEcgList() {
        return this.ecgList;
    }

    public void setEcgList(List<Short> ecgList) {
        this.ecgList = ecgList;
    }

    public List<Short> getxList() {
        return this.xList;
    }

    public void setxList(List<Short> xList) {
        this.xList = xList;
    }

    public List<Short> getyList() {
        return this.yList;
    }

    public void setyList(List<Short> yList) {
        this.yList = yList;
    }

    public List<Short> getzList() {
        return this.zList;
    }

    public void setzList(List<Short> zList) {
        this.zList = zList;
    }

    public List<Integer> getRespList() {
        return this.respList;
    }

    public void setRespList(List<Integer> respList) {
        this.respList = respList;
    }

    public List<Integer> getAbdominalList() {
        return this.abdominalList;
    }

    public void setAbdominalList(List<Integer> abdominalList) {
        this.abdominalList = abdominalList;
    }

    public List<Integer> getOldRespList() {
        return this.oldRespList;
    }

    public void setOldRespList(List<Integer> oldRespList) {
        this.oldRespList = oldRespList;
    }

    public List<Integer> getOldAbdominalRespList() {
        return this.oldAbdominalRespList;
    }

    public void setOldAbdominalRespList(List<Integer> oldAbdominalRespList) {
        this.oldAbdominalRespList = oldAbdominalRespList;
    }

    public List<Short> getSpo2List() {
        return this.spo2List;
    }

    public void setSpo2List(List<Short> spo2List) {
        this.spo2List = spo2List;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Short getStep() {
        return this.step;
    }

    public void setStep(Short step) {
        this.step = step;
    }

    public Double getEnergy() {
        return this.energy;
    }

    public void setEnergy(Double energy) {
        this.energy = energy;
    }

    public byte getFall() {
        return this.fall;
    }

    public void setFall(byte fall) {
        this.fall = fall;
    }

    public byte getHeartRateAlarm() {
        return this.heartRateAlarm;
    }

    public void setHeartRateAlarm(byte heartRateAlarm) {
        this.heartRateAlarm = heartRateAlarm;
    }

    public byte getRespRateAlarm() {
        return this.respRateAlarm;
    }

    public void setRespRateAlarm(byte respRateAlarm) {
        this.respRateAlarm = respRateAlarm;
    }

    public byte getPlusRateAlarm() {
        return this.plusRateAlarm;
    }

    public void setPlusRateAlarm(byte plusRateAlarm) {
        this.plusRateAlarm = plusRateAlarm;
    }

    public byte getTemperatureAlarm() {
        return this.temperatureAlarm;
    }

    public void setTemperatureAlarm(byte temperatureAlarm) {
        this.temperatureAlarm = temperatureAlarm;
    }

    public byte getSpo2Alarm() {
        return this.spo2Alarm;
    }

    public void setSpo2Alarm(byte spo2Alarm) {
        this.spo2Alarm = spo2Alarm;
    }

    public byte getOutBatteryAlarm() {
        return this.outBatteryAlarm;
    }

    public void setOutBatteryAlarm(byte outBatteryAlarm) {
        this.outBatteryAlarm = outBatteryAlarm;
    }

    public byte getBluetoothTemperatureBatteryAlarm() {
        return this.bluetoothTemperatureBatteryAlarm;
    }

    public void setBluetoothTemperatureBatteryAlarm(byte bluetoothTemperatureBatteryAlarm) {
        this.bluetoothTemperatureBatteryAlarm = bluetoothTemperatureBatteryAlarm;
    }

    public byte getBluetoothSpoBatteryAlarm() {
        return this.bluetoothSpoBatteryAlarm;
    }

    public void setBluetoothSpoBatteryAlarm(byte bluetoothSpoBatteryAlarm) {
        this.bluetoothSpoBatteryAlarm = bluetoothSpoBatteryAlarm;
    }

    public byte getBluetoothSphygmomanometerBatteryAlarm() {
        return this.bluetoothSphygmomanometerBatteryAlarm;
    }

    public void setBluetoothSphygmomanometerBatteryAlarm(byte bluetoothSphygmomanometerBatteryAlarm) {
        this.bluetoothSphygmomanometerBatteryAlarm = bluetoothSphygmomanometerBatteryAlarm;
    }

    public String getApMac() {
        return this.apMac;
    }

    public void setApMac(String apMac) {
        this.apMac = apMac;
    }

    public boolean getEcgNoise() {
        return this.ecgNoise;
    }

    public void setEcgNoise(boolean ecgNoise) {
        this.ecgNoise = ecgNoise;
    }
}

