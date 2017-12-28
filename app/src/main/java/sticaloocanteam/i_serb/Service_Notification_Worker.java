package sticaloocanteam.i_serb;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arvin on 7/10/2017.
 */

public class Service_Notification_Worker extends Service {

    static  String TAG = "Service_Notification_Worker";

    //TIMER
    static String worker_id;
    static String TOKEN;
    static Handler handler;
    static Timer timer;
    static TimerTask timerTask;
    int tick=0;
    int seconds;
    int maxCount=5;
    boolean continueCount=true;
    int new_count=0;
    //NOTIFICATION
    MediaPlayer mp;
    Uri notification;
    static NotificationManager nm;
    static NotificationCompat.Builder b;

    //VOLLEY
    static RequestQueue requestQueue;
    static SharedPreferences sharedPreferences;
    static ArrayList<String > req_update_id_list = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //start timer ticks
        Log.wtf("NotificationService","Serivice Started");
        try{

            sharedPreferences = getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,MODE_PRIVATE);
            worker_id = sharedPreferences.getString(SharedPrefConfig.USER_ID,null);
            TOKEN = sharedPreferences.getString(SharedPrefConfig.USER_TOKEN,"");
           if(worker_id != null){
               startTimer();
           }else{
               stopService(new Intent(this,Service_Notification_Worker.class));
               Log.wtf("Service_Worker","Service Stopped");
           }
        }
        catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK", "onStartCommand Exception "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","onStartCommand Throwable: "+t.getMessage());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try{
            super.onDestroy();
            handler.removeCallbacksAndMessages(null);
            timer.cancel();
            timer.purge();
            timerTask.cancel();
            timer=null;
            timerTask=null;
            handler=null;
            Log.wtf("service_doWork", "request service is stopped");
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","onDestroy Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","onDestroy Throwable: "+t.getMessage());
        }

    }

    public void startTimer(){
        try{
            Log.wtf("service_startTimer", "Timer started");
            initializeTimer();
            timer.scheduleAtFixedRate(timerTask, seconds, seconds);
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","startTimer Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","startTimer Throwable: "+t.getMessage());
        }

    }

    private void restartCounting(){
        try{
            tick=0;
            continueCount=true;
            Log.wtf("service_restartCounting", "Timer restarted");
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","restartCounting Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","restartCounting Throwable: "+t.getMessage());
        }

    }

    private void initializeTimer(){
        try{
            seconds=1000;
            //*********************
            //just to clear the objects
            timer=null;
            timerTask=null;
            handler=null;
            //********************
            handler = new Handler();
            timer = new Timer(false);
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(continueCount){
                                tick++;
                                if(tick==maxCount) {
                                    if (isNetworkAvailable()){
                                        doWork();
                                    }
                                    else{
                                        Log.wtf("tick==maxcount","no network available, restarting");
                                        restartCounting();
                                    }
                                }
                            }
                            Log.wtf("service_timer", "Timer Tick: "+tick);
                        }
                    });
                }
            };
            Log.wtf("service_initializeTimer", "Timer initialized");
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","initializeTimer Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","initializeTimer Throwable: "+t.getMessage());
        }
    }

    private void stopCounting(){
        try{
            tick=0;
            continueCount=false;
            Log.wtf("service_stopCounting", "Timer stopped");
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","stopCounting Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","stopCounting Throwable: "+t.getMessage());
        }
    }

    private void doWork(){
        try{
            //start the taskworker
            String url = ServerDataHolder.serverUrl+"/get_data.php";
            requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                Log.wtf(TAG,"doWork - Response: "+response);
                                req_update_id_list = new ArrayList<>();
                                req_update_id_list.clear();
                                JSONObject object = new JSONObject(response);
                                JSONArray Jarray = object.getJSONArray("mydata");
                                if (Jarray.length() > 0) {
                                    for (int x=0;x<Jarray.length();x++){
                                        JSONObject Jasonobject = Jarray.getJSONObject(x);
                                        req_update_id_list.add(Jasonobject.getString("service_id"));
                                    }

                                    Log.wtf("onResponse","Response Count: "+new_count);
                                    //a notification is received
                                   if (Fragment_Worker_works.context != null) {
                                       //app is running
                                       Fragment_Worker_works.checkIfTheresCurrentWork();
                                       showNotification();
                                       playRingtone();
                                       updateDelivered();
                                   }
                                }
                                restartCounting();
                            }catch (Exception e){
                                Log.wtf("onResponse","Exception "+e.getMessage());
                                restartCounting();
                            }catch (Throwable t){
                                restartCounting();
                                Log.wtf("onResponse","Throwable "+t.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.wtf("NotificationService","An error occured in requestQue \nError\n"+error.getMessage()+"\nCause: "+error.getCause());
                            restartCounting();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String qry = "select service_id from service_transactions where status='pending' and delivered=0 and worker_id ="+worker_id+";";
                    Log.wtf(TAG, "DoWork Map<> qry: "+qry);
                    params.put("qry",qry);
                    stopCounting();
                    return params;
                }
            };
            int socketTimeout = ServerDataHolder.TIMEOUT;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            stringRequest.setShouldCache(false);
            requestQueue.add(stringRequest);
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","doWork Exception: "+e.getMessage());
            restartCounting();
        }catch (Throwable t){
            restartCounting();
            Log.wtf("ServiceNotification_TRUCK","doWork Throwable: "+t.getMessage());
        }
    }

    protected void updateDelivered(){
        Log.wtf("updateDeliveredReportsNotif()","CALLED");
        String url = ServerDataHolder.serverUrl+"/do_query.php";
        RequestQueue requestQueue  = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.wtf("updateDeliveredReportsNotif()","Response: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.wtf("updateDeliveredReportsNotif()","Error: "+getVolleyError(error));

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String entries ="";
                for(int x = 0;x<req_update_id_list.size();x++){
                    entries= entries+req_update_id_list.get(x);
                    if(x!=(req_update_id_list.size()-1)){
                        //not the last item in the list
                        entries = entries+", ";
                    }
                }
                String query = "UPDATE service_transactions SET delivered = 1 WHERE service_id IN("+entries+");";
                Log.wtf("updateDeliveredReportsNotif()","Map<String><String>, Query: "+query);
                params.put("query",query);
                return params;
            }
        };
        int socketTimeout = ServerDataHolder.TIMEOUT;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        request.setShouldCache(false);
        requestQueue.add(request);
    }
    protected String getVolleyError(VolleyError volleyError){
        String message="";
        if (volleyError instanceof NetworkError) {
            message = "Network Error Encountered";
            Log.wtf("getVolleyError (Volley Error)","NetworkError");
        } else if (volleyError instanceof ServerError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)","ServerError");
        } else if (volleyError instanceof AuthFailureError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)","AuthFailureError");
        } else if (volleyError instanceof ParseError) {
            message = "An error encountered";
            Log.wtf("getVolleyError (Volley Error)","ParseError");
        } else if (volleyError instanceof NoConnectionError) {
            message = "No internet connection";
            Log.wtf("getVolleyError (Volley Error)","NoConnectionError");
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection Timeout";
            Log.wtf("getVolleyError (Volley Error)","TimeoutError");
        }
        return message;
    }

    private boolean isNetworkAvailable() {
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager)  getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","isNetworkAvailable Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","isNetworkAvailable Throwable: "+t.getMessage());
        }
        return false;
    }

    protected void playRingtone(){
        try{
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mp = MediaPlayer.create(getApplicationContext(), notification);
            mp.start();
            Log.wtf("PlayRingtone","Ringtone played");
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    Log.wtf("playringtone","mp is released");
                }
            });
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","playRingtone Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","playRingtone Throwable: "+t.getMessage());
        }
    }

    protected void showNotification(){
        try{
            final Intent mainIntent = new Intent(this,MainActivity_Worker.class);
            mainIntent.putExtra("notif","notify");
            final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    (mainIntent), PendingIntent.FLAG_UPDATE_CURRENT);
            b = new NotificationCompat.Builder(this);
            b.setAutoCancel(true)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.logo)
                    .setTicker("New Notification Received")
                    .setContentTitle("i-Serb")
                    .setContentText("New Notification(s)")
                    .setContentIntent(pendingIntent);
            nm = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE);
            nm.notify(100, b.build());
        }catch (Exception e){
            Log.wtf("ServiceNotification_TRUCK","showNotification Exception: "+e.getMessage());
        }catch (Throwable t){
            Log.wtf("ServiceNotification_TRUCK","showNotification Throwable: "+t.getMessage());
        }
    }
}
