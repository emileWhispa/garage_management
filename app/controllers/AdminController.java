package controllers;

import Helper.Finder;
import Helper.SuperBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.security.AdminSecurity;
import models.Info;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;


@Security.Authenticated(AdminSecurity.class)
public class AdminController extends SuperBase {

    @Inject
    public AdminController(FormFactory formFactory) {
        fFactory = formFactory;
    }


    public Result editProfile(){
        String url = routes.AdminController.updateProfile().url();
        ObjectNode node = Json.newObject();
        this.putToken(node);
        Finder<Info> infoFinder = Info.on.setSaveRoute(url);
        infoFinder.setToken(node);

        Info info = info();

        if( info.id > 0 ){
            return ok(infoFinder.form(info.id));
        }

        return ok(infoFinder.form());
    }



    public Result updateProfile(){
        Form<Info> form = fFactory.form(Info.class).bindFromRequest();

        if( form.hasErrors() ) return ok(form.errorsAsJson());

        Info get = form.get();

        Info info = info();
        info.address = get.address;
        info.email = get.email;
        info.phone = get.phone;
        info.isOldAllowed = get.isOldAllowed;

        info.save();

        return one;
    }

}
