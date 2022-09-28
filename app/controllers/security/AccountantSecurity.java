package controllers.security;

import Helper.SuperBase;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class AccountantSecurity extends Security.Authenticator  {
    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get(SuperBase.sessionAccountant);
    }
    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return ok("error");
    }
}
