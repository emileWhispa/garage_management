package models.Garage;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import Helper.NoJsonReport;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ArrayNode;
import controllers.StoreKeeper;
import models.BaseModel;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@EntityProperty(name = "Request new spare parts",ctrl = StoreKeeper.class,type = "k-request",order = 5)
public class SpareRequest extends BaseModel {

    @FormProp(isRel = true)
    @ManyToOne
    public Spare spare;

    @FormProp(tbl = true)
    public int quantity = 0;

    public boolean procurementMode;

    public double price = 0.0;

    public boolean garageMApproved = false;
    public String garageMComment;

    public boolean directorTransport = false;
    public String directorComment;

    public boolean procurementApproved = false;
    public String procurementComment;

    public boolean financeApproved = false;
    public String financeComment;

    public boolean storeApproved = false;


    public static Finder<SpareRequest> on = new Finder<>(SpareRequest.class);

    @FormProp(tblOnly = true,display = "Requested spare part")
    public String sp(){
        return spare.spareName;
    }

    @JsonProperty
    @NoJsonReport
    public String spareName(){
        return sp();
    }

    @JsonProperty
    public double total(){
        return price * quantity;
    }

    @JsonProperty
    public double vat(){
        return total() * 18 / 100;
    }

    @JsonProperty
    public double finalTotal(){
        return total() + vat();
    }

    @FormProp(tblOnly = true,order = 2,display = "Garage manager approved",isHtml = true)
    public String garageApp(){
        if( !garageMApproved ) this.warn();
        return this.badge(garageMApproved ? "Approved" : "Not yet");
    }

    @FormProp(tblOnly = true,order = 3,display = "MD approved",isHtml = true)
    public String directorApp(){
        if ( !directorTransport ) this.warn();
        return this.badge(directorTransport ? "Approved" : "Not yet");
    }

    @FormProp(tblOnly = true,order = 4,display = "Procurement approved",isHtml = true)
    public String procurementApprove(){
        if( !procurementApproved ) this.warn();
        return this.badge(procurementApproved ? "Approved" : "Not yet");
    }

    @FormProp(tblOnly = true,order = 4,display = "CFO approved",isHtml = true)
    public String finApprove(){
        if( !financeApproved ) this.warn();
        return this.badge(financeApproved ? "Approved" : "Not yet");
    }

    //@FormProp(isNode = true,display = "Choose procurement mode")
    public static ArrayNode node(){
        ArrayNode arrayNode = Json.newArray();
        arrayNode.add(on.createEl("text",true,"On contract"));
        arrayNode.add(on.createEl("text",false,"Not contract"));
        return arrayNode;
    }

    @JsonProperty
    public String print(){
        return sp() + " - "+quantity + " - "+fDate();
    }

}
