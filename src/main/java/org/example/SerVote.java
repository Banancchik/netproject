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

    protected synchronized String vote(Statement st, int message, String[] batch, String[] user_data, String client_ip) {
        try {
            ResultSet rs = st.executeQuery("SELECT 1 FROM account WHERE (email = '"+ user_data[2] +"' AND ip = '" + client_ip + "' AND have_voted = true)");
            if(rs.next()) {
                return "Error, you have already voted";
            }

            String candidate = batch[message - 1];
            String[] name_surname = candidate.split(" ");
            st.executeUpdate("UPDATE candidates SET votes = candidates.votes + 1 WHERE surname = '" + name_surname[1] + "'");
            st.executeUpdate("UPDATE account SET have_voted = 'true' WHERE (email = '"+ user_data[2] +"' AND ip = '" + client_ip + "')");
            return "Success";
        }
        catch (Exception e) {
            return e.getMessage();
        }
    }

    protected synchronized String[] display(Statement st, PrintWriter writer) {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM candidates WHERE name = 'John' ORDER BY surname ASC");

            String[] batch = new String[101];

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

    protected synchronized void login(Statement st, String[] user_data, String client_ip) {
        try {
            ResultSet rs = st.executeQuery("SELECT 1 FROM account WHERE ip = '" + client_ip + "'");
            if (rs.next()) {
                rs = st.executeQuery("SELECT 1 FROM account WHERE email = '" + user_data[2] + "'");
                if (rs.next()) {
                    return;
                }
            }

            st.executeQuery("INSERT INTO account(name, surname, email, ip) VALUES('" + user_data[0] + "','" + user_data[1] + "','" + user_data[2] + "','" + client_ip + "')");
            System.out.println("Success");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
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
        String clientSocketIP = this.clientsocket.getInetAddress().toString().replace("/", "");
        System.out.println(clientSocketIP);
        try {
            Statement st = system.database.createStatement();
            InputStream in = this.clientsocket.getInputStream();
            OutputStream out = clientsocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            PrintWriter writer = new PrintWriter(out, true);

            writer.println("Enter your name, surname and e-mail:");
            String message = reader.readLine();
            String[] user_data = message.split(" ");
            system.login(st, user_data, clientSocketIP);



            String[] batch = system.display(st, writer);//TODO Список просили сохранять в list.txt локально на устройстве, у нас есть база, но можно и локально на всякий

            while (true) {
                message = reader.readLine();

                if (message.startsWith("VOTE")) {
                    int vote_no = Integer.parseInt(message.replaceAll("[^\\d]", ""));
                    System.out.println(vote_no);
                    if (vote_no > 100 || vote_no < 1) {
                        writer.println("Invalid vote number");
                    }
                    else {
                        writer.println(system.vote(st, vote_no, batch, user_data, clientSocketIP));
                    }
                    }

                else if (Objects.equals(message, "INFO")) {
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


public class SerVote {
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