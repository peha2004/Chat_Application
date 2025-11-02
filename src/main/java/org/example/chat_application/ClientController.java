package org.example.chat_application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class ClientController {
    public TextArea txtArea;
    public TextField txtMessage;
    public ImageView imageView;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    public void initialize() {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3000);
                appendText("Connected to server!");

                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String msg = input.readUTF();
                    if (msg.equals("IMAGE")) {
                        int length=input.readInt();
                        byte[] imageBytes=new byte[length];
                        input.readFully(imageBytes);
                        ByteArrayInputStream bais=
                                new ByteArrayInputStream(imageBytes);
                        Image image=new Image(bais);
                        imageView.setImage(image);

                    }
                    appendText("Server: " + msg);
                }

            } catch (IOException e) {
                appendText("Connection closed or error occurred.");
            }
        }).start();
    }

    public void onSend(ActionEvent actionEvent) {
        try {
            if (output == null) {
                appendText("Not connected yet");
                return;
            }

            String msg = txtMessage.getText();
            output.writeUTF(msg);
            output.flush();

            appendText("Me: " + msg);
            txtMessage.clear();
        } catch (IOException e) {
            appendText("Error sending message!");
        }
    }



    private void appendText(String text) {
        Platform.runLater(() -> txtArea.appendText(text + "\n"));
    }



    public void sendImageOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());

                output.writeUTF("IMAGE");
                output.writeInt(imageBytes.length);
                output.write(imageBytes);
                output.flush();

                appendText("Sent image: " + file.getName());
            } catch (IOException e) {
                appendText("Error sending image");
            }
        }
    }
}

