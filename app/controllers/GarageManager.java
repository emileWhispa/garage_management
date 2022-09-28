package controllers;

import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.GarageSecurity;
import io.ebean.Expr;
import io.ebean.Expression;
import models.BaseModel;
import models.Garage.OldSpare;
import models.Garage.OldSpareRequest;
import models.Garage.SpareRequest;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@Security.Authenticated(GarageSecurity.class)
public class GarageManager extends SuperBase {

    @Inject
    public GarageManager(FormFactory formFactory) {
        fFactory = formFactory;
    }

    public Result approveContent(){
        JsonNode nodeList = SpareRequest.on.setPageExp(Expr.eq("garageMApproved", false)).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.put("save",routes.GarageManager.submitApproval().url());
        return ok(node);
    }

    public Result submitOldApproval(){

        Http.MultipartFormData<Object> formData = request().body().asMultipartFormData();

        if( formData == null ) return ok();

        Map<String, String[]> map = formData.asFormUrlEncoded();

        if( map == null ) return ok();

        String[] items = map.get("item");

        Optional<String> comment = fFactory.form().bindFromRequest().field("comment").getValue();

        String cmt = comment.orElse("");

        if( items == null ) return ok();

        for (String s : items){
            Long l = Long.parseLong(s);

            OldSpare spare = OldSpare.on.obj(l);

            if( spare == null ) continue;

            spare.setGarageComment(cmt);

            spare.gApproved = true;
            spare.update();

        }

        return one;
    }


    public Result approveOld(){
        Expression expression = Expr.eq("gApproved",false);
        JsonNode nodeList = OldSpare.on.setPageExp(expression).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.putObject("garage");
        node.put("save",routes.GarageManager.submitOldApproval().url());
        return ok(node);
    }

    public static Result approve(String type){
        Http.MultipartFormData<Object> formData = request().body().asMultipartFormData();

        if( formData == null ) return ok();

        Map<String, String[]> map = formData.asFormUrlEncoded();

        if( map == null ) return ok();


        Set<String> keySet = map.keySet();

        String comment;

        Optional<String> value = fFactory.form().bindFromRequest().field("comment").getValue();

        comment = value.orElse("");

        List<SpareRequest> requestList = new ArrayList<>();
        double sum = 0.0;
        for (String string : keySet){
            String[] strings = map.get(string);
            if( isArrayNumeric(strings) && strings.length == 3 ){
                long l = Long.parseLong(strings[0]);
                SpareRequest request = SpareRequest.on.obj(l);

                double price = Double.parseDouble(strings[1]);
                int quantity = Integer.parseInt(strings[2]);

                if( request == null ) continue;

                request.quantity = quantity;
                request.price = price;

                switch (type){
                    case "garage":{
                        request.garageMApproved = true;
                        request.garageMComment = comment;
                        break;
                    }
                    case "transportMD":{
                        request.directorTransport = true;
                        request.directorComment = comment;
                        break;
                    }
                    case "finance":{
                        request.financeApproved = true;
                        request.financeComment = comment;
                        break;
                    }
                    case "procurement":{
                        request.procurementApproved = true;
                        request.procurementComment = comment;
                        request.procurementMode = (int)price == (int)request.spare.price;
                        break;
                    }
                }

                request.update();

                sum += request.finalTotal();

                requestList.add(request);
            }
        }

        ObjectNode node = Json.newObject();
        node.set("list",Json.toJson(requestList));
        ObjectNode object = Json.newObject();
        object.put("date",BaseModel.f(new Date()));
        node.set("object",object);
        node.put("sum",sum);

        return ok(node);
    }

    public Result submitApproval(){
        return approve("garage");
    }

}
