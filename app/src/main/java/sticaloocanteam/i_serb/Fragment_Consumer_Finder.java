package sticaloocanteam.i_serb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Fragment_Consumer_Finder extends Fragment {

    public static Context staticContext;
    Context context;
    SharedPreferences sharedPreferences;
    String TOKEN = "";
    static String USER_ID="";

    static String TAG = "Fragment_Consumer_Finder";
    //bottom layouts
    static ScrollView layoutResults;
    static ScrollView layoutCategories;
    static LinearLayout  layoutLoading;

    //category layout views
    static ExpandableListView categoryListview;

    //layout views
    static ProgressBar loadingProgressBar;
    static TextView loadingMessage;
    static Button loadingButton;

    //popout layout
    static View layout_view;
    TextView txtOpenFilter;

    //volley
    private static RequestQueue requestQueue;
    private static StringRequest stringRequest;
    static String url;

    //expandable listview adapter
    static ExpandableListAdapter listAdapter;
    static List<String> listDataHeaderKey;
    static List<String> listDataHeader;
    static List<String> listDataHeaderIcon;
    static HashMap<String, String[][]> listDataChild;

    //recycler view
    private RecyclerView recyclerView;
    private static Adapter_SearchResults adapter_searchResults;
    static Object_SearchResults object_searchResults;
    static List<Object_SearchResults> object_searchResultsList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.staticContext = context;
    }

    public Fragment_Consumer_Finder() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layout_view = view;

        //layouts
        layoutResults = view.findViewById(R.id.tab2_layout_results);
        layoutCategories = view.findViewById(R.id.tab2_layout_categories);
        layoutLoading = view.findViewById(R.id.tab2_layout_loading);

        //layout views
        loadingProgressBar = view.findViewById(R.id.tab2_layout_loading_progressbar);
        loadingMessage = view.findViewById(R.id.tab2_layout_loading_message);
        loadingButton = view.findViewById(R.id.tab2_layout_loading_button);

        //ExpandableListview
        categoryListview = view.findViewById(R.id.tab2_layout_categories_listview);

        txtOpenFilter = view.findViewById(R.id.txtOpenFilterSearch);
        txtOpenFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDialog();
            }
        });

        sharedPreferences = context.getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME, Context.MODE_PRIVATE);

        TOKEN = sharedPreferences.getString(SharedPrefConfig.USER_TOKEN, null);

        recyclerView = getActivity().findViewById(R.id.results_recycler);
        object_searchResultsList = new ArrayList<>();
        adapter_searchResults = new Adapter_SearchResults(getContext(), object_searchResultsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter_searchResults);

        setCurrentdisplay();

        loadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setCurrentdisplay();
            }
        });
    }

    public static void setCurrentdisplay(){
        switch (ServerDataHolder.consumer_Tab1CurrentActiveScreenLayer){
            case 0:
                loadCategories();
                break;
            case 1:
                internetLoader(ServerDataHolder.consumer_Tab1CurrentRequestKey,ServerDataHolder.consumer_Tab1Currenturl,ServerDataHolder.consumer_Tab1CurrentSearchParam);
                break;
            case 2:
                //Fragment
                MainActivity_Consumer.fragNavController.pushFragment(new Fragment_WorkerDetailed());
                break;
        }
    }

    //filter
    private void openFilterDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View forgotPassDialog = inflater.inflate(R.layout.popup_filter_layout, null);
        //  final EditText email =  forgotPassDialog.findViewById(R.id.email_dialog);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setView(forgotPassDialog);
        builder.show();
    }

    //categories
    protected static void loadCategories() {
        url = ServerDataHolder.serverUrl + "get_data.php";
        Map<String, String> params = new HashMap<>();
        params.put("qry", "select a.skill_id,a.icon_filename,a.parent_id,b.skill_description as parentname,a.skill_description from tbl_category_bank a LEFT JOIN tbl_category_bank b ON a.parent_id = b.skill_id order by a.parent_id;");
        //  params.put("token",TOKEN);
        // params.put("Content-Type","application/json");
        //  params.put("X-API-KEY","12345");

        ServerDataHolder.consumer_Tab1lastActiveScreenLayer = 0;
        ServerDataHolder.consumer_Tab1lastsearchParam = params;
        ServerDataHolder.consumer_Tab1LastRequestKey = "categories";
        ServerDataHolder.setNewScreen(0,"categories",url,params);

        internetLoader("categories", url, params);
    }

    protected static void childCategoryItemListener() {
        categoryListview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {

                url = ServerDataHolder.serverUrl + "get_data.php";

                String arr[][] = listDataChild.get(listDataHeader.get(groupPosition));
                ServerDataHolder.params = new HashMap<>();
                ServerDataHolder.params.put("qry", "SELECT * from tbl_users u INNER JOIN tbl_skills s ON u.user_id = s.worker_id INNER JOIN tbl_category_bank c ON s.skill_id = c.skill_id WHERE c.skill_id = " + arr[childPosition][0]);

                ServerDataHolder.setNewScreen(1,"search_result",url,ServerDataHolder.params);

                internetLoader("search_result", url, ServerDataHolder.params);
                Log.wtf("childClickEvent", "Group: " + groupPosition + "\tName:" + listDataHeader.get(groupPosition) + "\rChild Position: " + childPosition + "\tKey: " + arr[childPosition][0] + "\tValue: " + arr[childPosition][1]);
                return false;
            }
        });
    }

    protected static void internetLoader(final String requestKey, String url, final Map<String, String> params) {
        showLoadingLayout(true, true, "Loading", false, null);
        requestQueue = Volley.newRequestQueue(staticContext);
        stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            switch (requestKey) {
                                case "categories":
                                    categoryJSONExtractor(response);
                                    break;
                                case "search_result":
                                    searchResultJSONExtractor(response);
                                    break;
                                default:
                                    Log.wtf(TAG, "INTERNET_LOADER: no request key given");
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(staticContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showLoadingLayout(true, false, getVolleyError(error), true, "Retry");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        int socketTimeout = 1000 * 5;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    //JSON EXTRACTORS
    private static void categoryJSONExtractor(String response) {
        try {
           // Log.wtf("categoryJSONExtractor","Response: "+response);
            int parent_id, lastParent_id = -1;
            String tempArray[][];
            String skill_id, parentName = "", description;
            List<String> tempChildHolder = new ArrayList<>();
            List<String> tempChildKeyValueHolder = new ArrayList<>();

            listDataHeaderKey = new ArrayList<>();
            listDataHeader = new ArrayList<>();
            listDataHeaderIcon = new ArrayList<>();
            listDataChild = new HashMap<>();

            JSONObject object = new JSONObject(response);
            JSONArray Jarray = object.getJSONArray("mydata");

           // Log.wtf("onResponse", response.trim());
            if (Jarray.length() > 0) {
              //  Log.wtf("onResponse", "Result count: " + Jarray.length());

                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject Jasonobject = Jarray.getJSONObject(i);

                    skill_id = Jasonobject.getString("skill_id");
                    parent_id = Jasonobject.getInt("parent_id");
                    description = Jasonobject.getString("skill_description").trim();

                    if (parent_id == 0) {
                        //it is a parent
                    //    Log.wtf("onResponse", "parentid=0");
                        listDataHeaderKey.add(Jasonobject.getString("skill_id").trim());
                        listDataHeaderIcon.add(ServerDataHolder.iconImagePath +Jasonobject.getString("icon_filename").trim());
                        listDataHeader.add(description);
                    } else {

                        //it is a child
                        if (lastParent_id == -1) {
                         //   Log.wtf("onResponse", "start of list");
                            //start of the list
                            lastParent_id = parent_id;
                        }
                        if (parent_id == lastParent_id) {
                          //  Log.wtf("onResponse", "same parent");
                            //same parent child
                            parentName = Jasonobject.getString("parentname").trim();
                         //   Log.wtf("SAME PARENT", "ParentName: " + parentName + "\tChildDescription: " + description);

                            tempChildKeyValueHolder.add(skill_id);
                            tempChildHolder.add(description);

                            if (i + 1 == Jarray.length()) {
                                //it is the last
                        //        Log.wtf("onResponse", "insert because this is the last");
                                //move the values
                                tempArray = new String[tempChildHolder.size()][2];
                                for (int x = 0; x < tempChildHolder.size(); x++) {
                                    tempArray[x][1] = tempChildHolder.get(x);
                                    tempArray[x][0] = tempChildKeyValueHolder.get(x);
                                }
                                listDataChild.put(parentName, tempArray);
                            }
                        } else {
                         //   Log.wtf("onResponse", "new parent id");
                            //this is a new child, insert to listdatachild the last list
                         //   Log.wtf("onResponse", "inserted last list");

                            //move the values
                            tempArray = new String[tempChildHolder.size()][2];
                            for (int x = 0; x < tempChildHolder.size(); x++) {
                                tempArray[x][1] = tempChildHolder.get(x);
                                tempArray[x][0] = tempChildKeyValueHolder.get(x);
                            }
                            listDataChild.put(parentName, tempArray);
                            parentName = Jasonobject.getString("parentname").trim();
                            tempChildKeyValueHolder.clear();
                            tempChildHolder.clear();
                            tempChildKeyValueHolder.add(skill_id);
                            tempChildHolder.add(description);
                        }
                        lastParent_id = parent_id;
                    }
                }
                layoutCategories.setVisibility(View.VISIBLE);
                listAdapter = new CategoryExpandableListviewAdapater(layout_view.getContext(), listDataHeaderIcon, listDataHeader, listDataChild);
                // categoryAdpater = new CategoryAdpater(getActivity(),skill_id,icon_filename,description);
                categoryListview.setAdapter(listAdapter);
                childCategoryItemListener();
            } else {
                //no results
                showLoadingLayout(true, false, "No Categories", true, "Refresh");
            }
        } catch (Exception e) {
            Log.wtf(TAG, "Exception error: " + e.getMessage());
        }
    }

    private static void searchResultJSONExtractor(String response) {
        try {
            Log.wtf("searchResultJSONExtractor","Response: "+response);
            JSONObject object = new JSONObject(response);
            JSONArray Jarray = object.getJSONArray("mydata");

            object_searchResultsList.clear();
            Log.wtf("onResponse", response.trim());
            Log.wtf("onResponse",Jarray.length()+"");
            if (Jarray.length() > 0) {
                Log.wtf("onResponse", "Result count: " + Jarray.length());
                 String skill_id,user_icon,user_id,worker_name,skill_description,service_fee,rate;

                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject Jasonobject = Jarray.getJSONObject(i);

                    skill_id = Jasonobject.getString("skill_id");
                    user_id = Jasonobject.getString("user_id");
                    user_icon = ServerDataHolder.profileImagePath +Jasonobject.getString("profile_picture");
                    worker_name = Jasonobject.getString("fname")+" "+Jasonobject.getString("lname");
                    skill_description = Jasonobject.getString("skill_description");
                    service_fee = Jasonobject.getString("service_fee");
                    //rate = Jasonobject.getString("rating");
                    object_searchResults = new Object_SearchResults(skill_id,user_icon,user_id,worker_name,skill_description,service_fee,"");
                    object_searchResultsList.add(object_searchResults);
                }
                adapter_searchResults.notifyDataSetChanged();
                layoutResults.setVisibility(View.VISIBLE);
            }else{
                showLoadingLayout(true,false,"No Results",true,"Refresh");
            }
        } catch (Exception e) {
            Log.wtf(TAG, "searchResultJSONExtractor: " + e.getMessage());
        }
    }

    protected static String getVolleyError(VolleyError volleyError) {
        String message = "";
        if (volleyError instanceof NetworkError) {
            message = "Network Error Encountered";
            Log.wtf("getVolleyError (Volley Error)", "NetworkError");
        } else if (volleyError instanceof ServerError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)", "ServerError");
        } else if (volleyError instanceof AuthFailureError) {
            message = "Please check your internet connection";
            Log.wtf("getVolleyError (Volley Error)", "AuthFailureError");
        } else if (volleyError instanceof ParseError) {
            message = "An error encountered, Please try again";
            Log.wtf("getVolleyError (Volley Error)", "ParseError");
        } else if (volleyError instanceof NoConnectionError) {
            message = "No internet connection";
            Log.wtf("getVolleyError (Volley Error)", "NoConnectionError");
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection Timeout";
            Log.wtf("getVolleyError (Volley Error)", "TimeoutError");
        }
        return message;
    }

    protected static void showLoadingLayout(boolean showLayout, boolean showProgressbar, String messageText, boolean showButton, String btnText) {
        if (showLayout) {
            layoutCategories.setVisibility(View.GONE);
            layoutResults.setVisibility(View.GONE);
            loadingMessage.setText(messageText);

            if (showProgressbar) loadingProgressBar.setVisibility(View.VISIBLE);
            else loadingProgressBar.setVisibility(View.GONE);

            if (showButton) {
                loadingButton.setText(btnText);
                loadingButton.setVisibility(View.VISIBLE);
            } else {
                loadingButton.setVisibility(View.GONE);
            }
        } else {
            layoutLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consumer_finder, container, false);
    }


}
