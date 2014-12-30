/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import command.CommandList;
import command.Message;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Muhammad Izzuddin
 */
public class threadClient implements Runnable {

    private Socket sockcli;
    private final ArrayList<threadClient> alThread;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private ObjectOutputStream oos = null;
    private ObjectInputStream ois = null;
    private final SocketAddress sa;
    private String username;
    private String roomname;
    private FileInputStream fis = null;
    private FileOutputStream fos = null;
    private ArrayList<String> Recipient = new ArrayList<>();
    private threadServer server;
    private int score;
    public threadClient(threadServer server, Socket sockcli, ArrayList<threadClient> t) {
        this.server = server;
        this.sockcli = sockcli;
        this.alThread = t;
        this.sa = this.sockcli.getRemoteSocketAddress();
        this.score = 0;
    }

    @Override
    public void run() {
        try {
            dos = new DataOutputStream(this.getSockcli().getOutputStream());
            oos = new ObjectOutputStream(this.getSockcli().getOutputStream());
            dis = new DataInputStream(this.getSockcli().getInputStream());
            ois = new ObjectInputStream(this.getSockcli().getInputStream());

            String msg;
            while (true) {
                Object recv;
                try {
                    recv = ois.readObject();
                    if (recv instanceof Message) {
                        Message baru = (Message) recv;
                        sendMultiple(baru,roomname);
                    }
                    if (recv instanceof command.CommandList) {
                        command.CommandList comm = (command.CommandList) recv;
                        msg = comm.getCommand();

                        command.CommandList baru = new CommandList();
                        if (msg.equals("END")) {
                            break;
                        } 
                        else if (msg.equals("START")) {
                            System.out.println("START");
                            boolean flagExist = false;
                            for (int i = 0; i < alThread.size(); i++) {
                                if (comm.getCommandDetails().get(0).equals(alThread.get(i).getUsername())) {
                                    baru.setCommand("EXIST");
                                    send(baru);
                                    flagExist = true;
                                    break;
                                }
                            }

                            if (flagExist == false) {
                                username = comm.getCommandDetails().get(0);
                                baru.setCommand("ROOMLIST");
                                baru.setCommandDetails(server.getRoomList());
                                send(baru);
                                
                            }

                            //sendWord();-- ke setelah 
                        } 
                        else if (msg.equals("FIN")) {
                            if (comm.getCommandDetails().get(0).equals(server.getCurrentWord(roomname))) {
                                System.out.println("FINISH " + comm.getCommandDetails().get(1));
                                server.changeCurrentWord(roomname);
                                updateWord(roomname);
                                Message notif = new Message();
                                notif.setDari("Pemberitahuan");
                                this.score += 10;
                                notif.setIsi(comm.getCommandDetails().get(1) + " Menang" + score);
                                System.out.println(username+' '+score);
                                sendMultiple(notif, roomname);
                                baru.setCommand("SCORE");
                                ArrayList<String> s = new ArrayList();
                                s.add(score+"");
                                baru.setCommandDetails(s);
                                send(baru);
                            }
                        }
                        
                        else if(msg.equals("RNAME")){
                            roomname = comm.getCommandDetails().get(0);
                            System.out.println("set" + username + "room" + roomname);
                            sendWord();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(threadClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(threadClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ois.close();
            oos.close();
            getSockcli().close();
            synchronized (this.alThread) {
                this.alThread.remove(this);
            }
        } catch (IOException ex) {
            Logger.getLogger(threadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void sendMultiple(Object msg, String roomnamecari) throws IOException {
        for (int i = 0; i < this.alThread.size(); i++) {
            threadClient tc = this.alThread.get(i);
            if (tc.roomname.equals(roomnamecari))
                tc.send(msg);
        }
    }

    public synchronized void updateWord(String roomnamecari) throws IOException {
        for (int i = 0; i < this.alThread.size(); i++) {
            threadClient tc = this.alThread.get(i);
            if (tc.roomname.equals(roomnamecari))
                   tc.sendWord();
        }
    }

    public void send(Object msg) {
        try {
            this.oos.writeObject(msg);
            this.oos.flush();
            this.oos.reset();
        } catch (IOException ex) {
            Logger.getLogger(threadClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendWord() {
        command.CommandList baru = new command.CommandList();
        baru.setCommand("WORDS");
        ArrayList<String> detail = new ArrayList<>();
        detail.add(server.getCurrentWord(roomname));
        baru.setCommandDetails(detail);
        send(baru);
    }

    /**
     * @return the sockcli
     */
    public Socket getSockcli() {
        return sockcli;
    }

    /**
     * @param sockcli the sockcli to set
     */
    public void setSockcli(Socket sockcli) {
        this.sockcli = sockcli;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }
}
