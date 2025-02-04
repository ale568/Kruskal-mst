import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.scene.shape.Line;
import javafx.scene.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KruskalGUI {

    private BorderPane mainPane;
    private TextArea outputArea;
    private Spinner<Integer> nodeSpinner;
    private Button loadButton;
    private Button executeButton;
    private Button resetButton;
    private ProgressBar progressBar;
    private Pane graphPane;
    private Label timeLabel; 


    private Graph graph;
    private Button confirmNodesButton; // Pulsante per confermare la selezione dei nodi

    private Map<Integer, Circle> nodeCircles = new HashMap<>();

    public KruskalGUI() {
        // Layout principale
        mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10));

        // Spinner per selezionare il numero di nodi
        nodeSpinner = new Spinner<>(1, 5000, 5);
        nodeSpinner.setEditable(true);

        // Pulsanti
        confirmNodesButton = new Button("Conferma Nodi");
        confirmNodesButton.setDisable(false);
        confirmNodesButton.setOnAction(e -> confirmNodes());

        loadButton = new Button("Carica CSV");
        loadButton.setOnAction(e -> loadGraph());
        loadButton.setDisable(true);

        executeButton = new Button("Esegui Algoritmo");
        executeButton.setOnAction(e -> executeKruskal());
        executeButton.setDisable(true);

        resetButton = new Button("Reset");
        resetButton.setOnAction(e -> resetUI());
        resetButton.setDisable(true);

        // Barra di progresso
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(400);

        // Label del tempo
        timeLabel = new Label("Tempo: -- s");

        // Layout superiore
        HBox topBox = new HBox(10, new Label("Numero di nodi:"), nodeSpinner, confirmNodesButton, loadButton, executeButton, resetButton);
        topBox.setPadding(new Insets(10));
        mainPane.setTop(topBox);

        // Layout centrale
        graphPane = new Pane();
        graphPane.setPrefSize(600, 400);
        graphPane.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 2;");
        ScrollPane outputScrollPane = createOutputArea();
        SplitPane splitPane = new SplitPane(graphPane, outputScrollPane);
        splitPane.setDividerPositions(0.7);
        mainPane.setCenter(splitPane);

        // Layout inferiore
        VBox bottomBox = new VBox(10, progressBar, timeLabel); // Usa variabili correttamente inizializzate
        bottomBox.setPadding(new Insets(10));
        mainPane.setBottom(bottomBox);

    }   

    private ScrollPane createOutputArea() {
        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(400);
        ScrollPane scrollPane = new ScrollPane(outputArea);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private void loadGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File CSV", "*.csv"));
    
        // Imposta la directory iniziale al percorso delle risorse
        File initialDirectory = new File("/test");
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }
    
        File file = fileChooser.showOpenDialog(null);
    
        if (file != null) {
            try {
                // Conta i nodi nel file
                int actualNodeCount = getNodeCountFromFile(file);
    
                // Confronta il numero di nodi dal file con lo spinner
                if (actualNodeCount != nodeSpinner.getValue()) {
                    outputArea.appendText("Errore: il numero di nodi specificato nello spinner (" 
                            + nodeSpinner.getValue() + ") non corrisponde al numero di nodi nel file CSV (" 
                            + actualNodeCount + ").\n");
    
                    // Disabilita il pulsante Carica CSV e abilita Spinner e Conferma Nodi
                    loadButton.setDisable(true); 
                    nodeSpinner.setDisable(false); 
                    confirmNodesButton.setDisable(false);
                    return; // Interrompi l'elaborazione
                }
    
                // Se il numero di nodi è corretto, carica il grafo
                graph = Graph.fromCSV(file, actualNodeCount);
                graphPane.getChildren().clear(); // Pulisci il pannello grafico
                outputArea.appendText("File caricato correttamente: " + file.getName() + "\n");
    
                // Disegna il grafo
                drawGraph(graph.getNodes(), graph.getEdges(), List.of()); // Disegna senza MST
                loadButton.setDisable(true); // Disabilita il pulsante Carica CSV
                executeButton.setDisable(false); // Abilita il pulsante per eseguire l'algoritmo
    
            } catch (Exception e) {
                outputArea.appendText("Errore durante il caricamento del file: " + e.getMessage() + "\n");
    
                // Pulisci il pannello grafico in caso di errore
                graphPane.getChildren().clear();
    
                // Disabilita il pulsante Carica CSV e abilita Spinner e Conferma Nodi
                loadButton.setDisable(true); 
                nodeSpinner.setDisable(false); 
                confirmNodesButton.setDisable(false);
            }
        }
    }

    private void confirmNodes() {
        // Disabilita lo spinner e il pulsante di conferma
        nodeSpinner.setDisable(true);
        confirmNodesButton.setDisable(true);
    
        // Abilita il pulsante per caricare il file CSV
        loadButton.setDisable(false);
    
        outputArea.appendText("Numero di nodi confermato: " + nodeSpinner.getValue() + "\n");
    }

    private void resetUI() {
        // Resetta il pannello grafico
        graphPane.getChildren().clear();
    
        // Resetta l'area di output
        outputArea.clear();
    
        // Resetta il grafo
        graph = null;
    
        // Ripristina lo stato iniziale dei controlli
        nodeSpinner.setDisable(false); // Riabilita lo spinner
        confirmNodesButton.setDisable(false); // Riabilita il pulsante Conferma Nodi
        loadButton.setDisable(true); // Disabilita il pulsante Carica CSV
        executeButton.setDisable(true); // Disabilita il pulsante Esegui Algoritmo
        resetButton.setDisable(true); // Disabilita il pulsante Reset
    
        // Resetta la barra di progresso
        progressBar.progressProperty().unbind(); // Scollega eventuali binding
        progressBar.setProgress(0); // Resetta la barra di progresso
    
        // Resetta la label del tempo
        timeLabel.setText("Tempo: -- s");
    }
    

    private int getNodeCountFromFile(File file) throws IOException {
        Set<Integer> uniqueNodes = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                uniqueNodes.add(Integer.parseInt(parts[0].trim()));
                uniqueNodes.add(Integer.parseInt(parts[1].trim()));
            }
        }
        return uniqueNodes.size();
    }

    private void executeKruskal() {
        if (graph != null) {
            outputArea.appendText("Esecuzione dell'algoritmo di Kruskal...\n");
            progressBar.setProgress(0); // Reset della barra di progresso
            timeLabel.setText("Tempo: -- s"); // Reset del testo della label
    
            if (!graph.isConnected()) {
                outputArea.appendText("Errore: il grafo non è connesso.\n");
                resetButton.setDisable(false); // Abilita il pulsante Reset
                return;
            }
    
            // Crea un task per l'esecuzione in un thread separato
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        long startTime = System.currentTimeMillis(); // Tempo di inizio
    
                        // Calcola l'MST e aggiorna la barra di progresso
                        List<Edge> mst = graph.Kruskal(progress -> updateProgress(progress, 1.0));
    
                        long endTime = System.currentTimeMillis(); // Tempo di fine
                        double elapsedTime = (endTime - startTime) / 1000.0;
    
                        // Aggiorna la GUI al completamento
                        Platform.runLater(() -> {
                            outputArea.appendText("Archi MST:\n");
                            mst.forEach(edge -> outputArea.appendText(edge + "\n"));
                            drawGraph(graph.getNodes(), graph.getEdges(), mst); // Disegna il grafo con MST
                            executeButton.setDisable(true); // Disabilita il pulsante Esegui
                            resetButton.setDisable(false); // Abilita il pulsante Reset
                            timeLabel.setText(String.format("Tempo: %.3f s", elapsedTime)); // Mostra il tempo di esecuzione ai millesimi
                        });
                    } catch (Exception e) {
                        // Gestione degli errori
                        Platform.runLater(() -> {
                            outputArea.appendText("Errore durante l'esecuzione dell'algoritmo: " + e.getMessage() + "\n");
                            resetButton.setDisable(false); // Abilita il pulsante Reset anche in caso di errore
                        });
                    }
                    return null;
                }
            };
    
            // Collega la barra di progresso al task
            progressBar.progressProperty().bind(task.progressProperty());
            new Thread(task).start(); // Avvia il task in un nuovo thread
        }
    }
    
    private void drawGraph(List<Integer> nodes, List<Edge> edges, List<Edge> mst) {
        graphPane.getChildren().clear(); // Pulisci il pannello grafico
        nodeCircles.clear(); // Resetta la mappa dei nodi
        
        Map<Integer, double[]> nodePositions = new HashMap<>();
        int gridSize = (int) Math.ceil(Math.sqrt(nodes.size())); // Calcola la dimensione della griglia
        
        double cellWidth = graphPane.getWidth() / gridSize; // Larghezza di ogni cella
        double cellHeight = graphPane.getHeight() / gridSize; // Altezza di ogni cella
    
        Random rand = new Random();
        Set<String> usedPositions = new HashSet<>(); // Per tracciare celle già occupate
    
        // Disegna i nodi
        for (int node : nodes) {
            int row, col;
            String cellKey;
    
            // Trova una cella libera
            do {
                row = rand.nextInt(gridSize);
                col = rand.nextInt(gridSize);
                cellKey = row + "," + col;
            } while (usedPositions.contains(cellKey));
            usedPositions.add(cellKey); // Segna la cella come occupata
    
            // Posiziona il nodo in modo casuale all'interno della cella
            double x = col * cellWidth + rand.nextDouble() * (cellWidth * 0.8);
            double y = row * cellHeight + rand.nextDouble() * (cellHeight * 0.8);
    
            nodePositions.put(node, new double[]{x, y});
    
            Circle circle = new Circle(x, y, 10);
            circle.setStyle("-fx-fill: #3498db; -fx-stroke: #2980b9; -fx-stroke-width: 2;");
    
            // Imposta un'azione al clic sul nodo
            int currentNode = node; // Variabile finale per uso nel lambda
            circle.setOnMouseClicked(e -> showNodeDetails(currentNode));
    
            nodeCircles.put(node, circle); // Mappa il nodo al cerchio
            graphPane.getChildren().add(circle); // Aggiungi il cerchio al pannello grafico
        }
    
        // Disegna gli archi
        for (Edge edge : edges) {
            double[] srcPos = nodePositions.get(edge.getSrc());
            double[] destPos = nodePositions.get(edge.getEnd());
    
            Line line = new Line(srcPos[0], srcPos[1], destPos[0], destPos[1]);
            if (mst.contains(edge)) {
                line.setStyle("-fx-stroke: #e74c3c; -fx-stroke-width: 3;"); // Rosso per gli archi MST
            } else {
                line.setStyle("-fx-stroke: #95a5a6; -fx-stroke-width: 1;"); // Grigio per altri archi
            }
    
            graphPane.getChildren().add(line); // Aggiungi la linea al pannello grafico
        }
    }
    

    private void showNodeDetails(int node) {
        // Resetta lo stile di tutti i nodi
        for (Node n : graphPane.getChildren()) {
            if (n instanceof Circle) {
                ((Circle) n).setStyle("-fx-fill: #3498db; -fx-stroke: #2980b9; -fx-stroke-width: 2;");
            }
        }
    
        // Cambia lo stile del nodo selezionato
        Circle selectedCircle = nodeCircles.get(node);
        if (selectedCircle != null) {
            selectedCircle.setStyle("-fx-fill: #3498db; -fx-stroke: #9b59b6; -fx-stroke-width: 4;"); // Viola per nodo selezionato
        }
    
        // Costruisci i dettagli da mostrare
        StringBuilder details = new StringBuilder("Dettagli per il nodo: " + node + "\n\n");
        details.append("Connessioni:\n");
    
        // Evidenzia i nodi connessi e aggiungi i dettagli
        for (Edge edge : graph.getEdges()) {
            if (edge.getSrc() == node || edge.getEnd() == node) {
                int connectedNode = (edge.getSrc() == node) ? edge.getEnd() : edge.getSrc();
    
                // Cambia lo stile dei nodi connessi
                Circle connectedCircle = nodeCircles.get(connectedNode);
                if (connectedCircle != null) {
                    connectedCircle.setStyle("-fx-fill: #3498db; -fx-stroke: #2c3e50; -fx-stroke-width: 4;"); // Grigio scuro per nodi connessi
                }
    
                details.append(" -> Nodo ").append(connectedNode)
                       .append(" (Peso: ").append(edge.getWeight()).append(")\n");
            }
        }
    
        // Mostra i dettagli in una finestra di dialogo
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dettagli Nodo");
        alert.setHeaderText(null);
        alert.setContentText(details.toString());
        alert.showAndWait();
    }    
    
    public BorderPane getMainPane() {
        return mainPane;
    }
}