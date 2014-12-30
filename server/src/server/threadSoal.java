/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import java.util.TimerTask;
import java.util.Timer;

/**
 *
 * @author Nata
 */
public class threadSoal extends Thread{
    threadServer tS;
    private int count;
    public Timer baru;
    
    @Override
    public void run(){
        startWaktu();
    }
    public void startWaktu(){
        baru = new Timer();
        baru.schedule(new SayHello(this), 0,1000);
    }
    
    public threadSoal(threadServer tSr){
        this.tS = tSr;
    }
    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }
}

class SayHello extends TimerTask {
    private threadSoal t;
    SayHello(threadSoal t){
        this.t = t;
    }
    public void run() {
        if(t.getCount() == 30){
            t.setCount(0);
            for(int i=0; i<t.tS.roomList.size(); i++){
                t.tS.changeCurrentWord(t.tS.roomList.get(i));
                t.tS.sendWord(t.tS.roomList.get(i));
            }
            
            //System.out.println("send word dari thread soal");
        }
        System.out.println(t.getCount());
        t.setCount(t.getCount()+1);
        
    }
 }

