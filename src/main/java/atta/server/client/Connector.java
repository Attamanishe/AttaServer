package atta.server.client;

import atta.utill.callback.CallBackSingle;
import atta.utill.container.Container;
import atta.utill.loger.LogController;
import processors.diagnostically.DiagnosticallyTaskProcessor;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class Connector extends DiagnosticallyTaskProcessor
{
    private static int TIME_TO_RECONNECT;
    private static int UPDATE_TIME;
    private static boolean IS_LISTENING_FOR_RECONNECTION;
    private static boolean IS_ALLOW_MULTIPLE_CONNECTIONS;

    private AtomicLong idGenerator;
    private List<Integer> connectedClients;
    private Map<Integer, Container<atta.server.client.Client, Long>> clientsLostConnectionByHashes;
    private List<Container<atta.server.client.Client, Long>> clientsLostConnection;
    private CallBackSingle<atta.server.client.Client> onClientConnected, onClientDisconnected;

    public Connector(CallBackSingle<atta.server.client.Client> onClientConnected, CallBackSingle<Client> onClientDisconnected)
    {
        super("Connection checker", UPDATE_TIME);
        clientsLostConnectionByHashes = new ConcurrentHashMap<>();
        connectedClients = Collections.synchronizedList(new ArrayList());
        clientsLostConnection = Collections.synchronizedList(new ArrayList());
        this.onClientConnected = onClientConnected;
        this.onClientDisconnected = onClientDisconnected;
        idGenerator = new AtomicLong(0);
    }

    public static void setSettings(boolean multiConnection, boolean listeningForReconnection, int reconnectTime, int reconnectUpdateTime)
    {
        IS_ALLOW_MULTIPLE_CONNECTIONS = multiConnection;
        IS_LISTENING_FOR_RECONNECTION = listeningForReconnection;
        UPDATE_TIME = reconnectUpdateTime;
        TIME_TO_RECONNECT = reconnectTime;
    }

    public void gotConnection(Socket socket)
    {
        int hash = socket.getInetAddress().hashCode();
        String message = "Connected new socket with ip: " + socket.getInetAddress();
        System.out.println(message);
        LogController.getInstance().Log(message);
        if (IS_LISTENING_FOR_RECONNECTION)
        {
            if (clientsLostConnectionByHashes.containsKey(hash))
            {
                clientsLostConnectionByHashes.get(hash).first.reconnect(socket);
                clientsLostConnection.remove(clientsLostConnectionByHashes.remove(hash));
            }
        }
        if ((IS_ALLOW_MULTIPLE_CONNECTIONS == !IS_LISTENING_FOR_RECONNECTION) || !connectedClients.contains(hash))
        {
            addClient(socket, hash);
        }
    }

    public void clientLostConnection(Client client)
    {
        if (IS_LISTENING_FOR_RECONNECTION)
        {
            String message = "Client with id: " + client.getId() + " lost connection";
            System.out.println(message);
            LogController.getInstance().Log(message);
            if (!clientsLostConnectionByHashes.containsKey(client.getIP()))
            {
                Container<Client, Long> clientContainer = new Container<>(client, System.currentTimeMillis());
                clientsLostConnectionByHashes.put(client.getIP(), clientContainer);
                clientsLostConnection.add(clientContainer);
            }
        } else
        {
            onClientDisconnected.call(client);
        }
    }

    @Override
    public String getGroupName()
    {
        return "Connector";
    }

    @Override
    protected void taskCycle()
    {
        boolean clientRemoved;
        long timeNow = System.currentTimeMillis();
        do
        {
            clientRemoved = false;
            if (clientsLostConnection.size() > 0)
            {
                if (clientsLostConnection.get(0).second < timeNow - TIME_TO_RECONNECT)
                {
                    clientRemoved = true;
                    Client client = clientsLostConnection.get(0).first;
                    clientsLostConnectionByHashes.remove(client.getIP());
                    clientsLostConnection.remove(0);
                    connectedClients.remove(connectedClients.indexOf(client.getIP()));
                    onClientDisconnected.call(client);
                }
            }
        } while (clientRemoved);
    }

    private void addClient(Socket socket, int hash)
    {
        connectedClients.add(hash);
        onClientConnected.call(new Client(socket, idGenerator.getAndIncrement()));
    }
}
