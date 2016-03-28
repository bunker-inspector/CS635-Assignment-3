import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.Map;

/**
 * Created by ted on 3/27/16.
 *
 * //TODO retrieve, transactions, cursor, unit tests....
 */
public class Database {
    protected static Database ourInstance;
    protected static JSONObject data = new JSONObject();
    protected Memento history = new Memento();

    public static Database getInstance() {
        if (ourInstance == null) {
            ourInstance = new Database();
            ourInstance.history.restore();
        }
        return ourInstance;
    }

    private Database() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dbfile"));
            history = (Memento) ois.readObject();
            ois.close();
        }
        catch (FileNotFoundException e) {
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
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
        RetrieveCommand r = new RetrieveCommand(tag);
        Object o = r.execute();

        if(o instanceof Integer) {
            System.out.println("INT");
        }
        else if (o instanceof  String) {
            System.out.println("String");
        }
        else if (o instanceof Object[]) {
            System.out.println("JArray");
        }
        else if (o instanceof Object) {
            System.out.println("JObject");
        }

        return o;
    }

    private void close() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dbfile"));
            oos.writeObject(history);
            oos.close();
        }
        catch (FileNotFoundException e){}
        catch (IOException e) {}
    }

    public static void main(String[] args) {
        Database d = Database.getInstance();
        Object[] l = {1, "sda", 3};
        d.put("first", l);
        System.out.print((d.retrieve("first")).toString());
        System.out.println(d.data);
        d.close();
    }
}
