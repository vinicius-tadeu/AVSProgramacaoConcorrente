import org.json.JSONArray;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class MainThreads {

    public void execute(int qtdThreads) throws InterruptedException, URISyntaxException, IOException{
        String capitais = new String(Files.readAllBytes(Paths.get("src\\Capitais.json")));
        JSONArray jsonArray = new JSONArray(capitais);
        var gruposCapitais = dividirJSONArray(jsonArray, qtdThreads);
        ArrayList<Thread> threads = new ArrayList();
        gruposCapitais.forEach((key, jsonArrayGroup) -> {
            Runnable myThread = new MyThread(jsonArrayGroup, key);
            Thread thread = new Thread(myThread,"myThread");
            threads.add(thread);
            thread.start();
        });
        threads.forEach(o -> {
            try{
                o.join();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        });
    }

    private HashMap<String, JSONArray> dividirJSONArray(JSONArray jsonArray, int totalGroup) {
        HashMap<String, JSONArray> grupos = new HashMap<>();
        int tamanhoArray = jsonArray.length();
        int tamanhoGrupo = tamanhoArray / totalGroup;
        int startIndex = 0;

        for (int i = 0; i < totalGroup; i++) {
            int endIndex = startIndex + tamanhoGrupo;

            if (i == totalGroup - 1) {
                // Último grupo recebe o restante dos elementos
                endIndex = tamanhoArray;
            }

            JSONArray subArray = new JSONArray();
            for (int j = startIndex; j < endIndex; j++) {
                subArray.put(jsonArray.getJSONObject(j));
            }

            grupos.put(String.valueOf(i), subArray);
            startIndex = endIndex;
        }

        return grupos;
    }

}
