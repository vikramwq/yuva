package com.multitv.yuv.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import com.multitv.yuv.R;
import com.multitv.yuv.models.tvguide.Epg;
import com.multitv.multitvcommonsdk.utils.HttpUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.multitv.yuv.api.ApiRequest;
import com.multitv.yuv.models.tvguide.Tvguide;
import com.multitv.yuv.sharedpreference.SharedPreference;

/**
 * Created by naseeb on 10/7/2016.
 */

public class EpgUtils {

    private static final String TAG = "EpgUtils";

    public static void getEpgUrlAndName(final Context context, final String channelName, final Date currentTime, final Date currentDate
            , final TextView programNameTV, final ImageView programImageView, final OnEPGDataFetched onEPGDataFetched) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SharedPreference sharedPreference = new SharedPreference();

                    long epgJsonSavingTime = sharedPreference.getPreferencesLong(context, "EPG_SAVING_TIME" + channelName);
                    Date epgJsonSavingDate = new Date(epgJsonSavingTime);


                    String epgJson = sharedPreference.getPreferencesString(context, "EPG" + channelName);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:SS");

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(context).load(R.mipmap.place_holder)
                                    .into(programImageView);
                        }
                    });

                    //Comparing if dates are on same day


                    Tracer.error("MKR", " LIST run:1 " + simpleDateFormat.format(currentDate) + "  ");
                    Tracer.error("MKR", " LIST run:1.1 " + sdf.format(currentTime) + "  ");
                    Tracer.error("MKR", " LIST run:2 " + epgJsonSavingDate + "  ");
                    Tracer.error("MKR", " LIST run:3 " + simpleDateFormat.format(epgJsonSavingDate) + "  ");
                    String URL = AppUtils.generateEpgUrl(context, ApiRequest.TV_GUIDE + channelName.replace(" ", "%20"));
                    Tracer.debug(TAG, "Epg url is " + URL);

                    if (!simpleDateFormat.format(currentDate).equals(simpleDateFormat.format(epgJsonSavingDate))
                            || epgJson.isEmpty()) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Setting dummy text and place holder
                                programNameTV.setText(context.getResources().getString(R.string.loding_hint));
                            }
                        });

                        HttpUtils httpUtils = new HttpUtils();
                        String url = AppUtils.generateEpgUrl(context, ApiRequest.TV_GUIDE + channelName.replace(" ", "%20"));
                        Tracer.debug(TAG, "Epg url is " + url);

                        epgJson = httpUtils.executeHttpGetRequest(url);

                        sharedPreference.setPreferencesString(context, "EPG" + channelName, epgJson);
                        String times = String.valueOf(currentTime);
                        sharedPreference.setPreferencesLong(context, "EPG_SAVING_TIME" + channelName, currentDate.getTime());
                    }

                    if (epgJson != null && !epgJson.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(epgJson);
                        if (jsonObject.optInt("code") == 1) {
                            String data = jsonObject.optString("result");

                            if (!data.equals("No record found")) {
                                List<Tvguide> tvGuideList = Json.parseList(data, Tvguide.class);

                                final List<Epg> epgListFinal = new ArrayList<>();
                                final List<Epg> epgListFinal1 = new ArrayList<>();

                                if (tvGuideList.size() != 0) {
                                    for (Epg epg : tvGuideList.get(0).epg) {
                                        try {
//                                            //---render data from current time---*****
//                                            Date epgTime = new SimpleDateFormat("HH:mm:ss").parse(epg.progtime);
//                                            if (epgTime.after(currentTime) || epgTime.equals(currentTime))
                                            Date epgTime = new SimpleDateFormat("HH:mm:ss").parse(epg.progtime);
                                            Tracer.error(TAG, "getCurrentEpgName:>>>>>>>>>epg.progtime>>>>>>>> " + epg.progtime + "     " + epg.title + "    " + epgTime.getTime() + "     " + currentTime.getTime());
                                            if (epgTime.getTime() > currentTime.getTime())
                                                epgListFinal.add(epg);
                                            else
                                                epgListFinal1.add(epg);
                                        } catch (Exception e) {
                                            ExceptionUtils.printStacktrace(e);
                                        }
                                    }
                                }

                                if (epgListFinal1.size() != 0)
                                    epgListFinal.add(0, epgListFinal1.get(epgListFinal1.size() - 1));

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (epgListFinal.size() != 0) {
                                            programNameTV.setText(epgListFinal.get(0).title);

                                            if (!epgListFinal.get(0).logo.isEmpty() && epgListFinal.get(0).logo != null){
                                                Picasso.with(context).load(epgListFinal.get(0).logo)
                                                        .placeholder(R.mipmap.place_holder).error(R.mipmap.place_holder)
                                                        .resize(programImageView.getWidth(),
                                                                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, context.getResources().getDisplayMetrics()))
                                                        .into(programImageView);
                                        }


                                            if (onEPGDataFetched != null) {
                                                EPGDataReceived epgDataReceived = new EPGDataReceived();
                                                epgDataReceived.mName = epgListFinal.get(0).title;
                                                epgDataReceived.mThumb = epgListFinal.get(0).logo;
                                                epgDataReceived.duration = epgListFinal.get(0).duration;
                                                onEPGDataFetched.onEPGDataFetched(epgDataReceived);
                                            }
                                            Tracer.error("***epg -image***", epgListFinal.get(0).logo);
                                            Tracer.error("****epg-title***", "" + epgListFinal.get(0).title);
                                        } else {
                                            programNameTV.setText(context.getResources().getString(R.string.no_info_available));

                                            Picasso.with(context).load(R.mipmap.place_holder)
                                                    .into(programImageView);
                                        }
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    ExceptionUtils.printStacktrace(e);
                }
            }
        }).start();
    }


    public static String getCurrentEpgName(final Context context, final String channelName, final Date currentTime, final Date currentDate) {
        try {
            SharedPreference sharedPreference = new SharedPreference();
            long epgJsonSavingTime = sharedPreference.getPreferencesLong(context, "EPG_SAVING_TIME" + channelName);
            Date epgJsonSavingDate = new Date(epgJsonSavingTime);
            String epgJson = sharedPreference.getPreferencesString(context, "EPG" + channelName);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:SS");
            if (!simpleDateFormat.format(currentDate).equals(simpleDateFormat.format(epgJsonSavingDate))
                    || epgJson.isEmpty()) {
                HttpUtils httpUtils = new HttpUtils();
                String url = AppUtils.generateEpgUrl(context, ApiRequest.TV_GUIDE + channelName.replace(" ", "%20"));
                epgJson = httpUtils.executeHttpGetRequest(url);
                sharedPreference.setPreferencesString(context, "EPG" + channelName, epgJson);
                sharedPreference.setPreferencesLong(context, "EPG_SAVING_TIME" + channelName, currentDate.getTime());
            }
            if (epgJson != null && !epgJson.isEmpty()) {
                JSONObject jsonObject = new JSONObject(epgJson);
                if (jsonObject.optInt("code") == 1) {
                    String data = jsonObject.optString("result");
                    if (!data.equals("No record found")) {
                        List<Tvguide> tvGuideList = Json.parseList(data, Tvguide.class);
                        final List<Epg> epgListFinal = new ArrayList<>();
                        final List<Epg> epgListFinal1 = new ArrayList<>();
                        if (tvGuideList.size() != 0) {
                            for (Epg epg : tvGuideList.get(0).epg) {
                                try {
                                    Date epgTime = new SimpleDateFormat("HH:mm:ss").parse(epg.progtime);
                                    Tracer.error(TAG, "getCurrentEpgName:>>>>>>>>>epg.progtime>>>>>>>> " + epg.progtime + "     " + epg.title + "    " + epgTime.getTime() + "     " + currentTime.getTime());
                                    /*if (epgTime.getTime() <= currentTime.getTime())
                                        epgListFinal.add(epg);
                                    else
                                        epgListFinal1.add(epg);*/

                                    if (epgTime.getTime() > currentTime.getTime())
                                        epgListFinal.add(epg);
                                    else
                                        epgListFinal1.add(epg);
                                } catch (Exception e) {
                                    ExceptionUtils.printStacktrace(e);
                                }
                            }
                        }
                        if (epgListFinal1.size() != 0)
                            epgListFinal.add(0, epgListFinal1.get(epgListFinal1.size() - 1));

                        return epgListFinal.get(0).title;
                        /*return epgListFinal.get(epgListFinal.size() - 1).title;*/
                    }
                } else {

                }
            }
        } catch (Exception e) {
            Tracer.error(TAG, "getEpgUrlAndName: " + e.getMessage());
            e.printStackTrace();
        }
        return "Unknown";
    }


    public interface OnEPGDataFetched {
        void onEPGDataFetched(EPGDataReceived epgDataReceived);
    }

    public static class EPGDataReceived {
        public String mName;
        public String mThumb;
        public Integer duration;
    }
}
