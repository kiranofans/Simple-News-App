package com.android_projects.newsapipractice.network;

public class ApiConstants {
    public static String BASE_URL = "https://newsapi.org/";
    public static String GOOGLE_BASE_URL="https://accounts.google.com/";

    public static final String API_KEY = "6051104d60c2436bbee3352d5554addb";

    public static final String ENDPOINT_EVERYTHING="v2/everything";
    public static final String ENDPOINT_TOP_HEADLINES = "v2/top-headlines";
    public static final String ENDPOINT_SOURCES="v2/sources";

    //RC for Response Code
    public static final int API_RC_UNKNOWN_ERROR = 0;

    public static final int API_RC_RESPONSE_SUCCESS = 200;
    public static final int API_RC_UNKNOWN_HOST = 404;
}
