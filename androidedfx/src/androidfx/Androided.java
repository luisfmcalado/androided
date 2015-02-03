/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package androidfx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author cala
 */
public class Androided extends Task<Void> {

    private final ServerSocket serv;
    
    public Androided(int port) throws IOException {
        serv = new ServerSocket(port);
    }

    public void listen() throws IOException, InterruptedException {
        PrintWriter out;
        BufferedReader in;

        while (true) {
            Socket client = serv.accept();

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);

            String msg = in.readLine();
            updateMessage(msg);
            out.println("1");
        }
    }

    public void close() throws IOException {
        serv.close();
    }

    @Override
    protected Void call() throws Exception {
        listen();
        return null;
    }
}
