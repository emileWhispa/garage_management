package models.Garage;

import Helper.Finder;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.BaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class OldSpare extends BaseModel {
    @OneToOne(cascade = CascadeType.ALL)
    public Stock stock;

    @ManyToOne(cascade = CascadeType.ALL)
    private Car car;

    @ManyToOne(cascade = CascadeType.ALL)
    private Mechanic mechanic;


    private String eMechanic;

    @OneToOne(cascade = CascadeType.ALL)
    private Stock newStock;

    @ManyToOne(cascade = CascadeType.ALL)
    private OldSpareRequest request;

    public boolean gApproved = false;

    public boolean approved = false;

    public static Finder<OldSpare> on = new Finder<>(OldSpare.class);

    @JsonProperty
    public String print(){
        return stock.print();
    }


    public void setRequest(OldSpareRequest request) {
        this.request = request;
    }

    public void setNewStock(Stock newStock) {
        this.newStock = newStock;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public boolean hasCar(){
        return  this.car != null;
    }

    public boolean hasNewStock(){
        return this.newStock != null;
    }

    public boolean hasMechanic(){
        return this.mechanic != null;
    }

    public void setStoreComment(String comment){
        if( this.hasRequest() ) this.request.storeComment = comment;
    }

    public void setGarageComment(String comment){
        if( this.hasRequest() ) this.request.gManagerComment = comment;
    }

    @JsonProperty
    public boolean hasRequest(){
        return this.request != null;
    }

    @JsonProperty
    public String gComment(){
        return hasRequest() ? request.gManagerComment : "--";
    }

    @JsonProperty
    public String fComment(){
        return hasRequest() ? request.foreManComment  : "--";
    }

    @JsonProperty
    public String sComment(){
        return hasRequest() ? request.storeComment  : "--";
    }

    public String serialN(){
        return hasNewStock() ? newStock.serialNumber : "--";
    }

    public String carNumber(){
        return hasCar() ? car.plateNumber : "--";
    }

    @JsonProperty
    public String mechanicBoy(){
        return hasMechanic() ? mechanic.name : "--";
    }

    public boolean canInsert(){
        return !on.exist("newStock.id",this.newStock.id);
    }

    public void seteMechanic(String eMechanic) {
        this.eMechanic = eMechanic;
    }
}
