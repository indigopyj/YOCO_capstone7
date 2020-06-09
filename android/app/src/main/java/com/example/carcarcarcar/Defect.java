package com.example.carcarcarcar;

public class Defect {

    private String label;

    public void setLabel(String label) {
        this.label = label;
    }

    public void setTopx(String topx) {
        this.topx = topx;
    }

    public void setTopy(String topy) {
        this.topy = topy;
    }

    public void setBtmx(String btmx) {
        this.btmx = btmx;
    }

    public void setBtmy(String btmy) {
        this.btmy = btmy;
    }

    private String topx;
    private String topy;
    private String btmx;
    private String btmy;

    public String getLabel() {
        return label;
    }

    public String getTopx() {
        return topx;
    }

    public String getTopy() {
        return topy;
    }

    public String getBtmx() {
        return btmx;
    }

    public String getBtmy() {
        return btmy;
    }

    public Defect(String label, String topx, String topy, String btmx, String btmy ){
        this.label = label;
        this.topx = topx;
        this.topy = topy;
        this.btmx = btmx;
        this.btmy = btmy;
    }


}
