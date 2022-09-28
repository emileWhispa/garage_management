package controllers;

import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.FinanceSecurity;
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
@Security.Authenticated(FinanceSecurity.class)
public class Finance extends SuperBase {

    @Inject
    public Finance(FormFactory formFactory) {
        fFactory = formFactory;
    }


    public Result approve(){
        Expression expression = Expr.and(Expr.eq("directorTransport", true),Expr.eq("financeApproved", false));
        JsonNode nodeList = SpareRequest.on.setPageExp(expression).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.putObject("finance");
        node.putObject("disabled");
        node.put("save",routes.Finance.submitApproval().url());
        return ok(node);
    }


    public Result submitApproval(){
        return GarageManager.approve("finance");
    }
}
