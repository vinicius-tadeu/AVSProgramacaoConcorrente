import java.io.IOException;
import java.net.URISyntaxException;

public class Threads27 {
    public static  final long startTime = System.currentTimeMillis();
    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException{
        new MainThreads().execute(27);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Tempo de execução do código com "+27+" threads: " + elapsedTime + " milliseconds");
    }

}
