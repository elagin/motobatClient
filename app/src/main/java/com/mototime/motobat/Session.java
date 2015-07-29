package com.mototime.motobat;

import android.content.Context;
import android.content.Intent;

import com.mototime.motobat.activity.LoginActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by pavel on 08.07.15.
 */
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
    private final Context context;
    private       String  login;
    private       String  password;
    private MyApp   myAmp    = null;
    private Boolean isMember = false;

    public Session(Context context, MyApp myAmp) {
        this.context = context;
        this.myAmp = myAmp;
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

    public Boolean auth(Context context, String login, String password) {
        this.password = password;
        this.login = login;
//        AuthRequest auth = new AuthRequest(context);
//        auth.setUserID(login);
//        auth.setPassword(password);
//        JSONObject result = auth.execute();
//        if (auth.isError(result)) {
//            String isError = auth.getError(result);
//            Toast.makeText(context, isError, Toast.LENGTH_LONG).show();
//            isAuthorized = false;
//        } else {
//            try {
//                name = result.getString("name");
//                role = result.getString("role");
//                id = Integer.parseInt(result.getString("id"));
//                if (name.length() > 0) {
//                    prefs.setUserID(login);
//                    prefs.setPassword(password);
//                    isAuthorized = true;
//                } else {
//                    isAuthorized = false;
//                }
//            } catch (JSONException e) {
//                Toast.makeText(context, context.getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
//                isAuthorized = false;
//            }
//        }
        return isAuthorized;
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

    public Boolean isMember() {
        return isMember;
    }

    public void setIsMember(Boolean value) {
        this.isMember = value;
    }

    public void collectData() {
        //new IsMemberVKRequest(new IsMemberVKCallback(), context, myAmp.getPreferences().getVkToken());
        //new GetUserInfoVKRequest(new GetUserInfoVKCallback(), context, myAmp.getPreferences().getVkToken());
        //new RoleRequest(new RoleCallback(), context, myAmp.getPreferences().getUserID());
    }

//    private class IsMemberVKCallback implements AsyncTaskCompleteListener {
//        @Override
//        public void onTaskComplete(JSONObject result) {
//            try {
//                isMember = result.getBoolean("response");
//            } catch (JSONException e) {
//            }
//        }
//    }

//    private class RoleCallback implements AsyncTaskCompleteListener {
//
//        @Override
//        public void onTaskComplete(JSONObject response) {
//            String role = "readonly";
//            if (!RequestErrors.isError(response)) {
//                try {
//                    JSONObject result = response.getJSONObject("RESULT");
//                    role = result.getString("role");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            setRole(role);
//        }
//    }

//    private class GetUserInfoVKCallback implements AsyncTaskCompleteListener {
//
//        @Override
//        public void onTaskComplete(JSONObject response) throws JSONException {
//            try {
//                StringBuilder userName = new StringBuilder();
//                JSONArray resArray = (JSONArray) response.get("response");
//                JSONObject result = (JSONObject) resArray.get(0);
//                String firstName = result.getString("first_name");
//                String lastName = result.getString("last_name");
//                if (lastName != null)
//                    userName.append(lastName);
//                if (firstName != null) {
//                    if (userName.length() > 0)
//                        userName.append(" ");
//                    userName.append(firstName);
//                }
//                name = userName.toString();
//            } catch (JSONException e) {
//            }
//        }
//    }
}
