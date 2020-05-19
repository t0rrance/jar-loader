package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.jarloader.loader.FileLoader;
import sample.jarloader.loader.JarManager;
import sample.jarloader.log.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private TextField input1;

    @FXML
    private TextField input2;

    @FXML
    private TextField returnExecute;

    @FXML
    private Button buttonExecute;

    @FXML
    private ListView listOfClasses;

    @FXML
    private ListView listOfMethods;

    @FXML
    private AnchorPane anchorPane;

    private JarManager jarManager;
    private String CHECKED_METHOD;
    private String CHECKED_CLASS;

    @FXML
    public void selectFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select jar file");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("file .jar/.class", "*.jar", "*.class");
        fileChooser.getExtensionFilters().add(extensionFilter);
        Stage stage = (Stage) anchorPane.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return;
        String path = file.getAbsolutePath();
        String pathToFile = path.replace('/', File.separatorChar).replace('\\', File.separatorChar);
        try {
            FileLoader fileLoader = new FileLoader(new File(pathToFile));
            jarManager = new JarManager(fileLoader);
            jarManager.load();
            showAllClass();
        } catch (NoSuchFileException | IllegalArgumentException e) {
            Log.syserr(e.getMessage());
        }
    }

    private void showAllClass() {
        List<Class<?>> jarClass = new ArrayList<>(jarManager.getJarClass());

        for (Class cls : jarClass) {
            listOfClasses.getItems().add(cls.getSimpleName());
        }

        listOfClasses.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (listOfMethods != null) listOfMethods.getItems().clear();
                String checkClass = listOfClasses.getSelectionModel().getSelectedItems().toString();
                String pureName = checkClass.substring(1, checkClass.length() - 1);
                CHECKED_CLASS = pureName;
                selectClass(pureName);
            }
        });
    }

    private void selectClass(String className) {
        List<Method> methods = new ArrayList<>(jarManager.getClassMethods(className));
        for (Method method : methods) {
            listOfMethods.getItems().add(method);
        }
        listOfMethods.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String checkMethod = listOfMethods.getSelectionModel().getSelectedItems().toString();
                int startCut = checkMethod.lastIndexOf(".") + 1;
                int stopCut = checkMethod.lastIndexOf("(");

                String pureName = checkMethod.substring(startCut, stopCut);
                System.out.println("pure method:" + pureName);
                CHECKED_METHOD = pureName;
            }
        });
    }

    @FXML
    private void callMethod() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class cls = jarManager.getClass(CHECKED_CLASS);
        String returnValue = jarManager.callMethod(cls, CHECKED_METHOD, Double.parseDouble(input1.getText()), Double.parseDouble(input2.getText()));
        returnExecute.setText(returnValue);
    }

}
