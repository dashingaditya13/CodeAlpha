import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentGradeTracker extends JFrame {
    private final List<Student> students = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Roll No", "Name", "Subjects", "Average", "Highest", "Lowest", "Result"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField nameField = new JTextField(12);
    private final JTextField rollField = new JTextField(7);
    private final JTextField marksField = new JTextField(22);
    private final JLabel summaryLabel = new JLabel("Add a student to see the class summary.");

    static class Student {
        String name;
        int rollNo;
        double[] marks;
        Student(String name, int rollNo, double[] marks) {
            this.name = name; this.rollNo = rollNo; this.marks = marks;
        }
        double average() {
            double sum = 0;
            for (double mark : marks) sum += mark;
            return sum / marks.length;
        }
        double highest() {
            double result = marks[0];
            for (double mark : marks) result = Math.max(result, mark);
            return result;
        }
        double lowest() {
            double result = marks[0];
            for (double mark : marks) result = Math.min(result, mark);
            return result;
        }
        String result() { return average() >= 40 ? "PASS" : "FAIL"; }
    }

    public StudentGradeTracker() {
        super("CodeAlpha - Student Grade Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 570);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Student Details"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;

        addField(form, c, 0, "Student name:", nameField);
        addField(form, c, 1, "Roll number:", rollField);
        addField(form, c, 2, "Marks (comma-separated, 0-100):", marksField);

        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Selected");
        JButton reportButton = new JButton("Class Report");
        JButton clearButton = new JButton("Clear Fields");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addButton); buttons.add(deleteButton); buttons.add(reportButton); buttons.add(clearButton);

        JPanel north = new JPanel(new BorderLayout());
        north.add(form, BorderLayout.CENTER);
        north.add(buttons, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        add(summaryLabel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteSelected());
        reportButton.addActionListener(e -> showReport());
        clearButton.addActionListener(e -> { nameField.setText(""); rollField.setText(""); marksField.setText(""); });

        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    }

    private void addField(JPanel panel, GridBagConstraints c, int row, String label, JTextField field) {
        c.gridx = 0; c.gridy = row;
        panel.add(new JLabel(label), c);
        c.gridx = 1;
        panel.add(field, c);
    }

    private void addStudent() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Enter a student name.");
            int roll = Integer.parseInt(rollField.getText().trim());
            if (roll <= 0) throw new IllegalArgumentException("Roll number must be positive.");
            for (Student s : students) if (s.rollNo == roll)
                throw new IllegalArgumentException("That roll number already exists.");

            String[] parts = marksField.getText().trim().split("\\s*,\\s*");
            if (parts.length == 0 || parts[0].isEmpty())
                throw new IllegalArgumentException("Enter marks separated by commas, e.g. 78, 85, 91.");
            double[] marks = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                marks[i] = Double.parseDouble(parts[i]);
                if (marks[i] < 0 || marks[i] > 100)
                    throw new IllegalArgumentException("Each mark must be between 0 and 100.");
            }

            Student s = new Student(name, roll, marks);
            students.add(s);
            model.addRow(new Object[]{s.rollNo, s.name, s.marks.length,
                String.format("%.2f", s.average()), String.format("%.2f", s.highest()),
                String.format("%.2f", s.lowest()), s.result()});
            updateSummary();
            nameField.setText(""); rollField.setText(""); marksField.setText("");
            nameField.requestFocus();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Check that roll number and marks are valid numbers.",
                "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student row first.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        int roll = (Integer) model.getValueAt(modelRow, 0);
        students.removeIf(s -> s.rollNo == roll);
        model.removeRow(modelRow);
        updateSummary();
    }

    private void updateSummary() {
        if (students.isEmpty()) {
            summaryLabel.setText("No student records.");
            return;
        }
        double total = 0, highestAverage = -1, lowestAverage = Double.MAX_VALUE;
        Student top = null, low = null;
        for (Student s : students) {
            total += s.average();
            if (s.average() > highestAverage) { highestAverage = s.average(); top = s; }
            if (s.average() < lowestAverage) { lowestAverage = s.average(); low = s; }
        }
        summaryLabel.setText(String.format("Students: %d | Class average: %.2f | Topper: %s (%.2f) | Lowest average: %s (%.2f)",
            students.size(), total / students.size(), top.name, top.average(), low.name, low.average()));
    }

    private void showReport() {
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No student data to report.");
            return;
        }
        StringBuilder report = new StringBuilder("STUDENT GRADE REPORT\\n\\n");
        for (Student s : students) {
            report.append(String.format("Roll: %d | Name: %s | Average: %.2f | Highest: %.2f | Lowest: %.2f | %s%n",
                s.rollNo, s.name, s.average(), s.highest(), s.lowest(), s.result()));
        }
        JTextArea area = new JTextArea(report.toString(), 15, 60);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Class Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGradeTracker().setVisible(true));
    }
}
