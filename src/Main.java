import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class QuizQuestion {
    String question;
    String[] choices;
    int correctAnswer;
    String difficulty;

    public QuizQuestion(String question, String[] choices, int correctAnswer, String difficulty) {
        this.question = question;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }
}

class Category {
    String name;
    QuizQuestion[] questions;
    int highScore;

    public Category(String name, QuizQuestion[] questions) {
        this.name = name;
        this.questions = questions;
        this.highScore = 0;
    }
}

class RoundedButton extends JButton {
    private static final int ARC_WIDTH = 20;
    private static final int ARC_HEIGHT = 20;

    public RoundedButton(String label) {
        super(label);
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getModel().isArmed()) {
            g2.setColor(getBackground().darker());
        } else {
            g2.setColor(getBackground());
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC_WIDTH, ARC_HEIGHT);

        FontMetrics fm = g.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(getText())) / 2;
        int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(getForeground());
        g2.drawString(getText(), textX, textY);

        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground().darker());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);
        g2.dispose();
    }
}

public class Main extends JFrame {
    private QuizQuestion[] questions;
    private int currentQuestion = 0;
    private int score = 0;
    private JLabel questionLabel;
    private JButton[] answerButtons;
    private JLabel timerLabel;
    private Timer timer;
    private int timeLeft = 10;
    private JPanel welcomePanel;
    private JPanel categoryPanel;
    private JPanel highScoresPanel;
    private JPanel quizPanel;
    private JProgressBar progressBar;
    private JButton pauseButton;
    private boolean isPaused = false;
    private int pausedQuestionIndex = -1;
    private static Category[] categories;
    private Category currentCategory;
    private static final String HIGH_SCORE_FILE = "high_score.txt";
    private JLabel funFactLabel;
    private String[] funFacts = {
            "Zambia is home to Victoria Falls, one of the Seven Natural Wonders of the World.",
            "The Zambian flag features an eagle, symbolizing the country's ability to rise above its challenges.",
            "Zambia has more than 72 ethnic groups and languages.",
            "Zambia is known as the 'Copper Country' due to its rich copper deposits.",
            "The Zambian national football team is nicknamed 'Chipolopolo', meaning 'Copper Bullets'."
    };
    private static final int QUESTIONS_PER_CATEGORY = 10;
    private static final int TIMER_SECONDS = 15;

    public Main() {
        createCategories();
        loadHighScores();

        setTitle("QuiZam");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());
        setResizable(false);
        setLocationRelativeTo(null);

        createWelcomePanel();
        createCategoryPanel();
        createHighScoresPanel();
        createQuizPanel();

        add(welcomePanel, "Welcome");
        add(categoryPanel, "Categories");
        add(highScoresPanel, "HighScores");
        add(quizPanel, "Quiz");

        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Welcome");
    }

    private static void createCategories() {
        categories = new Category[] {
                new Category("General Knowledge", new QuizQuestion[] {
                        new QuizQuestion("What is the traditional dance of the Lozi people in Zambia?", new String[]{"Makishi", "Gule Wamkulu", "Ngoma", "Likumbi Lya Mize"}, 2, "hard"),
                        new QuizQuestion("Which Zambian football club has won the most national titles?", new String[]{"Nkana FC", "ZESCO United", "Green Buffaloes", "Power Dynamos"}, 0, "hard"),
                        new QuizQuestion("What is the official motto of Zambia?", new String[]{"Unity and Freedom", "One Zambia, One Nation", "Pride and Prosperity", "Strength in Unity"}, 1, "medium"),
                        new QuizQuestion("What is the capital city of Zambia?", new String[]{"Lusaka", "Kitwe", "Ndola", "Livingstone"}, 0, "easy"),
                        new QuizQuestion("Which river forms the border between Zambia and Zimbabwe?", new String[]{"Nile", "Zambezi", "Congo", "Limpopo"}, 1, "medium"),
                }),
                new Category("History", new QuizQuestion[] {
                        new QuizQuestion("Who was the first president of Zambia?", new String[]{"Levy Mwanawasa", "Kenneth Kaunda", "Michael Sata", "Frederick Chiluba"}, 1, "medium"),
                        new QuizQuestion("What is Zambia's main export?", new String[]{"Diamonds", "Oil", "Copper", "Gold"}, 2, "medium"),
                        new QuizQuestion("What is the largest city in Zambia?", new String[]{"Lusaka", "Ndola", "Kitwe", "Livingstone"}, 0, "easy"),
                }),
                new Category("Geography", new QuizQuestion[] {
                        new QuizQuestion("Which Zambian national park is famous for its walking safaris?", new String[]{"Kafue", "South Luangwa", "Lower Zambezi", "Liuwa Plain"}, 1, "medium"),
                        new QuizQuestion("Which Victoria Falls is located in Zambia?", new String[]{"North Falls", "East Falls", "South Falls", "Livingstone Falls"}, 3, "medium"),
                        new QuizQuestion("Which is the largest national park in Zambia?", new String[]{"Lower Zambezi", "South Luangwa", "Kafue", "Liuwa Plain"}, 2, "medium"),
                        new QuizQuestion("Which Zambian lake is known for being a man-made lake?", new String[]{"Lake Tanganyika", "Lake Kariba", "Lake Bangweulu", "Lake Mweru"}, 1, "hard")
                }),
                new Category("Civic Education", new QuizQuestion[] {
                        new QuizQuestion("What is the official language of Zambia?", new String[]{"English", "Bemba", "Nyanja", "Tonga"}, 0, "easy"),
                        new QuizQuestion("What is the currency of Zambia?", new String[]{"Kwacha", "Rand", "Shilling", "Dollar"}, 0, "easy"),
                        new QuizQuestion("When did Zambia become an independent country?", new String[]{"1924", "1890", "1864", "1964"}, 3, "easy"),
                })
        };
    }

    private void createWelcomePanel() {
        welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Welcome to Quizam", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0x2E86C1));
        welcomePanel.add(titleLabel, BorderLayout.NORTH);

        funFactLabel = new JLabel(getRandomFunFact(), JLabel.CENTER);
        funFactLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        funFactLabel.setForeground(new Color(0x2E86C1));
        welcomePanel.add(funFactLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);

        String[] options = {"Play Quiz", "View High Scores", "Exit"};
        for (String option : options) {
            JButton button = new RoundedButton(option);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setBackground(new Color(0x5DADE2));
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> handleWelcomeOption(option));
            buttonPanel.add(button);
        }

        welcomePanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleWelcomeOption(String option) {
        switch (option) {
            case "Play Quiz":
                ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Categories");
                break;
            case "View High Scores":
                updateHighScoresPanel();
                ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "HighScores");
                break;
            case "Exit":
                System.exit(0);
                break;
        }
    }

    private void createCategoryPanel() {
        categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel chooseLabel = new JLabel("Choose Category", JLabel.CENTER);
        chooseLabel.setFont(new Font("Arial", Font.BOLD, 24));
        chooseLabel.setForeground(new Color(0x2E86C1));
        categoryPanel.add(chooseLabel, BorderLayout.NORTH);

        JPanel categoriesGrid = new JPanel(new GridLayout(0, 2, 10, 10));
        categoriesGrid.setOpaque(false);

        for (Category category : categories) {
            JButton categoryButton = new RoundedButton(category.name);
            categoryButton.setFont(new Font("Arial", Font.BOLD, 18));
            categoryButton.setBackground(new Color(0x5DADE2));
            categoryButton.setForeground(Color.WHITE);
            categoryButton.addActionListener(e -> startCategoryQuiz(category));
            categoriesGrid.add(categoryButton);
        }

        JScrollPane scrollPane = new JScrollPane(categoriesGrid);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        categoryPanel.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new RoundedButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(0xF39C12));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Welcome"));
        categoryPanel.add(backButton, BorderLayout.SOUTH);
    }

    private void createHighScoresPanel() {
        highScoresPanel = new JPanel(new BorderLayout());
        highScoresPanel.setOpaque(false);
        highScoresPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("High Scores", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0x2E86C1));
        highScoresPanel.add(titleLabel, BorderLayout.NORTH);

        JButton backButton = new RoundedButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(0xF39C12));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(e -> ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Welcome"));
        highScoresPanel.add(backButton, BorderLayout.SOUTH);
    }

    private void updateHighScoresPanel() {
        JPanel scoresPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        scoresPanel.setOpaque(false);

        for (int i = 0; i < categories.length; i++) {
            JLabel scoreLabel = new JLabel((i + 1) + ". " + categories[i].name + ": " + categories[i].highScore);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            scoresPanel.add(scoreLabel);
        }

        JScrollPane scrollPane = new JScrollPane(scoresPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Remove the previous scores panel if it exists
        for (Component comp : highScoresPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                highScoresPanel.remove(comp);
                break;
            }
        }

        highScoresPanel.add(scrollPane, BorderLayout.CENTER);
        highScoresPanel.revalidate();
        highScoresPanel.repaint();
    }

    private void createQuizPanel() {
        quizPanel = new JPanel(new BorderLayout());
        quizPanel.setOpaque(false);
        quizPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        questionLabel = new JLabel("", JLabel.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 22));
        questionLabel.setForeground(new Color(0x2E86C1));
        quizPanel.add(questionLabel, BorderLayout.NORTH);

        JPanel answerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        answerPanel.setOpaque(false);
        answerButtons = new JButton[4];
        for (int i = 0; i < 4; i++) {
            answerButtons[i] = new RoundedButton("");
            answerButtons[i].setFont(new Font("Arial", Font.PLAIN, 14));
            answerButtons[i].setPreferredSize(new Dimension(150, 50));
            answerButtons[i].setBackground(new Color(0x5DADE2));
            answerButtons[i].setForeground(Color.WHITE);
            final int index = i;
            answerButtons[i].addActionListener(e -> checkAnswer(index));
            answerPanel.add(answerButtons[i]);
        }
        quizPanel.add(answerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);


        timerLabel = new JLabel("Time left: 10", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        timerLabel.setForeground(new Color(0xC0392B));
        bottomPanel.add(timerLabel, BorderLayout.NORTH);

        progressBar = new JProgressBar(0, QUESTIONS_PER_CATEGORY);
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font("Arial", Font.PLAIN, 14));
        progressBar.setForeground(new Color(0x28B463));
        progressBar.setBackground(new Color(0xD5D8DC));
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        pauseButton = new JButton("Pause");
        pauseButton.setFont(new Font("Arial", Font.BOLD, 18));
        pauseButton.setBackground(new Color(0xF39C12));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);
        pauseButton.addActionListener(e -> pauseQuiz());
        JPanel pausePanel = new JPanel();
        pausePanel.setOpaque(false);
        pausePanel.add(pauseButton);
        bottomPanel.add(pausePanel, BorderLayout.CENTER);

        quizPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startCategoryQuiz(Category category) {
        currentCategory = category;
        questions = getRandomQuestions(category.questions, QUESTIONS_PER_CATEGORY);
        currentQuestion = 0;
        score = 0;
        ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Quiz");
        updateProgressBar();
        loadNextQuestion();
    }

    private QuizQuestion[] getRandomQuestions(QuizQuestion[] allQuestions, int count) {
        List<QuizQuestion> questionList = new ArrayList<>(Arrays.asList(allQuestions));
        Collections.shuffle(questionList);
        return questionList.subList(0, Math.min(count, questionList.size())).toArray(new QuizQuestion[0]);
    }

    private void loadNextQuestion() {
        if (isPaused) {
            currentQuestion = pausedQuestionIndex;
            isPaused = false;
        }
        if (currentQuestion < questions.length) {
            QuizQuestion q = questions[currentQuestion];
            questionLabel.setText(q.question);
            for (int i = 0; i < q.choices.length; i++) {
                answerButtons[i].setText(q.choices[i]);
                answerButtons[i].setBackground(new Color(0x5DADE2));
            }
            updateProgressBar();
            startTimer();
        } else {
            showResult();
        }
    }

    private void updateProgressBar() {
        progressBar.setValue(currentQuestion + 1);
        progressBar.setString((currentQuestion + 1) + " / " + questions.length);
    }

    private void startTimer() {
        timeLeft = TIMER_SECONDS;
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    SwingUtilities.invokeLater(() -> {
                        timerLabel.setText("Time left: " + timeLeft);
                        timerLabel.setForeground(timeLeft <= 5 ? Color.RED : new Color(0xC0392B));
                    });
                    timeLeft--;
                } else {
                    timer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(Main.this, "Time's up! The correct answer is: " +
                                questions[currentQuestion].choices[questions[currentQuestion].correctAnswer]);
                        currentQuestion++;
                        loadNextQuestion();
                    });
                }
            }
        }, 0, 1000);
    }

    private void checkAnswer(int selectedAnswer) {
        timer.cancel();
        QuizQuestion q = questions[currentQuestion];
        if (selectedAnswer == q.correctAnswer) {
            score++;
            answerButtons[selectedAnswer].setBackground(new Color(0x28B463));
        } else {
            answerButtons[selectedAnswer].setBackground(new Color(0xC0392B));
            answerButtons[q.correctAnswer].setBackground(new Color(0x28B463));
        }

        Timer delayTimer = new Timer();
        delayTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    currentQuestion++;
                    loadNextQuestion();
                });
            }
        }, 1000);
    }

    private void showResult() {
        if (score > currentCategory.highScore) {
            currentCategory.highScore = score;
            saveHighScores();
            JOptionPane.showMessageDialog(this, "Congratulations! New High Score for " + currentCategory.name + ": " + score);
        }

        String[] options = {"Replay Category", "Change Category", "End Quiz"};
        int choice = JOptionPane.showOptionDialog(this,
                "Quiz Over! Your score: " + score + "/" + questions.length +
                        "\nHigh Score for " + currentCategory.name + ": " + currentCategory.highScore,
                "Quiz Finished",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        switch (choice) {
            case 0: // Replay Category
                startCategoryQuiz(currentCategory);
                break;
            case 1: // Change Category
                ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Categories");
                break;
            case 2: // End Quiz
            default:
                ((CardLayout) getContentPane().getLayout()).show(getContentPane(), "Welcome");
                break;
        }
    }

    private void pauseQuiz() {
        if (timer != null) {
            timer.cancel();
        }
        isPaused = true;
        pausedQuestionIndex = currentQuestion;
        JOptionPane.showMessageDialog(this, "Quiz Paused");
        currentQuestion = (currentQuestion + 1) % questions.length;
        loadNextQuestion();
    }

    private static void loadHighScores() {
        try {
            File file = new File(HIGH_SCORE_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        for (Category category : categories) {
                            if (category.name.equals(parts[0])) {
                                category.highScore = Integer.parseInt(parts[1]);
                                break;
                            }
                        }
                    }
                }
                reader.close();
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void saveHighScores() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE));
            for (Category category : categories) {
                writer.write(category.name + ":" + category.highScore);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRandomFunFact() {
        return funFacts[(int) (Math.random() * funFacts.length)];
    }

    public static void main(String[] args) {
        createCategories();
        loadHighScores();
        SwingUtilities.invokeLater(() -> {
            Main quiz = new Main();
            quiz.setVisible(true);
        });
    }
}
