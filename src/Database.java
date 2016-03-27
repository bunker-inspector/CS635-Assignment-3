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

    public void put(String tag, int value) {
        PutCommand p = new PutCommand(tag, value);
        p.execute();
    }

    public void put(String tag, String value) {
    }

    public void put(String tag, Object[] value) {
    }

    public boolean put(String tag, Map value) {
        return true;
    }

    public Object retrieve (String tag) {
        return null;
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
