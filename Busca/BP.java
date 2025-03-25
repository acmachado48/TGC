import java.io.*;
import java.util.*;

public class BP {

    private final int n; // Número de vértices
    private final List<List<Integer>> adj; // Lista de adjacências
    private final int[] TD; // Tempo de descoberta
    private final int[] TT; // Tempo de término
    private final int[] pai; // Pai de cada vértice
    private int tempo; // Contador global de tempo
    private final List<String> arestasArvore; // Lista das arestas de árvore

    // Construtor
    public BP(String arquivo) throws IOException {
        adj = new ArrayList<>();
        arestasArvore = new ArrayList<>();
        tempo = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha = br.readLine();
            if (linha == null || linha.trim().isEmpty()) {
                throw new IOException("Arquivo vazio ou formato incorreto.");
            }

            String[] linhaInicial = linha.trim().split("\\s+");
            if (linhaInicial.length < 2) {
                throw new IOException("Cabeçalho do arquivo inválido.");
            }

            n = Integer.parseInt(linhaInicial[0]); // Número de vértices
            int m = Integer.parseInt(linhaInicial[1]); // Número de arestas

            // Inicializa lista de adjacências
            for (int i = 0; i <= n; i++) {
                adj.add(new ArrayList<>());
            }

            // Lê e armazena as arestas
            for (int i = 0; i < m; i++) {
                linha = br.readLine();
                if (linha == null || linha.trim().isEmpty()) {
                    throw new IOException("Número de arestas inconsistente.");
                }

                String[] valores = linha.trim().split("\\s+");
                if (valores.length < 2) {
                    throw new IOException("Linha de aresta mal formatada.");
                }

                int origem = Integer.parseInt(valores[0]);
                int destino = Integer.parseInt(valores[1]);
                adj.get(origem).add(destino);
            }

            // Ordena a lista 
            for (List<Integer> lista : adj) {
                Collections.sort(lista);
            }
        }

        // Inicializa os vetores
        TD = new int[n + 1];
        TT = new int[n + 1];
        pai = new int[n + 1];
        Arrays.fill(TD, 0);
        Arrays.fill(TT, 0);
        Arrays.fill(pai, -1);

        // Executa DFS para todos os vértices ainda não visitados
        for (int v = 1; v <= n; v++) {
            if (TD[v] == 0) {
                dfs(v);
            }
        }
    }

    private void dfs(int v) {
        TD[v] = ++tempo; // Define tempo de descoberta

        for (int w : adj.get(v)) {
            if (TD[w] == 0) { // Aresta de árvore (v -> w)
                pai[w] = v;
                arestasArvore.add(v + " -> " + w);
                dfs(w);
            }
        }

        TT[v] = ++tempo; // Define tempo de término
    }

    public void imprimirInformacoesVertice(int v) {
        if (v < 1 || v > n) {
            System.out.println("Vértice inválido.");
            return;
        }

        System.out.println("Arestas de árvore encontradas:");
        for (String aresta : arestasArvore) {
            System.out.println(aresta);
        }

        System.out.println("\nClassificação das arestas que saem do vértice " + v + ":");
        for (int w : adj.get(v)) {
            if (pai[w] == v) {
                System.out.println(v + " -> " + w + " é uma Aresta de Árvore");
            } else if (TD[w] > TD[v] && TT[w] == 0) {
                System.out.println(v + " -> " + w + " é uma Aresta de Avanço");
            } else if (TD[w] < TD[v] && TT[w] == 0) {
                System.out.println(v + " -> " + w + " é uma Aresta de Retorno");
            } else {
                System.out.println(v + " -> " + w + " é uma Aresta de Cruzamento");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        //String nomeArquivo = "graph-test-100.txt"; // Nome do arquivo 
        try {
            System.out.print("Digite o nome do arquivo: ");
            String nomeArquivo = sc.nextLine();
            BP grafo = new BP(nomeArquivo);

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
