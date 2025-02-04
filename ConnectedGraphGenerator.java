import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ConnectedGraphGenerator {

    public static void main(String[] args) {
        int numNodes = 5000; // Numero di nodi
        int numEdges = 10000; // Numero di archi
        String outputFile = "5000,10000.csv"; // Nome del file di output

        try {
            generateConnectedGraph(numNodes, numEdges, outputFile);
            System.out.println("Grafo generato e salvato in " + outputFile);
        } catch (IOException e) {
            System.err.println("Errore durante la generazione del grafo: " + e.getMessage());
        }
    }

    public static void generateConnectedGraph(int numNodes, int numEdges, String outputFile) throws IOException {
        if (numEdges < numNodes - 1) {
            throw new IllegalArgumentException("Il numero di archi deve essere almeno pari a numNodes - 1 per garantire la connessione.");
        }

        Random random = new Random();
        List<int[]> edges = new ArrayList<>();
        Set<String> edgeSet = new HashSet<>(); // Per evitare duplicati

        // Crea un albero minimo per garantire la connessione
        List<Integer> nodes = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) {
            nodes.add(i);
        }
        Collections.shuffle(nodes);

        for (int i = 1; i < numNodes; i++) {
            int src = nodes.get(i - 1);
            int dest = nodes.get(i);
            int weight = random.nextInt(100) + 1;
            edges.add(new int[]{src, dest, weight});
            edgeSet.add(src + "," + dest);
            edgeSet.add(dest + "," + src); // Non orientato
        }

        // Aggiungi archi casuali fino a raggiungere il numero desiderato
        while (edges.size() < numEdges) {
            int src = random.nextInt(numNodes);
            int dest = random.nextInt(numNodes);
            if (src != dest && !edgeSet.contains(src + "," + dest)) {
                int weight = random.nextInt(100) + 1;
                edges.add(new int[]{src, dest, weight});
                edgeSet.add(src + "," + dest);
                edgeSet.add(dest + "," + src); // Non orientato
            }
        }

        // Scrivi gli archi nel file CSV
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (int[] edge : edges) {
                writer.write(edge[0] + "," + edge[1] + "," + edge[2]);
                writer.newLine();
            }
        }
    }
}
