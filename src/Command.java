/**
 * Created by ted on 3/27/16.
 */
public abstract class Command {
    IExecute executer;
    IUndo undoer;

    void execute() {
        executer.execute();
    }

    void undo() {
        undoer.undo();
    }
}
