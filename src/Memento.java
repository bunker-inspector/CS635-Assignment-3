import java.util.ArrayList;

/**
 * Created by ted on 3/27/16.
 */
public class Memento {
    private ArrayList<Command> states = new ArrayList<>();

    int store(Command newState) {
        states.add(newState);
        return states.size() - 1;
    }

    void rollBack (int stateIndex) {
        for(int i = states.size() - 1; i > stateIndex; i++) {
            states.get(i).undo();
        }
    }
}
