package service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

public class GeolocationService implements GeolocationServiceInterface {
    private static Logger LOGGER = getLogger(service.GeolocationService.class);
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    public Response doGetRequest(String url, String ll) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        urlBuilder.addQueryParameter("ll", ll);
        urlBuilder.addQueryParameter("z", String.valueOf(19));
        String requestUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .addHeader("Cookie", "yandexuid=140418241542027639; i=SU51HgmRSW0kpcbCTkS5j9OBDgC14+LriRzKl3gZ0SToPotdKhfCnZ64x+PvTCyTP1XHzUuTOLt93wJnKyW035Oz0J8=;" +
                        " _ym_uid=1542089083655854322; mda=0; yandex_gid=44; my=YwA=; _ym_d=1544184125; " +
                        "fuid01=5c0a613c5eec4688.U6mBaPYeAd9LTGzSezOHcdd_mKBXZwZfSC5_wQnWi4fwGCmSDSGVV5nnDVdRgIJCfZxQEsNkTVsTXQNzc4eZe_l7EUx-DcXB8hwY_MoSBtZQvzjfV54lyRvtdnRSjrqg; " +
                        "zm=m-white_bender.webp.css-https%3Awww_zmn5fRGIkJxyNVNvb1YAxfO-1Zw%3Al; yabs-frequency=/4/0000000000000000/KRRlRVWo81vXxctuCY40/; " +
                        "device_id=\"b4cde91b2a4fcdc6e96d811ac08bfc7beba93048e\"; _ym_isad=2; " +
                        "_ym_wasSynced=%7B%22time%22%3A1544439694090%2C%22params%22%3A%7B%22eu%22%3A0%7D%2C%22bkParams%22%3A%7B%7D%7D; " +
                        "yp=1857387639.yrts.1542027639#1857387639.yrtsi.1542027639#1546776124.ygu.1#1545393725.ysl.1#1560208648.szm.1:1920x1080:1880x939; " +
                        "_ym_visorc_784657=b; _ym_visorc_2106601=w; _ym_visorc_2105623=b")
                .url(requestUrl)
                .build();
        Response response = null;

        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public String getTokenFromResponse(Response response) {
        Pattern pattern = Pattern.compile("csrfToken\":\"[a-z0-9]*:[0-9]*\"");
        try {
            Matcher matcher = pattern.matcher(response.body().string());
            if (matcher.find()) {
                return matcher.group().replaceAll("csrfToken\":\"", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String getYandexUId(Response response) {
        String securityPolice = response.header("Content-Security-Policy");
        return securityPolice.replaceAll("report-uri.*&", "");
    }

    @Override
    public Response doGetRequest(String url, String text, String token, String uid) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("text", text);
        urlBuilder.addQueryParameter("lang", "ru");
        urlBuilder.addQueryParameter("csrfToken", token);
        String requestUrl = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .addHeader("Cookie", uid)
                .url(requestUrl)
                .build();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void getCoordinatesFromResponse(Response response) {
        try {
            Document doc = Jsoup.parse(response.body().string());
            String scriptData = doc.getElementsByAttributeValue("class", "config-view").get(0).data();
            JsonObject jsonObject = new JsonParser().parse(scriptData).getAsJsonObject();
            for (JsonElement element : jsonObject.get("searchPreloadedResults").getAsJsonObject().get("items").getAsJsonArray()) {
                LOGGER.info(element.getAsJsonObject().get("coordinates").toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
