import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;        
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;

public class CommonIngredient {

    // Class to model a dish entry.
    public static class Dish {
        String name;
        List<String> ingredients;
        String diet;
        int prepTime;
        int cookTime;
        String flavorProfile;
        String course;
        String state;
        String region;
        
        public Dish(String name, String ingredientsStr, String diet, int prepTime, int cookTime,
                    String flavorProfile, String course, String state, String region) {
            this.name = name;
            this.ingredients = parseIngredients(ingredientsStr);
            this.diet = diet;
            this.prepTime = prepTime;
            this.cookTime = cookTime;
            this.flavorProfile = flavorProfile;
            this.course = course;
            this.state = state;
            this.region = region;
        }
        
        // Splits the ingredients string by commas and trims whitespace.
        private List<String> parseIngredients(String ingredientsStr) {
            List<String> list = new ArrayList<>();
            String[] parts = ingredientsStr.split(",");
            for (String part : parts) {
                String ingredient = part.trim();
                if (!ingredient.isEmpty()) {
                    list.add(ingredient);
                }
            }
            return list;
        }
    }
    
    // Reads the CSV file and returns a list of Dish objects.
    public static List<Dish> readDishesFromCSV(String filename) {
        List<Dish> dishes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                List<String> tokens = parseCSVLine(line);
                if (tokens.size() < 9) continue;
                
                String name           = tokens.get(0);
                String ingredientsStr = tokens.get(1);
                String diet           = tokens.get(2);
                int prepTime          = parseInt(tokens.get(3));
                int cookTime          = parseInt(tokens.get(4));
                String flavorProfile  = tokens.get(5);
                String course         = tokens.get(6);
                String state          = tokens.get(7);
                String region         = tokens.get(8);
                
                Dish dish = new Dish(name, ingredientsStr, diet, prepTime, cookTime,
                                     flavorProfile, course, state, region);
                dishes.add(dish);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
            e.printStackTrace();
        }
        return dishes;
    }
    
    // A basic CSV parser that handles quoted fields.
    public static List<String> parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());
        return tokens;
    }
    
    // Helper method to safely parse an integer from a String.
    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch(NumberFormatException e) {
            return -1;
        }
    }
    
    // Counts how many times each ingredient appears among a list of dishes.
    public static Map<String, Integer> countIngredients(List<Dish> dishes) {
        Map<String, Integer> counts = new HashMap<>();
        for (Dish dish : dishes) {
            for (String ingredient : dish.ingredients) {
                counts.put(ingredient, counts.getOrDefault(ingredient, 0) + 1);
            }
        }
        return counts;
    }
    
    // Calculates the (population) standard deviation of a collection of integers.
    public static double calculateStandardDeviation(Iterable<Integer> values) {
        double sum = 0;
        int count = 0;
        for (int value : values) {
            sum += value;
            count++;
        }
        double mean = sum / count;
        double sumSquaredDiffs = 0;
        for (int value : values) {
            sumSquaredDiffs += Math.pow(value - mean, 2);
        }
        double variance = sumSquaredDiffs / count;
        return Math.sqrt(variance);
    }
    
    // Finds the ingredient with the highest count.
    public static String findMostCommonIngredient(Map<String, Integer> counts) {
        String mostCommon = "";
        int max = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostCommon = entry.getKey();
            }
        }
        return mostCommon;
    }
    
    // Groups the dishes by region but skips the regions "Central" and "Unknown".
    public static Map<String, List<Dish>> groupDishesByRegion(List<Dish> dishes) {
        Map<String, List<Dish>> regionMap = new HashMap<>();
        for (Dish dish : dishes) {
            String region = dish.region;
            if (region == null || region.trim().isEmpty()) {
                region = "Unknown";
            }
            // Skip the regions "Central" and "Unknown"
            if (region.equalsIgnoreCase("Central") || region.equalsIgnoreCase("Unknown")) {
                continue;
            }
            regionMap.computeIfAbsent(region, k -> new ArrayList<>()).add(dish);
        }
        return regionMap;
    }
    
    // Returns a color for a given ingredient (using a fixed palette based on the ingredient's hash code).
    public static Color getColorForIngredient(String ingredient) {
        Color[] palette = {
            Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE, 
            Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW, 
            Color.LIGHT_GRAY, Color.GRAY
        };
        int index = Math.abs(ingredient.hashCode()) % palette.length;
        return palette[index];
    }
    
    // A custom JPanel that draws a bar chart for a given set of ingredient counts.
    // It also displays a title (for example, the region name) at the top.
    public static class BarChartPanel extends JPanel {
        List<Map.Entry<String, Integer>> sortedEntries;
        String title;
        
        public BarChartPanel(Map<String, Integer> counts, String title) {
            this.title = title;
            sortedEntries = new ArrayList<>(counts.entrySet());
            sortedEntries.sort((e1, e2) -> e2.getValue() - e1.getValue());
            // Retain only the top 10 ingredients.
            if (sortedEntries.size() > 10) {
                sortedEntries = sortedEntries.subList(0, 10);
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            // Draw the title at the top center.
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            g.drawString(title, (panelWidth - titleWidth) / 2, 20);
            
            if (sortedEntries == null || sortedEntries.isEmpty()) return;
            
            int numBars = sortedEntries.size();
            int maxCount = sortedEntries.get(0).getValue();
            int barWidth = panelWidth / numBars;
            int baseY = panelHeight - 40; // baseline for bars
            
            for (int i = 0; i < numBars; i++) {
                Map.Entry<String, Integer> entry = sortedEntries.get(i);
                int count = entry.getValue();
                int barHeight = (int) ((double) count / maxCount * (panelHeight - 60));
                int x = i * barWidth;
                int y = baseY - barHeight;
                
                // Set the bar color based on the ingredient.
                Color barColor = getColorForIngredient(entry.getKey());
                g.setColor(barColor);
                g.fillRect(x + 10, y, barWidth - 20, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x + 10, y, barWidth - 20, barHeight);
                
                // Draw the count above the bar.
                String countStr = Integer.toString(count);
                int countWidth = g.getFontMetrics().stringWidth(countStr);
                g.drawString(countStr, x + 10 + (barWidth - 20 - countWidth) / 2, y - 5);
                
                // Draw the ingredient name diagonally below the bar.
                String ingredientName = entry.getKey();
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
                // Determine the pivot point for rotation (center-bottom of the bar area).
                int textX = x + 10 + (barWidth - 20) / 2;
                int textY = baseY + 5;
                AffineTransform orig = g2d.getTransform();
                // Rotate -45 degrees around the pivot.
                g2d.rotate(Math.toRadians(-45), textX, textY);
                int textWidth = g2d.getFontMetrics().stringWidth(ingredientName);
                g2d.drawString(ingredientName, textX - textWidth / 2, textY);
                g2d.setTransform(orig);
                g2d.dispose();
            }
        }
    }
    
    public static void main(String[] args) {
        String filename = "indian_food.csv";
        List<Dish> dishes = readDishesFromCSV(filename);
        System.out.println("Total dishes read: " + dishes.size());
        
        // Overall counts and statistics.
        Map<String, Integer> overallCounts = countIngredients(dishes);
        String mostCommon = findMostCommonIngredient(overallCounts);
        System.out.println("The most common ingredient overall is: " 
                           + mostCommon + " (appears " + overallCounts.get(mostCommon) + " times)");
        double overallStdDev = calculateStandardDeviation(overallCounts.values());
        System.out.println("Standard Deviation of overall ingredient counts: " + overallStdDev);
        
        // Group dishes by region (skipping "Central" and "Unknown").
        Map<String, List<Dish>> regionMap = groupDishesByRegion(dishes);
        
        // Create a container panel to hold a chart for each region.
        int regionCount = regionMap.size();
        int rows = (int) Math.ceil(Math.sqrt(regionCount));
        int cols = (int) Math.ceil((double) regionCount / rows);
        JPanel containerPanel = new JPanel(new GridLayout(rows, cols, 10, 10));
        
        for (Map.Entry<String, List<Dish>> entry : regionMap.entrySet()) {
            String region = entry.getKey();
            List<Dish> regionDishes = entry.getValue();
            Map<String, Integer> regionCounts = countIngredients(regionDishes);
            BarChartPanel chartPanel = new BarChartPanel(regionCounts, region);
            chartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            containerPanel.add(chartPanel);
        }
        
        // Create a tabbed pane so the user can switch between the overall graph and the regional graphs.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Ingredient Usage by Region");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
                JTabbedPane tabbedPane = new JTabbedPane();
                
                // Overall graph tab.
                BarChartPanel overallChartPanel = new BarChartPanel(overallCounts, "Overall");
                overallChartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                tabbedPane.addTab("Overall", overallChartPanel);
                
                // Region graphs tab (wrapped in a scroll pane in case there are many regions).
                JScrollPane scrollPane = new JScrollPane(containerPanel);
                tabbedPane.addTab("By Region", scrollPane);
                
                frame.add(tabbedPane);
                frame.setSize(1000, 800);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
