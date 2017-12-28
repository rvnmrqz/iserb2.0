package sticaloocanteam.i_serb;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    String TAG = "Login.class";

    EditText txtUsername;
    ShowHidePasswordEditText txtPassword;
    Button btnSignin;

    String url="";
    StringRequest stringRequest;
    RequestQueue requestQueue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        btnSignin = findViewById(R.id.btnSign_in);

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(Login.this,MainActivity_Consumer.class));
                if(inputAreValid()){
                    login(txtUsername.getText().toString(),txtPassword.getText().toString());
                }
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Signing-in");
    }

    private void showHideProgressDialog(boolean showDialog){
        if(showDialog){
            progressDialog.show();
        }else{
            progressDialog.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(progressDialog!=null){
            progressDialog.hide();
            progressDialog.cancel();
        }
    }

    private void login(final String username, final String password) {
        showHideProgressDialog(true);
        url = ServerDataHolder.serverUrl+"get_data.php";
        requestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            showHideProgressDialog(false);
                            Log.wtf("response",response.trim());
                            JSONObject object = new JSONObject(response);
                            JSONArray Jarray = object.getJSONArray("mydata");
                            Log.wtf("onResponse",response.trim());
                            if (Jarray.length() > 0) {
                               extractJSON(response);
                            }
                        }catch (Exception e){
                            showHideProgressDialog(false);
                            Toast.makeText(Login.this, "Problem occurred while signing-in", Toast.LENGTH_SHORT).show();
                            Log.wtf(TAG,"Error in Signing-in :"+e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showHideProgressDialog(false);
                        Log.wtf(TAG,"Volley Error, Error in Signing-in :"+getVolleyError(error));
                        if(error instanceof AuthFailureError){
                            Toast.makeText(Login.this, "Wrong username or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                String qry = "SELECT * FROM tbl_users WHERE username = '"+username+"' AND password='"+password+"'";
                params.put("qry",qry);
                //params.put("username",username);
               // params.put("password",password);
               // params.put("Content-Type","application/json");
               // params.put("X-API-KEY","12345");
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

    protected String getVolleyError(VolleyError volleyError){
        String message="";
        if (volleyError instanceof NetworkError) {
            message = "Network Error Encountered";
            Log.wtf("getVolleyError (Volley Error)","NetworkError");
        } else if (volleyError instanceof ServerError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)","ServerError");
        } else if (volleyError instanceof AuthFailureError) {
            message = "Authentication Failed";
            Log.wtf("getVolleyError (Volley Error)","AuthFailureError");
        } else if (volleyError instanceof ParseError) {
            message = "An error encountered, Please try again";
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

    private void extractJSON(String response){
        try{

            String token,userid,username,fname,mname,lname,address,profile_picture,contact,email;

            /*

                           Log.wtf("onResponse",response.trim());
                           if (Jarray.length() > 0) {
                               Log.wtf("onResponse", "Result count: " + Jarray.length());
                               for (int i = 0; i < Jarray.length(); i++) {
                                   JSONObject Jasonobject = Jarray.getJSONObject(i);
             */
            JSONObject object = new JSONObject(response);
            JSONArray Jarray = object.getJSONArray("mydata");

            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject Jasonobject = Jarray.getJSONObject(i);
               // token = Jasonobject.getString("token");
                userid = Jasonobject.getString("user_id");
                username = Jasonobject.getString("username");
                fname = Jasonobject.getString("fname");
                lname = Jasonobject.getString("lname");
                address = Jasonobject.getString("full_address");
                contact = Jasonobject.getString("contact_number");
                email = Jasonobject.getString("email");
                profile_picture = ServerDataHolder.profileImagePath+Jasonobject.getString("profile_picture");
                saveInSharedPref(userid,username,fname,lname,address,profile_picture,contact,email);
               startActivity(new Intent(Login.this,MainActivity_Consumer.class));
               finish();
            }
        }catch (Exception e){
            Log.wtf(TAG,"Problem in extracting json: "+e.getMessage());
        }
    }

    private void saveInSharedPref(String userid ,String username,String fname, String lname, String address, String profile_picture, String contact, String email){
       // Log.wtf(TAG,token+" "+userid+" "+username+" "+fname+" "+lname+" "+address+" "+profile_picture);
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
       // editor.putString(SharedPrefConfig.USER_TOKEN,token);
        editor.putString(SharedPrefConfig.USER_ID,userid);
        editor.putString(SharedPrefConfig.USERNAME,username);
        editor.putString(SharedPrefConfig.FNAME,fname);
        editor.putString(SharedPrefConfig.LNAME,lname);
        editor.putString(SharedPrefConfig.ADDRESS,address);
        editor.putString(SharedPrefConfig.PROFILE_PICTURE,profile_picture);
        editor.putString(SharedPrefConfig.CONTACT,contact);
        editor.putString(SharedPrefConfig.EMAIL,email);
        editor.commit();
    }

    protected boolean inputAreValid(){
        if(txtUsername.getText().length()==0){
            txtUsername.setError("This field is required");
            return false;
        }
        if(txtPassword.getText().length()==0){
            txtPassword.setError("This field is required");
            return false;
        }
        return true;
    }
}
