package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;
import org.joda.time.Days;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityProperty(name = "Car settings",ctrl = StoreKeeper.class,type = "car-set")
public class Car extends BaseModel {
    @FormProp(isRel = true)
    @ManyToOne
    public BrandCategory brand;

    @FormProp(isRel = true,order = 1)
    @ManyToOne
    public Private owner;

    @FormProp(isRel = true,order = 2)
    @ManyToOne
    private Driver driver;

    @FormProp(isRel = true,order = 3,display = "Second driver")
    @ManyToOne
    private Driver other;

    //@FormProp(tbl = true,display = "Car name/Brand",order = 4,Aa = true)
    public String carName;

    @FormProp(tbl = true,display = "Plate number",order = 5)
    public String plateNumber;

    @FormProp(isUpload = true,tbl = true,display = "Carte jaune",order = 6)
    public String yellowCard;

    @FormProp(type = "textarea",order = 11)
    public String detail;


    public static Finder<Car> on = new Finder<>(Car.class);

    @JsonProperty
    public String print(){
        return brand.name + " - "+plateNumber;
    }

    @FormProp(tblOnly = true,display = "Driver A")
    public String dll(){
        return driver != null ? driver.name : "--";
    }

    @FormProp(tblOnly = true,display = "Driver B")
    public String d2(){
        return other != null ? other.name : "--";
    }

}
