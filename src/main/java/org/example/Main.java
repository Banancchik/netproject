package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.sql.*;


class Vote_system {
    private int votes;

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

    Thread_handle(Socket clientS, Vote_system system, Connection database) {
        this.clientsocket = clientS;
        this.system = system;
        this.database = database;
    }

    private Socket clientsocket;
    private Vote_system system;
    private Connection database;

    public void run() {
        System.out.println("Current thread: " + Thread.currentThread().getName());
        String clientSocketIP = this.clientsocket.getInetAddress().toString();
        System.out.println(clientSocketIP);
        try {
            while (true) {

                Statement st = database.createStatement();

                InputStream in = this.clientsocket.getInputStream();
                OutputStream out = clientsocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                PrintWriter writer = new PrintWriter(out, true);

                ResultSet rs = st.executeQuery("SELECT * FROM candidates WHERE name = 'John'");

                String[] batch = new String[6];

                int i = 0;
                while (rs.next()) {
                    batch[i] = rs.getString("name")+" "+rs.getString("surname") + "   Score: " + rs.getString("votes");
                    writer.println(batch[i]);
                    i++;
                }

                String message = reader.readLine();

                if (message.matches("[123456]")) {
                    String candidate = batch[Integer.parseInt(message)-1];
                    String [] name_surname = candidate.split(" ");
                    rs = st.executeQuery("UPDATE candidates SET votes = candidates.votes + 1 WHERE surname = '" + name_surname[1] + "'");
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
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


public class Main {
    public static void main(String[] args) throws IOException {
        Vote_system system = new Vote_system();

        Connection db = null;

        Scanner input = new Scanner(System.in);
        System.out.println("Provide database user account: ");
        String account = input.nextLine();
        System.out.println("Provide database password: ");
        String password = input.nextLine();
        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");

        try {
            db = DriverManager.getConnection("jdbc:postgresql://database-1.cv0oaegumj99.eu-north-1.rds.amazonaws.com:5432/postgres", account, password);
            Statement st = db.createStatement();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }



        System.out.println("Server host port was not specified. Please enter the desired port: ");
        int echoServPort = input.nextInt();
        input.nextLine();
        System.out.println("Server started!");

        try (ServerSocket servSock = new ServerSocket(echoServPort)) {
            Logger logger = Logger.getLogger("practical");
            Executor service = Executors.newCachedThreadPool();
            while (true) {
                Socket clientSock = servSock.accept();
                service.execute(new Thread_handle(clientSock, system, db));

            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}