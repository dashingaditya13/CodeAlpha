import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

public class AIChatbot extends JFrame {
    private final JTextArea chatArea = new JTextArea();
    private final JTextField inputField = new JTextField();

    public AIChatbot() {
        super("CodeAlpha - JavaBot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel heading = new JLabel("JavaBot — Rule-Based Chat Assistant", SwingConstants.CENTER);
        heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 8, 6, 8));
        add(heading, BorderLayout.NORTH);

        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        JButton clearButton = new JButton("Clear Chat");
        JPanel inputPanel = new JPanel(new BorderLayout(6, 6));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));
        inputPanel.add(inputField, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttons.add(clearButton);
        buttons.add(sendButton);
        inputPanel.add(buttons, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        clearButton.addActionListener(e -> {
            chatArea.setText("");
            append("JavaBot", "Hi! Ask me about Java, OOP, studies, or internships.");
        });

        append("JavaBot", "Hi! I'm JavaBot. Ask me about Java, OOP, studies, or internships.");
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (message.isEmpty()) return;
        append("You", message);
        inputField.setText("");

        if (message.equalsIgnoreCase("exit") || message.equalsIgnoreCase("quit")) {
            append("JavaBot", "Goodbye! You can close this window whenever you're ready.");
            return;
        }
        append("JavaBot", getResponse(message));
    }

    private void append(String speaker, String message) {
        chatArea.append(speaker + ": " + message + "\\n\\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    // Lightweight rule-based text matching; this is not a trained ML model.
    static String getResponse(String input) {
        String message = input.toLowerCase(Locale.ROOT).trim();

        if (message.matches(".*\\b(hi|hello|hey)\\b.*"))
            return "Hello! How can I help you today?";
        if (message.contains("your name"))
            return "I'm JavaBot, a Java Swing chatbot demo.";
        if (message.matches(".*\\bhow are you\\b.*"))
            return "I'm working properly. Thanks for asking!";
        if (message.matches(".*\\b(time|current time)\\b.*"))
            return "The current system time is " + LocalTime.now().withNano(0) + ".";
        if (message.matches(".*\\b(date|today)\\b.*"))
            return "The system date is " + LocalDate.now() + ".";
        if (message.contains("java"))
            return "Java is a class-based, object-oriented programming language widely used for backend and desktop applications.";
        if (message.contains("oops") || message.contains("object oriented"))
            return "The four pillars of OOP are encapsulation, inheritance, polymorphism, and abstraction.";
        if (message.contains("inheritance"))
            return "Inheritance lets a class acquire accessible properties and methods from another class using extends.";
        if (message.contains("polymorphism"))
            return "Polymorphism means one interface or parent type can represent different implementations; Java supports overloading and overriding.";
        if (message.contains("encapsulation"))
            return "Encapsulation bundles data with methods and controls access, often using private fields and public methods.";
        if (message.contains("abstraction"))
            return "Abstraction exposes essential behavior while hiding implementation details, using interfaces and abstract classes.";
        if (message.contains("internship"))
            return "Build a working project, document how it runs, push your source code to GitHub, and prepare a short demo.";
        if (message.contains("study") || message.contains("exam"))
            return "Break topics into small sessions, practise coding yourself, and revise with questions.";
        if (message.matches(".*\\b(thanks|thank you)\\b.*"))
            return "You're welcome!";
        if (message.matches(".*\\b(bye|goodbye)\\b.*"))
            return "Goodbye! Keep practising Java.";
        return "I don't have a prepared answer for that yet. Try asking about Java, OOP, inheritance, exams, or internships.";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AIChatbot().setVisible(true));
    }
}
