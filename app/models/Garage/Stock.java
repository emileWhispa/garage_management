package models.Garage;

import Helper.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.Application;
import controllers.StoreKeeper;
import io.ebean.Expr;
import io.ebean.ExpressionList;
import io.ebean.annotation.Formula;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.Date;

@Entity
@EntityProperty(name = "Quantity in stock",type = "view-s",ctrl = Application.class,addNew = false,noDelete = true,hasReport = true)
public class Stock extends BaseModel {
    @FormProp(isRel = true)
    @ManyToOne
    public Spare spare;

    @FormProp(tblOnly = true,display = "Serial number",order = 1)
    public String serialNumber;

    @FormProp(tblOnly = true,display = "Supplier name",order = 2)
    public String supplierName;

    public double price = 0.0;

    private Date timeOut;


    @JsonBackReference
    @OneToOne(mappedBy = "stock")
    private OldSpare oldSpare;

    @JsonBackReference
    @OneToOne(mappedBy = "newStock")
    private OldSpare otherOld;


    public static Finder<Stock> on = new Finder<>(Stock.class);

    @JsonProperty
    public String print(){
        return spare.print() + " - " + serialNumber;
    }

    private boolean isReplaced(){
        return oldSpare != null && oldSpare.approved;
    }

    @FormProp(tblOnly = true,isHtml = true,order = 5,display = "Status")
    public String old(){
        if( isReplaced() ) this.warn();
        return this.badge(oldStatus());
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    @JsonProperty(value = "Status")
    public String oldStatus(){
        return isReplaced() ? "Replaced" : "New";
    }

    public boolean hasTimeOut(){
        return this.timeOut != null;
    }

    private static ExpressionList<Stock> availableQuery(){
        return on.query().isNull("otherOld");
    }

    @FormProp(tblOnly = true,order = 3,display = "Replaced serial number")
    @JsonProperty
    public String replacedSerialN(){
        return isReplaced() ? this.oldSpare.serialN() : "--";
    }

    @FormProp(tblOnly = true,order = 4,display = "Car plate number")
    @JsonProperty
    public String plateNumber(){
        return isReplaced() ? this.oldSpare.carNumber() : "--";
    }

    @JsonProperty
    public String mechanic(){
        return isReplaced() ? this.oldSpare.mechanicBoy() : "--";
    }


    public static JsonNode allAvailableNode(){
        return Stock.on.setExpLst(availableQuery()).nodeList();
    }

    @FormProp(display = "Spare part name",tblOnly = true)
    @JsonProperty
    public String spName(){
        return spare.print();
    }

    @JsonProperty(value = "Date/Time in")
    public String timeIn(){
        return fDate();
    }

    @JsonProperty(value = "Date/Time out")
    public String timeOut(){
        return hasTimeOut() ? format(timeOut) : "--";
    }

}
