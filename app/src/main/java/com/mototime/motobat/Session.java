package com.mototime.motobat;

import android.content.Context;
import android.content.Intent;

import com.mototime.motobat.activity.LoginActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Session {

    private       String        role;
    private       String        name;
    private       int           id;
    private final MyPreferences prefs;
    private Boolean isCloseMember = false;
    private Boolean isOpenMember = false;

    public Session(Context context) {
        prefs = new MyPreferences(context);
        reset();
    }

    private void reset() {
        name = "";
        role = "Не установлена";
        id = 0;
    }

    public String getName() {
        return name;
    }

    public void setUserName(String userName) {
        this.name = userName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String value) {
        this.role = value;
    }

    public boolean isRO() {
        return getRole().equals("readonly");
    }

    public Boolean isCloseMember() {
        return isCloseMember;
    }

    public void setIsCloseMember(Boolean value) {
        this.isCloseMember = value;
    }

    public Boolean isOpenMember() {
        return isOpenMember;
    }

    public void setIsOpenMember(Boolean value) {
        this.isOpenMember = value;
    }
}
