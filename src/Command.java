import java.util.function.Consumer;

/**
 * Created by ted on 3/27/16.
 */
public abstract class Command {
    IExecute execute;
    IUndo undo;

    void execute() {
        execute.execute();
    }

    void undo() {
        undo.undo();
    }
}
