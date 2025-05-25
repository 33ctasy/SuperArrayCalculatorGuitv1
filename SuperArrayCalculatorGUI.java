import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SuperArrayCalculatorGUI extends JFrame {
    private JTextField displayField;
    private JTextArea historyArea;
    private ArrayList<Double> currentArray = new ArrayList<>();
    private LinkedList<String> history = new LinkedList<>();
    private String currentOperation = "";
    private double firstNumber = 0;

    // Цвета согласно палитре
    private final Color BG_MAIN = Color.decode("#F5F5F5");        // Основной фон
    private final Color PANEL_BG = Color.decode("#FFFFFF");       // Рабочие панели
    private final Color ACCENT = Color.decode("#4A90E2");         // Акценты - кнопки
    private final Color SUCCESS = Color.decode("#50E3C2");        // Сообщения успеха
    private final Color TEXT_MAIN = Color.decode("#333333");      // Основной текст
    private final Color TEXT_HINT = Color.decode("#888888");      // Подсказки
    private final Color ERROR = Color.decode("#D0021B");          // Ошибки

    public SuperArrayCalculatorGUI() {
        setTitle("SuperArrayCalculator");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        displayField = new JTextField("0");
        displayField.setFont(new Font("Arial", Font.BOLD, 24));
        displayField.setHorizontalAlignment(JTextField.RIGHT);
        displayField.setEditable(true);
        displayField.setBackground(PANEL_BG);
        displayField.setForeground(TEXT_MAIN);
        displayField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        displayField.addActionListener(e -> addNumberToArray());
        add(displayField, BorderLayout.NORTH);

        historyArea = new JTextArea(5, 20);
        historyArea.setEditable(false);
        historyArea.setBackground(PANEL_BG);
        historyArea.setForeground(TEXT_MAIN);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ACCENT),
                "Історія", 0, 0, new Font("Arial", Font.BOLD, 16), TEXT_MAIN));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(6, 4, 5, 5));
        buttonPanel.setBackground(BG_MAIN);
        String[] buttons = {
            "C", "←", "±", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", "More"
        };

        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setForeground(Color.WHITE);
            button.setBackground(ACCENT);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(ACCENT.darker(), 1));
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(ACCENT.darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(ACCENT);
                }
            });
            button.addActionListener(e -> handleButton(label));
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleButton(String label) {
        switch (label) {
            case "C" -> {
                displayField.setText("0");
                currentArray.clear();
                updateHistory("Очищено");
                displayField.setForeground(TEXT_MAIN);
            }
            case "←" -> {
                String text = displayField.getText();
                if (text.length() > 1) displayField.setText(text.substring(0, text.length() - 1));
                else displayField.setText("0");
                displayField.setForeground(TEXT_MAIN);
            }
            case "±" -> {
                String text = displayField.getText();
                if (text.startsWith("-")) displayField.setText(text.substring(1));
                else if (!text.equals("0")) displayField.setText("-" + text);
                displayField.setForeground(TEXT_MAIN);
            }
            case "+", "-", "*", "/", "%" -> {
                try {
                    firstNumber = Double.parseDouble(displayField.getText());
                    currentOperation = label;
                    displayField.setText("0");
                    displayField.setForeground(TEXT_MAIN);
                } catch (NumberFormatException ex) {
                    showError("Помилка вводу!");
                }
            }
            case "=" -> calculateResult();
            case "More" -> showAdvancedStatistics();
            default -> {
                if (label.matches("[0-9.]")) {
                    String current = displayField.getText();
                    // запретить больше одной точки
                    if (label.equals(".") && current.contains(".")) return;
                    displayField.setText(current.equals("0") ? label : current + label);
                    displayField.setForeground(TEXT_MAIN);
                }
            }
        }
    }

    private void addNumberToArray() {
        try {
            double number = Double.parseDouble(displayField.getText());
            currentArray.add(number);
            updateHistory("Додано: " + number);
            displayField.setText("0");
            displayField.setForeground(SUCCESS);
        } catch (NumberFormatException ex) {
            showError("Невірне число!");
        }
    }

    private void calculateResult() {
        try {
            double second = Double.parseDouble(displayField.getText());
            double result = switch (currentOperation) {
                case "+" -> firstNumber + second;
                case "-" -> firstNumber - second;
                case "*" -> firstNumber * second;
                case "/" -> {
                    if (second == 0) throw new ArithmeticException();
                    yield firstNumber / second;
                }
                case "%" -> firstNumber % second;
                default -> 0;
            };
            displayField.setText(String.valueOf(result));
            currentArray.add(result);
            updateHistory("Результат: " + result);
            displayField.setForeground(SUCCESS);
        } catch (NumberFormatException ex) {
            showError("Помилка обчислення!");
        } catch (ArithmeticException e) {
            showError("Ділення на нуль!");
        }
    }

    private void updateHistory(String msg) {
        history.addFirst(msg);
        if (history.size() > 10) history.removeLast();
        historyArea.setText(String.join("\n", history));
    }

    private void showError(String msg) {
        displayField.setForeground(ERROR);
        JOptionPane.showMessageDialog(this, msg, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    private void showAdvancedStatistics() {
        if (currentArray.isEmpty()) {
            showError("Масив порожній!");
            return;
        }

        List<Double> sorted = new ArrayList<>(currentArray);
        Collections.sort(sorted);

        double sum = sorted.stream().mapToDouble(Double::doubleValue).sum();
        double average = sum / sorted.size();
        double median = calculateMedian(sorted);
        double q1 = calculateMedian(sorted.subList(0, sorted.size() / 2));
        double q3 = calculateMedian(sorted.subList((sorted.size() + 1) / 2, sorted.size()));
        double max = Collections.max(sorted);
        double min = Collections.min(sorted);
        long positives = sorted.stream().filter(n -> n > 0).count();
        long negatives = sorted.stream().filter(n -> n < 0).count();
        double mode = findMode(sorted);

        StringBuilder sb = new StringBuilder("📊 Статистика:\n");
        sb.append("🔢 Кількість: ").append(sorted.size()).append("\n");
        sb.append("➕ Сума: ").append(sum).append("\n");
        sb.append("📈 Середнє: ").append(average).append("\n");
        sb.append("📌 Медіана: ").append(median).append("\n");
        sb.append("🟰 Q1: ").append(q1).append("\n");
        sb.append("🟰 Q3: ").append(q3).append("\n");
        sb.append("🔼 Макс: ").append(max).append("\n");
        sb.append("🔽 Мін: ").append(min).append("\n");
        sb.append("➕ Додатних: ").append(positives).append("\n");
        sb.append("➖ Від’ємних: ").append(negatives).append("\n");
        sb.append("🔁 Найчастіше: ").append(mode).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Розширена статистика", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calculateMedian(List<Double> list) {
        int size = list.size();
        if (size % 2 == 0)
            return (list.get(size / 2 - 1) + list.get(size / 2)) / 2.0;
        else
            return list.get(size / 2);
    }

    private double findMode(List<Double> list) {
        Map<Double, Long> freq = list.stream()
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
        return freq.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(Map.entry(0.0, 0L))
                .getKey();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SuperArrayCalculatorGUI().setVisible(true));
    }
}
