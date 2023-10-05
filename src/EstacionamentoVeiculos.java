import java.io.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EstacionamentoVeiculos {
    private static final int NUM_FAIXAS = 4;
    private static final int VAGAS_POR_FAIXA = 7;
    private static final int NUM_VAGAS_ESTACIONAMENTO = 28;

    private static Scanner scanner = new Scanner(System.in);
    private static Map<String, Veiculo> veiculosEstacionados = new HashMap<>();
    private static Deque<Veiculo>[] faixasEstacionamento = new ArrayDeque[NUM_FAIXAS];
    private static Set<Integer> apartamentosOcupados = new HashSet<>();

    public static void main(String[] args) {
        inicializarEstacionamento();

        int opcao;
        do {
            exibirMenu();
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    registrarEstacionamento();
                    break;
                case 2:
                    retirarVeiculo();
                    break;
                case 3:
                    consultarVeiculosParaRetirada();
                    break;
                case 4:
                    consultarEstatisticas();
                    break;
                case 5:
                    salvarDados();
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 5);
        scanner.close();
    }

    // Inicializar o aplicativo
    private static void inicializarEstacionamento() {
        for (int i = 0; i < NUM_FAIXAS; i++) {
            faixasEstacionamento[i] = new ArrayDeque<>();
        }
        carregarDados(); // Carrega os dados salvos em um arquivo externo
    }

    // Exibir tela de menu
    private static void exibirMenu() {
        System.out.println("|============ Estacionamento ==============|");
        System.out.println("|1. Registrar Veiculo                      |");
        System.out.println("|2. Retirar Veiculo                        |");
        System.out.println("|3. Consultar Veiculos Prontos P/ Retirada |");
        System.out.println("|4. Consultar Estatisticas                 |");
        System.out.println("|5. Sair                                   |");
        System.out.println("|==========================================|");
        System.out.print("\nEscolha uma opção: ");
    }

    // Registrar estacionameto
    private static void registrarEstacionamento() {
        System.out.print("\nPlaca do veículo (XXX0000 ou XXX0X00): ");
        String placa = scanner.nextLine().toUpperCase();

        if (!validarPlaca(placa)) {
            System.out.println("\nErro: Placa inválida. A placa deve seguir o formato XXX0000 ou XXX0X00.");
            return;
        }

        if (veiculosEstacionados.containsKey(placa)) {
            System.out.println("\nErro: Já existe um veículo estacionado com esta placa.");
            return;
        }

        System.out.print("\nCor predominante: ");
        String cor = scanner.nextLine();

        System.out.print("\nNúmero do apartamento (101-128): ");
        int numeroApartamento = scanner.nextInt();
        scanner.nextLine();

        if (numeroApartamento < 101 || numeroApartamento > 128) {
            System.out.println("\nErro: Número de apartamento inválido.");
            return;
        }

        if (apartamentosOcupados.contains(numeroApartamento)) {
            System.out.println("\nErro: O apartamento já está ocupado por outro veículo.");
            return;
        }

        Veiculo veiculo = new Veiculo(placa, cor, numeroApartamento, LocalTime.now());
        int faixa = escolherFaixaEstacionamento();
        if (faixa == -1) {
            System.out.println("Erro: Não há vagas disponíveis para estacionar.");
            return;
        }
        Deque<Veiculo> faixaEstacionamento = faixasEstacionamento[faixa];
        faixaEstacionamento.push(veiculo);
        veiculosEstacionados.put(placa, veiculo);
        apartamentosOcupados.add(numeroApartamento);
        System.out.print("\nVeículo estacionado com sucesso na faixa " + (faixa + 1));
    }

    private static int escolherFaixaEstacionamento() {
        int menorTamanho = Integer.MAX_VALUE;
        List<Integer> faixasMenorTamanho = new ArrayList<>();

        for (int i = 0; i < NUM_FAIXAS; i++) {
            Deque<Veiculo> faixaEstacionamento = faixasEstacionamento[i];
            int tamanho = faixaEstacionamento.size();
            if (tamanho < VAGAS_POR_FAIXA && tamanho < menorTamanho) {
                menorTamanho = tamanho;
                faixasMenorTamanho.clear();
                faixasMenorTamanho.add(i);
            } else if (tamanho == menorTamanho) {
                faixasMenorTamanho.add(i);
            }
        }

        if (faixasMenorTamanho.size() > 0) {
            if (faixasMenorTamanho.size() == 1) {
                // Apenas uma faixa disponível
                System.out.println("\nAlocando automaticamente na faixa: " + (faixasMenorTamanho.get(0) + 1));
                return faixasMenorTamanho.get(0);
            } else {
                // Mais de uma faixa com o menor tamanho
                List<Integer> faixasMenorTamanhoIncrementadas = new ArrayList<>();
                for (int faixa : faixasMenorTamanho) {
                    faixasMenorTamanhoIncrementadas.add(faixa + 1);
                }
                System.out.println("\nFaixas disponíveis para se estacionar: " + faixasMenorTamanhoIncrementadas);

                System.out.print("\nEscolha a faixa desejada para estacionar: ");
                int faixaEscolhida = scanner.nextInt();
                faixaEscolhida--;

                while (!faixasMenorTamanho.contains(faixaEscolhida)) {
                    System.out.print("Faixa inválida. Escolha uma das faixas disponíveis: ");
                    faixaEscolhida = scanner.nextInt();
                    faixaEscolhida--;
                }

                return faixaEscolhida;
            }
        } else {
            // Nenhuma faixa disponível
            return -1;
        }
    }

    // Remover veículo estacionado
    private static void removerVeiculoEstacionamento(int numeroFaixa) {
        if (numeroFaixa >= 0 && numeroFaixa < NUM_FAIXAS) {
            Deque<Veiculo> faixaEstacionamento = faixasEstacionamento[numeroFaixa];
            Veiculo veiculo = faixaEstacionamento.peek();

            if (veiculo != null) {
                faixaEstacionamento.remove();
                System.out.println("\nVeículo retirado da faixa " + (numeroFaixa + 1) + " do estacionamento.");
            } else {
                System.out.println("\nNão há veículos na faixa " + (numeroFaixa + 1) + ".");
            }
        } else {
            System.out.println("\nFaixa inválida.");
        }
    }

    private static void retirarVeiculo() {
        System.out.print("\nPlaca do veículo: ");
        String placa = scanner.nextLine().toUpperCase();

        if (!validarPlaca(placa)) {
            System.out.println("\nErro: Placa inválida. A placa deve seguir o formato XXX0000 ou XXX0X00.");
            return;
        }

        Veiculo veiculo = veiculosEstacionados.get(placa);
        if (veiculo == null) {
            System.out.println("\nErro: Não existe um veículo estacionado com essa placa.");
            return;
        }

        System.out.println("\nInformações do veículo:");
        System.out.println("Placa: " + veiculo.getPlaca());
        System.out.println("Cor: " + veiculo.getCor());
        System.out.println("Número do apartamento: " + veiculo.getNumeroApartamento());
        System.out.println("Hora de entrada: " + veiculo.getHoraEntrada());
        System.out.println("Tempo de estacionamento: " + calcularTempoEstacionamento(veiculo.getHoraEntrada()));

        System.out.print("\nTem certeza que deseja retirar o veículo? (s/n): ");
        String confirmacao = scanner.nextLine();
        if (confirmacao.equalsIgnoreCase("s")) {
            veiculosEstacionados.remove(placa);
            apartamentosOcupados.remove(veiculo.getNumeroApartamento());
            int faixaEstacionamento = -1;

            for (int i = 0; i < NUM_FAIXAS; i++) {
                if (faixasEstacionamento[i].contains(veiculo)) {
                    faixaEstacionamento = i;
                    break;
                }
            }

            if (faixaEstacionamento != -1) {
                Deque<Veiculo> faixa = faixasEstacionamento[faixaEstacionamento];
                Veiculo veiculoTopo = faixa.peek();

                if (veiculo.equals(veiculoTopo)) {
                    removerVeiculoEstacionamento(faixaEstacionamento);
                } else {
                    System.out
                            .println("\nErro: O veículo não pode sair da faixa " + (faixaEstacionamento + 1)
                                    + ". Ele está bloquedao por outros veículos.");
                }
            } else {
                System.out.println("\nErro: O veículo não está estacionado em nenhuma faixa.");
            }
        } else {
            System.out.println("\nRetirada de veículo cancelada.");
        }
    }

    // Consulta de veículos que podem ser retirados
    private static void consultarVeiculosParaRetirada() {
        System.out.println("\nVeiculos estacionados prontos para retirada:");

        boolean veiculosEncontrados = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int i = 0; i < NUM_FAIXAS; i++) {
            Deque<Veiculo> faixa = faixasEstacionamento[i];
            if (!faixa.isEmpty()) {
                Veiculo veiculo = faixa.peek();

                System.out.println("\nFaixa " + (i + 1) + ":");
                System.out.println("Placa: " + veiculo.getPlaca());
                System.out.println("Cor: " + veiculo.getCor());
                System.out.println("Número do apartamento: " + veiculo.getNumeroApartamento());
                System.out.println("Hora de entrada: " + veiculo.getHoraEntrada().format(formatter));
                System.out.println("Tempo de estacionamento: " + calcularTempoEstacionamento(veiculo.getHoraEntrada()));

                veiculosEncontrados = true;
            }
        }

        if (!veiculosEncontrados) {
            System.out.println("Nenhum veículo estacionado no momento.");
        }
    }

    private static String calcularTempoEstacionamento(LocalTime horaEntrada) {
        Duration duracao = Duration.between(horaEntrada, LocalTime.now());
        long horas = duracao.toHours();
        long minutos = duracao.toMinutesPart();
        return horas + " horas e " + minutos + " minutos";
    }

    private static long calcularTempoTotalEstacionamento(LocalTime horaEntrada) {
        Duration duracao = Duration.between(horaEntrada, LocalTime.now());
        long horas = duracao.toHours();
        long minutos = duracao.toMinutesPart();
        return minutos;
    }

    // Consulta de estatísticas gerais do programa
    private static void consultarEstatisticas() {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        int vagasDisponiveis = NUM_VAGAS_ESTACIONAMENTO - apartamentosOcupados.size();
        double porcentagemTotal = (double) veiculosEstacionados.size() / NUM_VAGAS_ESTACIONAMENTO * 100;

        System.out.println("\nEstatísticas:");
        System.out.println("Total de veículos estacionados: " + veiculosEstacionados.size());
        System.out.println("Total de apartamentos ocupados: " + apartamentosOcupados.size());
        System.out.println("Vagas disponíveis: " + vagasDisponiveis);
        System.out
                .println("Percentual de Ocupação Total do Estacionamento: " + decimalFormat.format(porcentagemTotal)
                        + "%");

        Duration tempoTotal = Duration.ZERO;

        for (Veiculo veiculo : veiculosEstacionados.values()) {
            LocalTime horaEntrada = veiculo.getHoraEntrada();
            Duration tempoPermanencia = Duration.between(horaEntrada, LocalTime.now());
            tempoTotal = tempoTotal.plus(tempoPermanencia);
        }

        int totalVeiculos = veiculosEstacionados.size();
        long tempoMedioMinutos = tempoTotal.toMinutes() / totalVeiculos;

        System.out.println(
                "Tempo médio de permanencia de veículos no estacionamento em minutos: " + tempoMedioMinutos
                        + " minutos.");

        for (int i = 0; i < NUM_FAIXAS; i++) {
            int vagasOcupadas = faixasEstacionamento[i].size();
            double porcentagemOcupadas = (double) vagasOcupadas / VAGAS_POR_FAIXA * 100;
            System.out.println("\nFaixa " + (i + 1) + ":");
            System.out.println("Vagas ocupadas: " + vagasOcupadas);
            System.out.println("Vagas disponíveis: " + (VAGAS_POR_FAIXA - vagasOcupadas));
            System.out.println("Porcentagem ocupada: " + decimalFormat.format(porcentagemOcupadas) + "%");
            Veiculo ultimoVeiculo = faixasEstacionamento[i].peek();
            if (ultimoVeiculo == null) {
                System.out.println("Esta faixa está vazia");
            } else {
                System.out.println("O último veículo estacionado permanece na vaga há: "
                        + calcularTempoEstacionamento(ultimoVeiculo.getHoraEntrada()));
            }

        }
    }

    // Validação de formato de placa do veículo
    private static boolean validarPlaca(String placa) {
        return placa.matches("[A-Z]{3}[0-9]{4}|[A-Z]{3}[0-9][A-Z][0-9]{2}");
    }

    // Escrever arquivo ao Encerrar e Iniciar o programa
    private static void salvarDados() {
        try {
            FileOutputStream fileOut = new FileOutputStream("dados.object");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            // Salva o array de faixas de estacionamento
            objectOut.writeObject(faixasEstacionamento);

            objectOut.close();
            fileOut.close();
            System.out.println("Estacionamento salvo com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao salvar o estacionamento: " + e.getMessage());
        }
    }

    private static void carregarDados() {
        try {
            FileInputStream fileIn = new FileInputStream("dados.object");
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            // Carrega o array de faixas de estacionamento
            faixasEstacionamento = (Deque<Veiculo>[]) objectIn.readObject();

            // Atualiza o mapa veiculosEstacionados com os veículos carregados
            for (Deque<Veiculo> faixa : faixasEstacionamento) {
                for (Veiculo veiculo : faixa) {
                    veiculosEstacionados.put(veiculo.getPlaca(), veiculo);
                    apartamentosOcupados.add(veiculo.getNumeroApartamento());
                }
            }

            objectIn.close();
            fileIn.close();
            System.out.print("\nEstacionamento carregado com sucesso!");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar o estacionamento: " + e.getMessage());
        }
    }
}