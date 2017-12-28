package sticaloocanteam.i_serb;


import java.util.Map;

/**
 * Created by kim on 12/2/2017.
 */

public class ServerDataHolder {
    //public static String serverUrl = "http://arvinmarquez.tech/uhack/iserb/restful-api/";
    public static String serverUrl = "http://10.0.2.2/uhack/";

    //public static String serverUrl = "http://arvinmarquez.tech/uhack/";
    public static String profileImagePath = serverUrl+"assets/profiles/";
    public static String iconImagePath = serverUrl+"assets/icons/";
    public static int TIMEOUT = 1000 * 5; //5 seconds

    //hashmaps
    public static Map<String,String> params;

    public static int consumer_Tab1lastActiveScreenLayer=0;
    public static String consumer_Tab1LastRequestKey = null;
    public static String consumer_Tab1lasturl;
    public static Map<String,String> consumer_Tab1lastsearchParam;

    public static int consumer_Tab1CurrentActiveScreenLayer=0;
    public static String consumer_Tab1CurrentRequestKey = null;
    public static String consumer_Tab1Currenturl;
    public static Map<String,String> consumer_Tab1CurrentSearchParam;

    public static void setNewScreen(int activeScreenLayer, String requestKey, String url, Map<String,String> params){
        consumer_Tab1lastActiveScreenLayer = consumer_Tab1CurrentActiveScreenLayer;
        consumer_Tab1LastRequestKey = consumer_Tab1CurrentRequestKey;
        consumer_Tab1lastsearchParam = consumer_Tab1CurrentSearchParam;
        consumer_Tab1lasturl = consumer_Tab1Currenturl;

        consumer_Tab1CurrentActiveScreenLayer = activeScreenLayer;
        consumer_Tab1CurrentRequestKey = requestKey;
        consumer_Tab1Currenturl = url;
        consumer_Tab1CurrentSearchParam = params;
    }

    public static void setBackScreen(){
        consumer_Tab1CurrentActiveScreenLayer = consumer_Tab1lastActiveScreenLayer;
        consumer_Tab1CurrentRequestKey =   consumer_Tab1LastRequestKey;
        consumer_Tab1CurrentSearchParam =  consumer_Tab1lastsearchParam;
        consumer_Tab1Currenturl = consumer_Tab1lasturl;
    }

}
