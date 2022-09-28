package controllers;

import Helper.SuperBase;
import controllers.security.AccountantSecurity;
import play.mvc.Security;

@Security.Authenticated(AccountantSecurity.class)
public class AccountantController extends SuperBase {

}
