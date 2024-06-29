
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
    public static final long startTime = System.currentTimeMillis();
    // Cores ANSI para formatação do texto no console
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m"; // Para representar o títulos e nomes das capitais.
    public static final String BLUE = "\u001B[34m"; // Para representar o título dos valores.
    public static final String PURPLE = "\u001B[35m"; // Para representar os valores

    public static double tempMinima(JSONArray tempsDiaria){
        double minimo = Double.MAX_VALUE;

        for (int j = 0; j < tempsDiaria.length(); j++) {
            double temperatura = tempsDiaria.getDouble(j);
            minimo = Math.min(minimo, temperatura);
        }
        return minimo;
    }

    public static double tempMaxima(JSONArray tempsDiaria){
        double maxima = Double.MIN_VALUE;

        for (int j = 0; j < tempsDiaria.length(); j++) {
            double temperatura = tempsDiaria.getDouble(j);
            maxima = Math.max(maxima, temperatura);
        }
        return maxima;
    }

    public static double tempMedia(JSONArray tempsDiaria){
        double total=0.0;
        for (int j = 0; j < tempsDiaria.length(); j++) {
            double temperatura = tempsDiaria.getDouble(j);
            total += temperatura;
        }
        return total/tempsDiaria.length();
    }

    public static void imprimirDia(int dia, JSONArray listaTemperaturasPorDia, double tempMedia,double tempMin, double tempMax){
        String tempMediaFormatado = String.format("%.2f",tempMedia);
        System.out.println(RESET+RED+"------ Dia "+dia+" ------" + RESET);
        System.out.println(BLUE+"Lista de temperaturas do dia " + dia + ": "+PURPLE+ listaTemperaturasPorDia+RESET);
        System.out.println(BLUE+"Temperatura média do dia " + dia + ": " +PURPLE+ tempMediaFormatado + RESET);
        System.out.println(BLUE+"Temperatura mínima do dia " + dia + ": " +PURPLE+ tempMin + RESET);
        System.out.println(BLUE+"Temperatura máxima do dia " + dia + ": " +PURPLE+ tempMax + RESET);
        System.out.println(RED+"--------------------");
        System.out.println("\n");
    }

    public static void imprimirInfosCapital(JSONObject apiResponse,String nomeCapital, float latitude, float longitude, JSONArray temperaturas){
        System.out.println("\n");
        System.out.println(RED+"------ "+nomeCapital+" ------" +PURPLE);
        System.out.println(RED+"Latitude: " + PURPLE+latitude);
        System.out.println(RED+"Latitude encontrada na api: " + PURPLE+apiResponse.get("latitude"));
        System.out.println(RED+"Longitude: " + PURPLE+longitude);
        System.out.println(RED+"Longitude encontrada na api: " + PURPLE+apiResponse.get("longitude"));
        System.out.println(RED+"Temperaturas recebidas: " + PURPLE+temperaturas);
        // Retornou a quantidade de 31 dias * 24 (744) horas por dia, ou seja todas as temperaturas por hora de janeiro, que estão por ordem crescente, portanto basta eu fazer um for e realizar as operações separando por arrays de tamanho 24.
        System.out.println(RED+"Quantidade de temperaturas registradas: " + PURPLE+temperaturas.length());
        System.out.println(RED+"--------------------");
        System.out.println("\n");
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
            System.out.println("\n");
            System.out.println("Link da API: "+"https://historical-forecast-api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longitude+"&start_date=2024-01-01&end_date=2024-01-31&hourly=temperature_2m");
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
            imprimirInfosCapital(apiResponse, nomeCapital, latitude,longitude,temperaturas);
            int dia = 1;
            double resultadoTempMedia;
            double resultadoTempMin;
            double resultadoTempMax;
            JSONArray listaTemperaturasPorDia = new JSONArray();
            for (int l = 0; l<temperaturas.length();l++){
                double temperatura = temperaturas.getDouble(l);
                listaTemperaturasPorDia.put(temperatura);

                    if((l+1)%24 == 0) {
                        resultadoTempMedia = tempMedia(listaTemperaturasPorDia);
                        resultadoTempMin = tempMinima(listaTemperaturasPorDia);
                        resultadoTempMax = tempMaxima(listaTemperaturasPorDia);
                        imprimirDia(dia,listaTemperaturasPorDia,resultadoTempMedia,resultadoTempMin,resultadoTempMax);
                        listaTemperaturasPorDia.clear();
                        dia++;
                    }
            }
            System.out.println(RESET);

        }
            long endTime = System.currentTimeMillis();
            long elapsedTime = endTime - startTime;
            System.out.println("Tempo de execução do código com 1 thread: " + elapsedTime + " milliseconds");
    }
}