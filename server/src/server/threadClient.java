/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

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
    private FileInputStream fis = null;
    private FileOutputStream fos = null;
    private ArrayList<String> Recipient = new ArrayList<>();
    private threadServer server;

    public threadClient(threadServer server, Socket sockcli, ArrayList<threadClient> t) {
        this.server = server;
        this.sockcli = sockcli;
        this.alThread = t;
        this.sa = this.sockcli.getRemoteSocketAddress();
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
                        sendMultiple(baru);
                    }
                    if (recv instanceof command.CommandList) {
                        command.CommandList comm = (command.CommandList) recv;
                        msg = comm.getCommand();
                        if (msg.equals("END")) {
                            break;
                        }
                        else if (msg.equals("START")) {
                            System.out.println("START");
                            sendWord();
                        }
                        else if (msg.equals("FIN")) {
                            if (comm.getCommandDetails().get(0).equals(server.getCurrentWord())){
                                System.out.println("FINISH " + comm.getCommandDetails().get(1));
                                server.changeCurrentWord();
                                updateWord();
                            }
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

    public synchronized void sendMultiple(Object msg) throws IOException {
        for (int i = 0; i < this.alThread.size(); i++) {
            threadClient tc = this.alThread.get(i);
            tc.send(msg);
        }
    }
    
    public synchronized void updateWord() throws IOException {
        for (int i = 0; i < this.alThread.size(); i++) {
            threadClient tc = this.alThread.get(i);
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
        detail.add(server.getCurrentWord());
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
}
