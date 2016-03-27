import org.json.JSONObject;
import java.io.*;
import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class Database {
    private static Database ourInstance = new Database();
    private JSONObject data = new JSONObject();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dbfile"));
            data = (JSONObject) ois.readObject();
            ois.close();
        }
        catch (FileNotFoundException e) {}
        catch (ClassNotFoundException e) {}
        catch (IOException e) {}
    }

    public void put(String tag, int i) {

    }

    public void put(String tag, String s) {

    }

    public void put(String tag, Object[] o) {

    }

    public void put(String tag, Map m) {

    }

    public void retrieve (String tag) {

    }

    private void close() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dbfile"));
            oos.writeObject(data);
            oos.close();
        }
        catch (FileNotFoundException e){}
        catch (IOException e) {}
    }
}
