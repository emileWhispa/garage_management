package controllers;

import Helper.EntityProperty;
import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.Expr;
import io.ebean.Expression;
import models.Garage.Budget;
import models.Garage.DriverActivity;
import models.Garage.Spare;
import models.Garage.Stock;
import models.User;
import models.UserRole;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.filters.csrf.CSRF;
import play.libs.Json;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;


public class Application extends SuperBase {

    @Inject
    public Application(FormFactory formFactory) {
        fFactory = formFactory;
    }


    public Result index() {

        this.loginUrl = routes.Application.login().url();
        this.socket = routes.HomeController.ws().webSocketURL(request());
        this.imgUrl = routes.Assets.at("images/sell.png").url();
        response().setHeader("defaultIcon",routes.Assets.at("images/boys.jpg").absoluteURL(request()));
        if( isDefaultLog() && isAjax() ){
            response().setHeader(systemAccess,"1");
            response().setHeader("socket",this.socket);
            return ok(tools());
        }



        Optional<CSRF.Token> token = CSRF.getToken(request());
        token.ifPresent(token1 -> {
            response().setHeader("tokenName",token1.name());
            response().setHeader("tokenValue",token1.value());
        });


        response().setHeader("loginImg",this.imgUrl);
        response().setHeader("loginUrl",this.loginUrl);
        response().setHeader("socket",this.socket);
        return ok(views.html.index.render(request()));
    }

    public Result login() {

        User user = User.on.formData();

        boolean userUser = user.isAuthorized();

        String s;
        if (userUser) {
            User logUser = user.logUser();
            s = logUser.userRoleList.size() > 0 ? "1" : "0";
            setDefaultUser(user.username);

            response().setHeader("status", s);
            return ok(Json.toJson(logUser.userRoleList));
        }

        return ok(Json.newArray());
    }

    public static String createRoute(long id) {
        return routes.Application.createRole(id).url();
    }

    public Result createRole(long id) {
        UserRole obj = UserRole.on.obj(id);
        if (isDefaultLog() && obj != null) {
            User user = user();
            boolean contains = user.inList(obj);
            if (contains) {
                response().setHeader(systemAccess, "1");
                session().clear();
                this.setDefaultUser(user.username);
                session().put(obj.role.sessionName, user.username);
                return ok(tools());
            }
        }

        return ok();
    }

    public Result pagination(String javaName) {
        return this.authenticate(javaName,"pagination",0);
    }

    public Result paginate(String javaName) {
        return super.pagination(javaName);
    }

    private Result authenticate(String javaName,String type,int i){
        try {
            Class<?> aClass = Class.forName(javaName);

            if (notEntityForm(aClass)) return ok(Json.newArray());

            EntityProperty annotation = aClass.getAnnotation(EntityProperty.class);

            Class<?> ctrl = annotation.ctrl();

            if (AdminController.class == ctrl ) {
                if( type.equals("pagination") ) {
                    return redirect(routes.AdminController.pagination(javaName));
                }else if( type.equals("data") ){
                    return redirect(routes.AdminController.data(javaName,i));
                }
            }

            if (GarageManager.class == ctrl ) {
                if( type.equals("pagination") ) {
                    return redirect(routes.GarageManager.pagination(javaName));
                }else if( type.equals("data") ){
                    return redirect(routes.GarageManager.data(javaName,i));
                }
            }

            if (StoreKeeper.class == ctrl ) {
                if( type.equals("pagination") ) {
                    return redirect(routes.StoreKeeper.pagination(javaName));
                }else if( type.equals("data") ){
                    return redirect(routes.StoreKeeper.data(javaName,i));
                }
            }

            if (Application.class == ctrl ) {
                if( type.equals("pagination") ) {
                    return redirect(routes.Application.paginate(javaName));
                }else if( type.equals("data") ){
                    return redirect(routes.Application.data(javaName,i));
                }
            }

            if (ChiefMechanic.class == ctrl ) {
                if( type.equals("pagination") ) {
                    return redirect(routes.ChiefMechanic.pagination(javaName));
                }else if( type.equals("data") ){
                    return redirect(routes.ChiefMechanic.data(javaName,i));
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }


        return ok(Json.newArray());
    }


    public static String getSaveRoute(String javaName){
        if( dClass == AdminController.class ){
            return routes.AdminController.save(javaName).url();
        }
        if( dClass == GarageManager.class ){
            return routes.GarageManager.save(javaName).url();
        }
        if( dClass == StoreKeeper.class ){
            return routes.StoreKeeper.save(javaName).url();
        }
        if( dClass == ChiefMechanic.class ){
            return routes.ChiefMechanic.save(javaName).url();
        }

        return "/";
    }

    public Result getProfile(){
        boolean defaultLog = isDefaultLog();
        if( defaultLog ) {
            User user = user();
            return updateValues(user.id, User.class.getName(),Application.class);
        }
        return ok();
    }

    public static String updateRoute(String javaName,long id){
        if( dClass == AdminController.class ){
            return routes.AdminController.update(id,javaName).url();
        }
        if( dClass == GarageManager.class ){
            return routes.GarageManager.update(id,javaName).url();
        }
        if( dClass == StoreKeeper.class ){
            return routes.StoreKeeper.update(id,javaName).url();
        }
        if( dClass == ChiefMechanic.class ){
            return routes.ChiefMechanic.update(id,javaName).url();
        }

        if( dClass == Application.class ){
            return routes.Application.update(id,javaName).url();
        }

        return "/";
    }

    public Result updatePageRedirect(String javaName,long id){
        return fRedirect(javaName,id,false);
    }

    private Result fRedirect(String javaName,long id,boolean isDelete){
        try {
            Class<?> aClass = Class.forName(javaName);

            if(notEntityForm(aClass)) return ok(Json.newObject());

            EntityProperty annotation = aClass.getAnnotation(EntityProperty.class);

            Class<?> ctrl = annotation.ctrl();


            if( isDelete ){
                if( ctrl == AdminController.class ){
                    return redirect(routes.AdminController.delete(id,javaName));
                }
                if( ctrl == GarageManager.class ){
                    return redirect(routes.GarageManager.delete(id,javaName));
                }
                if( ctrl == StoreKeeper.class ){
                    return redirect(routes.StoreKeeper.delete(id,javaName));
                }
                if( ctrl == Application.class ){
                    return redirect(routes.Application.delete(id,javaName));
                }
                if( ctrl == ChiefMechanic.class ){
                    return redirect(routes.ChiefMechanic.delete(id,javaName));
                }
                return ok(Json.newObject());
            }



            if( ctrl == AdminController.class ){
                return redirect(routes.AdminController.updatePage(id,javaName));
            }
            if( ctrl == GarageManager.class ){
                return redirect(routes.GarageManager.updatePage(id,javaName));
            }
            if( ctrl == StoreKeeper.class ){
                return redirect(routes.StoreKeeper.updatePage(id,javaName));
            }
            if( ctrl == Application.class ){
                return redirect(routes.Application.updatePage(id,javaName));
            }
            if( ctrl == ChiefMechanic.class ){
                return redirect(routes.ChiefMechanic.updatePage(id,javaName));
            }

        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return ok(Json.newObject());
    }

    public Result deletePageRedirect(String javaName,long id){
        return fRedirect(javaName,id,true);
    }




    public static String getUpdateRoute(String javaName,long id){
        return routes.Application.updatePageRedirect(javaName,id).url();
    }

    public static String getDeleteRoute(String javaName,long id){
        return routes.Application.deletePageRedirect(javaName,id).url();
    }


    public Result page(String javaName,int integer){
        return this.authenticate(javaName,"data",integer);
    }

    public Result logout(){
        session().clear();
        response().getHeaders().clear();
        return index();
    }

    public Result returnRoles(){
        if( isDefaultLog() ){
            ObjectNode node = Json.newObject();
            node.set("data",Json.toJson(user().userRoleList));
            return ok(node);
        }
        return ok();
    }

    public Result summary(){
        Expression id = Expr.in("id", Stock.on.query().select("spare.id"));
        JsonNode node = Spare.on.setPageExp(id).nodeList();
        ObjectNode object = Json.newObject();

        object.set("data",node);
        object.putArray("columns");
        object.set("columns",array());
        return ok(object);
    }

    public ArrayNode array(){
        ArrayNode arrayNode = Json.newArray();
        arrayNode.add(nodeEl("Spare name","spareName"));
        arrayNode.add(nodeEl("Brand name","bName"));
        arrayNode.add(nodeEl("date"));
        arrayNode.add(nodeEl("stockCount"));
        return arrayNode;
    }

    public Result summaryDetail(Long id){
        JsonNode node = Stock.on.setPageExp(Expr.eq("spare.id", id)).nodeList();
        return ok(node);
    }

    public static String dRoute(Long id){
        return routes.Application.summaryDetail(id).absoluteURL(request());
    }


    public Result viewReport(){
        return redirect(routes.ReportsController.index("extra-dynamic"));
    }


    public Result viewBudgetReport(){
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.put("route",routes.Application.viewFinalBudgetReport().url());
        node.put("title","View final report");
        return ok(node);
    }

    public Result viewFinalBudgetReport(){

        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> start = form.field("start").getValue();
        Optional<String> end = form.field("end").getValue();

        if( !start.isPresent() || !end.isPresent() ) return sError;


        JsonNode node = Budget.on.setPageExp(Expr.between("date", (Object) start.get(), end.get())).nodeList();

        ObjectNode object = Json.newObject();

        object.set("data",node);
        object.set("fields",budgetCols());
        object.put("title","Standard budget report");

        return ok(object);
    }

    public Result viewStandardReport(){
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.put("route",routes.Application.finalReportView().url());
        node.put("title","View final report");
        return ok(node);
    }

    public Result viewBeyondRevenueReport(){
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.put("route",routes.Application.finalRevenueReportView().url());
        node.put("title","View final report");
        return ok(node);
    }

    public Result viewBelowRevenueReport(){
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.put("route",routes.Application.finalRevenueReportViewBelow().url());
        node.put("title","View final report");
        return ok(node);
    }

    public Result viewSpareReport(){
        ObjectNode node = Json.newObject();
        this.putToken(node);
        node.put("route",routes.Application.finalSpareReportView().url());
        node.put("title","View final report");
        return ok(node);
    }

    private ArrayNode reportCols(){
        ArrayNode arrayNode = Json.newArray();
        arrayNode.add(nodeEl("Spare name","spName"));
        arrayNode.add(nodeEl("Serial number","serialNumber"));
        arrayNode.add(nodeEl("Supplier name","supplierName"));
        arrayNode.add(nodeEl("Replaced serial number","replacedSerialN"));
        arrayNode.add(nodeEl("Car plate number","plateNumber"));
        arrayNode.add(nodeEl("Car mechanic","mechanic"));
        arrayNode.add(nodeEl("Date/Time in","Date/Time in"));
        arrayNode.add(nodeEl("Date/Time out","Date/Time out"));
        arrayNode.add(nodeEl("Status","Status"));
        arrayNode.add(nodeEl("price","price"));
        return arrayNode;
    }


    private ArrayNode budgetCols(){
        ArrayNode arrayNode = Json.newArray();
        arrayNode.add(nodeEl("Spare name","spName"));
        arrayNode.add(nodeEl("Total budget","amount"));
        arrayNode.add(nodeEl("Year","year"));
        arrayNode.add(nodeEl("Budget used","totalValue"));
        arrayNode.add(nodeEl("Budget remaining","balance"));
        return arrayNode;
    }

    private ArrayNode RevenueReportCols(){
        ArrayNode arrayNode = Json.newArray();
        arrayNode.add(nodeEl("Date","date"));
        arrayNode.add(nodeEl("Route","route"));
        arrayNode.add(nodeEl("Zone name","zoneName"));
        arrayNode.add(nodeEl("Plate number","car"));
        arrayNode.add(nodeEl("number","number"));
        arrayNode.add(nodeEl("Driver name","driver"));
        arrayNode.add(nodeEl("Target","target"));
        arrayNode.add(nodeEl("Transport revenue","transportRevenue"));
        arrayNode.add(nodeEl("Fuel revenue","fuel"));
        arrayNode.add(nodeEl("Net revenue","netRevenue"));
        return arrayNode;
    }

    private ArrayNode report2Cols(){
        ArrayNode arrayNode = Json.newArray();
        arrayNode.add(nodeEl("Spare name","spareName"));
        arrayNode.add(nodeEl("Vehicle name/brand","bName"));
        arrayNode.add(nodeEl("Quantity received","stockCount"));
        arrayNode.add(nodeEl("Quantity out","quantityOut"));
        arrayNode.add(nodeEl("Balance","balance"));
        return arrayNode;
    }

    public Result finalReportView(){
        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> start = form.field("start").getValue();
        Optional<String> end = form.field("end").getValue();

        if( !start.isPresent() || !end.isPresent() ) return sError;


        JsonNode node = Stock.on.setPageExp(Expr.between("date", (Object) start.get(), end.get())).nodeList();

        ObjectNode object = Json.newObject();

        object.set("data",node);
        object.set("fields",reportCols());
        object.put("title","Standard stock report");

        return ok(object);
    }

    public Result finalRevenueReportViewBelow(){
        return this.viewReReport(true);
    }

    private Result viewReReport(Boolean isBelow){

        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> start = form.field("start").getValue();
        Optional<String> end = form.field("end").getValue();

        if( !start.isPresent() || !end.isPresent() ) return sError;

        Expression expr;

        String sql = "(transportRevenue - fuel) >= zone.target";

        if( isBelow ){
            sql = sql.replace(">=","<");
        }

        expr = Expr.raw(sql);

        Expression expression = Expr.and(Expr.between("date", (Object) start.get(), end.get()),expr);

        JsonNode node = DriverActivity.on.setPageExp(expression).nodeList();

        ObjectNode object = Json.newObject();

        object.set("data",node);
        object.set("fields",RevenueReportCols());

        return ok(object);
    }

    public Result finalRevenueReportView(){
        return this.viewReReport(false);
    }

    public Result finalSpareReportView(){
        DynamicForm form = fFactory.form().bindFromRequest();

        Optional<String> start = form.field("start").getValue();
        Optional<String> end = form.field("end").getValue();

        if( !start.isPresent() || !end.isPresent() ) return sError;


        JsonNode node = Spare.on.setPageExp(Expr.between("date", (Object) start.get(), end.get())).nodeList();

        ObjectNode object = Json.newObject();

        object.set("data",node);
        object.set("fields",report2Cols());

        return ok(object);
    }

}
