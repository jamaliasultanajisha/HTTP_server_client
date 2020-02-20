package http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HTTPServerSkeleton {
    static final int PORT = 8080;
    private static HttpURLConnection con;
    public static int workerThreadCount = 0;

    public static void main(String[] args) throws IOException {

        int id = 1;
        ServerSocket serverConnect = new ServerSocket(PORT);
        System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");
        while (true) {
            Socket s = serverConnect.accept();
            WorkerThread wt = new WorkerThread(s, id);
            Thread t = new Thread(wt);
            t.start();
            workerThreadCount++;
            System.out.println("Client [" + id + "] is now connected. No. of worker threads = " + workerThreadCount);
            id++;
        }
    }
}

