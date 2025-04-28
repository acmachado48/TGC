import java.io.*;
import java.util.*;

public class CaminhoMinimo {

    static class Edge {
        int to, weight;
        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    static class State implements Comparable<State> {
        int dist, edges, node;
        State(int dist, int edges, int node) {
            this.dist = dist;
            this.edges = edges;
            this.node = node;
        }
        public int compareTo(State other) {
            if (this.dist != other.dist)
                return Integer.compare(this.dist, other.dist);
            return Integer.compare(this.edges, other.edges);
        }
    }

    public static class Resultado {
        int pesoTotal;
        int numArestas;

        Resultado(int pesoTotal, int numArestas) {
            this.pesoTotal = pesoTotal;
            this.numArestas = numArestas;
        }
    }

    public static Resultado encontrarCaminhoMinimo(String filename, int source, int target) throws IOException {
        Scanner sc = new Scanner(new File(filename));
        int n = sc.nextInt();
        int m = sc.nextInt();

        List<Edge>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++)
            graph[i] = new ArrayList<>();

        for (int i = 0; i < m; i++) {
            int u = sc.nextInt();
            int v = sc.nextInt();
            int w = sc.nextInt();
            graph[u].add(new Edge(v, w));
        }
        sc.close();

        int[] dist = new int[n];
        int[] edges = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(edges, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        PriorityQueue<State> pq = new PriorityQueue<>();
        dist[source] = 0;
        edges[source] = 0;
        pq.add(new State(0, 0, source));

        while (!pq.isEmpty()) {
            State s = pq.poll();

            if (s.node == target)
                break;

            for (Edge e : graph[s.node]) {
                int newDist = s.dist + e.weight;
                int newEdges = s.edges + 1;

                if (newDist < dist[e.to] || (newDist == dist[e.to] && newEdges < edges[e.to])) {
                    dist[e.to] = newDist;
                    edges[e.to] = newEdges;
                    parent[e.to] = s.node;
                    pq.add(new State(newDist, newEdges, e.to));
                }
            }
        }

        if (dist[target] == Integer.MAX_VALUE) {
            System.out.println("Sem caminho possível.");
            return new Resultado(-1, -1);
        }

        return new Resultado(dist[target], edges[target]);
    }
}
