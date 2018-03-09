import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

public class GuiBuilder {
    //this class provides a GUI vbox object to build the javafx application

    File file;
    TextField textHost, textUser, textPass;
    TextArea textAreaLog;
    Button buttonConnect, buttonUpload, buttonBrowse, buttonCopy, buttonLoad;

    public VBox createGUI(SqlManager sqlManager, FtpManager ftpManager) {

        //actual method to create the gui object

        textHost = new TextField();
        textHost.setPromptText("Host");

        textUser = new TextField();
        textUser.setPromptText("Username");

        textPass = new PasswordField();
        textPass.setPromptText("Password");

        textAreaLog = new TextArea();
        Hyperlink linkPath = new Hyperlink();

        //button to load previously used login
        buttonLoad = new Button();
        buttonLoad.setText("Load login");

        buttonLoad.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //load the login from sqlite db
                String[] Result = sqlManager.fetchSQL();

                textHost.setText(Result[0]);
                textUser.setText(Result[1]);

            }
        });

        //button to connect with ftpserver
        buttonConnect = new Button();
        buttonConnect.setText("Connect");

        buttonConnect.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                // check if required fields are not blank
                if (textHost.getText().equals("") || textUser.getText().equals("") || textPass.getText().equals("")) {
                    textAreaLog.appendText("Host / Username / Password is missing.\n");
                    return;
                }

                if (ftpManager.ftpClient.isConnected()) {
                    //ftpclient is already connected, so we want to disconnect
                    ftpManager.disconnectFTP();
                    buttonConnect.setText("Connect");

                } else {
                    //ftpclient is not connected, so we want to connect

                    String [] replies = ftpManager.connectFTP(textHost.getText(), textUser.getText(), textPass.getText());
                    for (String reply : replies)
                        textAreaLog.appendText(reply);

                    //add host+usr to sql db

                    sqlManager.insertSQL(textHost.getText(), textUser.getText());

                    //check if really connected

                    if (ftpManager.ftpClient.isConnected()) {
                        buttonConnect.setText("Disconnect");
                    }

                }
            }
        });


        //button for uploading
        buttonUpload = new Button();
        buttonUpload.setText("Upload");
        buttonUpload.setDisable(true);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");

        buttonUpload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //first check if thers a working connection to ftpserver
                if (! ftpManager.ftpClient.isConnected())
                {
                    textAreaLog.appendText("Connect to the server first");
                    return;
                }

                //try to upload the file
                try {

                    InputStream inputStream = new FileInputStream(file);
                    boolean done = ftpManager.ftpClient.storeFile(file.getName(), inputStream);
                    System.out.println(ftpManager.ftpClient.getReplyCode());
                    inputStream.close();

                    if (done) {
                        //success
                        //creates a hyperlink to the uploaded file, assumes working directory = /upload/

                        linkPath.setText("http://" + textHost.getText() + "/upload/" + file.getName());
                        linkPath.setOnAction(e -> {

                            try {
                                //open default browser when hyperlink is clicked
                                Desktop.getDesktop().browse(new URI("http://" + textHost.getText() + "/upload/" + file.getName()));
                            } catch (IOException ex) {
                                System.out.println("Error: " + ex.getMessage());
                            } catch (URISyntaxException ex) {
                                System.out.println("Error: " + ex.getMessage());;
                            }

                        });
                        textAreaLog.appendText("The file is uploaded successfully.");
                    }

                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());

                }

            }
        });

        //button for selecting the file to be uploaded
        buttonBrowse = new Button();
        buttonBrowse.setText("Browse");
        buttonBrowse.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {

                file = fileChooser.showOpenDialog(((Node) event.getTarget()).getScene().getWindow());

                if (file != null) {
                    //if file selected use this create hyperlink

                    linkPath.setText(file.getAbsolutePath());
                    buttonUpload.setDisable(false);
                }

            }
        });

        //button for copying the upload url to clipboard
        buttonCopy = new Button();
        buttonCopy.setText("Copy URL to clipboard");
        buttonCopy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                StringSelection stringSelection = new StringSelection(linkPath.getText());
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);


            }
        });

        //create layout
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);

        HBox hb = new HBox();
        hb.getChildren().addAll(textHost, textUser, textPass, buttonConnect, buttonLoad);
        hb.setSpacing(10);

        HBox hb2 = new HBox();

        hb2.getChildren().addAll(buttonBrowse, buttonUpload, linkPath, buttonCopy);
        hb2.setSpacing(10);

        HBox hb3 = new HBox();
        hb3.getChildren().addAll(textAreaLog);
        hb3.setSpacing(10);

        vbox.getChildren().addAll(hb, hb2, hb3);

        return vbox;
    }
}
