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
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author Muhammad Izzuddin
 */
public class threadServer extends Thread {

    public ServerSocket server = null;
    public Socket client = null;
    private final ArrayList<threadClient> alThread;
    Scanner infile;
    private ArrayList<String> roomList = new ArrayList<>();
    //private ArrayList<Pair <String,ArrayList>> wordBank = new ArrayList<>();
    private HashMap<String, ArrayList<String>> wordBank = new HashMap<>();
    private HashMap<String, String> currentWord = new HashMap<>();
    
    public threadServer(ArrayList<threadClient> t, ServerSocket s) {
        server = s;
        this.alThread = t;
    }

    @Override
    public void run() {
        try {
            infile = new Scanner(new File("sets/roomName.txt"));
            while(infile.hasNextLine()){
                String tmp = infile.nextLine();
                ArrayList<String> tmp1 = new ArrayList<>();
                getRoomList().add(tmp);
                wordBank.put(tmp, tmp1);
            }
            
            System.out.println("start");
            infile = new Scanner(new File("sets/hangWordsSoftware.txt"));
            while (infile.hasNextLine()){
                wordBank.get("Software").add(infile.nextLine());
            }
            infile = new Scanner(new File("sets/hangWordsHewan.txt"));
            while (infile.hasNextLine()){
                wordBank.get("Hewan").add(infile.nextLine());
            }
            infile = new Scanner(new File("sets/hangWordsMakanan.txt"));
            while (infile.hasNextLine()){
                wordBank.get("Makanan").add(infile.nextLine());
            }
            infile = new Scanner(new File("sets/hangWordsBuah.txt"));
            while (infile.hasNextLine()){
                wordBank.get("Buah").add(infile.nextLine());
            }
            changeCurrentWord("Makanan");
            changeCurrentWord("Buah");
            changeCurrentWord("Hewan");
            changeCurrentWord("Software");
            
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
    public String getCurrentWord(String tmp) {
        return currentWord.get(tmp);
    }

    /**
     * @param currentWord the currentWord to set
     */
    public void setCurrentWord(String currentWord, String tmp) {
        this.currentWord.put(currentWord, tmp);
    }
    
    public void changeCurrentWord(String katagori) {
        int random = (int) (Math.random()*10000) % wordBank.get(katagori).size();
        this.currentWord.put(katagori,wordBank.get(katagori).get(random));
    }

    /**
     * @return the roomList
     */
    public ArrayList<String> getRoomList() {
        return roomList;
    }

    /**
     * @param roomList the roomList to set
     */
    public void setRoomList(ArrayList<String> roomList) {
        this.roomList = roomList;
    }
}
