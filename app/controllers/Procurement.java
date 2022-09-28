package controllers;

import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.ProcurementSecurity;
import io.ebean.Expr;
import io.ebean.Expression;
import models.Garage.SpareRequest;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Security.Authenticated(ProcurementSecurity.class)
public class Procurement extends SuperBase {

    @Inject
    public Procurement(FormFactory formFactory){
        fFactory = formFactory;
    }

    public Result approve(){
        Expression expression = Expr.and(Expr.eq("financeApproved", true),Expr.eq("procurementApproved", false));
        JsonNode nodeList = SpareRequest.on.setPageExp(expression).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.putObject("procurement");
        node.put("save",routes.Procurement.submitApproval().url());
        return ok(node);
    }


    public Result submitApproval(){
       return GarageManager.approve("procurement");
    }
}
