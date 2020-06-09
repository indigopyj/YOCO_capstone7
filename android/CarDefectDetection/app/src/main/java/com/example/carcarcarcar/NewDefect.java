package com.example.carcarcarcar;

import org.json.JSONException;

import java.util.ArrayList;

public class NewDefect implements Comparable<NewDefect>{

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

    public NewDefect(String part, ArrayList<Defect> defectArrayList) {
        this.part = part;
        this.defectArrayList = defectArrayList;

    }
    @Override
    public int compareTo(NewDefect o) {
        if(this.part.compareTo(o.part) > 0){
            return 1;
        } else if(this.part.compareTo(o.part) < 0)
            return -1;
        return 0;
    }
}
