package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;

@Entity
@EntityProperty(name = "Mechanic settings",ctrl = StoreKeeper.class,type = "car-set")
public class Mechanic extends BaseModel {

    @FormProp(tbl = true)
    public String name;

    @FormProp(tbl = true)
    public String email;

    @FormProp(tbl = true)
    public String phone;


    public static Finder<Mechanic> on = new Finder<>(Mechanic.class);


    @JsonProperty
    public String print(){
        return name;
    }
}
