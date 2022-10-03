package Helper;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.*;
import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.Expr;
import models.Info;
import models.User;
import play.data.FormFactory;
import play.filters.csrf.CSRF;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.persistence.Entity;
import java.io.File;
import java.util.*;

public abstract class SuperBase extends Controller {

    protected String imgUrl;
    protected String socket;
    protected String loginUrl;
    private String defaultUser = "user";
    protected String systemAccess = "system-access";
    public static final String sessionAdmin = "admin";
    public static final String sessionAccountant = "accountant";
    private static String rootFolder = "public/";
    public static final String sessionGarageManager = "garageManger";
    public static final String sessionStoreKeeper = "storeKeeper";
    public static final String sessionChiefMechanic = "chiefMechanic";
    public static final String sessionForeMan = "foreMan";
    public static final String sessionChairMan = "chairMan";
    public static final String sessionFinance = "finance";
    public static final String sessionMDTransport = "transportMD";
    public static final String sessionProcurement = "procurementM";
    private static final String sessionHr = "HrResource";
    private List<Menu> menuList = new ArrayList<>();
    protected static Class<?> dClass = String.class;
    protected Result one = ok("1");
    protected Result sError = ok("System error is here");
    private static String hashKey = "_~?9";
    private static String toBeReplaced = ".";


    private static EbeanServer db() {
        return Ebean.getDefaultServer();
    }


    protected Info info(){
        Info one = Info.on.query().setMaxRows(1).findOne();
        return one != null ? one : new Info();
    }

    private class Menu {
        public String href;
        public String title;
        public String icon;
        public String type;
        private boolean isLeft = false;
        public boolean redirect = false;
        public boolean js = false;

        @JsonProperty
        public List<Menu> menuList = new ArrayList<>();

        Menu(String href, String icon, String title) {
            this.icon = icon;
            this.title = title;
            this.href = href;
        }

        public Menu redirect() {
            this.redirect = true;
            return this;
        }

        public Menu isJs(String type){
            this.js = true;
            this.type = type;
            return this;
        }


        public void makeTop() {
            this.isLeft = false;
        }
    }

    public static String reverse(String s){
        s = s.replace(toBeReplaced,hashKey);
        return new StringBuilder(s).reverse().toString();
    }

    public static String inverse(String s){
        s = new StringBuilder(s).reverse().toString();
        return s.replace(hashKey,toBeReplaced);
    }

    private boolean isAdmin() {
        return session().containsKey(sessionAdmin);
    }

    private boolean isHr() {
        return session().containsKey(sessionHr);
    }

    private boolean isGrgManager() {
        return session().containsKey(sessionGarageManager);
    }

    private boolean isStorekeeper() {
        return session().containsKey(sessionStoreKeeper);
    }

    private boolean isProcurement() {
        return session().containsKey(sessionProcurement);
    }

    private boolean isTMD() {
        return session().containsKey(sessionMDTransport);
    }

    private boolean isFinance() {
        return session().containsKey(sessionFinance);
    }

    private boolean isAccountant() {
        return session().containsKey(sessionAccountant);
    }

    private boolean isChair() {
        return session().containsKey(sessionChairMan);
    }

    private boolean isChiefMechanic() {
        return session().containsKey(sessionChiefMechanic);
    }

    private boolean isForeMan() {
        return session().containsKey(sessionForeMan);
    }

    private void makeMenu() {
        menuList = new ArrayList<>();
        ReverseAdminController admin = routes.AdminController;
        ReverseApplication app = routes.Application;
        ReverseTransportMD md = routes.TransportMD;
        ReverseProcurement pro = routes.Procurement;
        ReverseFinance fn = routes.Finance;
        ReverseChiefMechanic mechanic = routes.ChiefMechanic;
        Menu menu = new Menu("#","fa fa-chevron-right","Garage management system");
        Menu settings = new Menu("#","fa fa-chevron-right","Settings");
        Menu report = new Menu("#", "fa fa-chevron-right", "Reports");
        if (isAdmin()) {
            settings.menuList.add(new Menu(admin.tabs("g-setting").url(), "fa fa-dashboard", "General settings"));
            menu.menuList.add(new Menu(admin.editProfile().url(), "fa fa-dashboard", "Edit profile info").isJs("tap_edit"));
        } else if (isHr()) {
            menuList.add(new Menu("/hello/m", "fa fa-ban", "Human resource settings"));
        } else if (isGrgManager()) {
            ReverseGarageManager gManager = routes.GarageManager;
            settings.menuList.add(new Menu(gManager.approveContent().url(), "fa fa-book", "Approve spare requests").isJs("g_app"));
            settings.menuList.add(new Menu(gManager.approveContent().url(), "fa fa-trash-o", "View approved requests"));
            settings.menuList.add(new Menu(gManager.approveOld().url(), "fa fa-trash-o", "Approve old spare parts").isJs("g_old"));
        } else if (isStorekeeper()) {
            ReverseStoreKeeper keeper = routes.StoreKeeper;
            settings.menuList.add(new Menu(keeper.tabs("k-request").url(), "fa fa-users", "General settings"));
            settings.menuList.add(new Menu(keeper.tabs("car-set").url(), "fa fa-user", "Vehicles settings"));
            settings.menuList.add(new Menu(keeper.addToStock().url(), "fa fa-user", "Add approved to stock").isJs("add_to_s"));
            settings.menuList.add(new Menu(keeper.approveOld().url(), "fa fa-trash-o", "Approve old spare parts").isJs("g_old"));
        } else if ( isTMD() ) {
            settings.menuList.add(new Menu(md.approve().url(), "fa fa-users", "Approve spare requests").isJs("g_app"));
        } else if ( isProcurement() ) {
            settings.menuList.add(new Menu(pro.approve().url(), "fa fa-users", "Approve spare requests").isJs("g_app"));
        } else if ( isChiefMechanic() ) {
            settings.menuList.add(new Menu(mechanic.tabs("k-req").url(), "fa fa-users", "Add old parts"));
        } else if ( isForeMan() ) {
            ReverseForeMan man = routes.ForeMan;
            settings.menuList.add(new Menu(man.approveOld().url(), "fa fa-users", "Approve old parts").isJs("o_app"));
            settings.menuList.add(new Menu(man.approveOld().url(), "fa fa-users", "Add Existing spare parts").isJs("x_o_app"));
        } else if ( isFinance() ) {
            settings.menuList.add(new Menu(fn.approve().url(), "fa fa-users", "Approve requests").isJs("g_app"));
        } else if ( isChair() ) {
            settings.menuList.add(new Menu(fn.approve().url(), "fa fa-users", "Print items requests"));
        }
        report.menuList.add(new Menu(app.summary().url(), "fa fa-user", "View stock summary").isJs("summary"));
        report.menuList.add(new Menu(app.tabs("view-s").url(), "fa fa-user", "View stock"));

        if( isChair() || isFinance() || isProcurement() || isGrgManager() || isTMD() || isAdmin() || isAccountant() ){

            report.menuList.add(new Menu(app.viewStandardReport().url(), "fa fa-user", "Standard stock report").isJs("s_x_report"));
            report.menuList.add(new Menu(app.viewSpareReport().url(), "fa fa-user", "Spare parts report").isJs("s_x_report"));
            report.menuList.add(new Menu(app.viewReport().url(), "fa fa-user", "Dynamic stock report").isJs("s_report"));
            report.menuList.add(new Menu(app.viewBudgetReport().url(), "fa fa-user", "Budget report").isJs("s_x_report"));
            Menu revenueReport = new Menu("#", "fa fa-chevron-right", "Revenue report");
            revenueReport.menuList.add(new Menu(app.viewBeyondRevenueReport().url(), "fa fa-user", "Beyond average").isJs("s_x_report"));
            revenueReport.menuList.add(new Menu(app.viewBelowRevenueReport().url(), "fa fa-user", "Below average").isJs("s_x_report"));
            report.menuList.add(revenueReport);
        }

        if( isAccountant() ){
            settings.menuList.add(new Menu(app.tabs("g-req").url(), "fa fa-users", "Revenue settings"));
        }

        if( isAdmin() ) menuList.add(menu);

        settings.menuList.add(new Menu(app.getProfile().url(), "fa fa-dashboard", "Update profile").isJs("load-profile"));

        menuList.add(settings);
        menuList.add(report);
        menu.menuList.add(new Menu(app.returnRoles().url(), "fa fa-sign-out", "Change user roles").isJs("role"));
        menuList.add(new Menu(app.logout().url(), "fa fa-sign-out", "SIGN OUT").isJs("sign-out"));
    }

    private JsonNode getMenu() {
        this.makeMenu();
        return Json.toJson(menuList);
    }


    protected static FormFactory fFactory;

    protected boolean isDefaultLog() {
        return session().containsKey(defaultUser);
    }

    protected void setDefaultUser(String defaultUser) {
        session().put(this.defaultUser, defaultUser);
    }

    private long dId() {
        try {
            return Long.parseLong(session(defaultUser));
        } catch (Exception e) {
            return 0;
        }
    }

    protected User user() {
        return User.on.setPageExp(Expr.eq("username", session(defaultUser))).single();
    }

    protected boolean isAjax() {
        Optional<String> s = request().getHeaders().get("X-Requested-With");
        return s.isPresent() && s.get().equals("XMLHttpRequest");
    }

    protected static boolean isNumeric(String v){
        try {
            Long l = Long.parseLong(v);
            return true;
        }catch (NumberFormatException e){
            return  false;
        }
    }

    protected static boolean isArrayNumeric(String[] strings){
        boolean valid = false;
        for (String string : strings){
            if( isNumeric(string) ){
                valid = true;
            }else{
                return false;
            }
        }
        return valid;
    }


    protected JsonNode tools() {
        ObjectNode node = Json.newObject();
        node.put("logo", routes.Assets.at("images/sell.png").absoluteURL(request()));
        node.put("buy", routes.Assets.at("images/buy.png").absoluteURL(request()));
        node.put("icon", routes.Assets.at("images/boys.jpg").absoluteURL(request()));
        node.set("menu", getMenu());
        node.set("user", Json.toJson(user()));
        return node;
    }

    protected String modalPackage() {
        return User.class.getPackage().getName();
    }

    protected boolean isEntity(Class<?> aClass) {
        return aClass.isAnnotationPresent(Entity.class);
    }

    protected boolean isDate(Class<?> clazz){
        return clazz == Date.class;
    }

    protected boolean notEntityForm(Class<?> aClass) {
        return !isEntity(aClass) || !aClass.isAnnotationPresent(EntityProperty.class);
    }

    public Result pagination(String javaName) {

        ArrayNode array = Json.newArray();

        try {
            Class<?> aClass = Class.forName(javaName);
            Finder<?> finder = new Finder<>(aClass);

            if ( notEntityForm(aClass)) return ok(array);

            int count = finder.count();
            for (int i = 1; i <= count; i++) {
                ObjectNode node = Json.newObject();
                node.put("number", i);
                node.put("value", routes.Application.page(javaName, i).url());
                array.add(node);
            }

            return ok(array);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return ok(array);
    }

    public Result data(String javaName, int i) {

        ArrayNode array = Json.newArray();

        try {
            Class<?> aClass = Class.forName(javaName);

            if (notEntityForm(aClass)) return ok(array);

            EntityProperty property = aClass.getAnnotation(EntityProperty.class);

            dClass = property.ctrl();

            Finder<?> finder = new Finder<>(aClass);


            ObjectNode node = Json.newObject();
            this.putToken(node);
            finder.setToken(node);
            if (!property.addNew()) finder.disableNew();

            return ok(finder.setSaveRoute(Application.getSaveRoute(javaName)).page(i));
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return ok(array);
    }

    public Result save(String javaName) {

        ArrayNode array = Json.newArray();

        try {
            Class<?> aClass = Class.forName(javaName);

            if (notEntityForm(aClass)) return ok(array);

            Finder<?> finder = new Finder<>(aClass);

            Object o = finder.formData();


            if( finder.checkExist(o) ) response().setHeader("error",finder.error);

            else Ebean.save(o);


            return one;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return ok(array);
    }

    public Result updatePage(long id,String javaName) {

        return updateValues(id,javaName,null);
    }

    protected Result updateValues(long id,String javaName,Class<?> type) {

        ObjectNode array = Json.newObject();

        try {
            Class<?> aClass = Class.forName(javaName);

            if (notEntityForm(aClass)) return ok(array);

            EntityProperty annotation = aClass.getAnnotation(EntityProperty.class);

            Finder<?> finder = new Finder<>(aClass);


            dClass = type == null ? annotation.ctrl() : type;

            ObjectNode node = Json.newObject();
            putToken(node);
            finder.setToken(node);
            return ok(finder.setSaveRoute(Application.updateRoute(javaName, id)).form(id));
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        return ok(array);
    }

    protected void putToken(ObjectNode node){
        Optional<CSRF.Token> token = CSRF.getToken(request());
        token.ifPresent(t -> {
            node.put("tokenName", t.name());
            node.put("tokenValue", t.value());
        });
    }

    public Result update(long id,String javaName) {

        try {
            Class<?> aClass = Class.forName(javaName);

            if (notEntityForm(aClass)) return one;

            Finder<?> finder = new Finder<>(aClass);

            Object o = finder.formData(id);


            if (o == null) return ok("System Error");

            if( finder.checkExist(o,id) ) response().setHeader("error",finder.error);

            else finder.update();


        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }


        return one;
    }

    public Result delete(long id,String javaName) {

        try {
            Class<?> aClass = Class.forName(javaName);

            if (notEntityForm(aClass)) return one;

            EntityProperty annotation = aClass.getAnnotation(EntityProperty.class);

            Finder<?> finder = new Finder<>(aClass);

            Object o = finder.obj(id);

            if (o == null || annotation.noDelete() ) return ok("System Error");

            db().delete(o);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        return one;
    }

    public Result tabs(String t) {
        ObjectNode node = Json.newObject();
        ArrayNode jsonNodes = Json.newArray();

        List<Class<?>> classList = ClassFinder.find(modalPackage());

        for (Class<?> clazz : classList) {

            if (notEntityForm(clazz)) continue;

            EntityProperty annotation = clazz.getAnnotation(EntityProperty.class);

            if (!annotation.type().equals(t)) continue;

            ObjectNode object = Json.newObject();
            object.put("title", annotation.name());
            object.put("icon", annotation.icon());
            object.put("order", annotation.order());
            object.put("href", routes.Application.pagination(clazz.getName()).url());
            jsonNodes.add(object);
        }


        node.set("link", jsonNodes);
        node.put("title", t);

        return ok(node);
    }

    public static String upload(String fileName){
        return uploadFile(fileName,fileName);
    }

    static String routeFile(String f){
        return routes.Assets.at("uploads/"+f).absoluteURL(request());
    }

    static String uploadFile(String dft, String fName) {
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> picture = body.getFile(fName);
        return singleFile(picture, dft);
    }

    static String randomString() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        UUID uuid2 = UUID.randomUUID();
        String randomUUIDString2 = uuid2.toString();
        return randomUUIDString + randomUUIDString2;
    }

    protected JsonNode nodeEl(String t){
        return nodeEl(t,t);
    }

    protected JsonNode nodeEl(String title,String code){
        return Json.newObject().put("title",title).put("key",code);
    }

    static String singleFile(Http.MultipartFormData.FilePart<File> file, String dft) {
        if (file != null) {
            String fileName = file.getFilename();
            String cType = file.getContentType();
            File newFile = file.getFile();
            String text = (new Date().getTime()) + randomString() + fileName;
            final boolean b = newFile.renameTo(new File(getDef(), text));
            return text;
        } else {
            return dft;
        }
    }



    public static String getDef() {
        File testExist = new File(defaultFolder());
        if (!testExist.exists()) {
            boolean mkDir = testExist.mkdir();
        }
        return defaultFolder();
    }


    protected static String defaultFolder() {
        return rootFolder + "uploads/";
    }

}
