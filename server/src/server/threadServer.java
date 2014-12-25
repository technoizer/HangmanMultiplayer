/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Muhammad Izzuddin
 */
public class threadServer extends Thread {

    public ServerSocket server = null;
    public Socket client = null;
    private final ArrayList<threadClient> alThread;
    private String currentWord;
    Scanner infile;
    private ArrayList<String> wordBank = new ArrayList<String>();

    public threadServer(ArrayList<threadClient> t, ServerSocket s) {
        server = s;
        this.alThread = t;
    }

    @Override
    public void run() {
        try {
            System.out.println("start");
            infile = new Scanner(new File("sets/hangWords.txt"));
            while (infile.hasNextLine()){
                wordBank.add(infile.nextLine());
            }
            changeCurrentWord();
            try {
                while (true) {
                    client = server.accept();
                    synchronized (alThread) {
                        //JOptionPane.showMessageDialog(null,Baru.getUsername() + " " + Baru.getPassword());
                        threadClient tc = new threadClient(this, client, alThread);
                        alThread.add(tc);
                        Thread t = new Thread(tc);
                        t.start();
                    }
                }
            } catch (IOException ex) {
                System.out.println("CLOSED");
                //JOptionPane.showMessageDialog(null,"No Client Available");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(threadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the currentWord
     */
    public String getCurrentWord() {
        return currentWord;
    }

    /**
     * @param currentWord the currentWord to set
     */
    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public void changeCurrentWord() {
        int random = (int) (Math.random()*10000) % wordBank.size();
        this.currentWord = wordBank.get(random);
    }
}
