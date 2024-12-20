package org.example;
import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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

            View.RegistrationFrame app = new View.RegistrationFrame(reader, writer);

            do{
                TimeUnit.SECONDS.sleep(10);
            }
            while (app.isActive());

            do{
                if(app.IsSuccessful()) {
                    View.InfoFrame info = new View.InfoFrame(reader);
                    View.DesktopApp deck = new View.DesktopApp(reader, writer);
                }
                else{
                    System.out.println("Awaiting");
                }
                TimeUnit.SECONDS.sleep(10);
            }while(!app.isActive());

            writer.println("EXIT");


//            System.out.println(reader.readLine());
//            String message = input.nextLine();
//            writer.println(message);
//
//            System.out.println(reader.readLine());
//
//            for (int i = 0; i < 100; i++) {
//                System.out.println(reader.readLine());
//            }
//
//
//
//
//            while (true){
//                System.out.println("\nEnter command for the server: ");
//                message = input.nextLine();
//                writer.println(message);
//
//                if (Objects.equals(message, "INFO")){
//                    for (int i = 0; i < 100; i++) {
//                        System.out.println(reader.readLine());
//                    }
//                }
//                else if (Objects.equals(message, "EXIT")){
//                    System.out.println("Connection closed.");
//                    socket.close();
//                    break;
//                }
//                else {
//                    System.out.println(reader.readLine());
//                }
//            }
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}