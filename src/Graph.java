import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class Graph {
    private List<Integer> nodes; // Lista dei nodi del grafo
    private List<Edge> edges; // Lista degli archi del grafo

    public Graph(int size) {
        nodes = new ArrayList<>(size); // Inizializza la lista dei nodi con la capacità specificata nello spinner
        edges = new ArrayList<>();    // Inizializza la lista degli archi
        for (int i = 0; i < size; i++) {
            nodes.add(i); // Aggiunge i nodi alla lista
        }
    }

    // Metodo per aggiungere un arco tra due nodi con un peso specificato
    public void addEdge(int src, int dest, int weight) {
        edges.add(new Edge(src, dest, weight));
    }

    // Restituisce la lista dei nodi del grafo
    public List<Integer> getNodes() {
        return nodes;
    }

    // Restituisce la lista degli archi del grafo
    public List<Edge> getEdges() {
        return edges;
    }    

    // Implementazione dell'algoritmo di Kruskal per calcolare il MST
    public ArrayList<Edge> Kruskal(Consumer<Double> progressCallback) {
        double timeStart = System.currentTimeMillis(); // Segna il tempo di inizio
        ArrayList<Edge> result = new ArrayList<>();    // Lista per memorizzare gli archi selezionati per l'MST
        ArrayList<Edge> edgesKruskal = new ArrayList<>(this.edges); // Copia la lista degli archi
    
        // Inizializza la struttura Union-Find per gestire le componenti connesse
        UnionFind uf = new UnionFind(this.nodes.size());
    
        // Ordina gli archi in base al peso utilizzando HeapSort
        SortingAlgorithms.heapSort(edgesKruskal);
        //SortingAlgorithms.selectionSort(edgesKruskal);
        //SortingAlgorithms.bubbleSort(edgesKruskal);
    
        int totalEdges = edgesKruskal.size(); // Numero totale di archi
        int processedEdges = 0;              // Contatore per gli archi elaborati
    
        // Itera attraverso gli archi ordinati per peso
        for (Edge edge : edgesKruskal) {
            int srcRoot = uf.find(edge.getSrc()); // Trova il rappresentante del nodo sorgente
            int destRoot = uf.find(edge.getEnd()); // Trova il rappresentante del nodo destinazione
    
            if (srcRoot != destRoot) { // Verifica se i nodi appartengono a insiemi diversi
                result.add(edge);     // Aggiunge l'arco all'MST
                uf.union(srcRoot, destRoot);
            }
    
            // Aggiorna il progresso
            processedEdges++;
            double progress = (double) processedEdges / totalEdges; // Calcola il progresso come percentuale
            progressCallback.accept(progress); // Passa il progresso al callback
        }
    
        double timeEnd = System.currentTimeMillis(); // Segna il tempo di fine
        double time = timeEnd - timeStart; 
        System.out.println("Eseguito in: " + time / 1000 + " secondi");
        return result; // Restituisce l'MST come lista di archi
    }
    


    // Metodo per creare un grafo da un file CSV
    public static Graph fromCSV(File file, int size) throws IOException {
        Graph graph = new Graph(size); // Crea un nuovo grafo con il numero di nodi specificato
        Set<Integer> uniqueNodes = new HashSet<>(); // Insieme per tenere traccia dei nodi connessi
    
        try (BufferedReader br = new BufferedReader(new FileReader(file))) { // Apre il file CSV in lettura
            String line;
            while ((line = br.readLine()) != null) { // Legge il file riga per riga
                String[] parts = line.split(","); // Divide la riga nei suoi componenti (sorgente, destinazione, peso)
                int src = Integer.parseInt(parts[0].trim()); // Nodo sorgente
                int dest = Integer.parseInt(parts[1].trim()); // Nodo destinazione
                int weight = Integer.parseInt(parts[2].trim()); // Peso dell'arco
    
                // Verifica se i nodi superano il numero massimo consentito
                if (src >= size || dest >= size) {
                    throw new IOException("Arco non valido: nodo " + src + " o " + dest + " supera il numero di nodi.");
                }
    
                // Aggiunge l'arco al grafo e segna i nodi come connessi
                graph.addEdge(src, dest, weight);
                uniqueNodes.add(src);
                uniqueNodes.add(dest);
            }
        }
    
        // Verifica se ci sono nodi isolati non presenti negli archi
        for (int i = 0; i < size; i++) {
            if (!uniqueNodes.contains(i)) {
                System.err.println("Nodo isolato: " + i);
            }
        }
    
        return graph; // Restituisce il grafo costruito
    }
    

    // Metodo per verificare se il grafo è connesso
    public boolean isConnected() {
        boolean[] visited = new boolean[nodes.size()]; // Array per tracciare i nodi visitati
        int startNode = nodes.get(0); // Prendi il primo nodo dalla lista
        dfs(startNode, visited); // Avvia la DFS dal primo nodo valido
        for (boolean visit : visited) {
            if (!visit) { // Se un nodo non è stato visitato, il grafo non è connesso
                return false;
            }
        }
        return true; // Grafo è connesso
    }
    

    // Metodo per eseguire la ricerca in profondità
    private void dfs(int node, boolean[] visited) {
        visited[node] = true; // Segna il nodo corrente come visitato
        
        // Itera su tutti gli archi del grafo
        for (Edge edge : edges) {
            // Visita il nodo di destinazione se il nodo corrente è sorgente e il nodo di destinazione non è ancora visitato
            if (edge.getSrc() == node && !visited[edge.getEnd()]) {
                dfs(edge.getEnd(), visited);
            }
            // Visita il nodo sorgente se il nodo corrente è destinazione e il nodo sorgente non è ancora visitato
            if (edge.getEnd() == node && !visited[edge.getSrc()]) { 
                dfs(edge.getSrc(), visited);
            }
        }
    }

    
}