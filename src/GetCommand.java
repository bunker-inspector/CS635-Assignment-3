import java.io.Serializable;

/**
 * Created by ted on 4/5/16.
 */
public class GetCommand extends Command implements Serializable {
    GetCommand(String tag) {
        Object result = Database.data.get(tag);
        executer = (IExecute & Serializable)() -> {
            Database.data.get(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> null;
        serializer = (ISerialize & Serializable)() -> null;
    }
}