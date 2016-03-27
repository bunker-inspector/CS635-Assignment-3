import java.util.ArrayList;

/**
 * Created by ted on 3/27/16.
 */
public class Memento {
    ArrayList<Command> states = new ArrayList<>();

    void store(Command newState) {
        states.add(newState);
    }

    void rollBack (int stateIndex) {
        for(int i = states.size() - 1; i > stateIndex; i++) {
            states.get(i).undo();
        }
    }

}
