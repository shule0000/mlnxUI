package com.medlinx.core.patient;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "user")
public class UserInfo {

    public static enum Role {

        ADMINISTRATOR, PHYSICIAN, NURSE
    }

    private String id;

    private String password;

    private Role role;

    private String fullName;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public Role getRole() {

        return role;
    }

    public void setRole(Role role) {

        this.role = role;
    }

    public String getFullName() {

        return fullName;
    }

    public void setFullName(String fullName) {

        this.fullName = fullName;
    }
}
