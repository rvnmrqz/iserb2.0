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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kim on 12/3/2017.
 */

public class Adapter_Booking extends RecyclerView.Adapter<Adapter_Booking.ViewHolder>{


    Context context;
    List<Object_BookingRequest> object_bookingRequestList;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sharedPreferences;
    String TOKEN;
    ProgressBar progressBar;

    public Adapter_Booking(Context context, List<Object_BookingRequest> object_bookingRequestList) {
        this.context = context;
        this.object_bookingRequestList = object_bookingRequestList;
    }

    @Override
    public Adapter_Booking.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_booking, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Object_BookingRequest object_bookingRequest = object_bookingRequestList.get(position);
        //txtService_id,txtCustomerName,txtServiceDescription,txtReqTimeDate;

        holder.txtService_id.setText(object_bookingRequest.getServiceid());
        holder.txtCustomerName.setText(object_bookingRequest.getCustomerName());
        holder.txtServiceDescription.setText(object_bookingRequest.getServiceDescription());
        holder.txtReqTimeDate.setText(object_bookingRequest.getReqDateTime());

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updater( holder.txtService_id.getText().toString(), "Reject");
                            }
                        })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();

            }
        });
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirmation?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updater( holder.txtService_id.getText().toString(), "Accept");
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
    }


    protected void updater(final String service_id, final String status){
        String server_url = ServerDataHolder.serverUrl+"/get_data.php";

        requestQueue = Volley.newRequestQueue(context);
        stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("onREsponse",response);
                        try{
                           /*  JSONObject object = new JSONObject(response);
                            JSONArray Jarray = object.getJSONArray("mydata");
                            Log.wtf("onResponse",response.trim());
                           if (Jarray.length() > 0) {
                                Log.wtf("onResponse", "Result count: " + Jarray.length());
                                for (int i = 0; i < Jarray.length(); i++) {
                                    JSONObject Jasonobject = Jarray.getJSONObject(i);
                                    String filename = Jasonobject.getString("filename");
                                    filename = "http:arvinmarquez.tech/firetrack/Images/"+filename;

                                }
                            }*/

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
                String qry = "update service_transaction set status='"+status+"' WHERE service_id="+service_id;
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

    @Override
    public int getItemCount() {
        return object_bookingRequestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public TextView txtService_id,txtCustomerName,txtServiceDescription,txtReqTimeDate;
        public Button btnReject,btnAccept;

        public ViewHolder(View itemView){
            super(itemView);
            txtService_id = itemView.findViewById(R.id.txtService_id);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            txtServiceDescription = itemView.findViewById(R.id.txtServiceDescription);
            txtReqTimeDate = itemView.findViewById(R.id.txtReqTimeDate);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }


}
