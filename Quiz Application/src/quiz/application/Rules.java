package quiz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Rules extends JFrame implements ActionListener{

    String name;
    JButton start, back;

    Rules(String name) {
        this.name = name;
        getContentPane().setBackground(Color.BLACK);
        setLayout(null);

        JLabel heading = new JLabel("Welcome " + name + " to Thinking Caps On!");
        heading.setBounds(20, 20, 700, 30);
        heading.setFont(new Font("Harlow Solid Italic", Font.BOLD, 28));
        heading.setForeground(new Color(255, 255, 255));
        add(heading);

        JLabel rules = new JLabel();
        rules.setBounds(20, 90, 700, 350);
        rules.setFont(new Font("Bookman Old Style", Font.BOLD, 16));
        rules.setForeground(new Color(255, 255, 255));
        rules.setText(
            "<html>"+ 
                "1. Register your name to start the Quiz" + "<br><br>" +
                "2. Answer questions within a set time limit." + "<br><br>" +
                "3. Points awarded for correct answers" + "<br><br>" +
                "4. Incorrect answers might result in point deductions" + "<br><br>" +
                "5. Aim for accuracy and speed" + "<br><br>" +
                "6. Play fair and avoid cheating" + "<br><br>" +
                "7. Press the submit button to view your performance score" + "<br><br>" +
                "8. Best of luck :)" + "<br><br>" +
            "<html>"
        );
        add(rules);

        back = new JButton("Back");
        back.setBounds(250, 500, 100, 30);
        back.setBackground(new Color(30, 144, 254));
        back.setForeground(Color.WHITE);
        back.addActionListener(this);
        add(back);

        start = new JButton("Start");
        start.setBounds(400, 500, 100, 30);
        start.setBackground(new Color(30, 144, 254));
        start.setForeground(Color.WHITE);
        start.addActionListener(this);
        add(start);

        setSize(800, 650);
        setLocation(350, 100);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == start) {
            setVisible(false);
            new Quiz(name);
        } else {
            setVisible(false);
            new Login();
        }
    }

    public static void main(String[] args) {
    
    }
}