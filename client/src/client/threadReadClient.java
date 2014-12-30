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
import javax.swing.JComboBox;
import javax.swing.JLabel;
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
    private JComboBox room;
    private JLabel scoring;

    public threadReadClient(client parent, Socket sock, ObjectInputStream ois, JTextArea txtReceived, JComboBox room, JLabel scoring) {
        this.sock = sock;
        this.ois = ois;
        this.txtReceived = txtReceived;
        this.parent = parent;
        this.room = room;
        this.scoring = scoring;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object recv = ois.readObject();
                if (recv instanceof Message) {
                    Message msg = (Message) recv;
                    this.txtReceived.append(msg.getDari() + ": " + msg.getIsi() + "\n");
                } 
                else if (recv instanceof command.CommandList) {
                    command.CommandList msg = (command.CommandList) recv;
                    if (msg.getCommand().equals("WORDS")) {
                        System.out.println("WORDS");
                        //this.currentWord = msg.getCommandDetails();
                        parent.setCurrentWord(msg.getCommandDetails().get(0));
                        System.out.println(this.currentWord);
                        parent.setCount(Integer.parseInt(msg.getCommandDetails().get(1)));
                        parent.StartGame();
                    }
                    if (msg.getCommand().equals("ROOMLIST")){
                        this.room.removeAllItems();
                        for(int i=0;i<msg.getCommandDetails().size();i++){
                            this.room.addItem(msg.getCommandDetails().get(i));
                            System.out.println(msg.getCommandDetails());
                        }
                    }
                    
                    if (msg.getCommand().equals("EXIST")){
                        parent.disconFrom();
                    }
                    
                    if(msg.getCommand().equals("SCORE")){
                        this.scoring.setText(msg.getCommandDetails().get(0));
                        System.out.println(msg.getCommandDetails().get(0));
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
