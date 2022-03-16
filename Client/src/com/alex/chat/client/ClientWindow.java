package com.alex.chat.client;

import com.alex.network.TCPConnection;
import com.alex.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Alex");
    private final JTextField fieldInput = new JTextField();

    private TCPConnection connection;

    private ClientWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        //Set on the center of monitor
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);

        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);

        setVisible(true);

        try {
            connection = new TCPConnection(this, IP_ADDRESS, PORT);
        } catch (IOException e) {
            printMessage("Connection exception " + e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if ("".equals(msg)) {return;}

        fieldInput.setText(null);
        connection.sendString(fieldNickName.getText() + ": " + msg);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            log.append(message + "\n");
            //set caret in the end of document
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

    @Override
    public void onConnectionReady(TCPConnection connection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection connection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection connection) {
        printMessage("Connection close");
    }

    @Override
    public void onException(TCPConnection connection, Exception e) {
        printMessage("Connection exception " + e);
    }
}
