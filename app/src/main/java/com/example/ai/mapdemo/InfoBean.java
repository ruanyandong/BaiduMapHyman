package com.example.ai.mapdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 存贮添加的覆盖物的信息
 */
public class InfoBean implements Serializable{

    private double latitude;//纬度
    private double longitude;//经度

    //图片
    private int imgId;
    //商家名称
    private String name;
    //距商家的距离
    private String distance;

    //点赞数量
    private int zan;

    public static List<InfoBean> infoBeans=new ArrayList<InfoBean>();

    static {
        infoBeans.add(new InfoBean(29.826462,106.428859,
                R.drawable.john,
                "John",
                "距离10米",
                100));
        infoBeans.add(new InfoBean(29.826542,106.427859,
                R.drawable.teacher,
                "teacher",
                "距离100米",
                200));
        infoBeans.add(new InfoBean(29.826446,106.426859,
                R.drawable.gran,
                "gran",
                "距离80米",
                108));
        infoBeans.add(new InfoBean(29.826483,106.425859,
                R.drawable.ruth,
                "ruth",
                "距离200米",
                66));
        infoBeans.add(new InfoBean(29.826512,106.425559,
                R.drawable.stefan,
                "stefan",
                "距离60米",
                88));
        infoBeans.add(new InfoBean(29.826590,106.426059,
                R.drawable.turtle,
                "turtle",
                "距离30米",
                160));

    }

    public InfoBean(double latitude,
                    double longitude,
                    int imgId,
                    String name,
                    String distance,
                    int zan) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.zan = zan;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }


}
