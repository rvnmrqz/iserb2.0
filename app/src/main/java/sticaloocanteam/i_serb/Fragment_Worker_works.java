package sticaloocanteam.i_serb;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Worker_works extends Fragment {

    static Context context;
    static SwipeRefreshLayout layoutWorkList;
    static LinearLayout layoutLoading,layoutCurrent;
    static ProgressBar loadingProgressBar;
    static TextView loadingTextViewMessage;
    SwipeRefreshLayout swipeRefreshLayout;

    static String worker_id = "";

    //recycler
    private RecyclerView recyclerView;
    private static Adapter_Booking adapter_booking;
    static Object_BookingRequest object_bookingRequest;
    static List<Object_BookingRequest> objectBookingRequestList;


    //VOLLEY
    static StringRequest stringRequest;
    static RequestQueue requestQueue;

    public Fragment_Worker_works() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_worker_works, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.works_layout_list);
        layoutLoading = view.findViewById(R.id.works_layout_loading);
        layoutCurrent = view.findViewById(R.id.works_layout_current);
        layoutWorkList = view.findViewById(R.id.works_layout_list);

        loadingProgressBar = view.findViewById(R.id.works_layout_loading_progress);
        loadingTextViewMessage = view.findViewById(R.id.works_layout_loading_msg);

        recyclerView = getActivity().findViewById(R.id.workList_recycler);
        objectBookingRequestList = new ArrayList<>();
        adapter_booking = new Adapter_Booking(getContext(), objectBookingRequestList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter_booking);

        SharedPreferences sharedPreferences = context.getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,Context.MODE_PRIVATE);
        worker_id = sharedPreferences.getString(SharedPrefConfig.USER_ID,"");

        checkPendings();
    }


    protected void checkPendings(){
        Log.wtf("checkPendings","Called");
        showLoadingLayout(true,true,null);
        String server_url = ServerDataHolder.serverUrl+"get_data.php";
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            objectBookingRequestList.clear();
                            JSONObject object = new JSONObject(response);
                            JSONArray Jarray = object.getJSONArray("mydata");
                            Log.wtf("onResponse",response.trim());
                            if (Jarray.length() > 0) {
                                Log.wtf("onResponse", "Result count: " + Jarray.length());
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject Jasonobject = Jarray.getJSONObject(i);

                                    String serviceid = Jasonobject.getString("service_id");
                                    String serviceDescription = Jasonobject.getString("skill_description");
                                    String reqDateTime =  Jasonobject.getString("date_time_requested");
                                    String customerName = Jasonobject.getString("fname") +" "+ Jasonobject.getString("lname");
                                    object_bookingRequest = new Object_BookingRequest(serviceid,serviceDescription,reqDateTime,customerName);
                                    objectBookingRequestList.add(object_bookingRequest);
                                }
                                adapter_booking.notifyDataSetChanged();
                                showLoadingLayout(false,false,null);
                                layoutWorkList.setVisibility(View.VISIBLE);
                            }else{
                                //show no work(s) yet
                                showLoadingLayout(true,false,"No work request(s) yet");
                            }

                        }catch (Exception e){
                            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String qry = "SELECT * from service_transactions s INNER JOIN tbl_users u ON s.customer_id = u.user_id INNER JOIN tbl_category_bank c ON s.skill_id = c.skill_id and s.worker_id="+worker_id+";";
                params.put("qry",qry);
                return params;
            }
        };
        int socketTimeout = 1000*5;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    private static void showLoadingLayout(boolean showLayout, boolean showProgress, String msgText){
        if(showLayout){
            layoutLoading.setVisibility(View.VISIBLE);
            if(showProgress) loadingProgressBar.setVisibility(View.VISIBLE);
            else loadingProgressBar.setVisibility(View.GONE);

            if(msgText!=null){
                loadingTextViewMessage.setText(msgText);
                loadingTextViewMessage.setVisibility(View.VISIBLE);
            }
            else loadingTextViewMessage.setVisibility(View.GONE);
        }else{
            layoutLoading.setVisibility(View.GONE);
        }
    }

    public static void checkIfTheresCurrentWork(){
        Log.wtf("Check","checkIfThereCurrentWork");
        String server_url = ServerDataHolder.serverUrl+"get_data.php";
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{

                            JSONObject object = new JSONObject(response);
                            JSONArray Jarray = object.getJSONArray("mydata");
                            Log.wtf("onResponse",response.trim());
                            if (Jarray.length() > 0) {
                                Log.wtf("onResponse", "Result count: " + Jarray.length());
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject Jasonobject = Jarray.getJSONObject(i);

                                    String serviceid = Jasonobject.getString("service_id");
                                    String serviceDescription = Jasonobject.getString("skill_description");
                                    String reqDateTime = Jasonobject.getString("date_time_requested");
                                    String customerName = Jasonobject.getString("fname") + Jasonobject.getString("lname");
                                    object_bookingRequest = new Object_BookingRequest(serviceid,serviceDescription,reqDateTime,customerName);
                                    objectBookingRequestList.add(object_bookingRequest);
                                }
                                adapter_booking.notifyDataSetChanged();
                                showLoadingLayout(false,false,null);
                                layoutWorkList.setVisibility(View.VISIBLE);
                            }else{
                                //show no works yet
                                showLoadingLayout(true,false,"No work request(s) yet");
                            }

                        }catch (Exception e){
                            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String qry = "SELECT * from service_transactions s INNER JOIN tbl_users u ON s.customer_id = u.user_id INNER JOIN tbl_category_bank c ON s.skill_id = c.skill_id and s.worker_id="+worker_id+";";
                params.put("qry",qry);
                return params;
            }
        };
        int socketTimeout = 1000*5;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

}
