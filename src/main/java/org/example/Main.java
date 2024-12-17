package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

class Vote_system {
    private int votes;

    Vote_system() {
        this.votes = 0;
    }

    protected synchronized int getVotes(){
        return this.votes;
    }

    protected synchronized String deposit(int amount) {
        this.votes += amount;
        return "successfully deposited " + amount + " to the votes.";
    }

    private synchronized String withdrawal(int amount) {
        if (votes >= amount) {
            votes -= amount;
        }
        else{
            return "Insufficient votes for a withdrawal of " + amount + " from the candidate.";
        }
        return "successfully withdrawed " + amount + " votes from the candidate.";
    }
}

class Thread_handle extends Thread{

    Thread_handle(Socket clientS, Vote_system system) {
        this.clientsocket = clientS;
        this.system = system;
    }

    private Socket clientsocket;
    private Vote_system system;

    public void run() {
        System.out.println("Current thread: " + Thread.currentThread().getName());
        String clientSocketIP = this.clientsocket.getInetAddress().toString();
        System.out.println(clientSocketIP);
        try {
            while (true) {
                InputStream in = this.clientsocket.getInputStream();
                OutputStream out = clientsocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintWriter writer = new PrintWriter(out, true);
                String message = reader.readLine();
                if (Objects.equals(message, "+1")) {
                    writer.println(this.system.deposit(1));
                }
                else if (Objects.equals(message, "DISPLAY")) {
                    writer.println(this.system.getVotes());

                }
                else if (Objects.equals(message, "EXIT")) {
                    in.close();
                    out.close();
                    reader.close();
                    writer.close();
                    clientsocket.close();
                    break;
                }
                else {
                    writer.println(message);
                }
            }
        }
        catch (IOException e) {
            System.out.println("Client disconnected. " + e.getMessage());
        }
    }
}




public class Main {
    public static void main(String[] args) throws IOException {
        Vote_system system = new Vote_system();


        Scanner input = new Scanner(System.in);
        System.out.println("Server host port was not specified. Please enter the desired port: ");
        int echoServPort = input.nextInt();
        input.nextLine();
        System.out.println("Server started!");

        try (ServerSocket servSock = new ServerSocket(echoServPort)) {
            Logger logger = Logger.getLogger("practical");
            Executor service = Executors.newCachedThreadPool();
            while (true) {
                Socket clientSock = servSock.accept();
                service.execute(new Thread_handle(clientSock, system));

            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}