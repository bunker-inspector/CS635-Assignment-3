import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class PutCommand extends Command {
    PutCommand(String tag, int value) {
        executer = () -> Database.getInstance().data.put(tag, value);
        undoer   = () -> Database.getInstance().data.remove(tag);
    }

    PutCommand(String tag, String value) {
        executer = () -> Database.getInstance().data.put(tag, value);
        undoer   = () -> Database.getInstance().data.remove(tag);
    }

    PutCommand(String tag, Object[] value) {
        executer = () -> Database.getInstance().data.put(tag, value);
        undoer   = () -> Database.getInstance().data.remove(tag);
    }

    PutCommand(String tag, Map value) {
        executer = () -> Database.getInstance().data.put(tag, value);
        undoer   = () -> Database.getInstance().data.remove(tag);
    }
}
