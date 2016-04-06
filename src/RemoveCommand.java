import java.io.Serializable;

/**
 * Created by ted on 3/27/16.
 */
public class RemoveCommand extends Command implements Serializable{
    RemoveCommand(String tag) {
        Object result = Database.data.get(tag);
        executer = (IExecute & Serializable)() -> {
            Database.data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> Database.data.put(tag, result);
    }
}
