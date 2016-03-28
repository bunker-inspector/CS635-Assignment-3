import java.io.Serializable;
import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class RetrieveCommand extends Command implements Serializable{
    RetrieveCommand(String tag, int value) {
        executer = (IExecute & Serializable)() -> {
            Object result = Database.data.getInt(tag);
            Database.data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> Database.data.put(tag, value);
    }

    RetrieveCommand(String tag, String value, Database d) {
        executer = (IExecute & Serializable)() -> {
            Object result = Database.data.getString(tag);
            Database.data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> Database.data.put(tag, value);
    }

    RetrieveCommand(String tag, Object[] value, Database d) {
        executer = (IExecute & Serializable)() -> {
            Object result = Database.data.getJSONArray(tag);
            Database.getInstance().data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> Database.data.put(tag, value);
    }

    RetrieveCommand(String tag, Map value, Database d) {
        executer = (IExecute & Serializable)() -> {
            Object result = Database.data.getJSONObject(tag);
            Database.data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> Database.data.put(tag, value);
    }
}
