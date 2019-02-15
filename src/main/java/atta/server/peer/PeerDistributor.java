package atta.server.peer;

import atta.utill.callback.CallBackSingle;
import processors.load.LoadBalancingTaskProcessor;

import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class PeerDistributor extends LoadBalancingTaskProcessor
{
    private final int HIGH_LOAD_LEVEL = 1000;
    private final int LOW_LOAD_LEVEL = 100;

    private final int MAX_PART = 10;
    private CallBackSingle<Socket> distributeAction;
    private Queue<Socket> socketsToDistribute = new ConcurrentLinkedQueue<>();

    public PeerDistributor(CallBackSingle<Socket> distributeAction)
    {
        super(5, 1000, "Peer distributor", 10);
        this.distributeAction = distributeAction;
    }

    public void add(Socket socket)
    {
        socketsToDistribute.add(socket);
    }

    private void distribute(Socket s)
    {
        distributeAction.call(s);
    }

    @Override
    protected void process()
    {
        for (int i = 0; i < MAX_PART && !socketsToDistribute.isEmpty(); i++)
        {
            distribute(socketsToDistribute.poll());
        }
    }

    @Override
    protected int getLoadValue()
    {
        if (socketsToDistribute.size() > HIGH_LOAD_LEVEL)
        {
            return LOAD_HIGH;
        } else if (socketsToDistribute.size() < LOW_LOAD_LEVEL)
        {
            return LOAD_LOW;
        }
        return LOAD_NORMAL;
    }
}