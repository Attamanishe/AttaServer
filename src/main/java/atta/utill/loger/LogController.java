package atta.utill.loger;

import processors.base.TaskProcessor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogController
{
    private static LogController ourInstance = new LogController();

    public static LogController getInstance()
    {
        return ourInstance;
    }

    private final int WAIT_TIME = 1000;
    private Queue<String> logQueue;
    private List<ILogger> loggers;

    private LogController()
    {
        loggers = new ArrayList<>();
        logQueue = new ConcurrentLinkedQueue<>();
        addLogger(new InternalFileLogger());
        new TaskProcessor("LogController", WAIT_TIME)
        {
            @Override
            protected void task()
            {
                try
                {
                    writeLogQueue();

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void LogError(String error)
    {
        writeLog("ERORR:" + getTime() + "   " + error);
    }

    public void Log(String log)
    {
        writeLog("LOG:" + getTime() + "     " + log);
    }

    public void LogWarning(String war)
    {
        writeLog("WARNING:" + getTime() + " " + war);
    }

    public void addLogger(ILogger logger)
    {
        loggers.add(logger);
    }

    public void removeLogger(ILogger logger)
    {
        loggers.remove(logger);
    }

    private void writeLogQueue()
    {
        while (!logQueue.isEmpty())
        {
            writeLog(logQueue.poll());
        }
    }

    private void writeLog(String mes)
    {
        for (ILogger logger : loggers)
        {
            logger.writeLog(mes);
        }
    }

    private String getTime()
    {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("kk:mm:ss")).toString();
    }
}
