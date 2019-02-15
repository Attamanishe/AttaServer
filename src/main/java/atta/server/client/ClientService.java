package atta.server.client;

import atta.utill.callback.CallBackDouble;
import atta.utill.callback.CallBackSingle;
import atta.server.peer.PeerManager;

import java.net.Socket;

public class ClientService
{
    private PeerManager peerManager;
    private ClientManager clientManager;
    private Connector connector;

    public static void setSettings(int clientsToProcessCount,
                                   int listeningUpdateTime,
                                   int sendingUpdateTime,
                                   int messagePerUpdateSend,
                                   int pingUpdateTime,
                                   boolean listeningForReconnection,
                                   boolean multiConnection,
                                   int reconnectTime,
                                   int reconnectUpdateTime)
    {
        Connector.setSettings(multiConnection, listeningForReconnection, reconnectTime, reconnectUpdateTime);
        ClientManager.setSettings(clientsToProcessCount);
        ClientPinger.setSettings(pingUpdateTime);
        ClientMessageReceiver.setSettings(listeningUpdateTime);
        ClientMessageSender.setSettings(sendingUpdateTime, messagePerUpdateSend);
    }

    public ClientService(CallBackDouble<Long, String> onMessageReceived)
    {
        peerManager = new PeerManager(
                new CallBackSingle<>()
                {
                    @Override
                    public void call(Socket socket)
                    {
                        onPeerConnected(socket);
                    }
                });

        clientManager = new ClientManager(onMessageReceived);

        connector = new Connector(
                new CallBackSingle<>()
                {
                    @Override
                    public void call(Client client)
                    {
                        clientConnected(client);
                    }
                },
                new CallBackSingle<>()
                {
                    @Override
                    public void call(Client client)
                    {
                        clientDisconnected(client);
                    }
                });
        Client.setOnClientDisconnected(
                new CallBackSingle<>()
                {
                    @Override
                    public void call(Client client)
                    {
                        clientLostConnection(client);
                    }
                }
        );
        connector.start();
    }

    public void sendMessage(long clientId, String message)
    {
        clientManager.sendMessage(clientId, message);
    }

    private void onPeerConnected(Socket peer)
    {
        connector.gotConnection(peer);
    }

    private void clientConnected(Client client)
    {
        clientManager.addClient(client);
    }

    private void clientDisconnected(Client client)
    {
        clientManager.removeClient(client);
    }

    private void clientLostConnection(Client client)
    {
        connector.clientLostConnection(client);
    }
}
