import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class RetrieveCommand extends Command {
    RetrieveCommand(String tag, int value) {
        executer = () -> {
            Object result = Database.getInstance().data.getInt(tag);
            Database.getInstance().data.remove(tag);
            return result;
        };
        undoer   = () -> Database.getInstance().data.put(tag, value);
    }

    RetrieveCommand(String tag, String value, Database d) {
        executer = () -> {
            Object result = Database.getInstance().data.getString(tag);
            Database.getInstance().data.remove(tag);
            return result;
        };
        undoer   = () -> Database.getInstance().data.put(tag, value);
    }

    RetrieveCommand(String tag, Object[] value, Database d) {
        executer = () -> {
            Object result = Database.getInstance().data.getJSONArray(tag);
            Database.getInstance().data.remove(tag);
            return result;
        };
        undoer   = () -> Database.getInstance().data.put(tag, value);
    }

    RetrieveCommand(String tag, Map value, Database d) {
        executer = () -> {
            Object result = Database.getInstance().data.getJSONObject(tag);
            Database.getInstance().data.remove(tag);
            return result;
        };
        undoer   = () -> Database.getInstance().data.put(tag, value);
    }
}
