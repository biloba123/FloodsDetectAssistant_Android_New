package com.lvqingyang.floodsdetectassistant_android_new.bean;

import java.io.Serializable;

/**
 * 项目名称：FloodsDetectAssistant
 * 创建人：Double2号
 * 创建时间：2017.4.7 14:31
 * 修改备注：
 */
public class UploadInfo implements Serializable {

    /**
     * id : 76
     * upload_name : 黄家湖西路
     * upload_type : 公众
     * upload_resource : http://47.92.48.100:8099/urban/getImage?name=20171012161840.octet-stream
     * longitude : 0
     * latitude : 0
     * upload_address : 地点显示
     * upload_time : 1507796319770
     * upload_description : test
     * approval_status : 2
     */

    private int id;
    private String upload_name;
    private String upload_type;
    private String upload_resource;
    private double longitude;
    private double latitude;
    private String upload_address;
    private long upload_time;
    private String upload_description;
    private int approval_status;

    public void setId(int id) {
        this.id = id;
    }

    public void setUploadName(String upload_name) {
        this.upload_name = upload_name;
    }

    public void setUploadType(String upload_type) {
        this.upload_type = upload_type;
    }

    public void setUpload_resource(String upload_resource) {
        this.upload_resource = upload_resource;
    }


    public void setUploadAddress(String upload_address) {
        this.upload_address = upload_address;
    }

    public void setUploadTime(long upload_time) {
        this.upload_time = upload_time;
    }

    public void setUploadDescription(String upload_description) {
        this.upload_description = upload_description;
    }

    public void setApprovalStatus(int approval_status) {
        this.approval_status = approval_status;
    }

    public int getId() {
        return id;
    }

    public String getUploadName() {
        return upload_name;
    }

    public String getUploadType() {
        return upload_type;
    }

    public String getUploadResource() {
        return upload_resource;
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

    public String getUploadAddress() {
        return upload_address;
    }

    public long getUploadTime() {
        return upload_time;
    }

    public String getUploadDescription() {
        return upload_description;
    }

    public int getApprovalStatus() {
        return approval_status;
    }
}
