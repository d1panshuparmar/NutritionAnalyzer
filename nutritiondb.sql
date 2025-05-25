-- Create database
CREATE DATABASE nutritiondb;

-- Use the database
USE nutritiondb;

-- Create the foods table
CREATE TABLE foods (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    calories DOUBLE,
    protein DOUBLE,
    fat DOUBLE,
    carbs DOUBLE
);INSERT INTO foods (name, calories, protein, fat, carbs) VALUES 
('Apple', 52, 0.3, 0.2, 14),
('Banana', 96, 1.3, 0.3, 27),
('Carrot', 41, 0.9, 0.2, 10),
('Spinach', 23, 2.9, 0.4, 3.6),
('Broccoli', 55, 3.7, 0.6, 11.1),
('Orange', 47, 0.9, 0.1, 12),
('Strawberry', 33, 0.7, 0.3, 8),
('Mango', 60, 0.8, 0.4, 15),
('Tomato', 18, 0.9, 0.2, 3.9),
('Potato', 77, 2, 0.1, 17),
('Peas', 81, 5.4, 0.4, 14.5),
('Corn', 86, 3.2, 1.2, 19),
('Cucumber', 16, 0.7, 0.1, 3.6),
('Beetroot', 43, 1.6, 0.2, 10),
('Papaya', 43, 0.5, 0.3, 11),
('Pineapple', 50, 0.5, 0.1, 13),
('Avocado', 160, 2, 15, 9),
('Pumpkin', 26, 1, 0.1, 6.5),
('Cabbage', 25, 1.3, 0.1, 6),
('Lettuce', 15, 1.4, 0.2, 2.9);

