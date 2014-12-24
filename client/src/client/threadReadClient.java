/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import command.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Muhammad Izzuddin
 */
public class threadReadClient extends Thread {

    private Socket sock;
    private ObjectInputStream ois;
    private JTextArea txtReceived;
    private String currentWord;
    private client parent;

    public threadReadClient(client parent, Socket sock, ObjectInputStream ois, JTextArea txtReceived) {
        this.sock = sock;
        this.ois = ois;
        this.txtReceived = txtReceived;
        this.currentWord = currentWord;
        this.parent = parent;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object recv = ois.readObject();
                if (recv instanceof Message) {
                    Message msg = (Message) recv;
                    this.txtReceived.append(msg.getDari() + ":" + msg.getIsi() + "\n");
                } else if (recv instanceof command.CommandList) {
                    command.CommandList msg = (command.CommandList) recv;
                    if (msg.getCommand().equals("WORDS")) {
                        System.out.println("WORDS");
                        //this.currentWord = msg.getCommandDetails();
                        parent.setCurrentWord(msg.getCommandDetails());
                        System.out.println(this.currentWord);
                        parent.StartGame();
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("CLOSED");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(threadReadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
