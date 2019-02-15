package atta.server.client;

import processors.diagnostically.DiagnosticallyTaskProcessor;

import java.util.List;

class ClientPinger extends DiagnosticallyTaskProcessor
{
    private static int UPDATE_TIME;

    private List<Client> clients;

    public ClientPinger(List<Client> clients)
    {
        super("Client pinger", UPDATE_TIME);
        this.clients = clients;
    }

    public static void setSettings(int pingUpdateTime)
    {
        UPDATE_TIME = pingUpdateTime;
    }

    @Override
    public String getGroupName()
    {
        return "Client pinger";
    }

    @Override
    protected void taskCycle()
    {
        for (int i = 0; i < clients.size(); i++)
        {
            clients.get(i).send("");
        }
    }
}
