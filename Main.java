import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        AlocadorBuddy alocador = new AlocadorBuddy();
        Scanner scanner = new Scanner(System.in);

        int opcao;

        do {
            System.out.println("\n===== ALOCADOR BUDDY BINARIO =====");
            System.out.println("1 - Alocar");
            System.out.println("2 - Liberar");
            System.out.println("3 - Desfazer");
            System.out.println("4 - Exibir memoria");
            System.out.println("5 - Exibir fila de pendentes");
            System.out.println("6 - Exibir listas de blocos livres");
            System.out.println("7 - Carregar dataset");
            System.out.println("8 - Sair");
            System.out.print("Escolha uma opcao: ");

            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.print("Digite o ID: ");
                    String idAlocar = scanner.nextLine();

                    System.out.print("Digite o tamanho em KB: ");
                    int tamanho = scanner.nextInt();
                    scanner.nextLine();

                    alocador.alocar(idAlocar, tamanho);
                    break;

                case 2:
                    System.out.print("Digite o ID para liberar: ");
                    String idLiberar = scanner.nextLine();

                    alocador.liberar(idLiberar);
                    break;

                case 3:
                    alocador.desfazer();
                    break;

                case 4:
                    alocador.exibirMemoria();
                    break;

                case 5:
                    alocador.exibirFila();
                    break;

                case 6:
                    alocador.exibirListasLivres();
                    break;

                case 7:
                    carregarDataset(alocador, "dataset.txt");
                    break;

                case 8:
                    System.out.println("Encerrando o programa.");
                    break;

                default:
                    System.out.println("Opcao invalida.");
            }

        } while (opcao != 8);

        scanner.close();
    }

    private static void carregarDataset(AlocadorBuddy alocador, String caminho) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();

                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                String[] partes = linha.split("\\s+");

                if (partes[0].equalsIgnoreCase("ALOCAR")) {
                    String id = partes[1];
                    int tamanho = Integer.parseInt(partes[2]);

                    System.out.println("\nOperacao: ALOCAR " + id + " " + tamanho + " KB");
                    alocador.alocar(id, tamanho);
                    alocador.exibirMemoria();

                } else if (partes[0].equalsIgnoreCase("LIBERAR")) {
                    String id = partes[1];

                    System.out.println("\nOperacao: LIBERAR " + id);
                    alocador.liberar(id);
                    alocador.exibirMemoria();
                }
            }

            System.out.println("\nDataset carregado com sucesso.");

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo dataset.txt: " + e.getMessage());
        }
    }
}