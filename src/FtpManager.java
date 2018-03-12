import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;

public class FtpManager {
    //this class provides the ftpmanager object used to manage connections to the ftpserver
    FTPClient ftpClient = new FTPClient();

    //method to connect with ftpserver
    public String[] connectFTP(String server, String user, String pass) {

        String [] replies = new String [4];
        int port = 21;

        try {

            //connect and use replystrings to inform the user

            ftpClient.connect(server, port);
            replies [0] = ftpClient.getReplyString();

            ftpClient.login(user, pass);
            replies [1] = ftpClient.getReplyString();

            ftpClient.enterLocalPassiveMode();
            replies [2] = ftpClient.getReplyString();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            replies [3] = ftpClient.getReplyString();

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
        return replies;
    }

    //method to disconnect from ftpserver
    public void disconnectFTP() {

        try {
            ftpClient.logout();
            ftpClient.disconnect();


        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }
}
