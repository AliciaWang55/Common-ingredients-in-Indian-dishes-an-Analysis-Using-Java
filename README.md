Common Ingredients in Indian Dishes

A Java data analysis project that answers one question: what are the most common ingredients in Indian dishes?

Built as a team project for PLTW AP Computer Science Applications (3.7.7 Data Lab) by Harveer Saini, Alex Cheng, Angela Xu, and Alicia Wang.

Original team repository: https://github.com/alxcheng/ChengSainiWangXu-PLTW3.7.7DataLab

Overview

Many people are unaware of what goes into Indian dishes, and sugar in particular can affect health. We analyzed a Kaggle dataset to find the most common ingredients nationally and by region, and displayed the results in an interactive Java Swing window.

Data
Source: a Kaggle dataset of 250+ Indian dishes
Format: CSV (indian_food.csv)
Fields used: dish name, ingredients, vegetarian or not, state, and region
How it works
Parsing: readDishesFromCSV reads the CSV and parseCSVline splits each row into fields. Each row becomes a Dish object, and the dishes are stored in an ArrayList.
Data class: Main.Dish (nested class) holds one row of data and parses the ingredient string into a list of ingredients.
Analysis: countIngredients loops over every dish, counts how often each ingredient appears, and returns the ingredients ranked by frequency.
Visualization: Main.BarChartPanel is a custom JPanel that draws a bar chart with titles, bar colors, and counts above each bar. A tab button switches between the overall chart and the charts grouped by region.
Results

Sugar is the most common ingredient across the whole dataset. In the regional charts, a region value of -1 means the region is unknown in the data.

Running the project

Requires a JDK (Java 8 or later). Keep indian_food.csv in the same folder as the Java file.

bash
javac CommonIngredient.java
java CommonIngredient

If the main class has a different name in your copy, replace CommonIngredient with it.

What we learned

The hardest part was integrating Java's graphics library to draw the charts. We worked through it with documentation and online resources.
