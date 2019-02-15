package atta.server.client;

import atta.utill.callback.CallBackDouble;
import processors.load.LoadBalancingTaskProcessor;

import java.util.List;

class ClientMessageReceiver extends LoadBalancingTaskProcessor
{
    private final static int HIGH_LOAD = 50, LOW_LOAD = 5;
    private static int UPDATE_TIME = 10;

    private List<Client> clients;
    private CallBackDouble<Long, String> onMessageReceived;
    private long deltaWait;

    public ClientMessageReceiver(List<Client> clients, CallBackDouble<Long, String> onMessageReceived)
    {
        super(5, 1000, "Client message handler", UPDATE_TIME);
        this.clients = clients;
        this.onMessageReceived = onMessageReceived;
    }

    public static void setSettings(int listeningUpdateTime)
    {
        UPDATE_TIME = listeningUpdateTime;
    }

    @Override
    protected void process()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < clients.size(); i++)
        {
            Client client = clients.get(i);
            if (client.isReady())
            {
                List<String> data = client.getData();
                if (data != null)
                {
                    for (int j = 0, messagesCount = data.size(); j < messagesCount; j++)
                    {
                        if (client != null)
                        {
                            onMessageReceived.call(client.getId(), data.get(j));
                        }
                    }
                }
            }
        }
        deltaWait = System.currentTimeMillis() - start;
    }

    @Override
    protected int getLoadValue()
    {
        long delta = deltaWait;
        deltaWait = 0;
        if (delta > HIGH_LOAD)
        {
            return LOAD_HIGH;
        }
        if (delta < LOW_LOAD)
        {
            return LOAD_LOW;
        }
        return LOAD_NORMAL;
    }
}
