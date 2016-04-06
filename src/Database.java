import org.json.JSONArray;
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

    class CommandExecutionFailedException extends Exception {}
    class ImproperTypeRequestException extends CommandExecutionFailedException {};

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
            }
            catch (CommandExecutionFailedException e) {
                history.rollBack(startIndex);
            }
        }
    }

    private static class Cursor extends Observable{
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

    @Override
    public void update(Observable o, Object arg) {
        modify((String)arg, ((Cursor)o).watchValue);
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
        if(!transactionPending()) {
            put.execute();
        }
    }

    static class DatabaseObject {
        DatabaseObject(JSONObject j) {

        }
    }

    static class DatabaseArray {
        DatabaseArray(JSONArray j) {

        }
    }

    private Object get(String tag) {
        return (new GetCommand(tag)).execute();
    }

    public Integer getInt(String tag) {
        return (Integer)get(tag);
    }

    public Double getDouble(String tag) {
        return (Double)get(tag);
    }

    public DatabaseObject getObject(String tag) {
        return new DatabaseObject((JSONObject)get(tag));
    }

    public DatabaseArray getArray(String tag) {
        return new DatabaseArray((JSONArray)get(tag));
    }

    public Object remove(String tag) {
        RemoveCommand remove = new RemoveCommand(tag);
        history.store(remove);

        Object result;
        if(!transactionPending()) {
            result = remove.execute();
        }
        else {
            result = data.get(tag);
        }

        return result;
    }

    public void modify(String tag, Object value) {
        RemoveCommand remove = new RemoveCommand(tag);
        PutCommand put           = new PutCommand(tag, value);

        history.store(remove);
        history.store(put);

        if(!transactionPending()) {
            remove.execute();
            put.execute();
        }
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

        Object[] o = {"s", new Object(), 4, "string"};

        d.modify("d", o);
        System.out.print(data.toString());

        Cursor c = new Cursor("d");
        c.set(912);

        System.out.print(data.toString());
        d.close();
    }
}
