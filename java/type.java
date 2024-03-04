import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class type {
    private static JTable scoreTable;
    private static JTextArea inputArea;
    private static JLabel textLabel;
    private static JLabel timeLabel;
    private static String paragraph;
    private static String typedText;
    private static boolean isTimerPaused = false;
    private static int score = 100; // Start with 100 points
    private static String playerName = "";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Typing Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setBounds(200, 100, 1080, 700);
        Container c = frame.getContentPane();

        JPanel timerPanel = createPanel(0, 0, 350, 300, Color.LIGHT_GRAY, Color.BLACK);
        JPanel paraPanel = createPanel(370, 0, 660, 300, Color.LIGHT_GRAY, Color.BLACK);
        JPanel scorePanel = createPanel(0, 320, 350, 325, Color.LIGHT_GRAY, Color.BLACK);
        JPanel enterPanel = createPanel(370, 320, 660, 325, Color.LIGHT_GRAY, Color.BLACK);

        JLabel timerLabel = createLabel("TIMER", 0, 0, 350, 70, 40, Font.BOLD, Color.BLACK);
        timeLabel = createLabel("00:00", 0, 70, 350, 60, 65, Font.PLAIN, Color.RED);
        timerPanel.add(timerLabel, BorderLayout.NORTH);
        timerPanel.add(timeLabel, BorderLayout.CENTER);

        Font fontPara = new Font(null, 0, 20);
        textLabel = createLabel("", 0, 0, 660, 300, 18, Font.PLAIN, Color.BLACK);
        JLabel titleLabel = createLabel("TYPING GAME", 0, 0, 660, 70, 40, Font.BOLD, Color.getHSBColor(242, 100, 50));
        paraPanel.add(titleLabel, BorderLayout.NORTH);
        paraPanel.add(textLabel, BorderLayout.CENTER);

        JLabel scoreLabel = createLabel("SCORE", 0, 0, 350, 70, 40, Font.BOLD, Color.BLACK);
        JButton startButton = createButton("START", 0, 0, 350, 30);
        JButton submitButton = new JButton("SUBMIT");
        enterPanel.add(submitButton, BorderLayout.SOUTH);
        submitButton.setEnabled(false);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Score");
        scoreTable = new JTable(tableModel);
        scoreTable.setBounds(0, 0, 350, 225);
        JScrollPane tableScrollPane = new JScrollPane(scoreTable);
        scorePanel.add(scoreLabel, BorderLayout.NORTH);
        scorePanel.add(tableScrollPane, BorderLayout.CENTER);
        scorePanel.add(startButton, BorderLayout.SOUTH);

        inputArea = createTextArea("", 0, 0, 660, 300, fontPara);
        inputArea.setEditable(false);
        enterPanel.add(inputArea, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGameInstructions();
                startTimer(timeLabel);
                paragraph = getRandomParagraph(paragraphs);
                textLabel.setText(htmlFormattedText(paragraph));
                inputArea.setText("");
                inputArea.setEditable(true);
                submitButton.setEnabled(true);
                startButton.setEnabled(false);
                isTimerPaused = false; // Resume the timer
                score = 100; // Start with 100 points
            }
        });

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkText();
                inputArea.setEditable(false);
                submitButton.setEnabled(false);
                startButton.setEnabled(true);
                isTimerPaused = true; // Pause the timer
                
                // Get the player's username
                playerName = getUsernameInput();
                
                if (playerName != null && !playerName.isEmpty()) {
                    // Calculate the time bonus and total score
                    int minutes = Integer.parseInt(timeLabel.getText().substring(0, 2));
                    int timeBonus = calculateTimeBonus(minutes);
                    int totalScore = score + timeBonus;
                    
                    // Update the scoreboard with the username and total score
                    DefaultTableModel model = (DefaultTableModel) scoreTable.getModel();
                    model.addRow(new Object[]{playerName, totalScore});
                    
                    // Reset the timer
                    timeLabel.setText("00:00");
                    
                    // Reset the displayed paragraph
                    paragraph = getRandomParagraph(paragraphs);
                    textLabel.setText(htmlFormattedText(paragraph));
                    
                    // Reset the score
                    score = 100;
                    
                    // Clear the input area
                    inputArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a valid username.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        c.add(timerPanel);
        c.add(paraPanel);
        c.add(scorePanel);
        c.add(enterPanel);

        frame.setVisible(true);
    }

    private static JPanel createPanel(int x, int y, int width, int height, Color background, Color border) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBounds(x, y, width, height);
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createLineBorder(border));
        return panel;
    }

    private static JLabel createLabel(String text, int x, int y, int width, int height, int fontSize, int style, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setBounds(x, y, width, height);
        Font font = new Font(null, style, fontSize);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    private static JTextArea createTextArea(String text, int x, int y, int width, int height, Font font) {
        JTextArea textArea = new JTextArea(text);
        textArea.setBounds(x, y, width, height);
        textArea.setFont(font);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        return textArea;
    }

    private static JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        return button;
    }

    private static void showGameInstructions() {
        JOptionPane.showMessageDialog(null, "Welcome to the Typing Game!\n\n" +
                "Instructions:\n" +
                "1. Click the 'START' button to begin the game.\n" +
                "2. Type the displayed paragraph in the 'Enter Text' box.\n" +
                "3. Your score will be updated as you type.\n" +
                "4. The timer will count up from 00:00. You have 10 minutes to type as much as you can.\n" +
                "5. Have fun typing!", "Game Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void startTimer(JLabel timerLabel) {
        new Thread(() -> {
            int seconds = 0;
            int minutes = 0;
            while (minutes < 10) {
                if (!isTimerPaused) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    seconds++;
                    if (seconds == 60) {
                        seconds = 0;
                        minutes++;
                    }
                    String timeStr = String.format("%02d:%02d", minutes, seconds);
                    timeLabel.setText(timeStr);
                }
            }
        }).start();
    }

    private static String[] paragraphs = {
        "The quick brown fox jumps over the lazy dog.",
        "Programming is fun and challenging.",
        "Java Swing provides a powerful GUI toolkit.",
        "Practice typing to improve your skills.",
        "Coding is a valuable skill in the digital age."
    };

    private static String getRandomParagraph(String[] paragraphArray) {
        int randomIndex = new Random().nextInt(paragraphArray.length);
        return paragraphArray[randomIndex];
    }

    private static String htmlFormattedText(String text) {
        StringBuilder formattedText = new StringBuilder("<html>");
        for (char c : text.toCharArray()) {
            formattedText.append("<font color='blue'>").append(c).append("</font>");
        }
        formattedText.append("</html>");
        return formattedText.toString();
    }

    private static void checkText() {
        typedText = inputArea.getText();
        String[] typedWords = typedText.split("\\s+");
        String[] paragraphWords = paragraph.split("\\s+");
    
        int minLen = Math.min(paragraphWords.length, typedWords.length);
        int missingWords = Math.abs(paragraphWords.length - typedWords.length);
    
        for (int i = 0; i < minLen; i++) {
            if (i == 0) {
                if (!paragraphWords[i].equals(typedWords[i]) || !Character.isUpperCase(typedWords[i].charAt(0))) {
                    score--; // Deduct 1 point for the first word error or lowercase start
                }
            } else if (!paragraphWords[i].equals(typedWords[i])) {
                score--; // Deduct 1 point for each word mistake
            }
        }
    
        // Deduct points for missing words
        score -= missingWords;
    
        // Deduct 10 points for going beyond 2 minutes
        int minutes = Integer.parseInt(timeLabel.getText().substring(0, 2));
        if (minutes > 2) {
            score -= 10;
        }
    }

    private static String getUsernameInput() {
        String playerName = JOptionPane.showInputDialog("Enter your username:");
        return playerName;
    }

    private static int calculateTimeBonus(int minutes) {
        int timeBonus = 0;
    
        if (minutes < 1) {
            timeBonus = 50;
        } else if (minutes < 2) {
            timeBonus = 40;
        } else {
            // Implement your scoring logic for longer times here.
            // For example, 30 points for 2-3 minutes, 20 points for 3-4 minutes, and so on
            // You can adjust these values according to your game rules.
            if (minutes >= 2 && minutes < 3) {
                timeBonus = 30;
            } else if (minutes >= 3 && minutes < 4) {
                timeBonus = 20;
            } else {
                // Set a default time bonus for very long durations
                timeBonus = 10;
            }
        }
    
        return timeBonus;
    }

    private static void saveScore(String playerName, int score) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/typing_game_db";
        String username = "your_mysql_username"; // Replace with your MySQL username
        String password = "your_mysql_password"; // Replace with your MySQL password
    
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String insertQuery = "INSERT INTO scores (player_name, score) VALUES (?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, playerName);
                preparedStatement.setInt(2, score);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}