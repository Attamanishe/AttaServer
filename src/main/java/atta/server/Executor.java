package atta.server;

import atta.server.comand.model.CommandModel;
import atta.utill.callback.CallBackDouble;

public abstract class Executor
{
    CallBackDouble<Long, CommandModel> sendCallBack;

    /**
     * Invokes when server got the command from client
     *
     * @param clientId client sent command
     * @param model    sent command
     */
    public abstract void execute(long clientId, CommandModel model);

    /**
     * @return type of command that can be execute by this executor. for example if return 1 this executor can execute command with ids 100-199
     */
    public abstract int getExecutableCommandTypes();

    /**
     * Send command to client
     *
     * @param clientId client to send command
     * @param model    command model to send
     */
    protected void send(long clientId, CommandModel model)
    {
        sendCallBack.call(clientId, model);
    }
}
