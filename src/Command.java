import java.io.Serializable;

/**
 * Created by ted on 3/27/16.
 */
public abstract class Command implements Serializable{
    static class ImproperFormattingException extends Exception {}

    IExecute executer = (IExecute & Serializable) () -> { return new Object(); };
    IUndo undoer = (IUndo & Serializable )() -> { return new Object(); };
    ISerialize serializer = (ISerialize & Serializable)() -> { return new String(); };

    Object execute() {
        return executer.execute();
    }

    Object undo() {
        return undoer.undo();
    }

    String serialize() {
        return serializer.serialize();
    }

    static Command deserializeCommand(String serializedCommand) throws ImproperFormattingException {
        String[] commandTokens = serializedCommand.split("\t");
        switch (commandTokens[0]) {
            case Database.PUT_ID:
                switch (commandTokens[2]) {
                    case Database.INTEGER:
                        return new PutCommand(commandTokens[1], Integer.parseInt(commandTokens[3]));
                    case Database.DOUBLE:
                        return new PutCommand(commandTokens[1], Double.parseDouble(commandTokens[3]));
                    case Database.STRING:
                        return new PutCommand(commandTokens[1], commandTokens[3]);
                    case Database.ARRAY:
                            return new PutCommand(commandTokens[1],
                                    Database.DatabaseArray.fromString(commandTokens[3]).toString());
                    case Database.OBJECT:
                            return new PutCommand(commandTokens[1],
                                    Database.DatabaseObject.fromString(commandTokens[3]).toString());
                }
                break;
            case Database.REMOVE_ID:
                return new RemoveCommand(commandTokens[1]);
        }
        throw new ImproperFormattingException();
    }
}