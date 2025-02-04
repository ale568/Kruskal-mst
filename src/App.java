import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        KruskalGUI gui = new KruskalGUI(); // Crea la GUI principale

        Scene scene = new Scene(gui.getMainPane(), 1920, 900); // Imposta dimensioni iniziali
        primaryStage.setTitle("Kruskal MST Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
