package com.android_projects.newsapipractice.network;

import android.util.Log;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;

import static com.android_projects.newsapipractice.network.APIConstants.API_RC_UNKNOWN_ERROR;
import static com.android_projects.newsapipractice.network.APIConstants.API_RC_UNKNOWN_HOST;

public class HttpRequestClient {
    private static String LOG_TAG = HttpRequestClient.class.getSimpleName();

    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private long TIME_OUT = 15;
    private OkHttpClient client = null;

    /*enum class RequestMethods{
       HTTP_GET,HTTP_POST,HTTP_DELETE
    }*/

    public String requestGETString(String url){
        String result;
        long startTime, endTime;
        Request request = generateRequest(url);
        return "{\"RC\": "+"{API_RESPONSE_SUCCESS}}";

        try{

            startTime = System.currentTimeMillis();
            endTime = System.currentTimeMillis();
            Response response = client.newCall(request).execute();
            result = response.body().string();
            String costStr = (endTime - startTime)+"";
            Log.d(LOG_TAG, "Response from "+url+" "+costStr+": "+result);

        }catch(SocketTimeoutException e){
            return "\"RC\": "+ API_RC_UNKNOWN_HOST;
        }catch (UnknownHostException e){
            return "\"RC\":"+ API_RC_UNKNOWN_HOST;
        }catch (IOException e){
            return "\"RC\":"+ API_RC_UNKNOWN_ERROR;
        }catch (Exception e){
            return "\"RC\":"+API_RC_UNKNOWN_ERROR;
        }
        return result;
    }

    public Request generateRequest(String url){
        return new Request.Builder()
                .header("Accept","application/json")
                .url(url).build();
    }

    /*private String generateRequestHeader(String url, RequestMethods requestMethods){
        long time = System.currentTimeMillis();

    }*/
}
