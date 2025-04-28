import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GrafoGerador {

    public static void gerarGrafoAleatorio(String filename, int numVertices, int numArestas) throws IOException {
        Random rand = new Random();
        FileWriter fw = new FileWriter(filename);

        fw.write(numVertices + " " + numArestas + "\n");

        for (int i = 0; i < numArestas; i++) {
            int u = rand.nextInt(numVertices);
            int v = rand.nextInt(numVertices);
            while (v == u) {
                v = rand.nextInt(numVertices);
            }
            int w = rand.nextInt(10) + 1;
            fw.write(u + " " + v + " " + w + "\n");
        }
        fw.close();
    }

    public static void gerarGrafoGrade(String filename, int linhas, int colunas) throws IOException {
        int numVertices = linhas * colunas;
        FileWriter fw = new FileWriter(filename);
        int numArestas = 0;

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int u = i * colunas + j;
                if (j + 1 < colunas) numArestas++;
                if (i + 1 < linhas) numArestas++;
            }
        }

        fw.write(numVertices + " " + numArestas + "\n");

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                int u = i * colunas + j;
                if (j + 1 < colunas) {
                    int v = u + 1;
                    int w = 1;
                    fw.write(u + " " + v + " " + w + "\n");
                }
                if (i + 1 < linhas) {
                    int v = u + colunas;
                    int w = 1;
                    fw.write(u + " " + v + " " + w + "\n");
                }
            }
        }
        fw.close();
    }
}
