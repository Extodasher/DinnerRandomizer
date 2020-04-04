package Randomizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public ObservableList<String> meals;
    public ListView<String> list;
    public TextField textField;
    public Label forDinner;
    public Button generatorButton;
    public Label debugLabel;

    private BufferedWriter writer;
    private String filename = "meals.txt";

    public void initialize(URL url, ResourceBundle rb) {
        startup();
    }

    private void startup() {
        String[] mealsFromFile = getMealsFromFile(filename);
        meals = FXCollections.observableArrayList(mealsFromFile);

        list.setItems(meals);
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public static String[] getMealsFromFile(String filename){
        try{
            int numLines = 0;
            int readAheadLimit = 1000;      // Change if things start to crash
            BufferedReader inputStream = new BufferedReader(new FileReader(filename));

            // Determine how many options there are
            inputStream.mark(readAheadLimit);
            String line = inputStream.readLine();

            while( line != null){
                numLines++;
                line = inputStream.readLine();
            }

            String[] meals = new String[numLines];

            // Put the options into options
            inputStream.reset();

            for(int i = 0; i < numLines; i++){
                meals[i] = inputStream.readLine();
            }

            inputStream.close();

            return meals;
        }
        catch(FileNotFoundException e){
            File file = new File(filename);
            try{
                file.createNewFile();
            }
            catch(Exception e2){
                System.out.println(e2.toString());
            }

            return new String[0];

        } catch (Exception e) { // This should never happen
            System.out.println(e.toString());
            return null;
        }
    }

    public void addMeal() {
        String text = textField.getText();
        meals.add(text);
        textField.clear();
        list.setItems(meals);

        try {
            writer = new BufferedWriter(new FileWriter(new File(filename), true));
            writer.write(text);
            writer.newLine();
            writer.close();
        }catch(Exception e){}
    }

    public void removeMeals(ActionEvent actionEvent) {
        ObservableList<String> toDelete = list.getSelectionModel().getSelectedItems();
        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            File tempFile = new File(filename + ".temp");
            writer = new BufferedWriter(new FileWriter(tempFile, true));


            String line;
            while((line = reader.readLine()) != null){
                if(toDelete.contains(line)){
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            reader.close();

            System.out.println("Deleted file?: " + file.delete());
            tempFile.renameTo(file);
//            tempFile.delete();
            startup();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void nextMeal(){
        int index = (int) (Math.random() * meals.size());
        forDinner.setText("We're having " + meals.get(index) + " for dinner.");
    }


    public void addMealEnter(KeyEvent keyEvent) {
        if(!(keyEvent.getCode() == KeyCode.ENTER)){//
            return;
        }
        addMeal();
        return;
    }

    // Debug
    public void setDebugLabel(String text){
        debugLabel.setText(text);
    }
}