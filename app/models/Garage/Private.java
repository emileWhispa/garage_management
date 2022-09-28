package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@EntityProperty(name = "Vehicle owners",ctrl = StoreKeeper.class,type = "k-request",order = 1)
public class Private extends BaseModel {

    @FormProp(tbl = true)
    public String name;

    @FormProp(tbl = true)
    public String address;

    @FormProp(tbl = true)
    public String phone;

    public static Finder<Private> on = new Finder<>(Private.class);

    @JsonProperty
    public String print(){
        return name;
    }
}
