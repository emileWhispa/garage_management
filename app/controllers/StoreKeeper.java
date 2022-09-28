package controllers;

import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.StoreSecurity;
import io.ebean.Expr;
import io.ebean.Expression;
import models.Garage.*;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
@Security.Authenticated(StoreSecurity.class)
public class StoreKeeper extends SuperBase {

    @Inject
    public StoreKeeper(FormFactory formFactory) {
        fFactory = formFactory;
    }

    public Result addToStock(){
        Expression expression = Expr.and(Expr.eq("procurementApproved", true),Expr.eq("storeApproved", false));
        JsonNode nodeList = SpareRequest.on.setPageExp(expression).nodeList();
        JsonNode nodeList2 = Mechanic.on.nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.putObject("stock");
        node.put("save",routes.StoreKeeper.setAdded().url());
        return ok(node);
    }

    public Result setAdded(){
        Http.MultipartFormData<Object> formData = request().body().asMultipartFormData();

        if( formData == null ) return one;

        Map<String, String[]> map = formData.asFormUrlEncoded();

        if( map == null ) return one;


        String[] stringSet = map.get("item");

        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> value = form.field("request").getValue();

        Optional<String> supplier = form.field("supplier").getValue();

        if( !value.isPresent() || !supplier.isPresent() ) return ok("Not enough system arguments");

        String s = value.get();

        if( !isNumeric(s) ) return one;

        Long reqId = Long.parseLong(s);

        SpareRequest request = SpareRequest.on.obj(reqId);

        if( request == null ) return one;


        for (String string : stringSet){



            Stock stock = new Stock();

            stock.spare = request.spare;
            stock.serialNumber = string;
            stock.supplierName = supplier.get();
            stock.price = request.price;

            if( Stock.on.exist("serialNumber",string) ) continue;

            stock.save();

        }


        request.storeApproved = true;
        request.update();

        return one;
    }


    public Result submitOldApproval(){

        Http.MultipartFormData<Object> formData = request().body().asMultipartFormData();

        if( formData == null ) return ok();

        Map<String, String[]> stringMap = formData.asFormUrlEncoded();

        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> comment = form.field("comment").getValue();

        String c = comment.orElse("");

        if( stringMap == null ) return ok();

        Set<String> stringSet = stringMap.keySet();


        for (String s : stringSet ){
            String[] strings = stringMap.get(s);
            if( isArrayNumeric(strings) && strings.length == 3 ){

                Long l = Long.parseLong(strings[0]);
                Long carId = Long.parseLong(strings[1]);
                Long newId = Long.parseLong(strings[2]);

                Car car = Car.on.obj(carId);
                Stock newStock = Stock.on.obj(newId);

                OldSpare spare = OldSpare.on.obj(l);

                if( spare == null || car == null || newStock == null  ) {
                 System.out.println("vn home");
                    continue;
                }

                newStock.setTimeOut(new Date());
                spare.setCar(car);
                spare.setNewStock(newStock);

                String[] arr = stringMap.get("m" + s);
                if( arr.length > 0 ){
                    String v = arr[0];
                    if( isNumeric(v) ){
                        Long mId = Long.parseLong(v);
                        Mechanic mechanic = Mechanic.on.obj(mId);
                        spare.setMechanic(mechanic);
                    }else{
                        spare.seteMechanic(v);
                    }
                }
                spare.setStoreComment(c);


                spare.approved = true;
                if( spare.canInsert() ) spare.update();
                else System.out.println("not possible");
            }


        }

        return one;
    }


    public Result approveOld(){
        Expression expression = Expr.and(Expr.eq("gApproved",true),Expr.eq("approved",false));
        JsonNode nodeList = OldSpare.on.setPageExp(expression).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.putObject("stock");
        node.set("cars",Car.on.nodeList());
        node.set("mechanic",Mechanic.on.nodeList());
        node.set("spares",Stock.allAvailableNode());
        node.put("save",routes.StoreKeeper.submitOldApproval().url());
        return ok(node);
    }


}
