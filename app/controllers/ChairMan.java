package controllers;

import Helper.SuperBase;
import controllers.security.ChairManSecurity;
import play.data.FormFactory;
import play.mvc.Result;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Security.Authenticated(ChairManSecurity.class)
public class ChairMan extends SuperBase {

    @Inject
    public ChairMan(FormFactory formFactory) {
        fFactory = formFactory;
    }


    public Result printRequest(){
        return ok();
    }

}
