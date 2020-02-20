package http;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import static http.HttpServerMethods.*;


//WorkerThread is the clientHandler class which includes the run() method
//handles incoming requests from the client aka the browser here

class WorkerThread implements Runnable {

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    private int id = 0;


    public WorkerThread(Socket s, int id) {
        this.socket = s;

        try {
            this.is = this.socket.getInputStream();
            this.os = this.socket.getOutputStream();
        } catch (Exception e) {
            System.err.println("Sorry. Cannot manage client [" + id + "] properly.");
        }

        this.id = id;
    }


    //
    public void run() {

        BufferedReader in = new BufferedReader(new InputStreamReader(this.is));
        PrintWriter pr = new PrintWriter(this.os);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
        String input = null;
        String welcome = null;
        try {
            input = in.readLine(); //reads the incoming inputstream
            System.out.println("\nClient Request : " + input); //input will be like 'method' + 'filename" + 'http version'
            String[] command = input.split(" "); //spillting GET /index.html HTTP/1.1
            String method;
            //method = "PUT";
            method = command[0];
            String fileName = "";
            fileName = command[1].replace("/", "");
            System.out.println(fileName);
            System.out.println("HTTP " + method + " request");

            //writing to log file
            try {
                FileWriter fw = new FileWriter("testout.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(input);
                bw.newLine();
                //bw.close();
            } catch (Exception e) {
                System.out.println(e);
            }


            //if browser sends anything except GET or POST request
            //501 method not supported error occurs
            if (!(method.equals("GET") || method.equals("POST"))) {
                System.out.println("here");

                try {
                    File file = new File("501_not_supported.html");
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fileInputStream.read(data);
                    fileInputStream.close();
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String responseLine = "HTTP/1.1 200 OK\r\n";
                    dataOutputStream.writeBytes(responseLine);

                    String responseHeader = "Content-Type: " + contentType(fileName) + "\r\nContent-Length: " + file.length() +
                            "\r\n\r\n";

                    dataOutputStream.writeBytes(responseHeader);
                    dataOutputStream.write(data);
                    System.out.println("in 501 not supported");


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            //handling GET request
            if (method.equals("GET")) {
                try {
                    //writing to log file
                    FileWriter fw = new FileWriter("testout.txt", true);
                    BufferedWriter bw = new BufferedWriter(fw);

                    bw.newLine();

                    pr.print("HTTP/1.1 200 OK\r\n");
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                    pr.print("Date: " + simpleDateformat.format(new Date()) + ", " + new Date() + "\r\n");
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                    pr.print("Accept-Ranges: bytes\r\n");
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                    pr.print("Keep-Alive: timeout=15, max=100\r\n");
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                    pr.print("Connection: Keep-Alive\r\n");
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                    pr.print("Content-Type: " + contentType(fileName) + "\r\n");
//                  System.out.println(contentType(fileName));
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                    pr.print("\r\n\r\n");
                    welcome = in.readLine();
                    bw.write(welcome);
                    bw.newLine();
                    System.out.println(welcome);

                  //  bw.close();

                    System.out.println("filename : " + fileName);
                    if (fileName.isEmpty() || fileName.equals("index.html")) {
                        try {
                            FileReader fr = new FileReader("index.html");
                            BufferedReader br = new BufferedReader(fr);
                            StringBuilder content = new StringBuilder(1024);
                            while ((welcome = br.readLine()) != null) {
                                content = content.append(welcome);
 //                               java.io.IOException: Stream closed why
//                                bw.write(welcome);
//                                bw.newLine();

                            }
                            pr.print(content);
                            welcome = in.readLine();
                            bw.write(welcome);
                            bw.newLine();
                            System.out.println(welcome);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            indexFileNotFound(pr, welcome, in);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (contentType(fileName).equals("text/html")) {

                        try {
                            displayHtmlPage(socket, fileName);


                        } catch (Exception e) {
                            e.printStackTrace();
                            fileNotFound(socket);
                        }


                    } else if (!(contentType(fileName).equals("text/html"))) {

                        try {
                            displayHtmlPage(socket, fileName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        FileReader fr = new FileReader("Not_FOUND.html");
                        BufferedReader br = new BufferedReader(fr);
                        StringBuilder content = new StringBuilder(1024);
                        while ((welcome = br.readLine()) != null) {
                            content = content.append(welcome);
                            //bw.write(welcome);
                            //bw.newLine();

                        }
                        pr.print(content);
                        welcome = in.readLine();
                        bw.write(welcome);
                        bw.newLine();
                        System.out.println(welcome);
                    }
                    bw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    pr.flush();
                    pr.close();
                    in.close();
                    //socket.close(); //difference ki
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } //end of GET

            //handling POST requests
            if (method.equals("POST")) {
                String Name = "";

                //FileWriter to write to log file
                BufferedWriter bw = new BufferedWriter(new FileWriter("testout.txt"));

                //extract username
                try {
                    char[] arr = new char[1000];
                    arr[0] = '\0';
                    /* for (int i = 0; i < arr.length; i++) {
                        arr[0] = '\0';
                    }
*/
                    in.read(arr);
                    bw.write(arr);  //writing to log file
                    bw.newLine();
                    System.out.println("PRINTING ARRAY");
                    System.out.println(arr);
                    String name = String.valueOf(arr);


                    bw.write("POST : ");
                    bw.write(name);
                   // bw.close();


                    if (name.contains("user=")) {
                        int temp = name.indexOf("user=");
                        Name = name.substring(temp + 5, temp + 20);
                        System.out.println(Name);
                    }

                    //sending response to browser, displays the form_submitted page with the input username
                    DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    String responseLine = "HTTP/1.1 200 OK\r\n";
                    dataOutputStream.writeBytes(responseLine);
                    String responseHeader = "Content-Type: " + contentType(fileName) +
                            "\r\n\r\n";
                    dataOutputStream.writeBytes(responseHeader);

                    String str = "\n" +
                            "<html>\n" +
                            "\t<head>\n" +
                            "\t\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                            "\t</head>\n" +
                            "\t<body>\n" +
                            "\t\t<h1> Welcome to CSE 322 Offline 1</h1>\n" +
                            "\t\t<h2> HTTP REQUEST TYPE-> </h2>\n" +
                            "\t\t<h2> Post-> " + Name + "</h2>\n" +
                            "\t\t<form name=\"input\" action=\"http://localhost:8080/form_submited.html\" method=\"post\">\n" +
                            "\t\tYour Name: <input type=\"text\" name=\"user\">\n" +
                            "\t\t<input type=\"submit\" value=\"Submit\">\n" +
                            "\t\t</form>\n" +
                            "\t</body>\n" +
                            "</html>";

                    // String str = Name;
                    dataOutputStream.writeBytes(str);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    pr.flush();
                    pr.close();
                    in.close();
                    //socket.close(); //difference ki
                    this.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bw.close();

            }//end of POST


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

