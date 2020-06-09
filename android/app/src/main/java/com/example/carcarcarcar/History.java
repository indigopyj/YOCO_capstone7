package com.example.carcarcarcar;

public class History {

    private String rent_id;
    private String car_id;
    private String rent_date;
    private String return_date;

    private Boolean returned;

    public Boolean getReturned() {
        return returned;
    }

    public void setReturned(Boolean returned) {
        this.returned = returned;
    }

    public void setRent_id(String rent_id) {
        this.rent_id = rent_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }

    public void setRent_date(String rent_date) {
        this.rent_date = rent_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }



    public String getRent_id(){
        return rent_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public String getRent_date() {
        return rent_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public History (String car_id, String rent_id,String rent_date, String return_date, Boolean returned ){
        this.car_id = car_id;
        this.rent_id = rent_id;
        this.rent_date = rent_date;
        this.return_date= return_date;
        this.returned = returned;
    }


}
