package models.Garage;

import Helper.EntityProperty;
import Helper.Exist;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import io.ebean.annotation.Formula;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@EntityProperty(name = "Budget settings",ctrl = StoreKeeper.class,type = "car-set")
public class Budget extends BaseModel {

    @FormProp
    @ManyToOne
    public Spare spare;

    @FormProp(tbl = true,display = "Budget amount")
    public double amount;

    @FormProp(tbl = true,str = true)
    public int year;

    @FormProp(tblOnly = true,display = "Total amount")
    @Formula(select = "(SELECT IFNULL(SUM(s.price),0) FROM stock s WHERE s.spare_id=${ta}.spare_id and ${ta}.year=year(s.date))")
    public int totalValue;

    public static Finder<Budget> on = new Finder<>(Budget.class);

    @FormProp(tblOnly = true,display = "Remaining",order = 5)
    public double getBalance(){
        return amount - totalValue;
    }

    @Exist
    public boolean exist(Long id){
        return on.existList("year",this.year)
                .existList("spare.id",spare.id).executeExist(id);
    }

    @JsonProperty
    public String spName(){
        return spare != null ? spare.spareName : "";
    }
}
