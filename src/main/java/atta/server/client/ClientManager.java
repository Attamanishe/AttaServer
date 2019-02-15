package atta.server.client;

import atta.utill.callback.CallBackDouble;
import atta.utill.loger.LogController;

import java.util.concurrent.ConcurrentHashMap;

public class ClientManager
{
    private static int MAX_CLIENTS_PER_PROCESSOR_COUNT;
    private CallBackDouble<Long, String> onMessageReceived;
    private ConcurrentHashMap<Long, ClientProcessor> clientProcessors;

    public ClientManager(CallBackDouble<Long, String> onMessageReceived)
    {
        this.onMessageReceived = onMessageReceived;
        clientProcessors = new ConcurrentHashMap<>();
    }

    public static void setSettings(int clientsToProcessCount)
    {
        MAX_CLIENTS_PER_PROCESSOR_COUNT = clientsToProcessCount;
    }

    public void sendMessage(long clientId, String message)
    {
        long key=clientId / MAX_CLIENTS_PER_PROCESSOR_COUNT;
        if(clientProcessors.containsKey(key))
        {
            clientProcessors.get(key).sendMessage(clientId, message);
        }
    }

    public void addClient(Client client)
    {
        String message = "Client with id: " + client.getId() + " has been connected";
        System.out.println(message);
        LogController.getInstance().Log(message);
        long processorId = client.getId() / MAX_CLIENTS_PER_PROCESSOR_COUNT;
        if (!clientProcessors.containsKey(processorId))
        {
            clientProcessors.put(processorId, new ClientProcessor(onMessageReceived));
        }
        clientProcessors.get(processorId).addClient(client.getId(), client);
    }

    public void removeClient(Client client)
    {
        String message = "Client with id: " + client.getId() + " has been disconnected";
        System.out.println(message);
        LogController.getInstance().Log(message);
        long processorId = client.getId() / MAX_CLIENTS_PER_PROCESSOR_COUNT;
        ClientProcessor processor = clientProcessors.get(processorId);
        processor.removeClient(client.getId());
        if (processor.getClientsCount() == 0)
        {
            processor.stop();
            clientProcessors.remove(processorId);
        }
        client.kill();
    }
}

