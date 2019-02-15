package atta.server.peer;

import atta.helpers.ServerConfigReader;
import atta.utill.callback.CallBackSingle;
import processors.load.LoadBalancingTaskProcessor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


class PeerListener extends LoadBalancingTaskProcessor
{
    private final float HIGH_LOAD_LEVEL = 0.05f;
    private final float LOW_LOAD_LEVEL = 0.001f;
    private CallBackSingle<Socket> onPeerConnected;

    private AtomicLong startTime;
    private AtomicInteger socketConnected;
    private ServerSocket serverSocket;

    public PeerListener(CallBackSingle<Socket> onPeerConnected)
    {
        super(5, 1000, "Peer listener", 0);
        try
        {
            serverSocket = new ServerSocket(
                    ServerConfigReader.getServerPort(),
                    ServerConfigReader.getMaxConnectionsCount(),
                    InetAddress.getByName(ServerConfigReader.getServerIP()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        resetLoadValue();
        this.onPeerConnected = onPeerConnected;
    }

    @Override
    protected void process()
    {
        try
        {
            Socket socket = serverSocket.accept();
            onPeerConnected.call(socket);
            socketConnected.incrementAndGet();
        } catch (IOException ex)
        {
            System.out.println(ex.toString());
            System.out.println(ex.fillInStackTrace());
        }
    }

    @Override
    protected int getLoadValue()
    {
        float loadValue = socketConnected.get() / (float) (System.currentTimeMillis() - startTime.get());
        resetLoadValue();
        if (loadValue > HIGH_LOAD_LEVEL)
        {
            return LOAD_HIGH;
        }
        if (loadValue < LOW_LOAD_LEVEL)
        {
            return LOAD_LOW;
        }
        return LOAD_NORMAL;
    }

    private void resetLoadValue()
    {
        startTime = new AtomicLong(System.currentTimeMillis());
        socketConnected = new AtomicInteger(0);
    }
}
