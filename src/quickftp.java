import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class quickftp extends Application {
    FTPClient ftpClient = new FTPClient();
    File file;
    TextField textHost, textUser, textPass;
    TextArea textAreaLog = new TextArea();

    public static void main(String[] args) {
        launch(args);
    }

    @Override

    public void start(Stage primaryStage) {
        primaryStage.setTitle("quickftp");

        //inputfields

        textHost = new TextField();
        textHost.setPromptText("Host");

        textUser = new TextField();
        textUser.setPromptText("Username");

        textPass = new PasswordField();
        textPass.setPromptText("Password");

        Hyperlink linkPath = new Hyperlink();

        //button to connect with a server
        Button buttonConnect = new Button();
        buttonConnect.setText("Connect");
        buttonConnect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                if (ftpClient.isConnected()) {
                    //ftpclient is already connected, so we want to disconnect
                    disconnectFTP();
                    buttonConnect.setText("Connect");

                } else {
                    //ftpclient is not connected, so we want to connect

                    connectFTP();
                    //check if really connected

                    if (ftpClient.isConnected()) {
                        buttonConnect.setText("Disconnect");
                    }
                }
            }
        });

        //button for uploading
        Button buttonUpload = new Button();
        buttonUpload.setText("Upload");
        buttonUpload.setDisable(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        buttonUpload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    InputStream inputStream = new FileInputStream(file);


                    boolean done = ftpClient.storeFile(file.getName(), inputStream);

                    System.out.println(ftpClient.getReplyCode());

                    inputStream.close();

                    if (done) {

                        //creates a hyperlink to the uploaded file, assumes working directory = /upload/

                        linkPath.setText("http://" + textHost.getText() + "/upload/" + file.getName());
                        linkPath.setOnAction(e -> {

                            try {
                                Desktop.getDesktop().browse(new URI("http://" + textHost.getText() + "/upload/" + file.getName()));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (URISyntaxException ex) {
                                ex.printStackTrace();
                            }

                        });

                        System.out.println("The first file is uploaded successfully.");
                    }

                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }

            }
        });

        //button for selecting the file to be uploaded
        Button buttonBrowse = new Button();
        buttonBrowse.setText("Browse");
        buttonBrowse.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                file = fileChooser.showOpenDialog(primaryStage);

                if (file != null) {
                    linkPath.setText(file.getAbsolutePath());


                    buttonUpload.setDisable(false);
                }

            }
        });

        Button buttonCopy = new Button();
        buttonCopy.setText("Copy URL to clipboard");
        buttonCopy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                StringSelection stringSelection = new StringSelection(linkPath.getText());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);


            }
        });

        //setup gui
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        HBox hb = new HBox();
        hb.getChildren().addAll(textHost, textUser, textPass, buttonConnect);
        hb.setSpacing(10);

        HBox hb2 = new HBox();
        hb2.getChildren().addAll(buttonBrowse, buttonUpload, linkPath, buttonCopy);
        hb2.setSpacing(10);

        HBox hb3 = new HBox();
        hb3.getChildren().addAll(textAreaLog);
        hb3.setSpacing(10);

        vbox.getChildren().addAll(hb, hb2, hb3);


        primaryStage.setScene(new Scene(vbox));
        primaryStage.sizeToScene();
        primaryStage.show();

        buttonConnect.requestFocus();
    }

    //method to connect with ftpserver
    public void connectFTP() {
        String server = textHost.getText();
        int port = 21;
        String user = textUser.getText();
        String pass = textPass.getText();


        try {

            ftpClient.connect(server, port);
            textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.login(user, pass);
            textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.enterLocalPassiveMode();
            textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            textAreaLog.appendText(ftpClient.getReplyString());

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());

        }
    }

    //method to disconnect from ftpserver
    public void disconnectFTP() {

        try {
            ftpClient.logout();
            textAreaLog.appendText(ftpClient.getReplyString());

            ftpClient.disconnect();


        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}