import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class App {

    private static final String ARQUIVO_DADOS = "produtos.dat";
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        Locale.setDefault(new Locale("pt", "BR"));

        Scanner sc = new Scanner(System.in);
        List<Produto> produtos = carregar();

        int opcao;
        do {
            System.out.println("\n=== MENU - SISTEMA DE PRODUTOS ===");
            System.out.println("1) Cadastrar produto NÃO perecível");
            System.out.println("2) Cadastrar produto PERECÍVEL");
            System.out.println("3) Listar produtos");
            System.out.println("4) Registrar venda (mostrar valor de venda)");
            System.out.println("0) Salvar e sair");
            System.out.print("Escolha: ");
            opcao = lerInt(sc);

            switch (opcao) {
                case 1 -> cadastrarNaoPerecivel(sc, produtos);
                case 2 -> cadastrarPerecivel(sc, produtos);
                case 3 -> listar(produtos);
                case 4 -> registrarVenda(sc, produtos);
                case 0 -> {
                    salvar(produtos);
                    System.out.println("Dados salvos. Saindo...");
                }
                default -> System.out.println("Opção inválida.");
            }

        } while (opcao != 0);

        sc.close();
    }

    // -------- MENU ACTIONS --------

    private static void cadastrarNaoPerecivel(Scanner sc, List<Produto> produtos) {
        try {
            System.out.print("Descrição (mín 3 chars): ");
            String desc = sc.nextLine().trim();

            System.out.print("Preço de custo: ");
            double custo = lerDouble(sc);

            System.out.print("Deseja informar margem de lucro? (S/N): ");
            String resp = sc.nextLine().trim().toUpperCase();

            Produto p;
            if (resp.equals("S")) {
                System.out.print("Margem (ex: 0.2 para 20%): ");
                double margem = lerDouble(sc);
                p = new ProdutoNaoPerecivel(desc, custo, margem);
            } else {
                p = new ProdutoNaoPerecivel(desc, custo);
            }

            produtos.add(p);
            salvar(produtos);
            System.out.println("Cadastrado com sucesso!");

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void cadastrarPerecivel(Scanner sc, List<Produto> produtos) {
        try {
            System.out.print("Descrição (mín 3 chars): ");
            String desc = sc.nextLine().trim();

            System.out.print("Preço de custo: ");
            double custo = lerDouble(sc);

            System.out.print("Margem (ex: 0.2 para 20%): ");
            double margem = lerDouble(sc);

            LocalDate validade = lerData(sc, "Validade (dd/MM/yyyy): ");

            Produto p = new ProdutoPerecivel(desc, custo, margem, validade);

            produtos.add(p);
            salvar(produtos);
            System.out.println("Cadastrado com sucesso!");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    private static void listar(List<Produto> produtos) {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        System.out.println("\n--- PRODUTOS CADASTRADOS ---");
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtos.get(i);

            System.out.println("[" + i + "] " + p.getClass().getSimpleName());
            System.out.println(p.toString());

            if (p instanceof ProdutoPerecivel pp) {
                System.out.println("Status: " + (LocalDate.now().isAfter(pp.getDataValidade()) ? "VENCIDO" : "OK"));
            }

            System.out.println("----------------------------");
        }
    }

    private static void registrarVenda(Scanner sc, List<Produto> produtos) {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        listar(produtos);
        System.out.print("Informe o índice do produto para vender: ");
        int idx = lerInt(sc);

        if (idx < 0 || idx >= produtos.size()) {
            System.out.println("Índice inválido.");
            return;
        }

        Produto p = produtos.get(idx);
        try {
            double valor = p.valorVenda();
            System.out.println("Venda registrada! Valor a cobrar: " + String.format(Locale.getDefault(), "%.2f", valor));
        } catch (IllegalStateException e) {
            System.out.println("Não foi possível vender: " + e.getMessage());
        }
    }

    // -------- PERSISTÊNCIA --------

    @SuppressWarnings("unchecked")
    private static List<Produto> carregar() {
        File f = new File(ARQUIVO_DADOS);
        if (!f.exists()) return new ArrayList<>();

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = in.readObject();
            if (obj instanceof List<?>) {
                return (List<Produto>) obj;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            System.out.println("Aviso: não foi possível carregar '" + ARQUIVO_DADOS + "'. Iniciando vazio.");
            return new ArrayList<>();
        }
    }

    private static void salvar(List<Produto> produtos) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DADOS))) {
            out.writeObject(produtos);
        } catch (IOException e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    // -------- LEITURAS SEGURAS --------

    private static int lerInt(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim();
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.print("Número inválido. Tente novamente: ");
            }
        }
    }

    private static double lerDouble(Scanner sc) {
        while (true) {
            String s = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido. Tente novamente: ");
            }
        }
    }

    private static LocalDate lerData(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                return LocalDate.parse(s, FMT_DATA);
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use dd/MM/yyyy (ex: 05/03/2026).");
            }
        }
    }
}