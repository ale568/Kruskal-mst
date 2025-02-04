public class Edge {
    private int src;
    private int end;
    private int weight;

    public Edge(int src, int end, int weight) {
        this.src = src;
        this.end = end;
        this.weight = weight;
    }

    public int getSrc() {
        return src;
    }

    public int getEnd() {
        return end;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("%d --(%d)--> %d", src, weight, end);
    }
}
