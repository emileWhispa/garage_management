package models;

import Helper.EntityProperty;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import controllers.AdminController;

import javax.persistence.Entity;

@Entity
@EntityProperty(name = "System roles setting",type = "g-setting",ctrl= AdminController.class,addNew = false,noDelete = true)
public class Role extends BaseModel {

    @FormProp(display = "Role name",tbl = true)
    public String roleName;

    @JsonIgnore
    @FormProp(isDisabled = true)
    public String sessionName;


    @Override
    public String print(){
        return roleName;
    }

    public static Finder<Role> on = new Finder<>(Role.class);
}
