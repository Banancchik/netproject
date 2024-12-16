package org.example;
import java.net.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

class Client {
    public static void ReadFromServer(InputStream in, byte[] data){
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
    }
    public static void main(String[] args)
    {
        String server = "62.80.235.213";
        int servPort = 1234;
        try (Socket socket = new Socket(server, servPort); Scanner input = new Scanner(System.in)) {
            System.out.println("Message for the server: ");
            String message = input.nextLine();
            byte[] data = message.getBytes();
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            ReadFromServer(in, data);

            //out.write(data); // Send the encoded string to the server
            // Receive the same string back from the server
            /*int totalBytesRcvd = 0; // Total bytes received so far
            int bytesRcvd; // Bytes received in last read
            while (totalBytesRcvd < data.length) {
                if ((bytesRcvd = in.read(data, totalBytesRcvd, data.length - totalBytesRcvd)) == -1)
                    throw new SocketException("Connection closed prematurely");
                totalBytesRcvd += bytesRcvd;
            } // data array is full
            System.out.println("Received from the server: " + new String(data));*/
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

}