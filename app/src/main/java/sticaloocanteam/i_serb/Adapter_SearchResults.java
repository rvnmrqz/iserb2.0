package sticaloocanteam.i_serb;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kim on 12/2/2017.
 */

public class Adapter_SearchResults extends RecyclerView.Adapter<Adapter_SearchResults.ViewHolder>{

    Context context;
    List<Object_SearchResults> object_searchResultsList;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sharedPreferences;
    String TOKEN;
    ProgressBar progressBar;
    String TAG = "Adapter_SearchResults";

    public Adapter_SearchResults(Context context, List<Object_SearchResults> object_searchResultsList) {
        this.context = context;
        this.object_searchResultsList = object_searchResultsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_result, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Object_SearchResults object_searchResults = object_searchResultsList.get(position);

        Glide.with(context)
                .load(object_searchResults.getUser_icon())
                .apply(new RequestOptions().placeholder(R.drawable.placeholder).error(R.drawable.placeholder))
                .into(holder.img_userprofile);

        holder.txtskill_id.setText(object_searchResults.getSkill_id());
        holder.txtUser_id.setText(object_searchResults.getUser_id());
        holder.txtWorkerName.setText(object_searchResults.getWorker_name());
        holder.txtSkill_service_fee.setText(object_searchResults.getService_fee());
        holder.txtSkill_serviceRating.setText(object_searchResults.getRating());

        holder.btnBookNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sharedPreferences = context.getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,Context.MODE_PRIVATE);
                TOKEN = sharedPreferences.getString(SharedPrefConfig.USER_TOKEN,"");
                String mySkill_id = holder.txtskill_id.getText().toString();
                String worker_id = holder.txtUser_id.getText().toString();
                String customer_id = sharedPreferences.getString(SharedPrefConfig.USER_ID,"");
                String service_fee = holder.txtSkill_service_fee.getText().toString();
                showConfimation(worker_id,customer_id,mySkill_id,service_fee);

            }
        });
    }

    @Override
    public int getItemCount() {
        return object_searchResultsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView img_userprofile;
        public TextView txtskill_id,txtUser_id,txtWorkerName,txtSkillname,txtSkill_service_fee,txtSkill_serviceRating;
        public Button btnBookNow;

        public ViewHolder(View itemView){
            super(itemView);
            img_userprofile = itemView.findViewById(R.id.img_userprofile);
            txtskill_id = itemView.findViewById(R.id.txtskill_id);
            txtUser_id = itemView.findViewById(R.id.txtUser_id);
            txtWorkerName = itemView.findViewById(R.id.txtWorkerName);
            txtSkillname = itemView.findViewById(R.id.txtSkill_name);
            txtSkill_service_fee = itemView.findViewById(R.id.txtSkill_service_fee);
            txtSkill_serviceRating = itemView.findViewById(R.id.txtSkill_serviceRating);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);

        }
    }


    protected void showConfimation(final String worker_id,final String customer_id,final String mySkillId, final String service_fee){
        new AlertDialog.Builder(context)
                .setTitle("Confirmation")
                .setMessage("Book now?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bookNow(worker_id,customer_id,mySkillId,service_fee);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    protected void bookNow(final String worker_id,final String customer_id,final String mySkillId, final String service_fee){
        String url = ServerDataHolder.serverUrl+"do_query.php";
        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            Toast.makeText(context, response.trim(), Toast.LENGTH_SHORT).show();
                            Log.wtf(TAG,"bookNow Response: "+response);

                            if(response.trim().equalsIgnoreCase("Process Successful")){
                                ServerDataHolder.setBackScreen();
                                MainActivity_Consumer.navigation.setSelectedItemId(R.id.nav_consumer_histories);
                            }

                        }catch (Exception e){
                            Toast.makeText(context, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.wtf(TAG, "BookNow OnResponse Exception: "+e.getMessage());
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
                String qry = "INSERT INTO service_transactions(worker_id,customer_id,service_fee,skill_id,date_time_requested) VALUES("+worker_id+","+customer_id+","+service_fee+","+mySkillId+",NOW());";
                params.put("qry",qry);
                //params.put("worker_id",worker_id);
                //params.put("customer_id",customer_id);
                //params.put("myskill_id",mySkillId);
                //params.put("service_fee",service_fee);
                //params.put("Content-Type","application/json");
                //params.put("X-API-KEY","12345");
                //params.put("token",TOKEN);
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
