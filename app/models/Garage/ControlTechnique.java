package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityProperty(name = "Vehicle Technique Control",ctrl = StoreKeeper.class,type = "car-set",order = 1)
public class ControlTechnique extends BaseModel {

    @FormProp(isRel = true)
    @ManyToOne(cascade = CascadeType.ALL)
    public Car car;

    @FormProp(isCal = true)
    public Date lastVisit;

    @FormProp(isCal = true)
    private Date nextVisit;

    @FormProp(isUpload = true,tbl = true)
    public String document;

    public static Finder<ControlTechnique> on = new Finder<>(ControlTechnique.class);


    @JsonProperty
    @FormProp(display = "Vehicle name",tblOnly = true)
    public String print(){
        return car.plateNumber;
    }


    @FormProp(tblOnly = true,display = "Last visit",order = 12,isHtml = true)
    @JsonProperty
    public String lVisit(){
        lastVisit = lastVisit != null ? lastVisit : new Date();
        return format(lastVisit);
    }

    @FormProp(tblOnly = true,display = "Next visit",order = 13,isHtml = true)
    @JsonProperty
    public String nVisit(){
        return this.fVisit(nextVisit);
    }

}
