package http;

import java.io.*;
import java.net.Socket;

public class HttpServerMethods {
    //method to return MIME type
    public static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".bmp")) {
            return "image/x-windows-bmp";
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (fileName.endsWith(".mp3")) {
            return "audio/x-mpeg-3";
        }
        //return "application/octet-stream";
        return "application/html";
    }

    public static void fileNotFound(Socket socket) {
        try {
            File file = new File("Not_FOUND.html");
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String responseLine = "HTTP/1.1 200 OK\r\n";
            dataOutputStream.writeBytes(responseLine);

            String responseHeader = "Content-Type: " + contentType("Not_FOUND.html") + "\r\nContent-Length: " + file.length() +
                    "\r\n\r\n";

            dataOutputStream.writeBytes(responseHeader);
            dataOutputStream.write(data);
            System.out.println("File sent successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void indexFileNotFound(PrintWriter pr, String welcome, BufferedReader in) {
        try {
            FileReader fr = new FileReader("Not_FOUND.html");
            BufferedReader br = new BufferedReader(fr);
            StringBuilder content = new StringBuilder(1024);
            while ((welcome = br.readLine()) != null) {
                content = content.append(welcome);
            }
            pr.print(content);
            welcome = in.readLine();
            System.out.println(welcome);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayHtmlPage(Socket socket, String fileName) {
        try {
            File file = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fileInputStream.read(data);
            fileInputStream.close();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String responseLine = "HTTP/1.1 200 OK\r\n";
            String responseHeader = "Content-Type: " + contentType(fileName) +
                    "\r\nContent-Length: " + file.length() + "\r\n\r\n";

            dataOutputStream.writeBytes(responseLine);
            dataOutputStream.writeBytes(responseHeader);
            dataOutputStream.write(data);
            System.out.println("File sent successfully!");


        } catch (IOException e) {
            e.printStackTrace();
            fileNotFound(socket);
        }
    }
}
