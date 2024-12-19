package org.example;
import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

class Client {
    public static void main(String[] args)
    {
        String server = "62.80.235.213";
        int servPort = 1234;

        try (Socket socket = new Socket(server, servPort); Scanner input = new Scanner(System.in)) {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            PrintWriter writer = new PrintWriter(out, true);

            System.out.println(reader.readLine());
            String message = input.nextLine();
            writer.println(message);

            System.out.println(reader.readLine());

            for (int i = 0; i < 100; i++) {
                System.out.println(reader.readLine());
            }

            while (true){
                System.out.println("Message for the server: ");
                message = input.nextLine();
                writer.println(message);

                if (Objects.equals(message, "INFO")){
                    for (int i = 0; i < 100; i++) {
                        System.out.println(reader.readLine());
                    }
                }
                else {
                    System.out.println(reader.readLine());
                }
            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

}