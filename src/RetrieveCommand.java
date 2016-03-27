import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class RetrieveCommand extends Command {
    RetrieveCommand(String tag, int value) {
        execute = () -> Database.getInstance().retrieve(tag);
        undo    = () -> Database.getInstance().put(tag, value);
    }

    RetrieveCommand(String tag, String value, Database d) {
        execute = () -> Database.getInstance().retrieve(tag);
        undo    = () -> Database.getInstance().put(tag, value);
    }

    RetrieveCommand(String tag, Object[] value, Database d) {
        execute = () -> Database.getInstance().retrieve(tag);
        undo    = () -> Database.getInstance().put(tag, value);
    }

    RetrieveCommand(String tag, Map value, Database d) {
        execute = () -> Database.getInstance().retrieve(tag);
        undo    = () -> Database.getInstance().put(tag, value);
    }
}
