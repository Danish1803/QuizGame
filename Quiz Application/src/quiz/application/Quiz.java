package quiz.application;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quiz extends JFrame implements ActionListener {

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String name;
    private List<Question> incorrectQuestions = new ArrayList<>();

    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionGroup;
    private JButton nextButton;
    private JButton submitButton;
    private JLabel timerLabel;
    private JComboBox<String> difficultyComboBox;

    private javax.swing.Timer timer; // Specify javax.swing.Timer
    private int timeLeft = 15;
    private boolean timerRunning = false;

    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    private String currentDifficulty = "Easy";

    // Store answered questions for each difficulty
    private Map<String, List<Integer>> answeredQuestionsMap = new HashMap<>();
    private List<Integer> userAnswers = new ArrayList<>();
    private List<Question> answeredQuestionsList = new ArrayList<>();

    public class Question {
        private int id;
        private String questionText;
        private String[] options;
        private int correctOption;
        private String difficulty;

        public Question(int id, String questionText, String[] options, int correctOption, String difficulty) {
            this.id = id;
            this.questionText = questionText;
            this.options = options;
            this.correctOption = correctOption;
            this.difficulty = difficulty;
        }

        public int getId() {
            return id;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String[] getOptions() {
            return options;
        }

        public int getCorrectOption() {
            return correctOption;
        }

        public String getDifficulty() {
            return difficulty;
        }
    }

    public Quiz(String name) {
        this.name = name;
        setTitle("Quiz Application");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nameLabel = new JLabel("Welcome, " + name + "!");
        nameLabel.setBounds(20, 10, 300, 25);
        add(nameLabel);

        JLabel difficultyLabel = new JLabel("Select Difficulty:");
        difficultyLabel.setBounds(20, 40, 150, 25);
        add(difficultyLabel);

        difficultyComboBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyComboBox.setBounds(150, 40, 100, 25);
        add(difficultyComboBox);

        timerLabel = new JLabel("Time Left: " + timeLeft + " seconds");
        timerLabel.setBounds(600, 10, 150, 25);
        timerLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        add(timerLabel);

        questionLabel = new JLabel();
        questionLabel.setBounds(20, 150, 740, 100);
        questionLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        questionLabel.setVerticalAlignment(JLabel.TOP);
        add(questionLabel);

        optionButtons = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setBounds(20, 220 + i * 30, 700, 25);
            optionButtons[i].setFont(new Font("Tahoma", Font.PLAIN, 14));
            optionButtons[i].setActionCommand(Integer.toString(i));
            optionGroup.add(optionButtons[i]);
            add(optionButtons[i]);
        }

        nextButton = new JButton("Next");
        nextButton.setBounds(150, 350, 100, 30);
        nextButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        nextButton.addActionListener(e -> handleNextButton());
        add(nextButton);

        submitButton = new JButton("Submit");
        submitButton.setBounds(300, 350, 100, 30);
        submitButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        submitButton.addActionListener(e -> handleSubmitButton());
        add(submitButton);
        submitButton.setEnabled(false);

        timer = new javax.swing.Timer(1000, e -> { // Specify javax.swing.Timer
            if (timeLeft > 0) {
                timeLeft--;
                updateTimerDisplay();
            } else {
                handleTimeout();
            }
        });

        connectToDatabase();
        difficultyComboBox.addActionListener(e -> startQuiz());
        startQuiz();

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizdb", "root", "user123");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchQuestions() {
        questions = new ArrayList<>();

        String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();

        // Check if answeredQuestionsMap contains answered questions for the current difficulty
        if (!answeredQuestionsMap.containsKey(selectedDifficulty)) {
            answeredQuestionsMap.put(selectedDifficulty, new ArrayList<>());
        }

        List<Integer> answeredQuestions = answeredQuestionsMap.get(selectedDifficulty);

        String query = "SELECT * FROM quiz_questions WHERE difficulty = ? AND id NOT IN (?) ORDER BY RAND() LIMIT 10";

        try {
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, currentDifficulty);
            preparedStatement.setString(2, answeredQuestions.toString());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String questionText = resultSet.getString("question_text");
                String[] options = new String[4];
                options[0] = resultSet.getString("option1");
                options[1] = resultSet.getString("option2");
                options[2] = resultSet.getString("option3");
                options[3] = resultSet.getString("option4");
                int correctOption = resultSet.getInt("correct_option");

                Question question = new Question(id, questionText, options, correctOption, currentDifficulty);
                questions.add(question);

                // Mark the question as answered
                answeredQuestions.add(id);
            }

            Collections.shuffle(questions);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void startQuiz() {
        fetchQuestions();

        if (!questions.isEmpty()) {
            currentQuestionIndex = 0;
            score = 0;
            loadQuestion(questions.get(currentQuestionIndex));
            nextButton.setEnabled(true);
            submitButton.setEnabled(false);
            timer.start();
            timerRunning = true;
        } else {
            JOptionPane.showMessageDialog(this, "No questions available for the selected difficulty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadQuestion(Question question) {
        questionLabel.setText("<html>" + question.getQuestionText() + "</html>");
        String[] options = question.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options[i]);
        }
        optionGroup.clearSelection();
        timeLeft = 55;
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        timerLabel.setText("Time Left: " + timeLeft + " seconds");
    }

    private void handleTimeout() {
        timeLeft = 0;
        optionGroup.clearSelection();
        handleNextButton();
    }

    private void handleNextButton() {
        if (optionGroup.getSelection() != null) {
            int selectedOption = Integer.parseInt(optionGroup.getSelection().getActionCommand());
            if (selectedOption + 1 == questions.get(currentQuestionIndex).getCorrectOption()) {
                // User answered correctly
                if (currentDifficulty.equals("Easy")) {
                    currentDifficulty = "Medium"; // Move to medium difficulty
                } else if (currentDifficulty.equals("Medium")) {
                    currentDifficulty = "Hard"; // Move to hard difficulty
                }
                score += 10;
            } else {
                // User answered incorrectly
                if (currentDifficulty.equals("Easy")) {
                    // User answered an easy question incorrectly, stay in easy
                } else if (currentDifficulty.equals("Medium")) {
                    currentDifficulty = "Easy"; // Move to easy difficulty
                } else if (currentDifficulty.equals("Hard")) {
                    currentDifficulty = "Medium"; // Move to medium difficulty
                }
            }
        }

        // Add the user's answer index to the userAnswers list
        int selectedOptionIndex = optionGroup.getSelection() != null
                ? Integer.parseInt(optionGroup.getSelection().getActionCommand())
                : -1;
        userAnswers.add(selectedOptionIndex);

        // Add the current question to the answeredQuestionsList
        answeredQuestionsList.add(questions.get(currentQuestionIndex));

        if (currentQuestionIndex + 1 < questions.size()) {
            currentQuestionIndex++;
            fetchQuestions(); // Fetch questions based on the updated difficulty
            loadQuestion(questions.get(currentQuestionIndex));
            timeLeft = 55;
            updateTimerDisplay();
            timer.restart();
        } else {
            timeLeft = 0;
            nextButton.setEnabled(false);
            submitButton.setEnabled(true);
        }
    }

    private void handleSubmitButton() {
        handleNextButton();
        timer.stop();
        new Score(name, score, answeredQuestionsList, userAnswers);

        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            handleNextButton();
        } else if (e.getSource() == submitButton) {
            handleSubmitButton();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String name = JOptionPane.showInputDialog("Enter your name:");
            if (name != null && !name.isEmpty()) {
                new Quiz(name);
            } else {
                JOptionPane.showMessageDialog(null, "Please enter a valid name.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}