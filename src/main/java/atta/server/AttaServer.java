package atta.server;

import atta.helpers.ServerConfigReader;
import atta.server.client.ClientService;
import atta.server.comand.CommandService;
import atta.server.comand.model.CommandModel;
import atta.utill.callback.CallBackDouble;
import atta.utill.loger.LogController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class for creating TCP socket server
 */
public class AttaServer
{
    private final int COMMANDS_PER_TYPE_COUNT = 100;
    private ClientService clientService;
    private CommandService commandService;
    private Map<Integer, Executor> executors;

    /**
     * Constructor
     *
     * @param configPath path to configuration for the server with extension .properties
     */
    public AttaServer(String configPath)
    {
        ServerConfigReader.setConfigPath(configPath);

        ClientService.setSettings(
                ServerConfigReader.getMaxClientsToPerformPerManager(),
                ServerConfigReader.getUpdateTimeForListening(),
                ServerConfigReader.getUpdateTimeForSending(),
                ServerConfigReader.getMaxCountToSendPerUpdate(),
                ServerConfigReader.getUpdateTimeForPing(),
                ServerConfigReader.isListeningForReconnection(),
                ServerConfigReader.isMultiConnectionFromIP(),
                ServerConfigReader.getTimeToReconnect(),
                ServerConfigReader.getUpdateTimeForReconnect());

        clientService = new ClientService(
                new CallBackDouble<>()
                {
                    @Override
                    public void call(Long id, String message)
                    {
                        onMessageReceive(id, message);

                    }
                });

        commandService = new CommandService(
                new CallBackDouble<Long, String>()
                {
                    @Override
                    public void call(Long id, String message)
                    {
                        sendMessage(id, message);
                    }
                },
                new CallBackDouble<Long, CommandModel>()
                {
                    @Override
                    public void call(Long id, CommandModel command)
                    {
                        processCommand(id, command);
                    }
                });

        executors = new ConcurrentHashMap<>();

        commandService.start();
        clientService.start();

        System.out.println("Server started");
        LogController.getInstance().Log("Server started");
    }

    /**
     * Send command to define client
     *
     * @param id      client identifier
     * @param command command to send
     */
    public void sendCommand(long id, CommandModel command)
    {
        commandService.addCommand(id, command);
    }

    /**
     * Add the new executor to process commands
     *
     * @param executor executor instance
     * @throws Exception if there is another executor with same command type
     */
    public void addExecutor(Executor executor) throws Exception
    {
        int commandType = executor.getExecutableCommandTypes();
        if (executors.containsKey(commandType))
        {
            throw new Exception("There is another executor for this command type " + commandType);
        } else
        {
            executors.put(commandType, executor);
            executor.sendCallBack = new CallBackDouble<Long, CommandModel>()
            {
                @Override
                public void call(Long id, CommandModel commandModel)
                {
                    sendCommand(id, commandModel);
                }
            };
        }
    }

    private void processCommand(Long id, CommandModel command)
    {
        int commandType = command._cmd / COMMANDS_PER_TYPE_COUNT;
        if (executors.containsKey(commandType))
        {
            executors.get(commandType).execute(id, command);
        } else
        {
            System.err.println("There is no executor for command type " + commandType);
        }
    }

    private void sendMessage(Long id, String message)
    {
        clientService.sendMessage(id, message);
    }

    private void onMessageReceive(Long id, String message)
    {
        commandService.addMassage(id, message);
    }
}
