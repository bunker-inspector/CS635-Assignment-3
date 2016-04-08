import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ted on 3/27/16.
 */
public class PutCommand extends Command implements Serializable{
    PutCommand(String tag, Object value) {
        executer = (IExecute & Serializable)() -> Database.data.put(tag, value);
        undoer   = (IUndo & Serializable)() -> Database.data.remove(tag);
    }
}
