package atta.server.comand;

import atta.helpers.JsonMessageParserHelper;
import atta.server.comand.model.CommandModel;
import atta.utill.callback.CallBackDouble;
import atta.utill.container.Container;
import processors.load.LoadBalancingTaskProcessor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommandService
{
    private abstract class CommandProcessor extends LoadBalancingTaskProcessor
    {
        protected final static int UPDATE_TIME = 10;
        protected final static int PERFORMED_PART = 1000;
        protected final static int HIGH_LOAD = 100000;
        protected final static int LOW_LOAD = 10000;

        /**
         * Constructor
         *
         * @param maxThreadsCount        the threads count that is max to be processed for this TaskProcessor
         * @param timeForUpdateLoadValue the time between checking of load value
         * @param groupName              the name of thread that will be open for this task
         */
        protected CommandProcessor(int maxThreadsCount, int timeForUpdateLoadValue, String groupName)
        {
            super(maxThreadsCount, timeForUpdateLoadValue, groupName, UPDATE_TIME);
        }


        @Override
        protected int getLoadValue()
        {
            if (toParse.size() > HIGH_LOAD)
            {
                return LOAD_HIGH;
            }
            if (toParse.size() < LOW_LOAD)
            {
                return LOAD_LOW;
            }
            return LOAD_NORMAL;
        }
    }

    private class CommandDistributor extends CommandProcessor
    {
        public CommandDistributor()
        {
            super(2, 1000, "CommandDistributor");
        }

        @Override
        protected void process()
        {
            Container<Long, CommandModel> model;
            for (int i = 0; i < PERFORMED_PART && (model = readyToProcess.poll()) != null; i++)
            {
                processCommand(model.first, model.second);
            }
        }
    }

    private class MessageDistributor extends CommandProcessor
    {
        public MessageDistributor()
        {
            super(2, 1000, "MessageDistributor");
        }

        @Override
        protected void process()
        {
            Container<Long, String> message;
            for (int i = 0; i < PERFORMED_PART && (message = readyToSend.poll()) != null; i++)
            {
                processMessage(message.first, message.second);
            }
        }
    }

    private class MessageParser extends CommandProcessor
    {
        public MessageParser()
        {
            super(50, 1000, "MessageParser");
        }

        @Override
        protected void process()
        {
            Container<Long, String> message;
            for (int i = 0; i < PERFORMED_PART && (message = toParse.poll()) != null; i++)
            {
                CommandModel model = parse(message.second);
                if (model != null)
                {
                    readyToProcess.add(new Container<>(message.first, model));
                }
            }
        }

        private CommandModel parse(String message)
        {
            CommandModel model = null;
            try
            {
                model = JsonMessageParserHelper.parse(message);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return model;
        }
    }

    private class CommandParser extends CommandProcessor
    {
        public CommandParser()
        {
            super(50, 1000, "CommandParser");
        }

        @Override
        protected void process()
        {
            Container<Long, CommandModel> command;
            for (int i = 0; i < PERFORMED_PART && (command = toSend.poll()) != null; i++)
            {
                String message = parse(command.second);
                if (message != null)
                {
                    readyToSend.add(new Container<>(command.first, message));
                }
            }
        }

        private String parse(CommandModel model)
        {
            String message = "";
            try
            {
                message = JsonMessageParserHelper.parse(model);
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return message;
        }
    }

    private Queue<Container<Long, String>> readyToSend;
    private Queue<Container<Long, CommandModel>> readyToProcess;

    private Queue<Container<Long, String>> toParse;
    private Queue<Container<Long, CommandModel>> toSend;

    private CallBackDouble<Long, String> onMessageReadyToSend;
    private CallBackDouble<Long, CommandModel> onCommandReadyToProcess;

    public CommandService(CallBackDouble<Long, String> onMessageReadyToSend, CallBackDouble<Long, CommandModel> onCommandReadyToProcess)
    {
        this.onMessageReadyToSend = onMessageReadyToSend;
        this.onCommandReadyToProcess = onCommandReadyToProcess;
        toParse = new ConcurrentLinkedQueue<>();
        toSend = new ConcurrentLinkedQueue<>();
        readyToProcess = new ConcurrentLinkedQueue<>();
        readyToSend = new ConcurrentLinkedQueue<>();
    }

    public void start()
    {
        new MessageDistributor().start();
        new MessageParser().start();
        new CommandDistributor().start();
        new CommandParser().start();
    }

    public void addCommand(long id, CommandModel model)
    {
        toSend.add(new Container<>(id, model));
    }

    public void addMassage(long id, String message)
    {
        toParse.add(new Container<>(id, message));
    }

    private void processCommand(long id, CommandModel model)
    {
        onCommandReadyToProcess.call(id, model);
    }

    private void processMessage(long id, String message)
    {
        onMessageReadyToSend.call(id, message);
    }
}
