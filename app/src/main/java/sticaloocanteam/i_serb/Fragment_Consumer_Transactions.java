package sticaloocanteam.i_serb;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static sticaloocanteam.i_serb.Fragment_Consumer_Finder.getVolleyError;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Consumer_Transactions extends Fragment {

    static String TAG  = "Fragment_Consumer_Transactions";
    static Context context;
    SharedPreferences sharedPreferences;
    static RequestQueue requestQueue;
    static StringRequest stringRequest;
    static String customer_id = "";
    static LinearLayout layout_current_info;

    static ProgressBar progressBar;

    static String CANCEL= "Cancel";
    static String END_TRANSACTION ="End Transaction";
    static String IN_PROGRESS ="In progress";
    static String PENDING="Pending";


    //review
    Dialog dialog;
    android.app.AlertDialog.Builder builder;
    TextView txtReviewRecipe;
    int review_value = 0;
    LinearLayout layout_writereview;
    LinearLayout layout_reviews;
    LinearLayout layout_review_message;

    //Current transactions
    static TextView txtService_id, txtStatus, txtWorkerName, txtSkillDescription, txtSkillFee;
    static CircleImageView worker_imageview;
    static Button btnEnd, btnTrack;

    public Fragment_Consumer_Transactions() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consumer_transactions, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = context.getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,Context.MODE_PRIVATE);
        customer_id = sharedPreferences.getString(SharedPrefConfig.USER_ID,"");

        txtService_id = view.findViewById(R.id.txtService_id);
        txtStatus = view.findViewById(R.id.txtStatus);
        txtWorkerName = view.findViewById(R.id.txtWorkerName);
        txtSkillDescription = view.findViewById(R.id.txtSkillname);
        txtSkillFee = view.findViewById(R.id.txtSkill_service_fee);
        layout_current_info = view.findViewById(R.id.layout_current_info);
        worker_imageview = view.findViewById(R.id.worker_profile);
        btnEnd = view.findViewById(R.id.btnEnd);
        btnTrack = view.findViewById(R.id.btnTrack);
        progressBar = view.findViewById(R.id.progressBar);

        loadCurrentTransactions();
        btnListeners();
    }

    public static void loadCurrentTransactions(){
        String server_url = ServerDataHolder.serverUrl+"get_data.php";
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Log.wtf(TAG,"onResponse: "+response.trim());
                            JSONObject object = new JSONObject(response);
                            JSONArray Jarray = object.getJSONArray("mydata");

                            if (Jarray.length() > 0) {
                                Log.wtf("onResponse", "Result count: " + Jarray.length());
                                String service_id, fname,lname,profile_picture,skill_description,status,service_fee;

                                if(Jarray.length()>0){

                                    JSONObject Jasonobject = Jarray.getJSONObject(0);

                                    service_id = Jasonobject.getString("service_id");
                                    fname = Jasonobject.getString("fname");
                                    lname = Jasonobject.getString("lname");
                                    skill_description = Jasonobject.getString("skill_description");
                                    status = Jasonobject.getString("status");
                                    service_fee = Jasonobject.getString("service_fee");
                                    profile_picture = ServerDataHolder.profileImagePath+Jasonobject.getString("profile_picture");

                                    Glide.with(context)
                                            .load(profile_picture)
                                            .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                                            .into(worker_imageview);

                                    txtService_id.setText(service_id);
                                    txtStatus.setText(status);
                                    txtWorkerName.setText(fname +" "+lname);
                                    txtSkillDescription.setText(skill_description);
                                    txtSkillFee.setText(service_fee);

                                    layout_current_info.setVisibility(View.VISIBLE);

                                    if (txtStatus.getText().toString().equalsIgnoreCase(IN_PROGRESS)){
                                        btnEnd.setText(END_TRANSACTION);
                                        btnTrack.setVisibility(View.VISIBLE);
                                   }else{
                                        //pending
                                        btnEnd.setText(CANCEL);
                                    }
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }catch (Exception e){
                            Log.wtf("Exception","Loading Current Exception: "+e.getMessage());
                            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String qry = "SELECT s.service_id,u.fname,u.lname,u.profile_picture,b.skill_description,s.status,s.service_fee from service_transactions s INNER JOIN tbl_users u ON s.worker_id = u.user_id  INNER JOIN tbl_category_bank b ON s.skill_id = b.skill_id WHERE s.status IN ('Pending'|'In Progress') and s.customer_id = "+customer_id+";";
                Log.wtf(TAG,"LoadCurrent Map<> Qry: "+qry);
                params.put("qry",qry);
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
    }

    protected void btnListeners( ){

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnEnd.getText().toString().equalsIgnoreCase(END_TRANSACTION)){
                    //SHOW RATING
                    showReviewDialog();
                }else if(btnEnd.getText().toString().equalsIgnoreCase(CANCEL)){

                }
            }
        });

        btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap();
            }
        });
    }

    private void showReviewDialog(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        review_value = 5;
        View view =  inflater.inflate(R.layout.popup_dialog_ratings, null);
        RatingBar review_ratingbar =  view.findViewById(R.id.review_ratingbar);
        final TextView txt_review_ratingValue = view.findViewById(R.id.review_ratingValue);
        final EditText txt_review_description =  view.findViewById(R.id.edittxt_reviewDescription);
        Button btn_submit =  view.findViewById(R.id.review_submitButton);


        review_ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                review_value = (int) rating;
                Log.wtf("RatingChanged",rating+"");
                txt_review_ratingValue.setText(review_value+"");
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("btn_submit","submit clicked");

                String desc = txt_review_description.getText().toString();

                if(desc.trim().length()==0){
                    Toast.makeText(context, "Review is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if( desc.length()!=0){
                    //submit
                    Toast.makeText(context, "   Submitting...", Toast.LENGTH_SHORT).show();
                   sendReview(txtService_id.getText().toString(),review_value,desc);
                }
            }
        });
        builder = new android.app.AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.show();
    }

    private void showMap(){
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.custom_map);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(lp.width,lp.height);
        dialog.show();

        MapView mMapView =  dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                LatLng position = new LatLng(14.701211, 120.989359); ////your lat lng
                googleMap.addMarker(new MarkerOptions().position(position).title("Worker's Location").snippet(position.latitude+","+position.longitude));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            }
        });


    }

    protected void sendReview(final String service_id, final int rating,  final String review){
            final String server_url = ServerDataHolder.serverUrl+"/booking/transaction";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            StringRequest request = new StringRequest(Request.Method.POST, server_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response!=null){
                                try{
                                    Log.wtf("onResponse","Response:" +response);
                                    if(response.trim().equals("Process Successful")) {
                                        if (dialog != null) {
                                            dialog.hide();
                                            dialog.dismiss();
                                        }
                                    }
                                    Toast.makeText(context, response.trim(), Toast.LENGTH_SHORT).show();
                                }catch (Exception ee)
                                {
                                    Log.wtf("loadRecipe ERROR (onResponse)",ee.getMessage());
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String message = getVolleyError(volleyError);
                            Log.wtf("loadRecipe: onErrorResponse","Volley Error \n"+message);
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("service_id",service_id);
                    params.put("review",review);
                    params.put("rating",rating+"");
                    params.put("Content-Type","application/json");
                    params.put("X-API-KEY","12345");
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


    protected void updateTransactionStatus(final String qry){
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
                                    String filename = Jasonobject.getString("filename");

                                }
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
