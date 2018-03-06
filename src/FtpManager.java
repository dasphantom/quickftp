import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class FtpManager {
    FTPClient ftpClient = new FTPClient();

    //method to connect with ftpserver
    public void connectFTP(String server, String user, String pass) {


        int port = 21;

        try {

            ftpClient.connect(server, port);
            //textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.login(user, pass);
            //textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.enterLocalPassiveMode();
            //textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //textAreaLog.appendText(ftpClient.getReplyString());

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }

    //method to disconnect from ftpserver
    public void disconnectFTP() {

        try {
            ftpClient.logout();
            //textAreaLog.appendText(ftpClient.getReplyString());
            ftpClient.disconnect();


        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
