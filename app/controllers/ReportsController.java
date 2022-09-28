package controllers;

import Helper.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.Expr;
import io.ebean.Expression;
import play.Logger;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
public class ReportsController extends SuperBase {

    public static String getReportRef(String name){
        return routes.ReportsController.getReportChooseValue(name).url();
    }

    @Inject
    public ReportsController(FormFactory formFactory){
        fFactory = formFactory;
    }

    public Result filteredData(String canonicalName){
        try {
            Class<?> aClass = Class.forName(canonicalName);
            if( isEntity(aClass) ) {
                Finder<?> look = new Finder<>(aClass);

                Http.MultipartFormData<Object> objectMultipartFormData = request().body().asMultipartFormData();
                if( objectMultipartFormData == null ) return ok();

                Map<String, String[]> map = objectMultipartFormData.asFormUrlEncoded();

                if( map == null ) return sError;

                Set<String> stringSet = map.keySet();

                Expression expression = null;

                for (String s : stringSet){
                    String inverse = inverse(s);
                    String[] strings = map.get(s);

                    if( look.inColList(inverse) && strings.length == 1 ){
                        String colWithId = look.getColWithId(inverse);

                        if( colWithId == null ) continue;

                        Expression expr = Expr.eq(colWithId,strings[0]);

                        if( expression == null ) expression = expr;
                        else expression = Expr.and(expression,expr);
                    }else if( strings.length == 2){
                        Object o = strings[0];
                        Object o2 =strings[1];
                        Expression expr = Expr.between(inverse,o,o2);
                        if( expression == null ) expression = expr;
                        else expression = Expr.and(expression,expr);
                    }else if( strings.length == 4 ){
                        Expression expr = Expr.eq(inverse,strings[0]);
                        if( expression == null ) expression = expr;
                        else expression = Expr.and(expression,expr);
                    }
                    //else {
                        //look.inQuery(s,modalPackage());
                    //}
                }

                if( expression != null ){
                    look.setPageExp(expression);
                }

                JsonNode node = look._structNodeList();
                return ok(node);

            }
        } catch (ClassNotFoundException ignored) {
        }
        return one;
    }

    public Result filteringPlace(String canonicalName){
        try {
            Class<?> aClass = Class.forName(canonicalName);
            boolean isEntity = isEntity(aClass);
            if( isEntity ) {
                Http.MultipartFormData<Object> formData = request().body().asMultipartFormData();
                if( formData == null ) return sError;

                Map<String, String[]> map = formData.asFormUrlEncoded();

                if( map == null ) return sError;

                Set<String> stringSet = map.keySet();

                List<Class<?>> classList = new ArrayList<>();
                List<Class<?>> appendList = new ArrayList<>();
                List<String[]> stringList = new ArrayList<>();

                String route = routes.ReportsController.filteredData(canonicalName).url();

                for (String s : stringSet ){
                    String[] strings = map.get(s);

                    if( strings.length < 2 ) continue;

                    String[] str = {s,strings[0]};


                    Class<?> clazz = Class.forName(strings[1]);

                    boolean entity = isEntity(clazz);

                    if( entity ) {
                        Finder<?> look = new Finder<>(aClass);
                        if (look.inColInverseList(s)) {
                            classList.add(clazz);
                            stringList.add(str);
                        } else{
                            appendList.add(clazz);
                        }
                    }else if( isDate(clazz) ){
                        classList.add(clazz);
                        stringList.add(str);
                    }else if( strings.length == 3 ){
                        String[] strip = {s,strings[0],strings[2]};
                        stringList.add(strip);
                        classList.add(clazz);
                    }

                }


                return createArrayForm(classList,stringList,route,appendList);

            }
        } catch (ClassNotFoundException e) {
            Logger.warn(e.getMessage());
        }
        return one;
    }

    public Result getReportChooseValue(String canonicalName){
        try {
            Class<?> aClass = Class.forName(canonicalName);
            if( isEntity(aClass) ) {
                JsonNode allColumnsHashed = new Finder<>(aClass).getModalCols();
                ObjectNode node = Json.newObject();
                node.set("columns",allColumnsHashed);
                this.putToken(node);
                node.put("route",routes.ReportsController.filteringPlace(canonicalName).url());
                node.put("nextRoute",routes.ReportsController.nextChooseValue(canonicalName).url());
                return ok(node);
            }
        } catch (ClassNotFoundException e) {
            Logger.warn(e.getMessage());
        }
        return one;
    }

    public Result nextChooseValue(String canonicalName){
        try {
            Class<?> aClass = Class.forName(canonicalName);
            if( isEntity(aClass) ) {
                final Finder<?> look = new Finder<>(aClass);

                return ok(look.getWhereAmNode(modalPackage()));
            }
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return one;
    }


    private Result createArrayForm(List<Class<?>> classList,List<String[]> stringList,String route,List<Class<?>> aList){
        ReportFinder finder = new ReportFinder();
        List<ReportMenu> menuList = new ArrayList<>();
        int i = 0;
        for (Class<?> clazz : classList ){

            if( stringList.size() <= i ) continue;

            String[] ss = stringList.get(i);
            String s = ss[0];
            if( s == null ) continue;
            ReportMenu menu = new ReportMenu();
            menu.setClass(clazz).setType("text").setName(s);
            if( ss.length == 3 ){
                String string = ss[2];
                string = string.replace("[","").replace("]","");
                String[] arr = string.split(",");
                ArrayNode arrayNode = Json.newArray();
                for (String x : arr){
                    arrayNode.add(Json.newObject().put("id",x.trim()).put("print",x.trim()));
                }
                menu.setName(s).setNode(arrayNode);
                menu.setTitle("Choose on list("+ss[1]+")");
            } else if( isDate(clazz) ){
                menu.isCal().setTitle(ss[1]+"(End date)");
                ReportMenu menu2 = new ReportMenu();
                menu2.setClass(clazz).setType("text").setName(s).isCal().setTitle(ss[1]+"(Start date)");
                menuList.add(menu2);
            }else {
                menu.setTitle(clazz.getSimpleName());
            }
            menuList.add(menu);
            i++;
        }
        for (Class<?> clazz : aList ){
            ReportMenu menu = new ReportMenu()
                    .setName(clazz.getName())
                    .isReadOnly()
                    .setChecked()
                    .setCheckValue("Will check this info.")
                    .setTitle(clazz.getSimpleName());
            menuList.add(menu);
        }
        ObjectNode tokeNode = Json.newObject();
        this.putToken(tokeNode);
        finder.setTokeNode(tokeNode);

        ObjectNode node = finder.putRoute(route).putMenuFinal("Submit to review the report", menuList);


        return ok(node);
    }

    public Result index(String s) {
        switch (s){
            case "extra-dynamic": {
                List<JsonNode> classList = ClassFinder.findReportable(modalPackage());
                return ok(Json.toJson(classList));
            }
        }
        return sError;
    }

    private static String capitalizeMe(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public Result jsonReport(String status) {
        return one;
    }

    public Result viewColumn(String t) {
        return one;
    }

    public Result finalReport(String t) {
        return one;
    }

    public static int count() {
        return 0;
    }

    public Result reportsByCampus(String s) {
       return sError;
    }
}
