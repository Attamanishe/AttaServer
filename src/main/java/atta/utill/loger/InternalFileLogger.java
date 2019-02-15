package atta.utill.loger;

import atta.helpers.ServerConfigReader;
import processors.base.TaskProcessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

class InternalFileLogger implements ILogger
{

    private final String LOG_PATH = ServerConfigReader.getLogPath();
    private String FILE_NAME;
    private BufferedWriter logWriter;
    private Queue<String> forRelog;

    public InternalFileLogger()
    {
        forRelog = new ConcurrentLinkedQueue<>();
        FILE_NAME = LOG_PATH + LocalDate.now() + LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")) + ".txt";
        try
        {
            logWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        new TaskProcessor("FileCloser", 1000)
        {
            @Override
            protected void task()
            {
                try
                {
                    logWriter.close();
                    logWriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();

        new TaskProcessor("FileLogWrite", 500)
        {
            @Override
            protected void task()
            {
                String lastLog = "";
                try
                {
                    while (!forRelog.isEmpty())
                    {
                        lastLog = forRelog.poll();
                        logWriter.write(lastLog);
                        logWriter.newLine();
                    }
                } catch (Exception e)
                {
                    if (!lastLog.equals(""))
                    {
                        forRelog.add(lastLog);
                    }
                }
            }
        }.start();
    }

    @Override
    public void writeLog(String logMessage)
    {
        forRelog.add(logMessage);
    }
}
