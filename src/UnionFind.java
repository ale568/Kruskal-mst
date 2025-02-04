public class UnionFind {
    private int[] parent; // Array che tiene traccia del "genitore" di ogni nodo
    private int[] rank;   // Array che tiene traccia della "profondità" dell'albero

    public UnionFind(int size) { // makeSet
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;  // Ogni elemento è il proprio genitore all'inizio
            rank[i] = 0;    // Ogni elemento ha rango iniziale 0
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path Compression
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);

        if (rootX != rootY) { // Se appartengono a insiemi diversi
            if (rank[rootX] > rank[rootY]) {       // Union by Rank
                parent[rootY] = rootX;
            } else if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else {                               // Stessi ranghi
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
}