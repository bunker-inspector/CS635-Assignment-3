import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by ted on 3/27/16.
 */
public abstract class AbstractCommand {
    Consumer execute;
    Consumer undo;
}
