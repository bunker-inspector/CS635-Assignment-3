import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class RetrieveCommand extends AbstractCommand {
    RetrieveCommand(String tag, int value) {
        execute = (Void) -> Database.getInstance().retrieve(tag);
        undo    = (Void) -> Database.getInstance().put(tag, value);
    }

    RetrieveCommand(String tag, String value) {
        execute = (Void) -> Database.getInstance().retrieve(tag);
        undo    = (Void) -> Database.getInstance().put(tag, value);
    }

    RetrieveCommand(String tag, Object[] value) {
        execute = (Void) -> Database.getInstance().retrieve(tag);
        undo    = (Void) -> Database.getInstance().put(tag, value);
    }

    RetrieveCommand(String tag, Map value) {
        execute = (Void) -> Database.getInstance().retrieve(tag);
        undo    = (Void) -> Database.getInstance().put(tag, value);
    }
}
