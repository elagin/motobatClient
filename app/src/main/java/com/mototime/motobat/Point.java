package com.mototime.motobat;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mototime.motobat.utils.MyUtils;
import com.mototime.motobat.utils.NewID;

import org.json.JSONObject;

import java.util.Date;

/**
 * Created by pavel on 07.07.15.
 */
public class Point {
    private Date created;
    private int id;
    private int ownerID;
    private String ownerName;

    private Location location;
    private String address;
    private String descr;
    private int karma;

    MyApp myApp = null;

    public Point(JSONObject json, Context context ) throws PointException{
        myApp = (MyApp) context.getApplicationContext();
        createPoint(json);
    }

    public class PointException extends Exception {
        private static final long serialVersionUID = 1L;

        public PointException() {
            super();
        }
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getDescr() {
        return descr;
    }

    public Date getCreated() {
        return created;
    }

    private void createPoint(JSONObject data) throws PointException {
//        if (!checkPrerequisites(data))
//            throw new PointException();
//        attributes = new HashMap<>();
        //{"owner":244452742,"id":7,"karma":0,"lng":37.631439208984,"created":1436646776,"lat":55.756504058838}]
        try {
            location = new Location(LocationManager.NETWORK_PROVIDER);
            location.setLatitude(Float.parseFloat(data.getString("lat")));
            location.setLongitude(Float.parseFloat(data.getString("lng")));
            location.setAccuracy(0);
            id = data.getInt("id");
            //address = data.getString("address");
            //created = myApp.dateFormat.parse(data.getString("created_date"));
            Long date = Long.parseLong(data.getString("created"));
            created = new Date(date * 1000);
            ownerID = data.getInt("owner");
            karma = data.getInt("karma");
            //descr = data.getString("descr");
        } catch (Exception e) {
            e.printStackTrace();
            throw new PointException();
        }
    }

    public void inflateRow(final Context context, ViewGroup viewGroup) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout fl;
//        if (hasInOwners()) {
//            fl = (FrameLayout) li.inflate(R.layout.accident_row_i_was_here, viewGroup, false);
//            switch (getStatusString()) {
//                case "acc_status_end":
//                    fl.setBackgroundResource(R.drawable.owner_accident_ended);
//                    break;
//                case "acc_status_hide":
//                    fl.setBackgroundResource(R.drawable.owner_accident_hidden);
//            }
//        } else
        {
            fl = (FrameLayout) li.inflate(R.layout.point_row, viewGroup, false);
//            switch (getStatusString()) {
//                case "acc_status_end":
//                    fl.setBackgroundResource(R.drawable.accident_row_ended);
//                    break;
//                case "acc_status_hide":
//                    fl.setBackgroundResource(R.drawable.accident_row_hidden);
//            }
        }

        StringBuilder generalText = new StringBuilder();
        //generalText.append(getTypeText());
        generalText.append(address);
//        if (!med.equals("mc_m_na")) {
//            generalText.append(", ").append(getMedText());
//        }
//        generalText.append("(").append(getDistanceText()).append(")\n").append(address).append("\n").append(descr);
//        String msgText = "<b>" + String.valueOf(messages.size()) + "</b>";
//        if (countUnreadMessages() > 0)
//            msgText += "<font color=#C62828><b>(" + String.valueOf(countUnreadMessages()) + ")</b></font>";
//
//        switch (getStatusString()) {
//            case "acc_status_end":
//                ((TextView) fl.findViewById(R.id.accident_row_content)).setTextColor(0x70FFFFFF);
//                break;
//            case "acc_status_hide":
//                ((TextView) fl.findViewById(R.id.accident_row_content)).setTextColor(0x30FFFFFF);
//        }

        ((TextView) fl.findViewById(R.id.accident_row_content)).setText(generalText + " \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0");
        ((TextView) fl.findViewById(R.id.accident_row_time)).setText(MyUtils.getIntervalFromNowInText(created));
//        ((TextView) fl.findViewById(R.id.accident_row_unread)).setText(Html.fromHtml(msgText));

        fl.setId(NewID.id());
//        fl.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                AccidentsGeneral.toDetails(v.getContext(), id);
//            }
//        });
//        fl.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                PopupWindow popupWindow;
//                popupWindow = (new AccidentListPopup(context, id, false)).getPopupWindow();
//                int viewLocation[] = new int[2];
//                v.getLocationOnScreen(viewLocation);
//                popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, viewLocation[0], viewLocation[1]);
//                return true;
//            }
//        });
        viewGroup.addView(fl);
    }

    public boolean isInvisible() {
//        Double dist = getDistanceFromUser();
//        return isHidden() && !Role.isModerator() || (dist != null && ((dist >= prefs.getVisibleDistance() * 1000) || prefs.toHideAccType(type)));
        return false;
    }

    public int getHoursAgo() {
        return (int) ((new Date()).getTime() - created.getTime()) / 3600000;
    }

    public Location getLocation() {
        return location;
    }
}