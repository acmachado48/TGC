import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class GeradorGrafosEuler {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        // Exibindo o menu de opções
        System.out.println("Escolha o tipo de grafo:");
        System.out.println("1. Grafo Euleriano");
        System.out.println("2. Grafo Semi-Euleriano");
        System.out.println("3. Grafo Não-Euleriano");
        System.out.print("Digite sua escolha (1, 2 ou 3): ");
        int escolha = scanner.nextInt();

        // Definindo os números de vértices para cada tipo de grafo
        int[] vertices = {100, 1000, 10000, 100000};

        // Gerando o grafo para cada quantidade de vértices
        for (int v : vertices) {
            gerarGrafo(escolha, v);
        }
        scanner.close();
    }

    // Função para gerar o grafo baseado na escolha
    public static void gerarGrafo(int escolha, int numVertices) throws IOException {
        Random random = new Random();
        
        // Garantindo que o grafo seja conexo:
        // - Mínimo de arestas: numVertices - 1 (árvore geradora)
        // - Máximo de arestas: 3 milhões ou n*(n-1)/2, o que for menor
        int minArestas = numVertices - 1;
        int maxArestasPossivel = (numVertices * (numVertices - 1)) / 2;
        int maxArestas = Math.min(3000000, maxArestasPossivel);
        
        // Gerando um número aleatório de arestas entre o mínimo e o máximo
        int numArestas = minArestas + random.nextInt(maxArestas - minArestas + 1);

        // Definindo o nome do arquivo de saída com base no tipo de grafo escolhido
        String tipoGrafo;
        switch (escolha) {
            case 1:
                tipoGrafo = "euleriano";
                break;
            case 2:
                tipoGrafo = "semi-euleriano";
                break;
            case 3:
                tipoGrafo = "nao-euleriano";
                break;
            default:
                tipoGrafo = "desconhecido";
                break;
        }

        // Criando o arquivo de saída no diretório raiz
        String nomeArquivo = "graph-" + numVertices + "-" + tipoGrafo + ".txt";
        File arquivo = new File(nomeArquivo);
        
        // Verificar se o arquivo já existe e deletar se necessário
        if (arquivo.exists()) {
            arquivo.delete();
        }
        
        // Criar o arquivo
        if (!arquivo.createNewFile()) {
            System.out.println("Erro ao criar o arquivo: " + nomeArquivo);
            return;
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo));

        // Escrevendo o cabeçalho com o número de vértices e arestas
        writer.write(numVertices + "  " + numArestas + "\n");

        // Gerar o grafo conforme a opção escolhida
        switch (escolha) {
            case 1: // Grafo Euleriano
                gerarGrafoEuleriano(writer, numVertices, numArestas);
                break;
            case 2: // Grafo Semi-Euleriano
                gerarGrafoSemiEuleriano(writer, numVertices, numArestas);
                break;
            case 3: // Grafo Não-Euleriano
                gerarGrafoNaoEuleriano(writer, numVertices, numArestas);
                break;
            default:
                System.out.println("Escolha inválida!");
                writer.close();
                return;
        }

        writer.close();
        
        // Verificar se o arquivo foi criado com sucesso
        if (arquivo.exists() && arquivo.length() > 0) {
            System.out.println("Arquivo gerado com sucesso: " + nomeArquivo);
            System.out.println("Caminho absoluto: " + arquivo.getAbsolutePath());
        } else {
            System.out.println("Erro: O arquivo não foi criado corretamente.");
        }
    }

    // Função para gerar um grafo Euleriano (todos os vértices com grau par)
    public static void gerarGrafoEuleriano(BufferedWriter writer, int numVertices, int numArestas) throws IOException {
        Random random = new Random();
        List<List<Integer>> grafo = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            grafo.add(new ArrayList<>());
        }

        // Cria uma árvore geradora mínima para garantir conectividade
        for (int i = 1; i < numVertices; i++) {
            int pai = random.nextInt(i);
            grafo.get(pai).add(i);
            grafo.get(i).add(pai);
            writer.write((pai + 1) + "      " + (i + 1) + "\n");
        }

        // Adiciona arestas extras (sem preocupação com a paridade, pois posteriormente o grafo pode ser ajustado)
        int arestasRestantes = numArestas - (numVertices - 1);
        while (arestasRestantes > 0) {
            int origem = random.nextInt(numVertices);
            int destino = random.nextInt(numVertices);
            
            if (origem != destino && !grafo.get(origem).contains(destino)) {
                grafo.get(origem).add(destino);
                grafo.get(destino).add(origem);
                writer.write((origem + 1) + "      " + (destino + 1) + "\n");
                arestasRestantes--;
            }
        }
    }

    // Função para gerar um grafo Semi-Euleriano (exatamente dois vértices com grau ímpar)
    public static void gerarGrafoSemiEuleriano(BufferedWriter writer, int numVertices, int numArestas) throws IOException {
        Random random = new Random();
        List<List<Integer>> grafo = new ArrayList<>();
        int[] degrees = new int[numVertices];
        
        // Inicializa a lista de adjacências e o vetor de graus
        for (int i = 0; i < numVertices; i++) {
            grafo.add(new ArrayList<>());
            degrees[i] = 0;
        }

        // Gera uma permutação aleatória dos vértices para formar um caminho Hamiltoniano
        List<Integer> permutation = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            permutation.add(i);
        }
        // Embaralha a permutação
        for (int i = numVertices - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = permutation.get(i);
            permutation.set(i, permutation.get(j));
            permutation.set(j, temp);
        }
        
        // Cria o caminho Hamiltoniano que garante conectividade
        // Esse caminho terá exatamente dois vértices com grau ímpar: os extremos
        for (int i = 0; i < numVertices - 1; i++) {
            int u = permutation.get(i);
            int v = permutation.get(i + 1);
            grafo.get(u).add(v);
            grafo.get(v).add(u);
            degrees[u]++;
            degrees[v]++;
            writer.write((u + 1) + "      " + (v + 1) + "\n");
        }
        
        // Adiciona arestas extras mantendo a condição semi-euleriana:
        // Só adiciona arestas que conectem um vértice com grau par a um vértice com grau ímpar,
        // de forma que a operação troque a paridade de ambos e mantenha o total de vértices ímpares inalterado.
        int extraEdges = numArestas - (numVertices - 1);
        while (extraEdges > 0) {
            int u = random.nextInt(numVertices);
            int v = random.nextInt(numVertices);
            if (u == v) continue;
            if (grafo.get(u).contains(v)) continue;
            // Verifica se um vértice é par e o outro ímpar
            if ((degrees[u] % 2) == (degrees[v] % 2)) continue;
            // Adiciona a aresta
            grafo.get(u).add(v);
            grafo.get(v).add(u);
            degrees[u]++;
            degrees[v]++;
            writer.write((u + 1) + "      " + (v + 1) + "\n");
            extraEdges--;
        }
        
        // Verifica se o grafo possui exatamente dois vértices com grau ímpar
        int oddCount = 0;
        for (int i = 0; i < numVertices; i++) {
            if (degrees[i] % 2 != 0) {
                oddCount++;
            }
        }
        if (oddCount != 2) {
            System.out.println("Aviso: O grafo semi-euleriano gerado não possui exatamente dois vértices com grau ímpar!");
            System.out.println("Número de vértices com grau ímpar: " + oddCount);
        }
    }

    // Função para gerar um grafo Não-Euleriano (que não satisfaz as condições de Euler ou Semi-Euler)
    public static void gerarGrafoNaoEuleriano(BufferedWriter writer, int numVertices, int numArestas) throws IOException {
        Random random = new Random();
        List<List<Integer>> grafo = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            grafo.add(new ArrayList<>());
        }

        // Cria uma árvore geradora mínima para garantir conectividade
        for (int i = 1; i < numVertices; i++) {
            int pai = random.nextInt(i);
            grafo.get(pai).add(i);
            grafo.get(i).add(pai);
            writer.write((pai + 1) + "      " + (i + 1) + "\n");
        }

        // Adiciona arestas extras sem se preocupar com a paridade
        int arestasRestantes = numArestas - (numVertices - 1);
        while (arestasRestantes > 0) {
            int origem = random.nextInt(numVertices);
            int destino = random.nextInt(numVertices);
            
            if (origem != destino && !grafo.get(origem).contains(destino)) {
                grafo.get(origem).add(destino);
                grafo.get(destino).add(origem);
                writer.write((origem + 1) + "      " + (destino + 1) + "\n");
                arestasRestantes--;
            }
        }
    }
}
