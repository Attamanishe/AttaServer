

import atta.server.client.MessageCountChecker;
import atta.utill.loger.ILogger;
import processors.base.TaskProcessor;
import processors.diagnostically.DiagnosticsTaskProcessorsManager;

import javax.swing.*;

public class ServerUI implements ILogger
{
    private final int MAX_LINES_COUNT = 1000;
    private static ServerUI ourInstance = new ServerUI();

    public static ServerUI getInstance()
    {
        return ourInstance;
    }

    private JFrame frame;
    private JTextArea logPlane;
    private JScrollPane logScrollPlane;

    private JTextArea diagnosticallyPlane;
    private JScrollPane diagnosticallyScrollPlane;

    private void initialize()
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setBounds(0, 0, 800, 500);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        logPlane = new JTextArea();
        logPlane.setBounds(0, 0, 380, 400);

        logScrollPlane = new JScrollPane(logPlane);
        logScrollPlane.setBounds(10, 10, 380, 400);
        logScrollPlane.setVisible(true);
        logScrollPlane.createVerticalScrollBar();

        diagnosticallyPlane = new JTextArea();
        diagnosticallyPlane.setBounds(0, 0, 380, 400);

        diagnosticallyScrollPlane = new JScrollPane(diagnosticallyPlane);
        diagnosticallyScrollPlane.setBounds(410, 10, 380, 400);
        diagnosticallyScrollPlane.setVisible(true);
        diagnosticallyScrollPlane.createVerticalScrollBar();

        frame.add(diagnosticallyScrollPlane);
        frame.add(logScrollPlane);

        logPlane.setText(" ");
        new TaskProcessor("ServerUI", 500)
        {
            @Override
            protected void task()
            {
                writeDiagnostics();
            }
        }.start();
    }

    private void writeDiagnostics()
    {
        diagnosticallyPlane.setText("");
        writeLoad(String.format("Send: %s Handled: %s",
                MessageCountChecker.SEND_MESSAGES_COUNT,
                MessageCountChecker.HANDLED_MESSAGES_COUNT));
        MessageCountChecker.HANDLED_MESSAGES_COUNT = 0;
        MessageCountChecker.SEND_MESSAGES_COUNT = 0;
        DiagnosticsTaskProcessorsManager.getInstance().getDiagnostics().forEach(s -> writeLoad(s));
    }

    private ServerUI()
    {
        initialize();
    }

    private void writeLoad(String line)
    {
        diagnosticallyPlane.insert(line + "\n", 0);
    }

    @Override
    public void writeLog(String logMessage)
    {
        int numLinesToTrunk = logPlane.getLineCount() - MAX_LINES_COUNT;
        logPlane.insert(logMessage + "\n", 0);
        if (numLinesToTrunk > 0)
        {
            try
            {
                logPlane.replaceRange("",
                        logPlane.getLineStartOffset(MAX_LINES_COUNT),
                        logPlane.getLineEndOffset(logPlane.getLineCount() - 1));
            } catch (Exception e)
            {
            }
        }
    }
}
