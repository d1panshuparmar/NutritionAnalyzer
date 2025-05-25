import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {

    // Get a list of all food names for the dropdown
    public List<String> getAllFoodNames() {
        List<String> foodNames = new ArrayList<>();
        String sql = "SELECT name FROM foods ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                foodNames.add(rs.getString("name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return foodNames;
    }

    // Get food details (calories, protein, fat, carbs) by name
    public Food getFoodByName(String name) {
        String sql = "SELECT * FROM foods WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Food(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("calories"),
                        rs.getDouble("protein"),
                        rs.getDouble("fat"),
                        rs.getDouble("carbs"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Optional: Add new food item (if needed later)
    public boolean addFood(Food food) {
        String sql = "INSERT INTO foods (name, calories, protein, fat, carbs) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, food.getName());
            ps.setDouble(2, food.getCalories());
            ps.setDouble(3, food.getProtein());
            ps.setDouble(4, food.getFat());
            ps.setDouble(5, food.getCarbs());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
