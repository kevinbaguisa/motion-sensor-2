package com.kevinbaguisa.motionsensor2.Model;

import com.google.gson.annotations.SerializedName;

public class AccelerometerModel {

    @SerializedName("averageX")
    private String averageX;
    @SerializedName("averageY")
    private String averageY;
    @SerializedName("averageZ")
    private String averageZ;

    public AccelerometerModel(){

    }

    public String getAverageX() {
        return averageX;
    }

    public void setAverageX(String averageX) {
        this.averageX = averageX;
    }

    public String getAverageY() {
        return averageY;
    }

    public void setAverageY(String averageY) {
        this.averageY = averageY;
    }

    public String getAverageZ() {
        return averageZ;
    }

    public void setAverageZ(String averageZ) {
        this.averageZ = averageZ;
    }
}
