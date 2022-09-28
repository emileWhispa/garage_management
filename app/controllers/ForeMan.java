package controllers;

import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.ForeManSecurity;
import io.ebean.Expr;
import io.ebean.Expression;
import io.ebean.ExpressionList;
import models.Garage.OldSpare;
import models.Garage.OldSpareRequest;
import models.Garage.SpareRequest;
import models.Garage.Stock;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Singleton
@Security.Authenticated(ForeManSecurity.class)
public class ForeMan extends SuperBase {
    @Inject
    public ForeMan(FormFactory formFactory){
        fFactory = formFactory;
    }

    public Result approveOld(){
        Expression expression = Expr.eq("chiefMechanicApproval",false);
        JsonNode nodeList = OldSpareRequest.on.setPageExp(expression).nodeList();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.set("data",nodeList);
        node.put("enabled",info().isOldAllowed);
        node.put("save",routes.ForeMan.submitApproval().url());
        return ok(node);
    }

    public Result submitApproval(){
        return approve("foreMan");
    }


    public static Result approve(String type){
        Http.MultipartFormData<Object> formData = request().body().asMultipartFormData();

        if( formData == null ) return ok();

        Map<String, String[]> map = formData.asFormUrlEncoded();

        if( map == null ) return ok();


        String[] keySet = map.get("item");

        String id,comment;

        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> value = form.field("old").getValue();

        Optional<String> optionalS = form.field("comment").getValue();

        Optional<String> escape = form.field("escape").getValue();

        boolean present = escape.isPresent();

        id = value.orElse("");

        comment = optionalS.orElse("");

        if( !isNumeric(id) ) return ok();

        Long lId = Long.parseLong(id);


        OldSpareRequest spare = OldSpareRequest.on.obj(lId);
        if( spare == null ) return ok();


        spare.foreManComment = comment;



        StringBuilder builder = new StringBuilder();

        String p = "<p class=\"text-muted %s well well-sm no-shadow\" style=\"margin-top: 10px;\">\n" +
                "                                                        <span>Old part serial number '%s' %s</span>\n" +
                "                                                        <i class=\"fa fa-info-circle big-font pull-right\"></i>\n" +
                "                                                    </p>";

        boolean b = true;
        for (String string : keySet){
            ExpressionList<Stock> expr = Stock.on.query().eq("serialNumber", string).not(Expr.in("id", OldSpare.on.query().eq("stock.serialNumber", string).select("stock.id")));

            int count = expr.findCount();

            Stock stock = null;
            if( count <= 0 && !present ) {
                String pr = String.format(p,"text-red",string,"is not recognized by system or already replaced");
                builder.append(pr);
                b = false;
                continue;
            }

            if( present ){
                stock = new Stock();
                stock.spare = spare.spare;
                stock.price = spare.spare.price;
                stock.serialNumber = string;
                stock.supplierName = "";
            }else{
                stock = expr.findOne();
            }


            String pr = String.format(p,"text-green",string,"is replaced successfully");
            builder.append(pr);

            OldSpare oldSpare = new OldSpare();
            oldSpare.stock = stock;
            oldSpare.setRequest(spare);

            if( oldSpare.stock == null ) continue;

            oldSpare.save();

            --spare.quantity;
            spare.update();


        }

        if( b ) {
            spare.foreManApproval = true;
        }
        spare.update();

        return builder.length() > 0 ? ok(builder.toString()) : ok("1");
    }
}
