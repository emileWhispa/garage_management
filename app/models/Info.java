package models;

import Helper.Finder;
import Helper.FormProp;

import javax.persistence.Entity;

@Entity
public class Info extends BaseModel {

    @FormProp(type = "checkbox",display = "Allow to enter non existing spare parts")
    public boolean isOldAllowed = false;

    @FormProp
    public String email;

    @FormProp
    public String address;

    @FormProp
    public String phone;

    public static Finder<Info> on = new Finder<>(Info.class);

}
