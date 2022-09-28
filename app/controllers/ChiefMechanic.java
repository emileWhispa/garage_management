package controllers;

import Helper.SuperBase;
import controllers.security.ChiefMechanicSecurity;
import play.data.FormFactory;
import play.mvc.Security;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Security.Authenticated(ChiefMechanicSecurity.class)
public class ChiefMechanic extends SuperBase {
    @Inject
    public ChiefMechanic(FormFactory formFactory){
        fFactory = formFactory;
    }

}
