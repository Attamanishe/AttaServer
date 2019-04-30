
import atta.server.Executor;
import atta.server.comand.model.CommandModel;

import java.io.Serializable;

class Client implements Serializable
{
    public String name;
    public int age;
}

public class SimpleExecutor extends Executor
{
    @Override
    public void execute(long clientId, CommandModel model)
    {
        send(clientId, model);
    }

    @Override
    public int getExecutableCommandTypes()
    {
        return 1;
    }
}
