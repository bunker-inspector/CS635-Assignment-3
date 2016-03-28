import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ted on 3/27/16.
 */
public class Memento implements Serializable{
    private ArrayList<Command> states = new ArrayList<>();

    int store(Command newState) {
        states.add(newState);
        return states.size() - 1;
    }

    void restore() {
        for (Command c: states) {
            c.execute();
        }
    }

    void rollBack (int stateIndex) {
        for(int i = states.size() - 1; i > stateIndex; i++) {
            states.get(i).undo();
        }
    }
}
