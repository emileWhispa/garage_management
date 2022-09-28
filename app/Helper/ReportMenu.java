package Helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.ebean.Ebean;
import io.ebean.Query;
import play.libs.Json;

import javax.persistence.Entity;
import java.util.Date;
import java.util.List;


public class ReportMenu {
    private String title;
    private Class<?> aClass;
    private String name;
    private String type = "text";
    private String dVal = "";
    private JsonNode node = null;
    private boolean isCal = false;
    private boolean checked = false;
    private String checkValue = null;
    private boolean disabled = false;
    private boolean readOnly = false;

    public ReportMenu setClass(Class<?> aClass) {
        this.aClass = aClass;
        return this;
    }

    public ReportMenu setNode(JsonNode node) {
        this.node = node;
        return this;
    }

    public ReportMenu setName(String name) {
        this.name = name;
        return this;
    }

    public ReportMenu setType(String type) {
        this.type = type;
        return this;
    }

    public ReportMenu setChecked() {
        setType("checkbox");
        checked = true;
        return this;
    }

    public ReportMenu setCheckValue(String checkValue) {
        this.checkValue = checkValue;
        this.dVal = checkValue;
        return this;
    }

    public ReportMenu isCal() {
        this.isCal = true;
        return this;
    }

    public ReportMenu isDisabled() {
        this.disabled = true;
        return this;
    }

    public ReportMenu isReadOnly() {
        this.readOnly = true;
        return this;
    }

    public ReportMenu setTitle(String title) {
        this.title = title;
        return this;
    }

    private String getVal(){
        return aClass == Date.class ? "dd-mm-yyy" : dVal;
    }

    void putNode(ObjectNode node,ArrayNode parent) {
        node.put("name",this.name);
        node.put("type",type);
        node.put("label",title);
        node.put("calendar",isCal);
        if( disabled ) {
            node.put("disabled", true);
        }
        if( readOnly ) {
            node.put("readOnly", true);
        }

        if( checked ) {
            node.put("checked", true);
            node.put("checkValue", checkValue);
        }
        node.put("className","form-control");
        if( aClass != null && aClass.isAnnotationPresent(Entity.class) ){
            List<?> listList;
            Query<?> query = Ebean.find(aClass);
            int rowCount = query.findCount();
            if( rowCount > 100 ){
                node.put("search","");
                listList = query.setMaxRows(100).findList();
            }else listList = query.findList();
            node.set("value",Json.toJson(listList));
        }else if( this.node != null ) {
            node.set("value",this.node);
        }else{
            node.put("value", getVal());
        }
        parent.add(node);
    }
}
