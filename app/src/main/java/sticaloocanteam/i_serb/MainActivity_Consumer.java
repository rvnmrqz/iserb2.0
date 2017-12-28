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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ncapdevi.fragnav.FragNavController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_Consumer extends AppCompatActivity implements FragNavController.RootFragmentListener{

    public static BottomNavigationView navigation;
    FragNavController.Builder builder;
    List<Fragment> fragments = new ArrayList<>(3);
    public static FragNavController fragNavController;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_consumer_finder:
                    fragNavController.switchTab(fragNavController.TAB1);
                    return true;
                case R.id.nav_consumer_histories:
                    fragNavController.switchTab(fragNavController.TAB2);
                    return true;
                case R.id.nav_consumer_profile:
                    fragNavController.switchTab(fragNavController.TAB3);
                    return true;
            }
            return false;
        }
    };

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
                    .setMessage("You will be signed-out as consumer and signed-in using service provider account")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences sharedPreferences = getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean(SharedPrefConfig.USER_IS_WORKER,true);
                            editor.commit();
                            startActivity(new Intent(MainActivity_Consumer.this,SplashScreen.class));
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
                        if(SharedPrefConfig.clearUserData(MainActivity_Consumer.this)){
                            startActivity(new Intent(MainActivity_Consumer.this,Login.class));
                            stopService(new Intent(MainActivity_Consumer.this,Service_Notification_Worker.class));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_consumer);

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        builder = FragNavController.newBuilder(savedInstanceState, getSupportFragmentManager(), R.id.container);
        fragments.add(new Fragment_Consumer_Finder());
        fragments.add(new Fragment_Consumer_Transactions());
        fragments.add(new Fragment_Consumer_Profile());
        builder.rootFragments(fragments);
        builder.rootFragmentListener(this, fragments.size());
        fragNavController = builder.build();

        if(isMyServiceRunning(Service_Notification_Worker.class)){
            stopService(new Intent(MainActivity_Consumer.this,Service_Notification_Worker.class));
        }
    }

    @Override
    public Fragment getRootFragment(int i) {
        switch (i) {
            case 0:
                return new Fragment_Consumer_Finder();
            case 1:
                return new Fragment_Consumer_Transactions();
            case 2:
                return new Fragment_Consumer_Profile();
        }
        throw new IllegalStateException("Need to send an index that we know");
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

    @Override
    public void onBackPressed() {

        if (fragNavController.getCurrentStack().size()==1) {
            if(fragNavController.getCurrentStackIndex()==0){
               //on finder tab
                ServerDataHolder.setBackScreen();
                Log.wtf("current screen: ",ServerDataHolder.consumer_Tab1CurrentActiveScreenLayer+"");
                if(Fragment_Consumer_Finder.staticContext != null){
                    Fragment_Consumer_Finder.setCurrentdisplay();
                }else{
                    super.onBackPressed();
                }
            }else {
                super.onBackPressed();
            }

        }else{
            fragNavController.popFragment();
        }
    }
}
