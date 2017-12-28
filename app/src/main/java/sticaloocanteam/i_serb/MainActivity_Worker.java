package sticaloocanteam.i_serb;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ncapdevi.fragnav.FragNavController;

import java.util.ArrayList;
import java.util.List;

import static sticaloocanteam.i_serb.MainActivity_Consumer.fragNavController;

public class MainActivity_Worker extends AppCompatActivity implements FragNavController.RootFragmentListener {


    BottomNavigationView navigation;
    FragNavController.Builder builder;
    List<Fragment> fragments = new ArrayList<>(3);
    public static FragNavController fragNavController;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_worker_works:
                    fragNavController.switchTab(fragNavController.TAB1);
                    return true;
                case R.id.nav_worker_histories:
                    fragNavController.switchTab(fragNavController.TAB2);
                    return true;
                case R.id.nav_worker_profile:
                    fragNavController.switchTab(fragNavController.TAB3);
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__worker);

        builder = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.container);
        fragments.add(new Fragment_Worker_works());
        fragments.add(new Fragment_worker_histories());
        fragments.add(new Fragment_Consumer_Profile());
        builder.rootFragments(fragments);
        builder.rootFragmentListener(this, 3);
        fragNavController = builder.build();

        navigation =  findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(!isMyServiceRunning(Service_Notification_Worker.class)){
            startService(new Intent(MainActivity_Worker.this,Service_Notification_Worker.class));
        }
    }

    @Override
    public Fragment getRootFragment(int index) {
        switch (index) {
            case 0:
                return new Fragment_Worker_works();
            case 1:
                return new Fragment_worker_histories();
            case 2:
                return new Fragment_Worker_profile();
        }
        throw new IllegalStateException("Need to send an index that we know");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fragNavController != null) {
            fragNavController.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragNavController.getCurrentStack().size() == 1) {
            super.onBackPressed();
        } else {
            fragNavController.popFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.consumer_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_sign_out){
            logout();
        }
        else if(id == R.id.menu_switch_account){
            new AlertDialog.Builder(this)
                    .setTitle("Switch User Account")
                    .setMessage("You will be signed-out as service provider and will be signed-in using consumer account")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SharedPrefConfig.USER_IS_WORKER,false);
                            editor.commit();

                            startActivity(new Intent(MainActivity_Worker.this,SplashScreen.class));
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).show();
        }
        return true;
    }

    protected void logout(){
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("You're about to sign-out from the app, continue?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(SharedPrefConfig.clearUserData(MainActivity_Worker.this)){
                            startActivity(new Intent(MainActivity_Worker.this,Login.class));
                            stopService(new Intent(MainActivity_Worker.this,Service_Notification_Worker.class));
                            finish();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

