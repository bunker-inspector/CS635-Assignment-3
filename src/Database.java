import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ted on 3/27/16.
 */
public class Database implements Observer {
    protected static Database ourInstance;
    protected static JSONObject data = new JSONObject();
    protected Memento history = new Memento();
    protected Transaction currentTransaction;

    class CommandExecutionFailedException extends Exception {
    }

    class ImproperTypeRequestException extends CommandExecutionFailedException {
    }

    static class OhHellNoNotInMyDatabaseException extends Exception {
    }

    public static Database getInstance() {
        if (ourInstance == null) {
            ourInstance = new Database();
            ourInstance.history.restore();
        }
        return ourInstance;
    }

    private class Transaction {
        int startIndex;
        Memento commands;

        private Transaction() {
            startIndex = history.size();
            commands = history;
        }

        protected void commit() {
            try {
                history.playBack(startIndex, history.size());
            } catch (CommandExecutionFailedException e) {
                history.rollBack(startIndex);
            }
        }
    }

    private static class Cursor extends Observable {
        protected Object watchValue;
        protected String tag;

        Cursor(String t) {
            tag = t;
            watchValue = data.get(tag);
            addObserver(Database.getInstance());
        }

        void set(Object newValue) {
            watchValue = newValue;
            setChanged();
            notifyObservers(tag);
            clearChanged();
        }
    }

    private Database() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("dbfile"));
            history = (Memento) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        modify((String) arg, ((Cursor) o).watchValue);
    }

    private boolean transactionPending() {
        return (currentTransaction != null);
    }

    public void startTransaction() {
        currentTransaction = new Transaction();
    }

    public void commmitTransaction() {
        currentTransaction.commit();
        currentTransaction = null;
    }

    public void put(String tag, Object value) {
        PutCommand put = new PutCommand(tag, value);
        history.store(put);
        if (!transactionPending()) {
            put.execute();
        }
    }

    public void put(String tag, DatabaseArray value) {
        put(tag, value.data.toString());
    }

    public void put(String tag, DatabaseObject value) {
        put(tag, value.data.toString());
    }

    public Object get(String tag) {
        return (new GetCommand(tag)).execute();
    }

    public Integer getInt(String tag) {
        return (Integer)get(tag);
    }

    public Double getDouble(String tag) {
        return (Double)get(tag);
    }

    public DatabaseObject getObject(String tag) {
        return DatabaseObject.fromString((String)get(tag));
    }

    public DatabaseArray getArray(String tag) {
        return DatabaseArray.fromString((String)get(tag));
    }

    public Object remove(String tag) {
        RemoveCommand remove = new RemoveCommand(tag);
        history.store(remove);

        Object result;
        if (!transactionPending()) {
            result = remove.execute();
        } else {
            result = data.get(tag);
        }

        return result;
    }

    public void modify(String tag, Object value) {
        RemoveCommand remove = new RemoveCommand(tag);
        PutCommand put = new PutCommand(tag, value);

        history.store(remove);
        history.store(put);

        if (!transactionPending()) {
            remove.execute();
            put.execute();
        }
    }

    private void close() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("dbfile"));
            oos.writeObject(history);
            oos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    static abstract class DatabaseType {
        public int length() {
            return data.length();
        }

        public String toString() {
            return data.toString();
        }
    }

    static class DatabaseObject {
        JSONObject data;

        DatabaseObject(JSONObject j) {
            data = j;
        }

        public JSONObject put(String tag, Object o) throws OhHellNoNotInMyDatabaseException {
            if (o == null) {
                throw new OhHellNoNotInMyDatabaseException();
            }
            data.put(tag, o);
            return data;
        }

        public Object get(String tag) {
            return data.get(tag);
        }

        public int getInt(String tag) {
            return data.getInt(tag);
        }

        public double getDouble(String tag) {
            return data.getDouble(tag);
        }

        public DatabaseArray getArray(String tag) {
            return new DatabaseArray(data.getJSONArray(tag));
        }

        public DatabaseObject getObject(String tag) {
            return new DatabaseObject(data.getJSONObject(tag));
        }

        public static DatabaseObject fromString(String newData) {
            return new DatabaseObject(new JSONObject(newData));
        }
    }

    static class DatabaseArray {
        protected JSONArray data;

        DatabaseArray(JSONArray j) {
            data = j;
        }

        public void put(Object o) throws OhHellNoNotInMyDatabaseException {
            if (o == null) {
                throw new OhHellNoNotInMyDatabaseException();
            }
            data.put(o);
        }

        public void remove(int index) {
            data.remove(index);
        }

        public Object get(int index) {
            try {
                return data.get(index);
            }
            catch(JSONException j) {
                throw new ArrayIndexOutOfBoundsException();
            }
        }

        public int getInt(int index) {
            return data.getInt(index);
        }

        public double getDouble(int index) {
            return data.getDouble(index);
        }

        public DatabaseArray getArray(int index) {
            return new DatabaseArray(data.getJSONArray(index));
        }

        public DatabaseObject getObject(int index) {
            return new DatabaseObject(data.getJSONObject(index));
        }

        public static DatabaseArray fromString(String newData) {
            return new DatabaseArray(new JSONArray(newData));
        }
    }

    public static void main(String[] args) {
        Database d = Database.getInstance();

        DatabaseObject o = DatabaseObject.fromString("{'d' : [4, 5, 6]}");

        d.put("d", o);
        o = d.getObject("d");

        System.out.print(o.data.toString());
        d.close();
    }
}
