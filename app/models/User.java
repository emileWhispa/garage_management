package models;

import Helper.EntityProperty;
import Helper.Exist;
import Helper.Finder;
import Helper.FormProp;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import controllers.AdminController;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityProperty(name = "User setting",type = "g-setting",ctrl=AdminController.class)
public class User extends BaseModel{

    @FormProp(tbl=true)
    public String phone;

    public String email;

    @FormProp(tbl = true)
    public String username;

    @FormProp(type = "password")
    private String password;

    @JsonBackReference
    @OneToMany(mappedBy = "user")
    public List<UserRole> userRoleList = new ArrayList<>();

    public static Finder<User> on = new Finder<>(User.class);


    private Finder<User> logObject(){
        return on.existList("username",this.username)
                .existList("password",this.password);
    }

    @Exist
    public boolean exist(Long id){
        return on.exist("username",this.username,id);
    }

    public boolean isAuthorized(){
        return logObject().executeExist();
    }

    public User logUser(){
        return logObject().object();
    }


    public boolean inList(UserRole role){
        for (UserRole userRole : this.userRoleList){
            if( userRole.id == role.id ) return true;
        }
        return false;
    }

    @JsonProperty
    public String print(){
        return this.username;
    }


}
