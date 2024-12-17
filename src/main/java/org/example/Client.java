package org.example;
import java.io.*;
import java.net.*;
import java.util.Scanner;

class Client {
    public static byte[] ReadFromServer(InputStream in, byte[] data){
        try {
            int totalBytesRcvd = 0;
            int bytesRcvd;
            while (totalBytesRcvd < data.length) {
                if ((bytesRcvd = in.read(data, totalBytesRcvd, data.length - totalBytesRcvd)) == -1)
                    throw new SocketException("Connection closed prematurely");
                totalBytesRcvd += bytesRcvd;
            }
        }
        catch (Exception ex){
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
        return data;
    }
    public static void main(String[] args)
    {
        String server = "62.80.235.213";
        int servPort = 1234;
        try (Socket socket = new Socket(server, servPort); Scanner input = new Scanner(System.in)) {
            while (true){
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintWriter writer = new PrintWriter(out, true);

                System.out.println("Message for the server: ");
                String message = input.nextLine();
                //byte[] data = message.getBytes();
                writer.println(message);
                //ReadFromServer(in, data);
                System.out.println(reader.readLine());
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

}