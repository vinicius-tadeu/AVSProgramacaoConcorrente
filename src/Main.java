
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

    public static double tempMinima(JSONArray tempsDiaria){
        double minimo = Double.MAX_VALUE;

        for (int j = 0; j < tempsDiaria.length(); j++) {
            double temperatura = tempsDiaria.getDouble(j);
            minimo = Math.min(minimo, temperatura);
        }
        double result = minimo;
        return result;
    }

    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        String capitais = new String(Files.readAllBytes(Paths.get("src\\Capitais.json")));
        JSONArray jsonArray = new JSONArray(capitais);

        for(int i = 0;i<jsonArray.length(); i++){
            String str = jsonArray.get(i).toString();
            JSONObject object1 = new JSONObject(str);

            String nomeCapital = object1.getString("nomeCapital");
            float latitude = object1.getFloat("latitude");
            float longitude = object1.getFloat("longitude");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://historical-forecast-api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&start_date=2024-01-01&end_date=2024-01-31&hourly=temperature_2m")).GET().build();
            System.out.println("https://historical-forecast-api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&start_date=2024-01-01&end_date=2024-01-31&hourly=temperature_2m");
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            int status = httpResponse.statusCode();
            if(status == 200){
                System.out.println("Requisição bem sucedida! (status 200)");
            }else{
                System.out.println("Ocorreu algum erro");
            }

            JSONObject apiResponse = new JSONObject(httpResponse.body());
            JSONObject hourly = apiResponse.getJSONObject("hourly");
            JSONArray temperaturas = hourly.getJSONArray("temperature_2m");
            System.out.println(apiResponse);
            System.out.println("Nome capital: " + nomeCapital);
            System.out.println("Latitude: " + latitude);
            System.out.println("Latitude encontrada na api: " + apiResponse.get("latitude"));
            System.out.println("Longitude: " + longitude);
            System.out.println("Longitude encontrada na api: " + apiResponse.get("longitude"));
            System.out.println("Temperaturas recebidas: " + temperaturas);
            // Retornou a quantidade de 31 dias * 24 (744) horas por dia, ou seja todas as temperaturas por hora de janeiro, que estão por ordem crescente, portanto basta eu fazer um for e realizar as operações separando por arrays de tamanho 24.
            System.out.println("Quantidade de temperaturas registradas: " + temperaturas.length());
            int dia = 1;
            double resultadoTempMin = 0.0;
            JSONArray listaTemperaturasPorDia = new JSONArray();
            for (int l = 0; l<temperaturas.length();l++){
                double temperatura = temperaturas.getDouble(l);
                listaTemperaturasPorDia.put(temperatura);

                    if((l+1)%24 == 0) {
                        resultadoTempMin = tempMinima(listaTemperaturasPorDia);
                        System.out.println("Dia :" + dia);
                        System.out.println("Lista de temperaturas do dia " + dia + ": " + listaTemperaturasPorDia);
                        System.out.println("Temperatura mínima do dia " + dia + ": " + resultadoTempMin);
                        System.out.println("\n");
                        listaTemperaturasPorDia.clear();
                        dia++;
                    }
            }

            System.out.println("Valor mínimo das temperaturas: " + resultadoTempMin);

            System.out.println("\n");



        }


        Thread.sleep(3000);

    }
}