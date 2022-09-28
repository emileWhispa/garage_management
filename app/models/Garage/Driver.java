package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@EntityProperty(name = "Driver settings",type = "k-request",ctrl = StoreKeeper.class,order = 2)
public class Driver extends BaseModel {

    @FormProp(tbl = true)
    public String name;

    public String email;

    @FormProp(tbl = true,order = 1)
    public String phone;

    @FormProp(tbl = true,display = "ID Number",order = 3)
    public String idNumber;

    @FormProp(tbl = true,display = "Driving category",order = 4)
    public String category;

    @FormProp(isCal = true,display = "Appointment date")
    public Date appointmentDate;

    public static Finder<Driver> on = new Finder<>(Driver.class);

    @FormProp(tblOnly = true,display = "Appointment date",order = 5)
    public String aDate(){
        return format(appointmentDate);
    }

    @JsonProperty
    public String print(){
        return name;
    }

}
