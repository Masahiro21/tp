package seedu.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import seedu.entities.Meal;
import seedu.entities.Food;
import com.opencsv.CSVWriter;

public class MealStorage extends Storage implements FileReadable, FileWritable {
    private static final String CSV_DELIMITER = ",";
    private static final String FOODS_DELIMITER = "-";
    private static final String DATE_FORMAT = "d/M/yyyy";
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_FORMAT, Locale.ENGLISH);
    private ArrayList<Meal> meals;
    private FoodStorage foodStorage;

    public MealStorage(String filePath, FoodStorage foodStorage) {
        super(filePath);
        this.foodStorage = foodStorage;
        meals = new ArrayList<Meal>();
        try {
            this.load();
        } catch (IOException e) {
            System.out.println("Error loading Meal Storage");
        }
    }

    @Override
    public void write() throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filePath),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.RFC4180_LINE_END);
        String[] header = { "Date", "Foods" };
        writer.writeNext(header);
        for (Meal meal : meals) {
            writer.writeNext(meal.toWriteFormat(FOODS_DELIMITER, DATE_FORMAT));
        }
        writer.close();
    }

    @Override
    public void load() throws IOException {
        String line = "";
        String[] mealLine;
        LocalDate date;
        String[] foodIndexes;
        ArrayList<Food> foods;

        BufferedReader br = new BufferedReader(new FileReader(filePath));

        // Skip Line 1 (header)
        br.readLine();

        while ((line = br.readLine()) != null) {
            mealLine = line.split(CSV_DELIMITER);
            date = LocalDate.parse(mealLine[0], DTF);
            foodIndexes = mealLine[1].split(FOODS_DELIMITER);
            foods = new ArrayList<Food>();
            for (String foodIndex : foodIndexes) {
                foods.add(foodStorage.getFoodById(Integer.parseInt(foodIndex)));
            }
            meals.add(new Meal(foods, date));
        }

        br.close();
    }

    public void saveMeal(Meal meal) {
        meals.add(meal);
        try {
            this.write();
            System.out.println("Meal was successfully added!");
        } catch (IOException e) {
            System.out.println("Could not add meal to storage!");
        }
    }

    public int getMealCount() {
        return this.meals.size();
    }

    public ArrayList<Meal> getMeal() {
        return this.meals;
    }

    public Meal getMealById(int id) {
        return this.meals.get(id);
    }

    public Meal deleteMeal(int index) throws IndexOutOfBoundsException {
        return meals.remove(index);
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return DTF;
    }
}
