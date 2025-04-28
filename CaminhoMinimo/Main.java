import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String[] grafos = {
            "grafo_aleatorio_100.txt",
            "grafo_aleatorio_400.txt",
            "grafo_aleatorio_700.txt",
            "grafo_aleatorio_1000.txt",
            "grafo_grade_10x10.txt",
            "grafo_grade_20x20.txt",
            "grafo_grade_30x30.txt",
            "grafo_grade_40x40.txt"
        };

        int[] vertices = {100, 400, 700, 1000, 100, 400, 900, 1600};
        int[] arestas = {500, 2000, 3500, 5000, 180, 760, 1740, 3120};

        // Gerar grafos
        GrafoGerador.gerarGrafoAleatorio("grafo_aleatorio_100.txt", 100, 500);
        GrafoGerador.gerarGrafoAleatorio("grafo_aleatorio_400.txt", 400, 2000);
        GrafoGerador.gerarGrafoAleatorio("grafo_aleatorio_700.txt", 700, 3500);
        GrafoGerador.gerarGrafoAleatorio("grafo_aleatorio_1000.txt", 1000, 5000);

        GrafoGerador.gerarGrafoGrade("grafo_grade_10x10.txt", 10, 10);
        GrafoGerador.gerarGrafoGrade("grafo_grade_20x20.txt", 20, 20);
        GrafoGerador.gerarGrafoGrade("grafo_grade_30x30.txt", 30, 30);
        GrafoGerador.gerarGrafoGrade("grafo_grade_40x40.txt", 40, 40);

        FileWriter fw = new FileWriter("resultados.csv");
        fw.write("Grafo,Vertices,Arestas,Tempo(ms)\n");

        for (int i = 0; i < grafos.length; i++) {
            long inicio = System.nanoTime();
            CaminhoMinimo.encontrarCaminhoMinimo(grafos[i], 0, vertices[i] - 1);
            long fim = System.nanoTime();
            double tempoMs = (fim - inicio) / 1e6;

            System.out.printf("Grafo: %s - Tempo: %.4f ms\n", grafos[i], tempoMs);
            fw.write(grafos[i] + "," + vertices[i] + "," + arestas[i] + "," + String.format("%.4f", tempoMs) + "\n");
        }

        fw.close();
        System.out.println("Resultados salvos em resultados.csv");
    }
}
