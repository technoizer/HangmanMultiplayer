/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Muhammad Izzuddin
 */
public class threadServer extends Thread{
    public ServerSocket server = null;
    public Socket client = null;
    private final ArrayList<threadClient> alThread;
    private boolean running;
    public threadServer (ArrayList<threadClient> t, ServerSocket s){
        server = s;
        this.alThread = t;
    }
    public void terminate() {
        running = false;
    }
    
    @Override
    public void run(){
        System.out.println("start");
        try {
            while (true){
                client = server.accept();
                synchronized(alThread)
                {
                    //JOptionPane.showMessageDialog(null,Baru.getUsername() + " " + Baru.getPassword());
                    threadClient tc = new threadClient(client,alThread);
                    alThread.add(tc);
                    Thread t = new Thread(tc);
                    t.start();
                }
            }
        } catch (IOException ex) {
            System.out.println("CLOSED");
            //JOptionPane.showMessageDialog(null,"No Client Available");
        }
    }
}
