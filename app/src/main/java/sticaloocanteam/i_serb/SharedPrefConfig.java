package sticaloocanteam.i_serb;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kim on 12/2/2017.
 */

public class SharedPrefConfig {
    public static String SHAREDPREFNAME = "iserb_sharedpref";
    public static String FNAME = "fname";
    public static String LNAME = "lname";
    public static String ADDRESS = "address";
    public static String EMAIL = "email";
    public static String CONTACT = "contact";
    public static String PROFILE_PICTURE = "profile_picture";
    public static String USER_ID = "user_id";
    public static String USERNAME = "username";
    public static String USER_TOKEN = "token";
    public static String USER_IS_WORKER = "user_is_worker";



    public static boolean clearUserData(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPREFNAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor  = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        return true;
    }


}
