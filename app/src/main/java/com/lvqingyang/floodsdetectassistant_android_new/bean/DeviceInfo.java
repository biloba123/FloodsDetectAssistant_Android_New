package com.lvqingyang.floodsdetectassistant_android_new.bean;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.4.15 9:02
 * 修改备注：
 */
public class DeviceInfo {

    /**
     * name : 设备1
     * danger_level : 1
     * position : 武汉科技大学黄家湖校区
     * longitude : 30.4013
     * latitude : 114.5031
     * description : 111123adawdaw
     */

    private String name;
    private int danger_level;
    private String position;
    private double longitude;
    private double latitude;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDangerLevel() {
        return danger_level;
    }

    public void setDangerLevel(int dangerLevel) {
        this.danger_level = dangerLevel;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
