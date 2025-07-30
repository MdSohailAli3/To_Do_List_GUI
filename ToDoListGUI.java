import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ToDoListGUI extends JFrame {

    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskJList;
    private JTextField taskInputField;
    private JComboBox<String> priorityComboBox;
    private JButton addButton, deleteButton, completeButton, increasePriorityButton, decreasePriorityButton;

    // Task class with priority
    private static class Task {
        String description;
        boolean isComplete;
        int priority; // 1 (highest) to 5 (lowest)

        Task(String description, int priority) {
            this.description = description;
            this.isComplete = false;
            this.priority = priority;
        }

        void markComplete() {
            this.isComplete = true;
        }

        void increasePriority() {
            if (priority > 1) {
                priority--;
            }
        }

        void decreasePriority() {
            if (priority < 5) {
                priority++;
            }
        }

        @Override
        public String toString() {
            // Return just the description; rendering will handle tick mark and color
            return description;
        }
    }

    public ToDoListGUI() {
        setTitle("To-Do List");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        taskListModel = new DefaultListModel<>();
        taskJList = new JList<>(taskListModel);
        taskJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskJList.setCellRenderer(new TaskCellRenderer());
        JScrollPane scrollPane = new JScrollPane(taskJList);

        taskInputField = new JTextField(20);
        String[] priorities = {"1 (Highest)", "2", "3", "4", "5 (Lowest)"};
        priorityComboBox = new JComboBox<>(priorities);
        priorityComboBox.setSelectedIndex(2); // default priority 3

        addButton = new JButton("Add Task");
        deleteButton = new JButton("Delete Task");
        completeButton = new JButton("Mark Complete");
        increasePriorityButton = new JButton("Increase Priority");
        decreasePriorityButton = new JButton("Decrease Priority");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Task:"));
        inputPanel.add(taskInputField);
        inputPanel.add(new JLabel("Priority:"));
        inputPanel.add(priorityComboBox);
        inputPanel.add(addButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(increasePriorityButton);
        buttonPanel.add(decreasePriorityButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addTask());
        deleteButton.addActionListener(e -> deleteTask());
        completeButton.addActionListener(e -> markTaskComplete());
        increasePriorityButton.addActionListener(e -> changePriority(true));
        decreasePriorityButton.addActionListener(e -> changePriority(false));

        taskJList.addListSelectionListener(e -> updateButtons());

        updateButtons();
    }

    private void addTask() {
        String description = taskInputField.getText().trim();
        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task description cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int priority = priorityComboBox.getSelectedIndex() + 1;
        Task newTask = new Task(description, priority);
        taskListModel.addElement(newTask);
        sortTasks();
        taskInputField.setText("");
    }

    private void deleteTask() {
        int selectedIndex = taskJList.getSelectedIndex();
        if (selectedIndex != -1) {
            taskListModel.remove(selectedIndex);
        }
    }

    private void markTaskComplete() {
        int selectedIndex = taskJList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = taskListModel.getElementAt(selectedIndex);
            task.markComplete();
            taskJList.repaint();
        }
    }

    private void changePriority(boolean increase) {
        int selectedIndex = taskJList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = taskListModel.getElementAt(selectedIndex);
            if (increase) {
                task.increasePriority();
            } else {
                task.decreasePriority();
            }
            sortTasks();
            taskJList.setSelectedIndex(selectedIndex);
            taskJList.repaint();
        }
    }

    private void sortTasks() {
        ArrayList<Task> tasks = Collections.list(taskListModel.elements());
        tasks.sort(Comparator.comparingInt((Task t) -> t.priority).thenComparing(t -> t.description));
        taskListModel.clear();
        for (Task t : tasks) {
            taskListModel.addElement(t);
        }
    }

    private void updateButtons() {
        boolean isSelected = taskJList.getSelectedIndex() != -1;
        deleteButton.setEnabled(isSelected);
        completeButton.setEnabled(isSelected);
        increasePriorityButton.setEnabled(isSelected);
        decreasePriorityButton.setEnabled(isSelected);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ToDoListGUI gui = new ToDoListGUI();
            gui.setVisible(true);
        });
    }

    // Custom ListCellRenderer to display task with priority and green tick mark after description if complete
    private class TaskCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Task task) {
                String priorityStr = switch (task.priority) {
                    case 1 -> "1";
                    case 2 -> "2";
                    case 3 -> "3";
                    case 4 -> "4";
                    case 5 -> "5";
                    default -> "";
                };
                String text = task.description + " (Priority " + priorityStr + ")";
                if (task.isComplete) {
                    // Append green tick mark after description
                    text += " \u2713"; // Unicode tick mark
                    label.setForeground(Color.GREEN.darker());
                } else {
                    label.setForeground(Color.BLACK);
                }
                label.setText(text);
            }
            return label;
        }
    }
}
