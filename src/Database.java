import org.json.JSONObject;
import java.io.*;
import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class Database {
    protected static Database ourInstance = new Database();
    protected JSONObject data = new JSONObject();
    protected Memento history = new Memento();

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
        history.store(p);
    }

    public void put(String tag, String value) {
        PutCommand p = new PutCommand(tag, value);
        p.execute();
        history.store(p);
    }

    public void put(String tag, Object[] value) {
        PutCommand p = new PutCommand(tag, value);
        p.execute();
        history.store(p);
    }

    public void put(String tag, Map value) {
        PutCommand p = new PutCommand(tag, value);
        p.execute();
        history.store(p);
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

    public static void main(String[] args) {
        Database d = Database.getInstance();
        Integer[] l = {1, 2, 3};
        d.put("First", l);
        System.out.println(d.data);
    }
}
