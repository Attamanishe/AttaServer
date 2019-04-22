package atta.server.peer;

import atta.utill.callback.CallBackSingle;
import java.net.Socket;

public class PeerManager
{
    private PeerDistributor distributor;
    private PeerListener listener;

    public PeerManager(CallBackSingle<Socket> onSocketConnected)
    {
        distributor = new PeerDistributor(onSocketConnected);
        listener = new PeerListener(new CallBackSingle<>()
        {
            @Override
            public void call(Socket socket)
            {
                distributor.add(socket);
            }
        });
    }

    public void start()
    {
        distributor.start();
        listener.start();
    }
}
