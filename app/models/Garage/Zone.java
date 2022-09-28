package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Application;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;

@Entity
@EntityProperty(name = "Zone settings",ctrl = Application.class,type = "g-req")
public class Zone extends BaseModel {

    @FormProp(tbl = true)
    public String name;

    @FormProp(tbl = true)
    public String route;

    @FormProp(tbl = true,display = "Symbol")
    public String zoneSymbol;

    @FormProp(tbl = true)
    public double target = 0.0;


    public static Finder<Zone> on = new Finder<>(Zone.class);


    @JsonProperty
    public String print(){
        return name + "("+target+")";
    }

}
