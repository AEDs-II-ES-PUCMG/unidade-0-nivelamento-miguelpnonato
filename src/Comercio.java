import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Comercio {
    static final int MAX_NOVOS_PRODUTOS = 10;

    static String nomeArquivoDados;

    static Scanner teclado;

    static Produto[] produtosCadastrados;

    static int quantosProdutos;

    static void pausa(){
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    static void cabecalho(){
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    static int menu(){
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e listar um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    static Produto[] lerProdutos(String nomeArquivoDados) {
        Produto[] vetorProdutos;

        try (Scanner arq = new Scanner(new File(nomeArquivoDados), Charset.forName("ISO-8859-2"))) {

            String primeira = arq.nextLine().trim();
            int n = Integer.parseInt(primeira);

            vetorProdutos = new Produto[n + MAX_NOVOS_PRODUTOS];
            quantosProdutos = 0;

            for (int i = 0; i < n && arq.hasNextLine(); i++) {
                String linha = arq.nextLine().trim();
                if (linha.isEmpty()) { i--; continue; }

                Produto p = Produto.criarDoTexto(linha);
                if (p != null) {
                    vetorProdutos[quantosProdutos] = p;
                    quantosProdutos++;
                }
            }

            return vetorProdutos;

        } catch (FileNotFoundException e) {
            // vetor vazio em caso de problemas (como o PDF pede)
            return new Produto[0];
        } catch (Exception e) {
            return new Produto[0];
        }
    }

    static void listarTodosOsProdutos(){
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if(produtosCadastrados[i] != null)
                System.out.println(String.format("%02d - %s", (i+1), produtosCadastrados[i].toString()));
        }
    }

    static void localizarProdutos(){
        cabecalho();
        System.out.print("Digite o nome do produto: ");
        String nome = teclado.nextLine().trim();

        if (nome.length() < 3) {
            System.out.println("Nome inválido (mínimo 3 caracteres).");
            return;
        }

        // objeto temporário só para usar equals() do jeito que o enunciado manda
        Produto chave = new ProdutoNaoPerecivel(nome, 0.01, 0.01);

        for (int i = 0; i < produtosCadastrados.length; i++) {
            if (produtosCadastrados[i] != null && produtosCadastrados[i].equals(chave)) {
                System.out.println("Produto encontrado:");
                System.out.println(produtosCadastrados[i].toString());
                return;
            }
        }
        System.out.println("Produto não encontrado.");
    }

    static void cadastrarProduto(){
        cabecalho();
        System.out.println("CADASTRAR PRODUTO");
        System.out.println("1 - Não perecível");
        System.out.println("2 - Perecível");
        System.out.print("Tipo: ");
        int tipo = Integer.parseInt(teclado.nextLine().trim());

        System.out.print("Descrição (mín 3 chars): ");
        String desc = teclado.nextLine().trim();

        System.out.print("Preço de custo: ");
        double custo = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

        System.out.print("Margem de lucro (ex: 0.20): ");
        double margem = Double.parseDouble(teclado.nextLine().trim().replace(",", "."));

        Produto novo;

        if (tipo == 1) {
            novo = new ProdutoNaoPerecivel(desc, custo, margem);
        } else if (tipo == 2) {
            System.out.print("Data validade (dd/MM/yyyy): ");
            String dataStr = teclado.nextLine().trim();
            LocalDate validade = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            // cadastro manual valida data no passado
            novo = new ProdutoPerecivel(desc, custo, margem, validade);
        } else {
            System.out.println("Tipo inválido.");
            return;
        }

        // verificar espaço
        if (quantosProdutos >= produtosCadastrados.length) {
            System.out.println("Sem espaço no vetor para novos produtos.");
            return;
        }

        produtosCadastrados[quantosProdutos] = novo;
        quantosProdutos++;
        System.out.println("Produto cadastrado com sucesso!");
    }

    public static void salvarProdutos(String nomeArquivo){
        try (FileWriter fw = new FileWriter(nomeArquivo, Charset.forName("ISO-8859-2"))) {
            fw.write(String.valueOf(quantosProdutos));
            fw.write("\n");
            for (int i = 0; i < quantosProdutos; i++) {
                if (produtosCadastrados[i] != null) {
                    fw.write(produtosCadastrados[i].gerarDadosTexto());
                    fw.write("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        teclado = new Scanner(System.in, Charset.forName("ISO-8859-2"));
        nomeArquivoDados = "dadosProdutos.csv";
        produtosCadastrados = lerProdutos(nomeArquivoDados);

        // se falhou e voltou vetor vazio, cria um “mínimo” pra conseguir usar o programa
        if (produtosCadastrados.length == 0) {
            produtosCadastrados = new Produto[MAX_NOVOS_PRODUTOS];
            quantosProdutos = 0;
        }

        int opcao;
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
            }
            pausa();
        }while(opcao != 0);

        salvarProdutos(nomeArquivoDados);
        teclado.close();
    }
}