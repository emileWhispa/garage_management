package controllers;

import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.TransportMDSecurity;
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
@Security.Authenticated(TransportMDSecurity.class)
public class TransportMD extends SuperBase {

    @Inject
    public TransportMD(FormFactory formFactory){
        fFactory = formFactory;
    }

    public Result approve(){
        Expression expression = Expr.and(Expr.eq("garageMApproved", true),Expr.eq("directorTransport", false));
        JsonNode nodeList = SpareRequest.on.setPageExp(expression).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.putObject("transport");
        node.put("save",routes.TransportMD.submitApproval().url());
        return ok(node);
    }

    public Result submitApproval(){
        return GarageManager.approve("transportMD");
    }
}
