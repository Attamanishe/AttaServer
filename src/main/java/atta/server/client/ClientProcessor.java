package atta.server.client;

import atta.utill.callback.CallBackDouble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class ClientProcessor
{

    class ClientContainer
    {
        private List<Client> clients;
        private ConcurrentHashMap<Long, Client> clientsIds;

        public ClientContainer()
        {
            clients = Collections.synchronizedList(new ArrayList());
            clientsIds = new ConcurrentHashMap<>();
        }

        public void addClient(long id, Client client)
        {
            if (!clientsIds.containsKey(id))
            {
                clientsIds.put(id, client);
                clients.add(client);
            } else
            {
                System.err.println("There is the client with same id: " + id);
            }
        }

        public Client getClientWithId(long id)
        {
            return clientsIds.get(id);
        }

        public Client get(int i)
        {
            return clients.get(i);
        }

        public void remove(Client client)
        {
            clients.remove(client);
            clientsIds.remove(client.getId());
        }

        public void removeWithId(long id)
        {
            clients.remove(clientsIds.remove(id));
        }

        public void remove(int i)
        {
            clientsIds.remove(clients.remove(i).getId());
        }

        public boolean contains(long id)
        {
            return clientsIds.containsKey(id);
        }

        public List<Client> getList()
        {
            return clients;
        }

        public int size()
        {
            return clients.size();
        }
    }

    private ClientMessageReceiver messageHandler;
    private ClientMessageSender messageSender;
    private ClientPinger pinger;
    private ClientContainer clients;

    public ClientProcessor(CallBackDouble<Long, String> onMessageReceived)
    {
        clients = new ClientContainer();
        List<Client> clientList = clients.getList();
        pinger = new ClientPinger(clientList);
        messageHandler = new ClientMessageReceiver(clientList, onMessageReceived);
        messageSender = new ClientMessageSender();
        messageHandler.start();
        messageSender.start();
        pinger.start();
    }

    public int getClientsCount()
    {
        return clients.size();
    }

    public void stop()
    {
        pinger.stop();
        messageSender.stop();
        messageHandler.stop();
    }

    public void addClient(long id, Client client)
    {
        clients.addClient(id, client);
    }

    public void removeClient(long id)
    {
        clients.removeWithId(id);
    }

    public void sendMessage(long id, String message)
    {
        messageSender.send(clients.getClientWithId(id), message);
    }
}

