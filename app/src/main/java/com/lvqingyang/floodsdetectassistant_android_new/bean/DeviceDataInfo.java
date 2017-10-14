package com.lvqingyang.floodsdetectassistant_android_new.bean;

import java.io.Serializable;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.4.7 13:53
 * 修改备注：
 */
public class DeviceDataInfo implements Serializable {

    /**
     "id": 21808,
     "rain_time": "2017-04-15T16:35:10",
     "rain_fall": "-1",
     "rain_level": 0,
     "water_speed": 0,
     "water_level": 0.04,
     "wind_speed": 0,
     "gas_warn": "1",
     "general_level": -1
     */

    private int id;
    private String rainTime;
    private double rainFall;
    private double rainLevel;
    private double waterSpeed;
    private double waterLevel;
    private double windSpeed;
    private String gasWarn;
    private double generalLevel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRainTime() {
        return rainTime;
    }

    public void setRainTime(String rainTime) {
        this.rainTime = rainTime;
    }

    public double getRainFall() {
        return rainFall;
    }

    public void setRainFall(double rainFall) {
        this.rainFall = rainFall;
    }

    public double getRainLevel() {
        return rainLevel;
    }

    public void setRainLevel(double rainLevel) {
        this.rainLevel = rainLevel;
    }

    public double getWaterSpeed() {
        return waterSpeed;
    }

    public void setWaterSpeed(double waterSpeed) {
        this.waterSpeed = waterSpeed;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getGasWarn() {
        return gasWarn;
    }

    public void setGasWarn(String gasWarn) {
        this.gasWarn = gasWarn;
    }

    public double getGeneralLevel() {
        return generalLevel;
    }

    public void setGeneralLevel(double generalLevel) {
        this.generalLevel = generalLevel;
    }

}
