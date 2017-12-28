package sticaloocanteam.i_serb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_scren);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        sharedPreferences = getSharedPreferences(SharedPrefConfig.SHAREDPREFNAME,MODE_PRIVATE);

        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(sharedPreferences.getString(SharedPrefConfig.USER_ID,null) != null){
                    if(sharedPreferences.getBoolean(SharedPrefConfig.USER_IS_WORKER,false)){
                        //user is worker
                        startActivity(new Intent(SplashScreen.this, MainActivity_Worker.class));
                    }else{
                        //user is customer
                        startActivity(new Intent(SplashScreen.this, MainActivity_Consumer.class));
                    }
                }else{
                    //no logged user
                    startActivity(new Intent(SplashScreen.this, Login.class));
                }
                finish();
            }
        }, secondsDelayed * 1000);
    }
}
