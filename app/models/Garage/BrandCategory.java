package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.StoreKeeper;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@EntityProperty(name = "Brand categories",ctrl = StoreKeeper.class,type = "k-request",order = 1)
public class BrandCategory extends BaseModel {
    @FormProp(isRel = true,display = "Choose brand")
    @ManyToOne
    public Brand brand;

    @FormProp(display = "Category name",tbl = true)
    public String name;

    public static Finder<BrandCategory> on = new Finder<>(BrandCategory.class);

    @FormProp(tblOnly = true,display = "Brand name")
    public String br(){
        return brand.brandName;
    }

    @JsonProperty
    public String print(){
        return brand.brandName + " - " + this.name;
    }
}
