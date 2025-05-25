import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class NutritionAnalyzer extends JFrame {
    private JComboBox<String> foodDropdown;
    private JTextField calField, proteinField, fatField, carbField;
    private JTextArea summaryArea;

    private final FoodDAO foodDAO = new FoodDAO();
    private final ArrayList<String> foodLog = new ArrayList<>();

    public NutritionAnalyzer() {
        setTitle("Nutrition Analyzer");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        loadFoodItems();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Select Food"));

        foodDropdown = new JComboBox<>();
        calField = new JTextField();
        proteinField = new JTextField();
        fatField = new JTextField();
        carbField = new JTextField();

        calField.setEditable(false);
        proteinField.setEditable(false);
        fatField.setEditable(false);
        carbField.setEditable(false);

        topPanel.add(new JLabel("Food:"));
        topPanel.add(foodDropdown);
        topPanel.add(new JLabel("Calories:"));
        topPanel.add(calField);
        topPanel.add(new JLabel("Protein (g):"));
        topPanel.add(proteinField);
        topPanel.add(new JLabel("Fat (g):"));
        topPanel.add(fatField);
        topPanel.add(new JLabel("Carbs (g):"));
        topPanel.add(carbField);

        JButton addBtn = new JButton("Add to Log");
        JButton showBtn = new JButton("Show Summary");
        topPanel.add(addBtn);
        topPanel.add(showBtn);

        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daily Nutrition Summary"));

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        foodDropdown.addActionListener(e -> updateFields());
        addBtn.addActionListener(e -> addToLog());
        showBtn.addActionListener(e -> showSummary());
    }

    private void loadFoodItems() {
        List<String> foods = foodDAO.getAllFoodNames();
        for (String food : foods) {
            foodDropdown.addItem(food);
        }

        if (!foods.isEmpty()) {
            foodDropdown.setSelectedIndex(0);
            updateFields();
        }
    }

    private void updateFields() {
        String selected = (String) foodDropdown.getSelectedItem();
        if (selected == null)
            return;

        Food food = foodDAO.getFoodByName(selected);
        if (food != null) {
            calField.setText(String.valueOf(food.getCalories()));
            proteinField.setText(String.valueOf(food.getProtein()));
            fatField.setText(String.valueOf(food.getFat()));
            carbField.setText(String.valueOf(food.getCarbs()));
        }
    }

    private void addToLog() {
        String food = (String) foodDropdown.getSelectedItem();
        if (food != null) {
            foodLog.add(food);
            JOptionPane.showMessageDialog(this, food + " added to log.");
        }
    }

    private void showSummary() {
        if (foodLog.isEmpty()) {
            summaryArea.setText("No items in log.");
            return;
        }

        double totalCalories = 0, totalProtein = 0, totalFat = 0, totalCarbs = 0;
        StringBuilder summary = new StringBuilder("---- Food Log ----\n");

        for (String foodName : foodLog) {
            Food food = foodDAO.getFoodByName(foodName);
            if (food != null) {
                summary.append(food.getName()).append(" - ").append(food.getCalories()).append(" cal\n");
                totalCalories += food.getCalories();
                totalProtein += food.getProtein();
                totalFat += food.getFat();
                totalCarbs += food.getCarbs();
            }
        }

        summary.append("\n--- Total Nutrition ---\n")
                .append("Calories: ").append(totalCalories).append("\n")
                .append("Protein: ").append(totalProtein).append(" g\n")
                .append("Fat: ").append(totalFat).append(" g\n")
                .append("Carbs: ").append(totalCarbs).append(" g\n");

        summaryArea.setText(summary.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NutritionAnalyzer().setVisible(true));
    }
}
