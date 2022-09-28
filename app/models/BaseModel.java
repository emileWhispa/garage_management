package models;

import Helper.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.Application;
import io.ebean.Model;
import play.libs.Json;
import play.mvc.Controller;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@MappedSuperclass
public abstract class BaseModel extends Model {
    @Id
    public long id;

    public String doneBy = "";

    @JsonProperty(value = "api-date")
    public Date date = new Date();


    @JsonIgnore
    @Transient
    private String badge = "bg-green";

    public BaseModel info() {
        this.badge = "bg-info";
        return this;
    }

    public void warn() {
        this.badge = "bg-warn";
    }

    public void primary() {
        this.badge = "bg-blue";
    }

    public void danger() {
        this.badge = "bg-red";
    }

    public void success() {
        this.badge = "bg-green";
    }

    protected String badge(Object o) {
        String s = "<span class=\"badge " + this.badge + "\">" + o.toString() + "</span>";
        this.success();
        return s;
    }

    private static String pattern = "EEEE , dd-MMMM-yyyy";
    private static SimpleDateFormat format = new SimpleDateFormat(pattern);
    protected static Calendar calendar = Calendar.getInstance();
    protected static NumberFormat formatInstance = NumberFormat.getNumberInstance(Locale.US);

    @JsonProperty
    protected String print(){
        return "To - be overridden";
    }

    @FormProp(display = "Update page", but = true,order = 998,tblOnly = true)
    public String button() {
        return Application.getUpdateRoute(cName(),id);
    }

    @FormProp(display = "Delete", del = true,order = 999,tblOnly = true)
    public String deletePage() {
        return Application.getDeleteRoute(cName(),id);
    }

    private String cName(){
        return this.getClass().getName();
    }

    @JsonProperty(value = "date")
    public String fDate(){
        return this.format(date);
    }

    protected String format(Date date){
        return format.format(date);
    }

    public static String f(Date date){
        return format.format(date);
    }

    protected String fVisit(Date nextVisit){
        nextVisit = nextVisit != null ? nextVisit : new Date();
        int i = daysBetween(nextVisit, new Date());

        String s = format(nextVisit);

        if( i <= 20 && i > 10 ) this.warn();
        else if( i <= 10 && i > 5 ) this.primary();
        else if( i <= 5 ) this.danger();
        else this.success();

        return this.badge(s);
    }

    public int daysBetween(Date d1, Date d2){
        Long t1 = d1.getTime();
        Long t2 = d2.getTime();
        return (int)( ( t1 > t2 ? t1 - t2 : t2 - t1 ) / (1000 * 60 * 60 * 24));
    }

    @JsonProperty
    @NoJsonReport
    public String unique(){
        return cName()+date.getTime()+""+id;
    }


}