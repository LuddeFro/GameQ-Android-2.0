package io.gameq.android;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Created by Ludvig on 14/07/15.
 * Copyright GameQ AB 2015
 */
public final class ConnectionHandler {

    private static ConnectionHandler instance = null;

    private static SecurePreferences preferences;
    private static Context context;
    private static String TAG = "GAMEQ";
    private static String sessionToken = "";
    private static String ip = "127.0.0.1";
    public static long serverDelay = 0;
    private static boolean autoAccept = false;


    protected ConnectionHandler() {
        // Exists only to defeat instantiation.
    }

    public static void instantiateDataModel(Context context) {
        if (preferences == null) {
            preferences = new SecurePreferences(context, ConnectionHandler.generatePreferenceStore(), ConnectionHandler.generateStoreKey(), true);
        }
    }


    public static String post(String extension, String arguments) {
        String serverURL = "http://server.gameq.io:8080/android/";
        String url = serverURL + extension + "?";
        arguments = arguments + "&key=68440fe0484ad2bb1656b56d234ca5f463f723c3d3d58c3398190877d1d963bb";
        Log.i(TAG, "sending: " + arguments + ", to: " + url);
        URL obj;
        String returnString = null;
        HttpURLConnection con;
        BufferedReader in = null;
        DataOutputStream wr = null;
        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("POST");
            StringBuilder response = new StringBuilder();

            // Send post request
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            byte[] buf = arguments.getBytes("UTF-8");
            wr.write(buf, 0, buf.length);
            //wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            Log.i(TAG, "\nSending 'POST' request to URL : " + url);
            Log.i(TAG, "Post parameters : " + arguments);
            Log.i(TAG, "Response Code : " + responseCode);

            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            returnString = response.toString();
            System.out.println(returnString);


        } catch (MalformedURLException e) {
            System.out.println("URL Error (MalformedURLException) for " + url );
        } catch (IOException e) {
            System.out.println("URL Error (IOException) for " + url );
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    System.out.println("wr close exception");

                }
            }
            if (in != null) {
                try {
                    in.close();

                } catch (IOException e) {
                    System.out.println("in close exception");

                }
            }
        }

        if (returnString == null) {
            System.out.println("URL Error (nullResponse) for " + url );
            return null;
        } else {
            return returnString;
        }

    }

    public static void acceptQueue(CallbackGeneral caller, final boolean accept) {
        final CallbackGeneral mCaller = caller;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = null;
                int aa = accept ? 1 : 0;
                String url = "http://"+ip+":8080/android/accept";
                String arguments = "accept="+aa+"&key=68440fe0484ad2bb1656b56d234ca5f463f723c3d3d58c3398190877d1d963bb" + "&session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID();
                Log.i(TAG, "sending: " + arguments + ", to: " + url);
                URL obj;
                HttpURLConnection con;
                BufferedReader in = null;
                DataOutputStream wr = null;
                try {
                    obj = new URL(url);
                    con = (HttpURLConnection) obj.openConnection();

                    //add request header
                    con.setRequestMethod("POST");
                    StringBuilder responsish = new StringBuilder();

                    // Send post request
                    con.setDoOutput(true);
                    wr = new DataOutputStream(con.getOutputStream());
                    byte[] buf = arguments.getBytes("UTF-8");
                    wr.write(buf, 0, buf.length);
                    //wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = con.getResponseCode();
                    Log.i(TAG, "\nSending 'POST' request to URL : " + url);
                    Log.i(TAG, "Post parameters : " + arguments);
                    Log.i(TAG, "Response Code : " + responseCode);

                    in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        responsish.append(inputLine);
                    }
                    in.close();
                    response = responsish.toString();
                    System.out.println(response);


                } catch (MalformedURLException e) {
                    System.out.println("URL Error (MalformedURLException) for " + url );
                } catch (IOException e) {
                    System.out.println("URL Error (IOException) for " + url );
                } finally {
                    if (wr != null) {
                        try {
                            wr.close();
                        } catch (IOException e) {
                            System.out.println("wr close exception");

                        }
                    }
                    if (in != null) {
                        try {
                            in.close();

                        } catch (IOException e) {
                            System.out.println("in close exception");

                        }
                    }
                }

                if (response == null) {
                    System.out.println("URL Error (nullResponse) for " + url );
                } else {

                }



                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }


    public static void logout(CallbackGeneral caller) {
        final CallbackGeneral mCaller = caller;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("logout", "session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                ConnectionHandler.saveEmail("");
                ConnectionHandler.savePassword("");
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }


    public static void login(CallbackGeneral caller, String email, String password) {
        final CallbackGeneral mCaller = caller;
        final String mEmail = email;
        final String mPassword = ConnectionHandler.hashSHA256(password);
        final int device = ConnectionHandler.loadDeviceID();
        String deviceStringTmp = "";
        if (device != 0) {
            deviceStringTmp = "&device_id=" + device;
        }
        final String deviceString = deviceStringTmp;

        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("login", "email="+mEmail+"&password="+mPassword + "&push_token=" + ConnectionHandler.loadToken() + deviceString);
                holder.populate(response);
                if (holder.success) {
                    ConnectionHandler.saveEmail(mEmail);
                    ConnectionHandler.savePassword(mPassword);
                    sessionToken = holder.session_token;
                    if (holder.device_id != 0) {
                        ConnectionHandler.saveDeviceID(holder.device_id);
                    }
                    serverDelay = (System.currentTimeMillis() / 1000L) - holder.time;
                }
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void register(CallbackGeneral caller, String email, String password) {
        final CallbackGeneral mCaller = caller;
        final String mEmail = email;
        final String mPassword = ConnectionHandler.hashSHA256(password);
        final int device = ConnectionHandler.loadDeviceID();
        String deviceStringTmp = "";
        if (device != 0) {
            deviceStringTmp = "&device_id=" + device;
        }
        final String deviceString = deviceStringTmp;

        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("register", "email="+mEmail+"&password="+mPassword + "&push_token=" + ConnectionHandler.loadToken() + deviceString);
                holder.populate(response);
                if (holder.success) {
                    ConnectionHandler.saveEmail(mEmail);
                    ConnectionHandler.savePassword(mPassword);
                    sessionToken = holder.session_token;
                    if (holder.device_id != 0) {
                        ConnectionHandler.saveDeviceID(holder.device_id);
                    }
                    serverDelay = (System.currentTimeMillis() / 1000L) - holder.time;
                }
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void submitForgotPassword(CallbackGeneral caller, String email) {
        final CallbackGeneral mCaller = caller;
        final String mEmail = email;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("forgotPassword", "email="+mEmail);
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void getStatus(CallbackGetStatus caller) {
        final CallbackGetStatus mCaller = caller;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("getStatus", "session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                ip = holder.ip;
                mCaller.callback(holder.success, holder.error, holder.status, holder.game, holder.accept_before);
                return null;
            }
        }.execute();
    }

    public static void getAutoAccept(CallbackAutoAccept caller) {

        final CallbackAutoAccept mCaller = caller;
        mCaller.callback(autoAccept);
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("getAutoAccept", "session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                if (holder.success && holder.error.equals("accept")) {
                    mCaller.callback(true);
                } else {
                    mCaller.callback(false);
                }
                return null;
            }
        }.execute();
    }

    public static void updateAutoAccept(final boolean setEnabled, CallbackGeneral caller) {
        final CallbackGeneral mCaller = caller;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                int a = setEnabled ? 1 : 0;
                String response = post("updateAutoAccept", "auto_accept="+ a +"&session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void setStatus(CallbackGeneral caller, int game, int status) {
        final CallbackGeneral mCaller = caller;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("setStatus", "session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void versionControl(CallbackVersionControl caller) {
        final CallbackVersionControl mCaller = caller;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("versionControl", "");
                holder.populate(response);
                mCaller.callback(holder.success, holder.error, holder.current_version, holder.download_link);
                return null;
            }
        }.execute();
    }

    public static void updateToken(CallbackGeneral caller, String token) {
        final CallbackGeneral mCaller = caller;
        final String mToken = token;
        saveToken(mToken);
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("updateToken", "push_token="+mToken+"&session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void submitCSV(CallbackGeneral caller, String csv, int game, int type) {
        final CallbackGeneral mCaller = caller;
        final String mCSV = csv;
        final int mGame = game;
        final int mType = type;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("submitCSV", "csv="+mCSV+"&game="+mGame+"&type="+mType+"&session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void submitFeedback(CallbackGeneral caller, String feedback) {
        final CallbackGeneral mCaller = caller;
        final String mFeedback = feedback;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("submitFeedback", "feedback="+mFeedback+"&session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void updatePassword(CallbackGeneral caller, String email, String newPassword, String oldPassword) {
        final CallbackGeneral mCaller = caller;
        final String mEmail = email;
        final String mOldPassword = oldPassword;
        final String mNewPassword = newPassword;
        new AsyncTask<Void, Void, Void>() {
            JSONHolder holder = new JSONHolder();
            @Override
            protected Void doInBackground(Void... params) {
                String response = post("updatePassword", "email="+mEmail+"&password="+mOldPassword+"&new_password="+mNewPassword+"&session_token=" + ConnectionHandler.sessionToken + "&device_id=" + ConnectionHandler.loadDeviceID());
                holder.populate(response);
                mCaller.callback(holder.success, holder.error);
                return null;
            }
        }.execute();
    }

    public static void loginWithRememberedDetails(CallbackGeneral caller) {
        final CallbackGeneral mCaller = caller;
        final String mEmail = ConnectionHandler.loadEmail();
        final String mPassword = ConnectionHandler.loadPassword();
        final int device = ConnectionHandler.loadDeviceID();
        String deviceStringTmp = "";
        if (device != 0) {
            deviceStringTmp = "&device_id=" + device;
        }
        final String deviceString = deviceStringTmp;
        if (mEmail == null || mPassword == null || !mEmail.matches("^[a-z0-9._%+\\-]+@[a-z0-9.\\-]+\\.[a-z]{2,4}$") || mPassword.length() < 6) {
            mCaller.callback(false, "invalid login details");
        } else {
            new AsyncTask<Void, Void, Void>() {
                JSONHolder holder = new JSONHolder();
                @Override
                protected Void doInBackground(Void... params) {
                    String response = post("login", "email="+mEmail+"&password="+mPassword + "&push_token=" + ConnectionHandler.loadToken() + deviceString);
                    holder.populate(response);
                    if (holder.success) {
                        ConnectionHandler.saveEmail(mEmail);
                        ConnectionHandler.savePassword(mPassword);
                        sessionToken = holder.session_token;
                        if (holder.device_id != 0) {
                            ConnectionHandler.saveDeviceID(holder.device_id);
                        }
                        serverDelay = (System.currentTimeMillis() / 1000L) - holder.time;
                    }
                    mCaller.callback(holder.success, holder.error);
                    return null;
                }
            }.execute();
        }


    }
























    // MARK: - DataHandling below


    private static void savePassword(String password) {
        preferences.put("password", password);
    }

    private static void saveEmail(String email) {
        preferences.put("email", email);
    }

    public static void saveToken(String token) {
        preferences.put("token", token);
    }

    private static void saveDeviceID(int id) {
        preferences.put("device_id", String.valueOf(id));
    }

    public static void saveFirstLoginDone() {
        preferences.put("first_login", String.valueOf(false));
    }

    public static void saveShouldReceiveNotifications(boolean registered) {
        preferences.put("notifications", String.valueOf(registered));
    }

    public static boolean loadFirstLogin() {
        if (preferences.getString("first_login") == null) {
            return true;
        } else {
            return false;
        }
    }

    private static String loadPassword() {
        if (preferences.getString("password") == null) {
            return "";
        }
        return preferences.getString("password");
    }

    public static String loadEmail() {
        if (preferences != null) {
            if (preferences.getString("email") == null) {
                return "";
            }
            return preferences.getString("email");
        } else {
            return "";
        }
    }

    public static String loadToken() {
        if (preferences.getString("token") == null) {
            return "";
        }
        return preferences.getString("token");
    }

    private static int loadDeviceID() {
        if (preferences.getString("device_id") == null) {
            return 0;
        }
        return Integer.parseInt(preferences.getString("device_id"));
    }

    public static boolean loadShouldReceiveNotifications() {
        return preferences.getString("notifications") == null || preferences.getString("notifications").equals(String.valueOf(true));
    }

    private static String hashSHA256(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());

            byte byteData[] = md.digest();
            return bytesToHex(byteData);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error: " + e);
            return null;
        }
    }

    private static String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder buf = new StringBuilder();
        for (byte aB : b) {
            buf.append(hexDigit[(aB >> 4) & 0x0f]);
            buf.append(hexDigit[aB & 0x0f]);
        }
        return buf.toString().toLowerCase(Locale.US);
    }

    private static String generatePreferenceStore() {
        String a = "aAzpAau2sdn3vpAuis1298370tAhu1baaAAdk";
        a = a.replaceFirst("d+","mAx45");
        return hashSHA256(a);

    }

    private static String generateStoreKey() {
        String a = "nu9y7TvrC56e4xwQ2za3w4scVtf7gBY8cdr5x4es";
        a = a.toUpperCase();
        return hashSHA256(a);
    }







}
