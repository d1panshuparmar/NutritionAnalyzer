import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class NutritionAnalyzer extends JFrame {
    private JComboBox<String> foodDropdown;
    private JTextField calField, proteinField, fatField, carbField;
    private JTextArea summaryArea;
    private ArrayList<String> foodLog = new ArrayList<>();
    private Connection conn;

    public NutritionAnalyzer() {
        setTitle("Nutrition Analyzer");
        setSize(650, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        connectDatabase();
        initializeDatabase();
        initUI();
        loadFoodItems();
    }

    private void connectDatabase() {
        try {
            File dbFile = new File("nutrition.db");
            boolean newDB = !dbFile.exists();

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:nutrition.db");

            if (newDB) {
                System.out.println("Creating new database...");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Connection Failed: " + e.getMessage());
            System.exit(1);
        }
    }

    private void initializeDatabase() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS foods (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "calories REAL," +
                    "protein REAL," +
                    "fat REAL," +
                    "carbs REAL)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM foods");
            if (rs.next() && rs.getInt(1) == 0) {
                String[] foods = {
                        "Apple,52,0.3,0.2,14", "Banana,96,1.3,0.3,27",
                        "Carrot,41,0.9,0.2,10", "Spinach,23,2.9,0.4,3.6",
                        "Broccoli,55,3.7,0.6,11.1", "Orange,47,0.9,0.1,12",
                        "Tomato,18,0.9,0.2,3.9", "Cucumber,16,0.7,0.1,3.6",
                        "Mango,60,0.8,0.4,15", "Grapes,69,0.6,0.2,18",
                        "Peach,39,0.9,0.3,10", "Pineapple,50,0.5,0.1,13",
                        "Strawberry,32,0.7,0.3,7.7", "Blueberry,57,0.7,0.3,14",
                        "Papaya,43,0.5,0.3,11", "Watermelon,30,0.6,0.2,7.6",
                        "Potato,77,2,0.1,17", "Sweet Potato,86,1.6,0.1,20",
                        "Beetroot,43,1.6,0.2,10", "Onion,40,1.1,0.1,9.3",
                        "Lettuce,15,1.4,0.2,2.9", "Zucchini,17,1.2,0.3,3.1",
                        "Avocado,160,2,15,9", "Pear,57,0.4,0.1,15", "Guava,68,2.6,1,14"
                };

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO foods (name, calories, protein, fat, carbs) VALUES (?, ?, ?, ?, ?)");
                for (String food : foods) {
                    String[] f = food.split(",");
                    ps.setString(1, f[0]);
                    ps.setDouble(2, Double.parseDouble(f[1]));
                    ps.setDouble(3, Double.parseDouble(f[2]));
                    ps.setDouble(4, Double.parseDouble(f[3]));
                    ps.setDouble(5, Double.parseDouble(f[4]));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        foodDropdown = new JComboBox<>();
        calField = new JTextField();
        calField.setEditable(false);
        proteinField = new JTextField();
        proteinField.setEditable(false);
        fatField = new JTextField();
        fatField.setEditable(false);
        carbField = new JTextField();
        carbField.setEditable(false);
        JButton addBtn = new JButton("Add to Log");
        JButton summaryBtn = new JButton("Show Summary");

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
        topPanel.add(addBtn);
        topPanel.add(summaryBtn);

        summaryArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(summaryArea);
        summaryArea.setEditable(false);
        scroll.setBorder(BorderFactory.createTitledBorder("Nutrition Summary"));

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        foodDropdown.addActionListener(e -> updateFields());
        addBtn.addActionListener(e -> addToLog());
        summaryBtn.addActionListener(e -> showSummary());
    }

    private void loadFoodItems() {
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT name FROM foods ORDER BY name")) {
            while (rs.next()) {
                foodDropdown.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateFields();
    }

    private void updateFields() {
        String food = (String) foodDropdown.getSelectedItem();
        if (food == null)
            return;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM foods WHERE name = ?")) {
            ps.setString(1, food);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                calField.setText(rs.getString("calories"));
                proteinField.setText(rs.getString("protein"));
                fatField.setText(rs.getString("fat"));
                carbField.setText(rs.getString("carbs"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            summaryArea.setText("Log is empty.");
            return;
        }

        double totalCal = 0, totalPro = 0, totalFat = 0, totalCarb = 0;
        StringBuilder summary = new StringBuilder("---- Food Log ----\n");

        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM foods WHERE name = ?")) {
            for (String food : foodLog) {
                ps.setString(1, food);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    summary.append(food).append(": ").append(rs.getDouble("calories")).append(" cal\n");
                    totalCal += rs.getDouble("calories");
                    totalPro += rs.getDouble("protein");
                    totalFat += rs.getDouble("fat");
                    totalCarb += rs.getDouble("carbs");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        summary.append("\n--- Total Nutrition ---\n");
        summary.append("Calories: ").append(totalCal).append("\n");
        summary.append("Protein: ").append(totalPro).append(" g\n");
        summary.append("Fat: ").append(totalFat).append(" g\n");
        summary.append("Carbs: ").append(totalCarb).append(" g\n");

        summaryArea.setText(summary.toString());

        showCharts(totalCal, totalPro, totalFat, totalCarb);
    }

    private void showCharts(double cal, double protein, double fat, double carbs) {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Protein", protein);
        pieDataset.setValue("Fat", fat);
        pieDataset.setValue("Carbs", carbs);

        JFreeChart pieChart = ChartFactory.createPieChart("Macronutrient Breakdown", pieDataset);
        ChartFrame pieFrame = new ChartFrame("Pie Chart", pieChart);
        pieFrame.setSize(400, 300);
        pieFrame.setLocationRelativeTo(null);
        pieFrame.setVisible(true);

        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        barDataset.addValue(protein, "Protein", "Protein");
        barDataset.addValue(fat, "Fat", "Fat");
        barDataset.addValue(carbs, "Carbs", "Carbs");

        JFreeChart barChart = ChartFactory.createBarChart("Macronutrient Totals", "Type", "Grams", barDataset);
        ChartFrame barFrame = new ChartFrame("Bar Chart", barChart);
        barFrame.setSize(400, 300);
        barFrame.setLocationRelativeTo(null);
        barFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NutritionAnalyzer().setVisible(true));
    }
}
