package atta.server.client;

import atta.utill.callback.CallBackSingle;
import atta.utill.loger.LogController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class Client
{
    private static CallBackSingle<Client> ON_CLIENT_DISCONNECTED;
    private static final int MAX_BUFFER_SIZE = 4096;
    private final String MESSAGE_DELIMITER = "%zmd%";
    private final String EMPTY = "";
    private long id;
    private Socket client;
    private InputStream reader;
    private OutputStream writer;
    private String cachedPart = "";
    private boolean isConnected;
    private ReentrantLock lock;
    private ReentrantLock lockHandling;

    public static void setOnClientDisconnected(CallBackSingle<Client> onDisconnected)
    {
        ON_CLIENT_DISCONNECTED = onDisconnected;
    }

    public Client(Socket socket, long id)
    {
        client = socket;
        this.id = id;
        InitConnection();
        lockHandling = new ReentrantLock(false);
        lock = new ReentrantLock(false);
    }

    private void InitConnection()
    {
        try
        {
            reader = client.getInputStream();
            writer = client.getOutputStream();
            isConnected = true;
        } catch (Exception ex)
        {
            try
            {
                lostConnection();
            } catch (Exception e1)
            {
                e1.printStackTrace();
            }
            System.err.println(ex.getMessage());
        }
    }

    public void send(String data)
    {
        if (isConnected)
        {
            try
            {
                MessageCountChecker.SEND_MESSAGES_COUNT++;
                writer.write(String.format("%s %s %s ", MESSAGE_DELIMITER, data, MESSAGE_DELIMITER).getBytes());
                writer.flush();
            } catch (Exception ex)
            {
                try
                {
                    lostConnection();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<String> getData()
    {
        ArrayList<String> messages = new ArrayList<>();
        try
        {
            lockHandling.lock();
            readMessages(messages);
        } catch (Exception ex)
        {
            LogController.getInstance().LogError(ex.getMessage());
            ex.printStackTrace();
        } finally
        {
            lockHandling.unlock();
        }
        return messages;
    }


    public boolean isReady()
    {
        if (!isConnected || lockHandling.isLocked())
        {
            return false;
        }
        try
        {
            return reader.available() > 0;
        } catch (Exception e)
        {
            return false;
        }
    }

    public long getId()
    {
        return id;
    }

    public void lostConnection()
    {
        lock.lock();
        if (isConnected)
        {
            ON_CLIENT_DISCONNECTED.call(this);
            isConnected = false;
        }
        lock.unlock();
    }

    public int getIP()
    {
        return client.getInetAddress().hashCode();
    }

    public void reconnect(Socket socket)
    {
        client = socket;
        InitConnection();
        System.out.println("client with id: " + id + " reconnected");
        LogController.getInstance().Log("client with id: " + id + " reconnected");
    }

    public void kill()
    {
        try
        {
            client.close();
            reader.close();
            writer.close();
        } catch (Exception e)
        {
        }
    }

    private void readMessages(ArrayList<String> messages) throws IOException
    {
        int countOfBytes;
        do
        {
            byte buffer[];
            buffer = new byte[MAX_BUFFER_SIZE];
            countOfBytes = reader.read(buffer, 0, MAX_BUFFER_SIZE);
            byte clearedBuffer[] = new byte[countOfBytes];
            System.arraycopy(buffer, 0, clearedBuffer, 0, countOfBytes);
            String subMessages[] = (cachedPart + new String(clearedBuffer)).split(MESSAGE_DELIMITER);
            cachedPart = EMPTY;

            for (int i = 0, length = subMessages.length - 1; i < length; i++)
            {
                String subMessage = subMessages[i];
                if (subMessage.length() > 1)
                {
                    messages.add(subMessage);
                    MessageCountChecker.HANDLED_MESSAGES_COUNT++;
                }
            }

            if (subMessages[subMessages.length - 1].length() > 1)
            {
                cachedPart = subMessages[subMessages.length - 1];
            }

        } while (countOfBytes == MAX_BUFFER_SIZE);
    }
}
