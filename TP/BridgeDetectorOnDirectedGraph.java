import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@SuppressWarnings("unused")
public class BridgeDetectorOnDirectedGraph {
    /**
     * Classe interna que representa um grafo e suas operações.
     * Implementa estruturas de dados e algoritmos para manipulação do grafo.
     */
    static class Grafo {
        private int qtdVertices;                    // Número total de vértices no grafo
        private List<Integer>[] adj;               // Lista de adjacência para representar o grafo
        private int[] descoberta;                  // Tempo de descoberta de cada vértice
        private int[] termino;                     // Tempo de término de cada vértice
        private int tempo;                         // Contador global de tempo para DFS
        private int[] pai;                         // Array para armazenar o pai de cada vértice
        private Set<String> arestasArvore;         // Conjunto de arestas que formam a árvore DFS
        private List<Edge> bridges;                // Lista de pontes encontradas no grafo
        private Set<String> pontesTarjan;          // Conjunto de pontes encontradas pelo algoritmo de Tarjan

        /**
         * Classe interna para representar uma aresta do grafo.
         * Armazena os vértices de origem e destino da aresta.
         */
        static class Edge {
            int from, to;
            Edge(int from, int to) {
                this.from = from;
                this.to = to;
            }
            @Override
            public String toString() {
                return from + " -> " + to;
            }
        }

        @SuppressWarnings("unchecked")
        public Grafo(int qtdVertices) {
            this.qtdVertices = qtdVertices;
            adj = new ArrayList[qtdVertices + 1];
            descoberta = new int[qtdVertices + 1];
            termino = new int[qtdVertices + 1];
            pai = new int[qtdVertices + 1];
            arestasArvore = new HashSet<>();
            bridges = new ArrayList<>();
            pontesTarjan = new HashSet<>();
            for (int i = 1; i <= qtdVertices; i++) {
                adj[i] = new ArrayList<>();
            }
        }

        public void adicionarAresta(int origem, int destino) {
            adj[origem].add(destino);
        }

        /**
         * Implementação do algoritmo de Tarjan para encontrar pontes no grafo direcionado.
         * Complexidade: O(V + E)
         * @return Lista de pontes encontradas
         */ 
        public List<Edge> encontrarPontesTarjan() {
            bridges.clear();
            pontesTarjan.clear();
            boolean[] visitado = new boolean[qtdVertices + 1];
            int[] low = new int[qtdVertices + 1];
            tempo = 0;
            
            for (int i = 1; i <= qtdVertices; i++) {
                if (!visitado[i]) {
                    tarjanDFS(i, -1, visitado, low);
                }
            }
            
            return bridges;
        }

        /**
         * Método auxiliar do algoritmo de Tarjan que realiza a DFS e identifica pontes.
         * @param v Vértice atual
         * @param parent Vértice pai na árvore DFS
         * @param visitado Array de vértices visitados
         * @param low Array de valores low para cada vértice
         */
        private void tarjanDFS(int v, int parent, boolean[] visitado, int[] low) {
            visitado[v] = true;
            descoberta[v] = low[v] = ++tempo;

            for (int u : adj[v]) {
                if (!visitado[u]) {
                    pai[u] = v;
                    tarjanDFS(u, v, visitado, low);
                    low[v] = Math.min(low[v], low[u]);
                    
                    // Se não existe caminho alternativo para u, então v-u é uma ponte
                    if (low[u] > descoberta[v]) {
                        Edge ponte = new Edge(v, u);
                        bridges.add(ponte);
                        pontesTarjan.add(v + "->" + u);
                    }
                } else if (u != parent && descoberta[u] < descoberta[v]) {
                    low[v] = Math.min(low[v], descoberta[u]);
                }
            }
        }

        /**
         * Implementação de um método para encontrar pontes em grafo direcionado.
         * Remove cada aresta e verifica se o grafo continua fortemente conexo.
         * Complexidade: O(E * (V + E))
         * @return Lista de pontes encontradas
         */
        public List<Edge> encontrarPontesRemovendoArestas() {
            bridges.clear();
            List<Edge> todasArestas = new ArrayList<>();
            
            // Coleta todas as arestas do grafo
            for (int i = 1; i <= qtdVertices; i++) {
                for (int j : adj[i]) {
                    todasArestas.add(new Edge(i, j));
                }
            }

            // Para cada aresta, remove-a temporariamente e verifica conectividade
            for (Edge aresta : todasArestas) {
                adj[aresta.from].remove(Integer.valueOf(aresta.to));
                
                if (!isFortementeConectado()) {
                    bridges.add(aresta);
                }
                
                // Restaura a aresta para a próxima iteração
                adj[aresta.from].add(aresta.to);
            }

            return bridges;
        }

        /**
         * Verifica se o grafo está fortemente conexo usando DFS.
         * @return true se o grafo está fortemente conexo, false caso contrário
         */
        private boolean isFortementeConectado() {
            boolean[] visitado = new boolean[qtdVertices + 1];
            
            // Primeira DFS a partir do vértice 1
            dfsConectividade(1, visitado);
            
            // Verifica se todos os vértices foram visitados
            for (int i = 1; i <= qtdVertices; i++) {
                if (!visitado[i]) {
                    return false;
                }
            }
            
            // Inverte o grafo
            Grafo grafoInvertido = inverterGrafo();
            
            // Segunda DFS no grafo invertido
            Arrays.fill(visitado, false);
            grafoInvertido.dfsConectividade(1, visitado);
            
            // Verifica se todos os vértices foram visitados no grafo invertido
            for (int i = 1; i <= qtdVertices; i++) {
                if (!visitado[i]) {
                    return false;
                }
            }
            
            return true;
        }

        /**
         * Inverte todas as arestas do grafo.
         * @return Novo grafo com as arestas invertidas
         */
        private Grafo inverterGrafo() {
            Grafo invertido = new Grafo(qtdVertices);
            for (int i = 1; i <= qtdVertices; i++) {
                for (int j : adj[i]) {
                    invertido.adicionarAresta(j, i);
                }
            }
            return invertido;
        }

        /**
         * Método auxiliar que realiza DFS para verificar conectividade.
         * @param v Vértice atual
         * @param visitado Array de vértices visitados
         */
        private void dfsConectividade(int v, boolean[] visitado) {
            visitado[v] = true;
            for (int u : adj[v]) {
                if (!visitado[u]) {
                    dfsConectividade(u, visitado);
                }
            }
        }

        /**
         * Implementação do algoritmo de Fleury para encontrar caminho Euleriano.
         * @return Lista de vértices que formam o caminho Euleriano, ou null se não existir
         * Complexidade: O(V + E)
         */
        public List<Integer> encontrarCaminhoEuleriano() {
            if (!temCaminhoEuleriano()) {
                return null;
            }

            List<Integer> caminho = new ArrayList<>();
            Grafo grafoTemp = clonarGrafo();
            
            // Encontra as pontes usando Tarjan antes de começar
            grafoTemp.encontrarPontesTarjan();
            
            int inicio = encontrarVerticeInicial();
            fleuryDFS(inicio, grafoTemp, caminho);
            
            return caminho;
        }

        /**
         * Verifica se o grafo possui um caminho Euleriano.
         * Um grafo direcionado tem caminho Euleriano se:
         * 1. O grafo é fortemente conexo
         * 2. Para todos os vértices, grau de entrada = grau de saída, ou
         * 3. Exatamente um vértice tem grau de saída = grau de entrada + 1 e
         *    exatamente um vértice tem grau de entrada = grau de saída + 1
         * @return true se existe caminho Euleriano, false caso contrário
         * Complexidade: O(V)
         */
        private boolean temCaminhoEuleriano() {
            if (!isFortementeConectado()) {
                return false;
            }

            int[] grauEntrada = new int[qtdVertices + 1];
            int[] grauSaida = new int[qtdVertices + 1];
            
            // Calcula graus de entrada e saída
            for (int i = 1; i <= qtdVertices; i++) {
                grauSaida[i] = adj[i].size();
                for (int j : adj[i]) {
                    grauEntrada[j]++;
                }
            }
            
            int verticesDiferenca1 = 0;
            int verticesDiferencaMenos1 = 0;
            
            for (int i = 1; i <= qtdVertices; i++) {
                int diferenca = grauSaida[i] - grauEntrada[i];
                if (diferenca == 1) {
                    verticesDiferenca1++;
                } else if (diferenca == -1) {
                    verticesDiferencaMenos1++;
                } else if (diferenca != 0) {
                    return false;
                }
            }
            
            return (verticesDiferenca1 == 0 && verticesDiferencaMenos1 == 0) ||
                   (verticesDiferenca1 == 1 && verticesDiferencaMenos1 == 1);
        }

        /**
         * Encontra o vértice inicial para o caminho Euleriano.
         * @return Vértice com grau de saída maior que grau de entrada, ou primeiro vértice
         * Complexidade: O(V)
         */
        private int encontrarVerticeInicial() {
            int[] grauEntrada = new int[qtdVertices + 1];
            int[] grauSaida = new int[qtdVertices + 1];
            
            for (int i = 1; i <= qtdVertices; i++) {
                grauSaida[i] = adj[i].size();
                for (int j : adj[i]) {
                    grauEntrada[j]++;
                }
            }
            
            for (int i = 1; i <= qtdVertices; i++) {
                if (grauSaida[i] > grauEntrada[i]) {
                    return i;
                }
            }
            
            return 1;
        }

        /**
         * Implementação do DFS modificado para o algoritmo de Fleury.
         * Evita atravessar pontes a menos que não haja alternativa.
         * @param v Vértice atual
         * @param grafo Grafo temporário para manipulação
         * @param caminho Lista que armazena o caminho Euleriano
         * Complexidade: O(V + E)
         */
        private void fleuryDFS(int v, Grafo grafo, List<Integer> caminho) {
            while (!grafo.adj[v].isEmpty()) {
                int u = grafo.adj[v].get(0);
                String aresta = v + "->" + u;
                
                // Remove a aresta temporariamente
                grafo.adj[v].remove(Integer.valueOf(u));
                
                // Verifica se a aresta é uma ponte
                boolean isPonteTarjan = grafo.pontesTarjan.contains(aresta);
                boolean isPonteIngenua = !grafo.isFortementeConectado();
                
                // Se é uma ponte em ambos os métodos, tenta outra aresta
                if (isPonteTarjan && isPonteIngenua) {
                    grafo.adj[v].add(u);
                    break;
                }
                
                // Se não é ponte em pelo menos um método, continua o caminho
                fleuryDFS(u, grafo, caminho);
            }
            caminho.add(0, v);
        }

        /**
         * Cria uma cópia do grafo atual.
         * @return Novo grafo com as mesmas arestas do grafo original
         */
        private Grafo clonarGrafo() {
            Grafo clone = new Grafo(qtdVertices);
            for (int i = 1; i <= qtdVertices; i++) {
                clone.adj[i] = new ArrayList<>(adj[i]);
            }
            return clone;
        }
    }

    /**
     * Método principal que lê o grafo de um arquivo e executa os algoritmos.
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Informe o nome do arquivo: ");
        String arquivo = sc.nextLine();

        try {
            // Lê o arquivo e constrói o grafo
            Scanner scanner = new Scanner(new File(arquivo));
            int qtdVertices = scanner.nextInt();
            int qtdArestas = scanner.nextInt();

            Grafo g = new Grafo(qtdVertices);

            for (int i = 0; i < qtdArestas; i++) {
                int origem = scanner.nextInt();
                int destino = scanner.nextInt();
                g.adicionarAresta(origem, destino);
            }

            scanner.close();

            // Executa e exibe resultados dos algoritmos
            System.out.println("\nPontes encontradas pelo algoritmo de Tarjan:");
            List<Grafo.Edge> pontesTarjan = g.encontrarPontesTarjan();
            if (pontesTarjan.isEmpty()) {
                System.out.println("Nenhuma ponte encontrada.");
            } else {
                for (Grafo.Edge ponte : pontesTarjan) {
                    System.out.println(ponte);
                }
            }

            System.out.println("\nPontes encontradas pelo método simples (removendo arestas):");
            List<Grafo.Edge> pontesRemovendoArestas = g.encontrarPontesRemovendoArestas();
            if (pontesRemovendoArestas.isEmpty()) {
                System.out.println("Nenhuma ponte encontrada.");
            } else {
                for (Grafo.Edge ponte : pontesRemovendoArestas) {
                    System.out.println(ponte);
                }
            }

            System.out.println("\nVerificação de caminho Euleriano:");
            List<Integer> caminhoEuleriano = g.encontrarCaminhoEuleriano();
            if (caminhoEuleriano != null) {
                System.out.println("Caminho Euleriano encontrado:");
                System.out.println(caminhoEuleriano);
            } else {
                System.out.println("Não existe caminho Euleriano neste grafo.");
            }

        } catch (FileNotFoundException e) {
            System.err.println("Arquivo não encontrado: " + arquivo);
        }

        sc.close();
    }
}
