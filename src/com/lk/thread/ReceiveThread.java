package com.lk.thread;

import com.lk.frame.ChatFrame;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReceiveThread extends Thread {
    private Socket s;
    private ChatFrame frame;
    private JTextArea ta;

    public ReceiveThread(Socket s, ChatFrame frame) {
        this.s = s;
        this.frame = frame;
        this.ta = frame.getChatTextArea();
    }

    @Override
    public void run() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(s.getInputStream());
            String text;
            while ((text = dis.readUTF()) != null) {
                final String msg = text;
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!frame.isVisible()) {
                            frame.setVisible(true);
                        }
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String time = sdf.format(date);
                        ta.append("Received " + time + "\n" + msg + "\n");
                        ta.setSelectionStart(ta.getText().length());
                    }
                });
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String name = frame.getTitle();
                    frame.setTitle(name + "已退出聊天");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
