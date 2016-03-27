import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class PutCommand extends AbstractCommand{
    PutCommand(String tag, int value) {
        execute = (Void) -> Database.getInstance().put(tag, value);
        undo    = (Void) -> Database.getInstance().retrieve(tag);
    }

    PutCommand(String tag, String value) {
        execute = (Void) -> Database.getInstance().put(tag, value);
        undo    = (Void) -> Database.getInstance().retrieve(tag);
    }

    PutCommand(String tag, Object[] value) {
        execute = (Void) -> Database.getInstance().put(tag, value);
        undo    = (Void) -> Database.getInstance().retrieve(tag);
    }

    PutCommand(String tag, Map value) {
        execute = (Void) -> Database.getInstance().put(tag, value);
        undo    = (Void) -> Database.getInstance().retrieve(tag);
    }
}
