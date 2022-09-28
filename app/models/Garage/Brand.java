package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;

@Entity
@EntityProperty(name = "Vehicles brand",ctrl = StoreKeeper.class,type = "k-request")
public class Brand extends BaseModel {

    @FormProp(display = "Brand name",tbl = true)
    public String brandName;

    @FormProp(type = "textarea",tbl = true)
    public String details;


    public static Finder<Brand> on = new Finder<>(Brand.class);

    @JsonProperty
    public String print(){
        return brandName;
    }

}
