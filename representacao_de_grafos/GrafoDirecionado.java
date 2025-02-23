import java.io.*;
import java.util.*;

public class GrafoDirecionado {
    private int n; // Número de vértices
    private List<List<Integer>> listaSucessores;
    private List<List<Integer>> listaPredecessores;

    public GrafoDirecionado(String arquivo) throws IOException {
        carregarGrafo(arquivo);
    }

    private void carregarGrafo(String arquivo) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();
            if (linha == null || linha.trim().isEmpty()) { //cabecalho
                throw new IOException("Arquivo vazio ou formato incorreto.");
            }

            String[] linhaInicial = linha.trim().split("\\s+");
            if (linhaInicial.length < 2) {
                throw new IOException("Cabeçalho do arquivo inválido. Esperado: 'n m'");
            }

            n = Integer.parseInt(linhaInicial[0]); // N de vértices
            int m = Integer.parseInt(linhaInicial[1]); // N de arestas

            // Inicializando listas de adjacência
            listaSucessores = new ArrayList<>(n + 1);
            listaPredecessores = new ArrayList<>(n + 1);
            for (int i = 0; i <= n; i++) {
                listaSucessores.add(new ArrayList<>());
                listaPredecessores.add(new ArrayList<>());
            }

            // Lendo as arestas
            for (int i = 0; i < m; i++) {
                linha = br.readLine();
                if (linha == null || linha.trim().isEmpty()) {
                    throw new IOException("Número de arestas inconsistente com o cabeçalho.");
                }

                String[] valores = linha.trim().split("\\s+");
                if (valores.length < 2) {
                    throw new IOException("Linha de aresta mal formatada. Esperado: 'origem destino'");
                }

                int origem = Integer.parseInt(valores[0]);
                int destino = Integer.parseInt(valores[1]);

                listaSucessores.get(origem).add(destino);
                listaPredecessores.get(destino).add(origem);
            }
        }
    }

    public void imprimirInformacoesVertice(int v) {
        if (v < 1 || v > n) {
            System.out.println("Vértice inválido.");
            return;
        }

        int grauSaida = listaSucessores.get(v).size();
        int grauEntrada = listaPredecessores.get(v).size();
        List<Integer> sucessores = listaSucessores.get(v);
        List<Integer> predecessores = listaPredecessores.get(v);

        System.out.println("Grau de saída: " + grauSaida);
        System.out.println("Grau de entrada: " + grauEntrada);
        System.out.println("Sucessores: " + sucessores);
        System.out.println("Predecessores: " + predecessores);
    }

    public static void main(String[] args) {
        String nomeArquivo = "graph-test-100.txt"; // Nome do arquivo 
        try {
            GrafoDirecionado grafo = new GrafoDirecionado(nomeArquivo);
            
            Scanner sc = new Scanner(System.in);
            System.out.print("Digite o número do vértice: ");
            int vertice = sc.nextInt();
            sc.close();

            grafo.imprimirInformacoesVertice(vertice);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número inteiro.");
        }

    }
}
