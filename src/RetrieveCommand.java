import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ted on 3/27/16.
 */
public class RetrieveCommand extends Command implements Serializable{
    Object result;

    RetrieveCommand(String tag) {
        result = Database.data.get(tag);
        executer = (IExecute & Serializable)() -> {
            Database.data.remove(tag);
            return result;
        };
        undoer   = (IUndo & Serializable)() -> Database.data.put(tag, result);
    }
}
