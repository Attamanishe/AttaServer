
import atta.server.Executor;
import atta.server.comand.model.CommandModel;

public class SimpleExecutor extends Executor
{
    @Override
    public void execute(long clientId, CommandModel model)
    {
        //send to client the same message he sent
        send(clientId,model);
    }

    @Override
    public int getExecutableCommandTypes()
    {
        return 1;
    }
}
