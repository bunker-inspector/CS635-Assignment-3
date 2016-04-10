import java.io.Serializable;

/**
 * Created by ted on 3/27/16.
 */
public class RemoveCommand extends Command implements Serializable{
    RemoveCommand(String tag) {

        executer = (IExecute & Serializable)() -> {
            Object result = Database.data.get(tag);
            Database.data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> {
            Object result = Database.data.get(tag);
            return Database.data.put(tag, result);
        };
        serializer = (ISerialize & Serializable)() -> {
            return (Database.REMOVE_ID + '\t'
                    + tag); };
    }
}
