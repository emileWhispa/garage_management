package controllers.security;

import Helper.SuperBase;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class TransportMDSecurity extends Security.Authenticator  {
    @Override
    public String getUsername(Http.Context ctx) {
        return ctx.session().get(SuperBase.sessionMDTransport);
    }
    @Override
    public Result onUnauthorized(Http.Context ctx) {
        return ok("error");
    }
}