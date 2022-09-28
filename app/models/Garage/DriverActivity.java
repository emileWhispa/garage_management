package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import Helper.NoJsonReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Application;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@EntityProperty(name = "Driver performance",ctrl = Application.class,type = "g-req",order = 1)
public class DriverActivity extends BaseModel {

    @FormProp(isRel = true)
    @ManyToOne
    public Car car;

    @FormProp(isRel = true)
    @ManyToOne
    public Zone zone;

    @FormProp(tbl = true)
    public int number;

    @FormProp(isCal = true)
    public Date date;

    @FormProp(tbl = true,display = "Transport revenue")
    public double transportRevenue = 0.0;

    @FormProp(tbl = true)
    public double fuel = 0.0;


    public static Finder<DriverActivity> on = new Finder<>(DriverActivity.class);


    @FormProp(tblOnly = true) @JsonProperty
    public double netRevenue(){
        return transportRevenue - fuel;
    }

    @FormProp(tblOnly = true)
    @JsonProperty
    public String print(){
        return zone.print();
    }


    @FormProp(tblOnly = true,display = "Car name")
    @JsonProperty
    public String car(){
        return car.print();
    }

    @JsonProperty @NoJsonReport
    public String driver(){
        return car.dll();
    }

    @JsonProperty @NoJsonReport
    public String zoneName(){
        return zone.name;
    }

    @JsonProperty @NoJsonReport
    public String route(){
        return zone.route;
    }

    @JsonProperty @NoJsonReport
    public double target(){
        return zone.target;
    }


}
