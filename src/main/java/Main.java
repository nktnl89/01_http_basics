
import com.squareup.okhttp.Response;
import service.GeolocationService;

public class Main {
    public static void main(String[] args) {
        GeolocationService geolocationService = new service.GeolocationService();

        Response responseForTokenAndUid = geolocationService.doGetRequest("https://yandex.ru/maps", "53.173563%2C56.87379");

        String token = geolocationService.getTokenFromResponse(responseForTokenAndUid);
        String uid = geolocationService.getYandexUId(responseForTokenAndUid);

        Response responseForCoordinates = geolocationService.doGetRequest("https://yandex.ru/maps", "Ижевск, студенческая, 50", token, uid);
        geolocationService.getCoordinatesFromResponse(responseForCoordinates);
    }
}
