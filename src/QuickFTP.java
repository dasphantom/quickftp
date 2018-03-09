import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QuickFTP extends Application {

    SqlManager sqlManager = new SqlManager();
    FtpManager ftpManager = new FtpManager();
    GuiBuilder gui = new GuiBuilder();

     public static void main(String[] args) {
             launch(args); //javafx
    }

    @Override
    public void start(Stage primaryStage) {

        //connect to sqlite db to get previously used host+usr
        sqlManager.connectSQL();
        sqlManager.createSQL();

        //clear up on exiting
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {

                ftpManager.disconnectFTP();
                sqlManager.disconnectSQL();
                ; }
        });

        //create the javafx scene and create the GUI

        primaryStage.setTitle("quickftp");

        primaryStage.setScene(new Scene(gui.createGUI(sqlManager, ftpManager)));
        primaryStage.sizeToScene();
        primaryStage.show();

        gui.buttonConnect.requestFocus();

    }
}