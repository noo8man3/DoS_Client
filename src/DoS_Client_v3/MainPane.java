package DoS_Client_v3;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MainPane {
    
    private final BorderPane mainPane;
    private final MenuBar menuBar;
    private final Menu info;
    private final MenuItem about, console;
    private final GridPane centerPane;
    private final Label ipLbl, portLbl, sizeLbl, delayLbl;
    private final TextField ipField, portField;
    private final ListView bufferSizeList;
    private final Slider delaySld;
    private final Button fireBtn, stopBtn, exitBtn;
    private final HBox btnPane;

    public MainPane() {
        about = new MenuItem("About");
        console = new MenuItem("Console");
        info = new Menu("Info");
        info.getItems().addAll(about, console);
        menuBar = new MenuBar();
        menuBar.getMenus().add(info);
            
        ipLbl = new Label("IP");
        portLbl = new Label("Port");
        sizeLbl = new Label("Buffer Size");
        delayLbl = new Label("Delay (s)");
        ipField = new TextField();
        portField = new TextField();
        bufferSizeList = new ListView();
        bufferSizeList.setPrefHeight(118);
        generateList();
        delaySld = new Slider(0.001, 1.6, 0.855);
        delaySld.setShowTickMarks(true);
        delaySld.setShowTickLabels(true);
        delaySld.setMajorTickUnit(0.5);
        delaySld.setMinorTickCount(1);
        centerPane = new GridPane();
        centerPane.addColumn(0, ipLbl, ipField, portLbl, portField, sizeLbl, 
                bufferSizeList, delayLbl, delaySld);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setVgap(4.0);
        
        fireBtn = new Button("Fire");
        stopBtn = new Button("Stop");
        exitBtn = new Button("Exit");
        btnPane = new HBox();
        btnPane.getChildren().addAll(fireBtn, stopBtn, exitBtn);
        btnPane.setAlignment(Pos.CENTER);
        btnPane.setSpacing(8.0);
        btnPane.setPadding(new Insets(8));
        
        mainPane = new BorderPane();
        mainPane.setTop(menuBar);
        mainPane.setCenter(centerPane);
        mainPane.setBottom(btnPane);
    }

    public String getIp() {
        return ipField.getText();
    }

    public String getPort() {
        return portField.getText();
    }

    public long getDelay() {
        return (long)(delaySld.getValue() * 1000);
    }
    
    public int getBufferSize() {
        try {
            return Integer.parseInt((String)bufferSizeList.getSelectionModel().
                    getSelectedItem());
        } catch(NumberFormatException ex) {
            return -1;
        }
    }

    public MenuItem getAbout() {
        return about;
    }

    public MenuItem getConsole() {
        return console;
    }

    public Button getFireBtn() {
        return fireBtn;
    }

    public Button getStopBtn() {
        return stopBtn;
    }

    public Button getExitBtn() {
        return exitBtn;
    }

    public BorderPane getMainPane() {
        return mainPane;
    }
    
    private void generateList() {
        bufferSizeList.getItems().addAll("2000", "4000", "6000", "8000", 
                "16000");
        bufferSizeList.getSelectionModel().select(0);
    }
}
