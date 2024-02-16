package quiz.application;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Score extends JFrame implements ActionListener {

    private String name;
    private int score;
    private List<Quiz.Question> questions;
    private List<Integer> userAnswers;

    public Score(String name, int score, List<Quiz.Question> questions, List<Integer> userAnswers) {
        this.name = name;
        this.score = score;
        this.questions = questions;
        this.userAnswers = userAnswers;

        setBounds(400, 150, 750, 550);
        getContentPane().setBackground(Color.BLACK);
        setLayout(null);

        JLabel heading = new JLabel("Thank you, " + name + " for playing Thinking Caps On");
        heading.setBounds(45, 30, 700, 30);
        heading.setFont(new Font("Harlow Solid Italic", Font.BOLD, 26));
        heading.setForeground(Color.WHITE);
        add(heading);

        

        // Create a table to display the questions, user answers, correct answers, and difficulty
        JTable table = new JTable();

        // Create a table model with columns: Question, Your Answer, Correct Answer, Difficulty
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Question");
        model.addColumn("Your Answer");
        model.addColumn("Correct Answer");
        model.addColumn("Difficulty");

        List<String> addedQuestions = new ArrayList<>(); // To prevent duplicates

        // Populate the table with data
        for (int i = 0; i < questions.size(); i++) {
            Quiz.Question q = questions.get(i);
            String questionText = q.getQuestionText();
            if (!addedQuestions.contains(questionText)) {
                String[] row = new String[4];
                row[0] = questionText;
                int userAnswerIndex = userAnswers.get(i);
                row[1] = (userAnswerIndex != -1) ? q.getOptions()[userAnswerIndex] : "Not Answered";
                row[2] = q.getOptions()[q.getCorrectOption() - 1];
                row[3] = q.getDifficulty();

                model.addRow(row);
                addedQuestions.add(questionText);
            }
        }

        table.setModel(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 100, 700, 300);
        add(scrollPane);

        JLabel totalScoreLabel = new JLabel("Total Score: " + score);
        totalScoreLabel.setBounds(350, 450, 300, 30);
        totalScoreLabel.setFont(new Font("Harlow Solid Italic", Font.BOLD, 26));
        totalScoreLabel.setForeground(Color.WHITE);
        add(totalScoreLabel);

        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.setBounds(380, 480, 120, 30);
        playAgainButton.setBackground(new Color(30, 144, 255));
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.addActionListener(this);
        add(playAgainButton);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getActionCommand().equals("Play Again")) {
            setVisible(false);
            new Login(); // Redirect to the Login frame
        }
    }

    public static void main(String[] args) {
        // Example usage:
        List<Quiz.Question> questions = new ArrayList<>(); // Replace with your actual list of questions
        List<Integer> userAnswers = new ArrayList<>(); // Replace with the user's answers
        new Score("User", 50, questions, userAnswers); // Replace "50" with the actual score
    }
}