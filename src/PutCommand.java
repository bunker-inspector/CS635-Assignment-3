import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class PutCommand extends Command {
    PutCommand(String tag, int value) {
        execute = () -> Database.getInstance().put(tag, value);
        undo = () -> Database.getInstance().retrieve(tag);
    }

    PutCommand(String tag, String value) {
        execute = () -> Database.getInstance().put(tag, value);
        undo    = () -> Database.getInstance().retrieve(tag);
    }

    PutCommand(String tag, Object[] value) {
        execute = () -> Database.getInstance().put(tag, value);
        undo    = () -> Database.getInstance().retrieve(tag);
    }

    PutCommand(String tag, Map value) {
        execute = () -> Database.getInstance().put(tag, value);
        undo    = () -> Database.getInstance().retrieve(tag);
    }
}
