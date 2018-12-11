package service;

import com.squareup.okhttp.Response;

public interface GeolocationServiceInterface {
    Response doGetRequest(String url, String ll);

    String getTokenFromResponse(Response response);

    String getYandexUId(Response response);

    Response doGetRequest(String url, String text, String token, String uid);

    void getCoordinatesFromResponse(Response response);
}
