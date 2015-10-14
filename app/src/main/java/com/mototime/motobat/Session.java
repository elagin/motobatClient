package com.mototime.motobat;

import android.content.Context;
import android.content.Intent;

import com.mototime.motobat.activity.LoginActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Session {

    private static final String[] ReadOnly  = new String[]{"readonly", "banned", "standart", "moderator", "admin", "developer"};
    private static final String[] Standart  = new String[]{"standart", "moderator", "admin", "developer"};
    private static final String[] Moderator = new String[]{"moderator", "admin", "developer"};
    private static final String[] Admin     = new String[]{"admin", "developer"};
    private static final String[] Developer = new String[]{"developer"};

    private       String        role;
    private       String        name;
    private       int           id;
    private final MyPreferences prefs;
    private boolean isAuthorized = false;
    private Boolean isCloseMember = false;
    private Boolean isOpenMember = false;

    public Session(Context context, MyApp myAmp) {
        prefs = new MyPreferences(context);
        reset();

//        if (!prefs.getUserID().isEmpty()) {
//            login = prefs.getUserID();
//            password = prefs.getPassword();
//            if (!auth(context, login, password)) {
//                showLogin(context);
//            }
//        } else {
//            showLogin(context);
//        }
    }

    private void showLogin(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
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

    public int getID() {
        return id;
    }

    public String getLogin() {
        return prefs.getUserID();
    }

    public static String makePassHash(String pass) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }

    // Ну нету в java параметров по-умолчанию.
    public String makePassHash() {
        return makePassHash(prefs.getPassword());
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void logoff() {
        reset();
        this.isAuthorized = false;
    }

    /*
        public boolean isRO() {
            return Arrays.asList(ReadOnly).contains(getRole());
        }
    */
    public boolean isRO() {
        return getRole().equals("readonly");
    }

    public boolean isStandart() {
        return Arrays.asList(Standart).contains(getRole());
    }

    public boolean isModerator() {
        return Arrays.asList(Moderator).contains(getRole());
    }

    public boolean isAdmin() {
        return Arrays.asList(Admin).contains(getRole());
    }

    public boolean isDeveloper() {
        return Arrays.asList(Developer).contains(getRole());
    }

    public String getName(Context context) {
        //Порядок важен.
        if (isDeveloper())
            return context.getString(R.string.role_developer);
        else if (isAdmin())
            return context.getString(R.string.role_admin);
        else if (isModerator())
            return context.getString(R.string.role_moderator);
        else if (isStandart())
            return context.getString(R.string.role_user);
        else
            return context.getString(R.string.role_read_only);
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
