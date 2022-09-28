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
@EntityProperty(name = "Car authorization settings",ctrl = StoreKeeper.class,type = "car-set",order = 3)
public class Authorization extends BaseModel {

    @FormProp(isRel = true)
    @ManyToOne
    public Car car;

    @FormProp(isCal = true)
    public Date lastAuthorization;

    @FormProp(isCal = true)
    public Date nextAuthorization;

    @FormProp(isUpload = true,tbl = true)
    public String document;

    public static Finder<Authorization> on = new Finder<>(Authorization.class);


    @FormProp(display = "Bus name(PLate No)",tblOnly = true)
    public String cName(){
        return car.plateNumber;
    }

    @FormProp(tblOnly = true,display = "Last authorization",order = 12,isHtml = true)
    @JsonProperty
    public String lVisit(){
        lastAuthorization = lastAuthorization != null ? lastAuthorization : new Date();
        return format(lastAuthorization);
    }

    @FormProp(tblOnly = true,display = "Next authorization",order = 13,isHtml = true)
    @JsonProperty
    public String nVisit(){
        return this.fVisit(nextAuthorization);
    }
}
