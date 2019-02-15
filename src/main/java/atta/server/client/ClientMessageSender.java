package atta.server.client;

import atta.utill.container.Container;
import processors.load.LoadBalancingTaskProcessor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class ClientMessageSender extends LoadBalancingTaskProcessor
{
    private final static int HIGH_LOAD = 5000, LOW_LOAD = 1000;
    private static int UPDATE_TIME;
    private static int PART_COUNT_MESSAGE;

    private Queue<Container<Client, String>> toSend = new ConcurrentLinkedQueue<>();

    public ClientMessageSender()
    {
        super(50, 1000, "Client message sender", UPDATE_TIME);
    }

    public static void setSettings(int updateTime, int messagePerUpdateSend)
    {
        UPDATE_TIME = updateTime;
        PART_COUNT_MESSAGE = messagePerUpdateSend;
    }

    public void send(Client client, String message)
    {
        toSend.add(new Container<>(client, message));
    }

    @Override
    protected void process()
    {
        for (int i = 0; i < PART_COUNT_MESSAGE && !toSend.isEmpty(); i++)
        {
            Container<Client, String> message = toSend.poll();
            if (message != null && message.first != null)
            {
                message.first.send(message.second);
            }
        }
    }

    @Override
    protected int getLoadValue()
    {
        if (toSend.size() > HIGH_LOAD)
        {
            return LOAD_HIGH;
        }
        if (toSend.size() < LOW_LOAD)
        {
            return LOAD_LOW;
        }
        return LOAD_NORMAL;
    }
}
