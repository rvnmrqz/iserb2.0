package sticaloocanteam.i_serb;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Consumer_Profile extends Fragment {

    Context context;
    SharedPreferences sharedPreferences;

    CircleImageView circleImageView;
    TextView txtUserFullName;
    TextView txtAddress;
    TextView txtContactNo;
    TextView txtEmail;

    public Fragment_Consumer_Profile() {
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
        return inflater.inflate(R.layout.fragment_consumer_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = context.getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,Context.MODE_PRIVATE);
        circleImageView = view.findViewById(R.id.profile_image);
        txtUserFullName = view.findViewById(R.id.txtUserFullName);
        txtAddress = view.findViewById(R.id.txtAddress);
        txtContactNo = view.findViewById(R.id.txtContact);
        txtEmail = view.findViewById(R.id.txtEmail);
        loadDisplay();
    }

    private void loadDisplay(){
        Log.wtf("image_path",sharedPreferences.getString(SharedPrefConfig.PROFILE_PICTURE,""));
        Glide.with(context)
                .load(sharedPreferences.getString(SharedPrefConfig.PROFILE_PICTURE,""))
                .apply(new RequestOptions().placeholder(R.drawable.user_default_avatar).error(R.drawable.user_default_avatar))
                .into(circleImageView);
        txtUserFullName.setText(sharedPreferences.getString(SharedPrefConfig.FNAME,"")+" "+sharedPreferences.getString(SharedPrefConfig.LNAME,""));
        txtAddress.setText(sharedPreferences.getString(SharedPrefConfig.ADDRESS,""));
        txtEmail.setText(sharedPreferences.getString(SharedPrefConfig.EMAIL,""));
        txtContactNo.setText(sharedPreferences.getString(SharedPrefConfig.CONTACT,""));
    }

}
