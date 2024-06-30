import java.io.IOException;
import java.net.URISyntaxException;

public class Threads9 {
    public static  final long startTime = System.currentTimeMillis();
    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException{
        new MainThreads().execute(9);
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        System.out.println("Tempo de execução do código com "+9+" threads: " + elapsedTime + " milliseconds");
    }

}
