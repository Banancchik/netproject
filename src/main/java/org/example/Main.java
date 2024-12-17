package org.example;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


class Vote_view extends Thread{
    private int votes;

    private Socket clientsocket;

    Vote_view(Socket clientS) {
        this.votes = 0;
        this.clientsocket = clientS;

    }

    synchronized int getVotes(){
        return this.votes;
    }

    private synchronized String deposit(int amount) {
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

    public void run() {
        System.out.println("hekllo");
        System.out.println("Current thread: " + Thread.currentThread().getName());
        String clientSocketIP = this.clientsocket.getInetAddress().toString();
        System.out.println(clientSocketIP);
    }
}




public class Main {
    public static void main(String[] args) throws IOException {
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
                service.execute(new Vote_view(clientSock));
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}