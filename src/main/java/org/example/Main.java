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
    Vote_system(Connection database) {
        this.database = database;
    }

    protected Connection database;

    protected synchronized String vote(Statement st, String message, String[] batch) {
        try {
            String candidate = batch[Integer.parseInt(message) - 1];
            String[] name_surname = candidate.split(" ");
            ResultSet rs = st.executeQuery("UPDATE candidates SET votes = candidates.votes + 1 WHERE surname = '" + name_surname[1] + "'");
            return "Success";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    protected synchronized String[] display(Statement st, PrintWriter writer) {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM candidates WHERE name = 'John' ORDER BY surname ASC");

            String[] batch = new String[6];

            int i = 0;
            while (rs.next()) {
                batch[i] = rs.getString("name")+" "+rs.getString("surname") + "   Score: " + rs.getString("votes");
                writer.println(batch[i]);
                i++;
            }
            return batch;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
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
            Statement st = system.database.createStatement();
            InputStream in = this.clientsocket.getInputStream();
            OutputStream out = clientsocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            PrintWriter writer = new PrintWriter(out, true);
            String[] batch = system.display(st, writer);

            while (true) {
                String message = reader.readLine();

                if (message.matches("[123456]")) {
                    writer.println(system.vote(st, message, batch));
                }

                else if (Objects.equals(message, "DISPLAY")) {
                    system.display(st, writer);
                }
                else if (Objects.equals(message, "EXIT")) {
                    in.close();
                    out.close();
                    reader.close();
                    writer.close();
                    clientsocket.close();
                    break;
                }
//                else {
//                    writer.println(message);
//                }
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

        Vote_system system = new Vote_system(db);

        System.out.println("Server host port was not specified. Please enter the desired port: ");
        int echoServPort = input.nextInt();
        input.nextLine();

        try (ServerSocket servSock = new ServerSocket(echoServPort)) {
            System.out.println("Server started!");
            Logger logger = Logger.getLogger("practical");
            Executor service = Executors.newCachedThreadPool();
            while (true) {
                Socket clientSock = servSock.accept();
                service.execute(new Thread_handle(clientSock, system));
            }

        }
        catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}