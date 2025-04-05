import java.util.*;

public class TesteDesempenho {
    public static void main(String[] args) {
        // Ajustando os tamanhos e parâmetros para otimizar o uso de memória
        int[] tamanhos = {100, 1000, 10000, 100000}; 
        int[] numExecucoes = {10, 10, 3, 1}; // Menos execuções para grafos maiores
        int[] numArestasMultiplicador = {2, 2, 1, 1}; // Menos arestas para grafos maiores
        int[] tamanhoMaximoArestas = {Integer.MAX_VALUE, Integer.MAX_VALUE, 50000, 100000}; // Limite de arestas

        for (int i = 0; i < tamanhos.length; i++) {
            int tamanho = tamanhos[i];
            int execucoes = numExecucoes[i];
            int multiplicadorArestas = numArestasMultiplicador[i];
            int maxArestas = tamanhoMaximoArestas[i];
            
            long tempoTarjanTotal = 0;
            long tempoNaiveTotal = 0;
            int execucoesCompletadas = 0;
            long memoriaMaximaUsada = 0;

            System.out.println("\nIniciando testes para grafo com " + tamanho + " vértices...");
            
            for (int exec = 0; exec < execucoes; exec++) {
                try {
                    Runtime runtime = Runtime.getRuntime();
                    long memoriaInicial = runtime.totalMemory() - runtime.freeMemory();
                    
                    // Verificar memória disponível antes de gerar o grafo
                    long memoriaDisponivel = runtime.freeMemory();
                    long memoriaNecessaria = (long)tamanho * 16; // Estimativa mais conservadora
                    
                    if (memoriaDisponivel < memoriaNecessaria) {
                        System.out.println("Aviso: Memória insuficiente para o grafo de " + tamanho + " vértices");
                        System.out.println("Memória disponível: " + (memoriaDisponivel / (1024 * 1024)) + " MB");
                        System.out.println("Memória necessária estimada: " + (memoriaNecessaria / (1024 * 1024)) + " MB");
                        break;
                    }

                    // Gera um grafo aleatório com menos arestas para grafos grandes
                    int numArestas = Math.min(tamanho * multiplicadorArestas, maxArestas);
                    BridgeDetectorOnDirectedGraph.Grafo g = gerarGrafoAleatorio(tamanho, numArestas);

                    // Mede tempo do algoritmo de Tarjan
                    long inicio = System.nanoTime();
                    g.encontrarPontesTarjan();
                    long fim = System.nanoTime();
                    tempoTarjanTotal += (fim - inicio);

                    // Mede tempo do método naïve
                    inicio = System.nanoTime();
                    g.encontrarPontesRemovendoArestas();
                    fim = System.nanoTime();
                    tempoNaiveTotal += (fim - inicio);

                    // Calcula memória usada
                    long memoriaFinal = runtime.totalMemory() - runtime.freeMemory();
                    long memoriaUsada = memoriaFinal - memoriaInicial;
                    memoriaMaximaUsada = Math.max(memoriaMaximaUsada, memoriaUsada);

                    execucoesCompletadas++;
                    
                    // Forçar garbage collection após cada execução
                    System.gc();

                } catch (OutOfMemoryError e) {
                    System.out.println("Erro de memória ao processar grafo de " + tamanho + " vértices");
                    break;
                } catch (Exception e) {
                    System.out.println("Erro ao processar grafo de " + tamanho + " vértices: " + e.getMessage());
                    break;
                }
            }

            if (execucoesCompletadas > 0) {
                System.out.println("\nResultados para grafo com " + tamanho + " vértices:");
                System.out.println("Execuções completadas: " + execucoesCompletadas);
                System.out.println("Média Tarjan: " + (tempoTarjanTotal / execucoesCompletadas / 1e6) + " ms");
                System.out.println("Média Naïve: " + (tempoNaiveTotal / execucoesCompletadas / 1e6) + " ms");
                System.out.println("Memória máxima usada: " + (memoriaMaximaUsada / (1024 * 1024)) + " MB");
            }
        }
    }

    private static BridgeDetectorOnDirectedGraph.Grafo gerarGrafoAleatorio(int vertices, int arestas) {
        BridgeDetectorOnDirectedGraph.Grafo g = new BridgeDetectorOnDirectedGraph.Grafo(vertices);
        Random rand = new Random();

        // Primeiro, criar uma árvore geradora mínima para garantir conectividade
        for (int i = 2; i <= vertices; i++) {
            int pai = rand.nextInt(i - 1) + 1;
            g.adicionarAresta(pai, i);
            g.adicionarAresta(i, pai); // Grafo não direcionado
        }

        // Depois, adicionar arestas extras até o limite
        int arestasRestantes = Math.min(arestas - (vertices - 1), vertices * (vertices - 1) / 2 - (vertices - 1));
        while (arestasRestantes > 0) {
            int origem = rand.nextInt(vertices) + 1;
            int destino = rand.nextInt(vertices) + 1;
            if (origem != destino) {
                g.adicionarAresta(origem, destino);
                g.adicionarAresta(destino, origem); // Grafo não direcionado
                arestasRestantes--;
            }
        }
        return g;
    }
}
