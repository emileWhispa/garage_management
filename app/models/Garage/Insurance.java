package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityProperty(name = "Car insurance settings",ctrl = StoreKeeper.class,type = "car-set",order = 2)
public class Insurance extends BaseModel {
    @FormProp(isRel = true)
    @ManyToOne
    public Car car;

    @FormProp(isUpload = true,tbl = true)
    public String document;

    @FormProp(tbl = true,display = "Insurance company",order = 1)
    public String company;

    @FormProp(isCal = true)
    public Date lastVisit;

    @FormProp(isCal = true)
    public Date nextVisit;

    @FormProp(isNaN = false,tbl = true,display = "Insurance cost",order = 6)
    public double cost;

    @FormProp(type = "textarea",tbl = true,order = 4)
    public String accidentType;

    @FormProp(isCal = true)
    public Date accidentDate;

    public static Finder<Insurance> on = new Finder<>(Insurance.class);


    @FormProp(display = "Bus name(PLate No)",tblOnly = true)
    public String cName(){
        return car.plateNumber;
    }

    @FormProp(display = "Accident date",tblOnly = true,order = 5)
    public String acDate(){
        return accidentDate == null ? "-" : format(accidentDate);
    }

    @FormProp(tblOnly = true,display = "Last visit",order = 2,isHtml = true)
    @JsonProperty
    public String lVisit(){
        lastVisit = lastVisit != null ? lastVisit : new Date();
        return format(lastVisit);
    }

    @FormProp(tblOnly = true,display = "Next visit",order = 3,isHtml = true)
    @JsonProperty
    public String nVisit(){
        return this.fVisit(nextVisit);
    }
}

