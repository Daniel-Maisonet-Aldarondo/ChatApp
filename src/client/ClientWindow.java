package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;



import javax.swing.*;
import java.util.Stack;

public class ClientWindow extends Application{
    private static Client client;
    private static JOptionPane Dialogs;
    private static TextArea area;

    public static void createClient() {
        String input = JOptionPane.showInputDialog("Enter name: ");
        client = new Client(input,"localhost", 8000);
    }
    @Override
    public void start(Stage stage) {
        stage.setTitle("Chat Client");
        Pane root = new Pane();
        stage.setScene(new Scene(root, 500, 300));
        area = new TextArea();
        area.setEditable(false);
        TextField field = new TextField();
        field.setPromptText("Message");
        Button btn = new Button("Send");

        btn.setOnAction((event) -> {
            if(!field.getText().equals("")) {
                client.send(field.getText());
//                client.sendToDB(field.getText());
                field.setText("");
            }
                });
        //handelling the disconnect from the chat room
        stage.setOnCloseRequest(event -> {
            client.send("\\dis: " + client.getName());

        });

        root.getChildren().addAll(area,field,btn);
        field.setLayoutY(250.0);
        field.setLayoutX(10.0);
        field.setPrefWidth(300);
        btn.setLayoutX(350);
        btn.setLayoutY(250);


        stage.show();
        createClient();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void printToConsole(String message) {
        area.appendText(message+"\n");

    }
}
