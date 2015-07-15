package io.gameq.android;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ludvig on 15/07/15.
 */
public class JSONHolder {
    boolean success;
    String error;
    int device_id;
    String session_token;
    String current_version;
    String download_link;
    int game;
    int status;
    long accept_before;
    long time;

    public JSONHolder() {
        success = false;
    }

    public void populate(String jsonString) {
        JSONObject jObject = null;
        try {
            jObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            success = false;
            System.out.println("json parse fail");
        }
        try {
            success = jObject.getInt("success") != 0;
        } catch (JSONException e) {
            success = false;
        }
        try {
            error = jObject.getString("error");
        } catch (JSONException e) {
            error = "";
        }
        try {
            status = jObject.getInt("status");
        } catch (JSONException e) {
            status = 0;
        }
        try {
            game = jObject.getInt("game");
        } catch (JSONException e) {
            game = 0;
        }
        try {
            accept_before = jObject.getInt("accept_before");
        } catch (JSONException e) {
            accept_before = 0;
        }
        try {
            device_id = jObject.getInt("device_id");
        } catch (JSONException e) {
            device_id = 0;
        }
        try {
            session_token = jObject.getString("session_token");
        } catch (JSONException e) {
            session_token = "";
        }
        try {
            current_version = jObject.getString("current_version");
        } catch (JSONException e) {
            current_version = "";
        }
        try {
            download_link = jObject.getString("download_link");
        } catch (JSONException e) {
            download_link = "";
        }
        try {
            time = jObject.getInt("time");
        } catch (JSONException e) {
            time = System.currentTimeMillis() / 1000L;
        }
    }
}