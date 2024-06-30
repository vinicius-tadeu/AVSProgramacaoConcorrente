import org.json.JSONArray;
import java.io.IOException;
import java.net.URISyntaxException;

public class MyThread implements Runnable{
    JSONArray jsonArray;
    String name;
    MyThread(JSONArray jsonArray, String name){
        this.jsonArray = jsonArray;
        this.name = name;
    }

    @Override
    public void run(){
        try{
        MyProgram.execute(this.jsonArray, this.name);
        }catch(IOException e){
            System.out.println(e.getMessage());
        }catch (URISyntaxException e){
            System.out.println(e.getMessage());
        }catch(InterruptedException e){
            System.out.println(e.getMessage());
        }
    }
}
