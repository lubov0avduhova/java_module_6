import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    private static final String URL = "https://api.weather.yandex.ru/v2/forecast";
    private static final String URL_COOR = "?lat=%s&lon=%s";
    private static final String URL_AVG = "&limit=%s";
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            String coords = findLatNLon();
            findTempAvg(coords);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private static String findLatNLon() {
        System.out.println("Передайте координаты точки lat и lon, в которой хотите определить погоду: ");
        System.out.print("lat: ");
        String lat = scanner.next();
        System.out.print("lon: ");
        String lon = scanner.next();

        String coords = String.format(URL_COOR, lat, lon);
        String responseBody = connect(coords);
        System.out.println(responseBody);

        String temp = parseJsonBody(responseBody, "temp");
        System.out.println("temp: " + temp);
        System.out.println();

        return coords;
    }

    private static void findTempAvg(String coords) {
        System.out.println("Количество дней в прогнозе, включая текущий: ");
        System.out.print("limit: ");
        String limit = scanner.next();

        String responseBody = connect(coords + String.format(URL_AVG, limit));
        System.out.println(responseBody);

        String temp = parseJsonBody(responseBody, "temp_avg");
        System.out.println("temp_avg: " + temp);
    }


    private static String connect(String pathVariables) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + pathVariables))
                .header("X-Yandex-Weather-Key", "afa95d01-e9b8-4d6b-b21c-de520a5a2654")
                .headers("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String parseJsonBody(String body, String keyJson) {
        body = body.trim();

        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1);
            String[] keyValuePairs = body.split(",");
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split(":");
                String key = parts[0].trim().replaceAll("[{\"}]", "");
                if (key.equals(keyJson)) {
                    return parts[1].trim().replaceAll("[{\"}]", "");
                }
            }
        }
        return "";
    }
}

