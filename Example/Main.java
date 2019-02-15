
import atta.server.AttaServer;
import atta.utill.loger.LogController;

public class Main
{
    public static void main(String agrs[])
    {
        AttaServer attaServer = new AttaServer("./server.properties");
        javax.swing.SwingUtilities.invokeLater(
                () -> LogController.getInstance().addLogger(ServerUI.getInstance()));
        try
        {
            attaServer.addExecutor(new SimpleExecutor());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
