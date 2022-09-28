package models.Garage;


import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.ChiefMechanic;
import models.BaseModel;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Constraint;

@Entity
@EntityProperty(name = "Old spare parts requests",type = "k-req",ctrl = ChiefMechanic.class)
public class OldSpareRequest extends BaseModel {

    @ManyToOne
    @FormProp(isRel = true)
    public Spare spare;

    public String serialNumber;

    @FormProp(tbl = true)
    public int quantity;

    public boolean chiefMechanicApproval = false;

    public boolean foreManApproval = false;
    public String foreManComment;

    public boolean gManagerApproval = false;
    public String gManagerComment;

    public boolean storeApproval = false;
    public String storeComment;


    public static Finder<OldSpareRequest> on = new Finder<>(OldSpareRequest.class);


    @JsonProperty
    @FormProp(tblOnly = true,display = "Old spare name")
    public String print(){
        return spare.print()+" - "+quantity;
    }

    @FormProp(tblOnly = true,display = "Fore man approval",isHtml = true)
    public String fApprove(){
        if( !foreManApproval ) this.warn();
        return this.badge(foreManApproval ? "Approved" : "not yet");
    }

    @FormProp(tblOnly = true,display = "Garage manager approval",isHtml = true)
    public String gApprove(){
        if ( !gManagerApproval ) this.warn();
        return this.badge(gManagerApproval ? "Approved" : "not yet");
    }

    @FormProp(tblOnly = true,display = "Store keeper approval",isHtml = true)
    public String sApprove(){
        if ( !storeApproval ) this.warn();
        return this.badge(storeApproval ? "Approved" : "not yet");
    }

}
