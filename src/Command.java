import java.io.Serializable;

/**
 * Created by ted on 3/27/16.
 */
public abstract class Command implements Serializable{
    IExecute executer = (IExecute & Serializable) () -> {return new Object();};
    IUndo undoer = (IUndo & Serializable )() -> {return new Object();};

    void execute() {
        executer.execute();
    }

    void undo() {
        undoer.undo();
    }
}
