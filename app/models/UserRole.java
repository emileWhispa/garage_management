package models;

import Helper.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import controllers.AdminController;
import controllers.Application;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
@EntityProperty(name = "User and role settings",type = "g-setting",ctrl = AdminController.class)
public class UserRole extends BaseModel {

    @ManyToOne
    @FormProp(isRel = true,order = 1)
    public Role role;

    @ManyToOne
    @FormProp(isRel = true)
    public User user;

    public boolean status = false;

    public static Finder<UserRole> on = new Finder<>(UserRole.class);

    @JsonProperty
    @NoJsonReport
    public String path(){
        return Application.createRoute(id);
    }

    @FormProp(tblOnly = true)
    public String name(){
        return user.username;
    }

    @FormProp(tblOnly = true)
    public String role(){
        return role.roleName;
    }

    @JsonProperty
    public String print(){
        return role();
    }

    @Exist
    public boolean exist(Long id){
        return on.existList("user.id",user.id).existList("role.id",role.id).executeExist(id);
    }
}
