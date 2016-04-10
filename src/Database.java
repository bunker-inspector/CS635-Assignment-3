import com.sun.xml.internal.bind.v2.schemagen.xmlschema.Import;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/**
 * Created by ted on 3/27/16.
 */
public class Database implements Observer {
    protected static Database ourInstance;
    protected static JSONObject data = new JSONObject();
    protected Memento history = new Memento();
    protected static PrintWriter deltas;
    public static final String  PUT_ID    = "PUT",
                                REMOVE_ID = "REM";
    public static final String  INTEGER = "class java.lang.Integer",
                                DOUBLE  = "class java.lang.Double",
                                STRING   = "class java.lang.String";

    class CommandExecutionFailedException extends Exception {
    }

    class ImproperTypeRequestException extends CommandExecutionFailedException {
    }

    static class OhHellNoNotInMyDatabaseException extends Exception {
    }

    public static Database getInstance() throws CommandExecutionFailedException,
            Command.ImproperFormattingException {
        if (ourInstance == null) {
            ourInstance = new Database();
            ourInstance.history.restore();
        }
        return ourInstance;
    }

    private class Transaction {
        protected boolean isActive;
        protected Memento commands;

        private Transaction() {
            isActive = true;
            commands = new Memento();
        }

        public void put(String tag, Object value) {
            commands.store(new PutCommand(tag, value));
        }

        private Object get(String tag) {
            GetCommand get = new GetCommand(tag);
            commands.store(get);
            return get.execute();
        }

        public Integer getInt(String tag) {
            return (Integer)get(tag);
        }

        public Double getDouble(String tag) {
            return (Double)get(tag);
        }

        public DatabaseArray getArray(String tag) {
            return DatabaseArray.fromString((String)get(tag));
        }

        public DatabaseObject getObject(String tag) {
            return DatabaseObject.fromString((String)get(tag));
        }

        public Object remove(String tag) {
            return commands.store(new RemoveCommand(tag));
        }

        public boolean isActive() {
            return isActive;
        }

        protected void abort() {
            history.rollBack(0);
        }

        protected void reset() {
            if(!isActive()) {
                isActive = true;
                commands = new Memento();
            }
        }

        protected void commit() {
            try {
                commands.playBack(0, commands.size());
                history.states.addAll(commands.states);
                isActive = false;
            }
            catch (CommandExecutionFailedException e) {
                abort();
            }
        }
    }

    private static class Cursor extends Observable {
        protected Object watchValue;
        protected String tag;

        Cursor(String t) throws CommandExecutionFailedException, Command.ImproperFormattingException {
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

    private Database() throws CommandExecutionFailedException, Command.ImproperFormattingException {
        try {
            recover();
        } catch (FileNotFoundException e) {
            System.out.println("Database file not found: Creating new dbfile...");
            history = new Memento();
            try {
                deltas = new PrintWriter(new File("commands.txt"));
            }
            catch (FileNotFoundException f) {
                //this should never happen
            }
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

    public Transaction createTransaction() {
        return new Transaction();
    }

    public void put(String tag, Object value) {
        PutCommand put = new PutCommand(tag, value);
        history.store(put);
        put.execute();
        deltas.println(put.serialize());
    }

    public void put(String tag, DatabaseArray value) {
        put(tag, value.data.toString());
    }

    public void put(String tag, DatabaseObject value) {
        put(tag, value.data.toString());
    }

    public Object get(String tag) {
        GetCommand get = new GetCommand(tag);
        return (get).execute();
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
        deltas.println(remove.serialize());
        return remove.execute();
    }

    public void modify(String tag, Object value) {
        RemoveCommand remove = new RemoveCommand(tag);
        PutCommand put = new PutCommand(tag, value);

        history.store(remove);
        history.store(put);

        remove.execute();
        put.execute();
    }

    public void rollBack(int version) {
        history.rollBack(version);
    }

    public void close() {
        if(deltas != null) {
            deltas.close();
        }
    }

    public void snapshot() {
        snapshot(new File("commands.txt"), new File("dbSnapshot.txt"));
    }

    public void snapshot(File commands, File snapshot) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(snapshot));
            oos.writeObject(history);
            oos.close();

            deltas = new PrintWriter(commands);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void recover() throws FileNotFoundException, IOException,
            ClassNotFoundException, CommandExecutionFailedException,
            Command.ImproperFormattingException{
        recover(new File("commands.txt"), new File("dbSnapshot.txt"));
    }

    public void recover(File commands, File snapshot) throws FileNotFoundException, IOException,
            ClassNotFoundException, CommandExecutionFailedException, Command.ImproperFormattingException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(snapshot));
        history = (Memento) ois.readObject();
        history.playBack(0, history.size() - 1);

        BufferedReader commandFileReader = new BufferedReader(new FileReader(commands));
        String currentLine;

        while((currentLine = commandFileReader.readLine() ) != null) {
            Command currentCommand = Command.deserializeCommand(currentLine);
            currentCommand.execute();
            history.store(currentCommand);
        }

        deltas = new PrintWriter(new FileWriter(commands, true));
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

        @Override
        public String toString() {
            return data.toString();
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

        @Override
        public String toString() {
            return data.toString();
        }
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public static void main(String[] args) throws CommandExecutionFailedException, Command.ImproperFormattingException {
        Database d = Database.getInstance();

        d.put("v", 5);
         DatabaseObject o = DatabaseObject.fromString("{'r': [1, 2 ,4]}");
        d.put("u", o);

        System.out.println(d.toString());

        d.snapshot();
        d.close();
    }
}
