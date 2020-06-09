package com.example.carcarcarcar;
import org.json.JSONException;
import java.util.ArrayList;

public class Predictions implements Comparable<NewDefect>{

    private  String part;
    private ArrayList<Defect> defectArrayList;

    public void setPart(String part) {
        this.part = part;
    }


    public String getPart() {
        return part;
    }

    public void setDefectArrayList(ArrayList<Defect> defectArrayList) throws JSONException {
        this.defectArrayList = defectArrayList;
    }
    public ArrayList<Defect> getDefectArrayList(){
        return defectArrayList;
    }

    public Predictions(String part, ArrayList<Defect> defectArrayList) {
        this.part = part;
        this.defectArrayList = defectArrayList;

    }

    @Override
    public int compareTo(NewDefect o) {
        return 0;
    }
}