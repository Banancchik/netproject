package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class View {

    static class DesktopApp extends JFrame implements ActionListener {
        BufferedReader reader;
        PrintWriter writer;
        JButton voteButton;
        JButton infoButton;
        JTextArea textArea;
        InfoFrame infoFrame;
        DesktopApp(BufferedReader reader, PrintWriter writer){
            this.writer = writer;
            this.reader = reader;

            voteButton = new JButton("Vote");
            infoButton = new JButton("Info");
            voteButton.addActionListener(this);
            infoButton.addActionListener(this);

            JPanel CandidatePanel = new JPanel();
            CandidatePanel.setLayout(new BorderLayout());

            textArea = new JTextArea(20, 15);

            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);

            CandidatePanel.add(textArea, BorderLayout.CENTER);
            CandidatePanel.add(voteButton, BorderLayout.SOUTH);
            CandidatePanel.add(infoButton, BorderLayout.NORTH);
            this.add(CandidatePanel);

            this.setSize(200, 200);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setTitle("Voting system");

            this.setResizable(false);
            this.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this.infoButton) {
                String message = "INFO";
                writer.println(message);
                infoFrame = new InfoFrame(reader);
            }
            else if (e.getSource() == this.voteButton) {
                String message = "VOTE " + textArea.getText();
                writer.println(message);
            }
        }
    }

    static class RegistrationFrame extends JFrame implements ActionListener {
        JButton regButton;
        JTextArea Name;
        JTextArea Surname;
        JTextArea Email;
        BufferedReader reader;
        PrintWriter writer;

        RegistrationFrame(BufferedReader reader, PrintWriter writer) {
            this.writer = writer;
            this.reader = reader;

            this.setSize(500,100);
            this.setLayout(new FlowLayout());
            this.setTitle("Registration");
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setResizable(false);

            regButton = new JButton("Register");
            regButton.addActionListener(this);

            Name = new JTextArea();
            Name.setWrapStyleWord(true);
            Name.setLineWrap(true);

            Surname = new JTextArea();
            Surname.setWrapStyleWord(true);
            Surname.setLineWrap(true);

            Email = new JTextArea();
            Email.setWrapStyleWord(true);
            Email.setLineWrap(true);

            this.add(regButton);
            this.add(Name);
            this.add(Surname);
            this.add(Email);


            this.setVisible(true);
        }

        public boolean IsSuccessful() {
            try {
                String reply = null;
                while (reply == null) {
                    reply = reader.readLine();
                }

                if (reply.startsWith("Login")) {
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (IOException e) {
                return false;
            }
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == regButton) {
                String name = Name.getText();
                String surname = Surname.getText();
                String email = Email.getText();
                writer.println(name + " " + surname + " " + email);


                if(IsSuccessful()){
                    this.dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Error", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }


    static class InfoFrame extends JFrame {

        BufferedReader reader;


        InfoFrame(BufferedReader reader){
            this.reader = reader;




            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

            // Create a JTextArea for displaying text
            JTextArea textArea = new JTextArea(10, 40);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);

            // Add the text area to a JScrollPane with a vertical scrollbar
            JScrollPane scrollPane = new JScrollPane(contentPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


            this.add(scrollPane);

            this.setSize(200, 300);
            this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            this.setTitle("Info window");

            this.setVisible(true);

            try {
                for (int i = 0; i < 100; i++) {

                    JLabel label = new JLabel(reader.readLine());
                    contentPanel.add(label);

                }
            }
            catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
