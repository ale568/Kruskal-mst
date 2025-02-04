import java.util.ArrayList;

public class SortingAlgorithms {

    // Bubble Sort
    public static void bubbleSort(ArrayList<Edge> edges) {
        int n = edges.size();
        int last;
        while (n > 0) {
            last = 0;
            for (int j = 1; j < n; j++) {
                if (edges.get(j).getWeight() < edges.get(j - 1).getWeight()) {
                    Edge temp = edges.get(j);
                    edges.set(j, edges.get(j - 1));
                    edges.set(j - 1, temp);
                    last = j;
                }
            }
            n = last;
        }
    }

    // Selection Sort
    public static void selectionSort(ArrayList<Edge> edges) {
        int n = edges.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (edges.get(j).getWeight() < edges.get(minIndex).getWeight()) {
                    minIndex = j;
                }
            }
            Edge temp = edges.get(i);
            edges.set(i, edges.get(minIndex));
            edges.set(minIndex, temp);
        }
    }

    // HeapSort
    public static void heapSort(ArrayList<Edge> edges) {
        int n = edges.size();

        // Build max heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(edges, n, i);
        }

        // Estrai gli elementi dal heap
        for (int i = n - 1; i >= 0; i--) {
            Edge temp = edges.get(0);
            edges.set(0, edges.get(i));
            edges.set(i, temp);

            // Chiama heapify sullo heap ridotto
            heapify(edges, i, 0);
        }
    }

    // Max heapify
    private static void heapify(ArrayList<Edge> edges, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && edges.get(left).getWeight() > edges.get(largest).getWeight()) {
            largest = left;
        }

        if (right < n && edges.get(right).getWeight() > edges.get(largest).getWeight()) {
            largest = right;
        }

        if (largest != i) {
            Edge swap = edges.get(i);
            edges.set(i, edges.get(largest));
            edges.set(largest, swap);

            heapify(edges, n, largest);
        }
    }
}
