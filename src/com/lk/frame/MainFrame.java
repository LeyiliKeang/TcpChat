package com.lk.frame;

import com.lk.thread.ReceiveThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class MainFrame extends JFrame {
    private JPanel contentPane;
    private JTextField nameTextField;
    private JButton loginButton;
    private JTextField ipTextField;
    private JButton chatButton;
    private JPanel loginPane;
    private JPanel chatPane;
    private JTextField usePortTextField;
    private JTextField sendPortTextField;

    private ServerSocket ss;

    public MainFrame() {
        setTitle("通过TCP实现聊天");
        usePortTextField.setText("10024");
        sendPortTextField.setText("10024");
        chatPane.setVisible(false);
        setContentPane(contentPane);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showConfirmDialog(contentPane, "确定退出吗？", "小小提示框", JOptionPane.YES_NO_OPTION);
                if (i == JOptionPane.YES_OPTION) {
                    if (ss != null) {
                        try {
                            ss.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                } else {
                    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
        pack();
        setLocationRelativeTo(null);
        nameTextField.requestFocus();

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!nameTextField.getText().trim().equals("") && !usePortTextField.getText().trim().equals("")) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                ss = new ServerSocket(Integer.parseInt(usePortTextField.getText().trim()));
                                EventQueue.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        loginPane.setVisible(false);
                                        chatPane.setVisible(true);
                                        ipTextField.requestFocus();
                                    }
                                });
                                while (true) {
                                    Socket s = ss.accept();
                                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                                    bw.write(nameTextField.getText().trim());
                                    bw.newLine();
                                    bw.flush();

                                    ChatFrame frame = new ChatFrame(s);
                                    frame.setTitle(s.getInetAddress().getHostAddress() + ":" + br.readLine());
                                    ReceiveThread rt = new ReceiveThread(s, frame);
                                    rt.start();
                                }
                            } catch (IllegalArgumentException ex) {
                                JOptionPane.showMessageDialog(contentPane, "请确保端口输入正确", "小小提示框", JOptionPane.WARNING_MESSAGE);
                                usePortTextField.setText("");
                                usePortTextField.requestFocus();
                            } catch (BindException ex) {
                                JOptionPane.showMessageDialog(contentPane, "端口已经被使用", "小小提示框", JOptionPane.WARNING_MESSAGE);
                                usePortTextField.setText("");
                                usePortTextField.requestFocus();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    JOptionPane.showMessageDialog(contentPane, "用户名和使用端口不能为空", "小小提示框", JOptionPane.WARNING_MESSAGE);
                    nameTextField.setText("");
                    usePortTextField.setText("");
                    nameTextField.requestFocus();
                }
            }
        });
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ipTextField.getText().trim().equals("") && !sendPortTextField.getText().trim().equals("")) {
                    try {
                        if (InetAddress.getByName(ipTextField.getText()).isReachable(1000)) {
                            Socket s = new Socket(ipTextField.getText(), Integer.parseInt(sendPortTextField.getText().trim()));

                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                            bw.write(nameTextField.getText().trim());
                            bw.newLine();
                            bw.flush();

                            ChatFrame frame = new ChatFrame(s);
                            frame.setTitle(ipTextField.getText().trim() + ":" + br.readLine());
                            frame.setVisible(true);
                            ReceiveThread rt = new ReceiveThread(s, frame);
                            rt.start();
                            ipTextField.setText("");
                            sendPortTextField.setText("");
                        } else {
                            JOptionPane.showMessageDialog(contentPane, "网络不可达", "小小提示框", JOptionPane.WARNING_MESSAGE);
                            ipTextField.setText("");
                            sendPortTextField.setText("");
                            ipTextField.requestFocus();
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(contentPane, "请确保端口输入正确", "小小提示框", JOptionPane.WARNING_MESSAGE);
                        sendPortTextField.setText("");
                        sendPortTextField.requestFocus();
                    } catch (ConnectException ex) {
                        JOptionPane.showMessageDialog(contentPane, "对方不在线", "小小提示框", JOptionPane.WARNING_MESSAGE);
                        ipTextField.setText("");
                        sendPortTextField.setText("");
                        ipTextField.requestFocus();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(contentPane, "对方IP和端口不能为空", "小小提示框", JOptionPane.WARNING_MESSAGE);
                    ipTextField.setText("");
                    sendPortTextField.setText("");
                    ipTextField.requestFocus();
                }
            }
        });
    }
}
