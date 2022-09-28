package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import Helper.NoJsonReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.Application;
import controllers.StoreKeeper;
import io.ebean.annotation.Formula;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@EntityProperty(name = "Vehicle spare parts",type = "k-request",ctrl= StoreKeeper.class,hasReport = true,order = 4)
public class Spare extends BaseModel {

    @FormProp(display = "Vehicle brand",isRel = true)
    @ManyToOne
    public BrandCategory brand;

    @FormProp(tbl = true,display = "Spare name")
    public String spareName;


    @FormProp(type = "textarea",tbl = true,display = "Spare details",order = 4)
    public String spareDetail;

    @FormProp(display = "Default price",tbl = true)
    public double price = 0.0;


    @FormProp(tblOnly = true,display = "Quantity in")
    @Formula(select = "(SELECT IFNULL(COUNT(s.id),0) as value FROM stock s WHERE s.spare_id=${ta}.id)")
    public int stockCount;


    @FormProp(tblOnly = true,display = "Quantity out")
    @Formula(select = "(SELECT IFNULL(count(s.id),0) FROM stock s WHERE s.spare_id=${ta}.id and s.id in (SELECT o.stock_id FROM old_spare o WHERE o.approved=1))")
    public int quantityOut;

    @FormProp(tblOnly = true,display = "Total amount")
    @Formula(select = "(SELECT IFNULL(SUM(s.price),0) FROM stock s WHERE s.spare_id=${ta}.id)")
    public int totalValue;



    public static Finder<Spare> on = new Finder<>(Spare.class);

    @JsonProperty
    public String print(){
        return spareName;
    }

    @JsonProperty @NoJsonReport
    public String bName(){
        return brand != null ? brand.print() : "";
    }

    @JsonProperty @NoJsonReport
    public String route(){
        return Application.dRoute(id);
    }

    @JsonProperty
    public int balance(){
        return stockCount - quantityOut;
    }

    @FormProp(tblOnly = true,display = "Brand name")
    public String br(){
        return brand.print();
    }
}
