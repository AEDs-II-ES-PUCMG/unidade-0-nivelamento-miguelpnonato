import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class Produto {

    private static final double MARGEM_PADRAO = 0.2;

    private String descricao;
    protected double precoCusto;
    protected double margemLucro;

    private void init(String desc, double precoCusto, double margemLucro) {
        if (desc != null && desc.length() >= 3 && precoCusto > 0.0 && margemLucro > 0.0) {
            this.descricao = desc;
            this.precoCusto = precoCusto;
            this.margemLucro = margemLucro;
        } else {
            throw new IllegalArgumentException("Valores inválidos para os dados do produto.");
        }
    }

    protected Produto(String desc, double precoCusto, double margemLucro) {
        init(desc, precoCusto, margemLucro);
    }

    protected Produto(String desc, double precoCusto) {
        init(desc, precoCusto, MARGEM_PADRAO);
    }

    public String getDescricao() {
        return descricao;
    }

    
    public double valorVenda() {
        return valorDeVenda();
    }

    
    public double valorDeVenda() {
        return (precoCusto * (1.0 + margemLucro));
    }

    @Override
    public String toString() {
        NumberFormat moeda = NumberFormat.getCurrencyInstance();
        return "NOME: " + descricao + ": " + moeda.format(valorDeVenda());
    }

    @Override
    public boolean equals(Object obj) {
        Produto outro = (Produto) obj;
        return this.descricao.toLowerCase().equals(outro.descricao.toLowerCase());
    }

    /**
     * Gera uma linha de texto a partir dos dados do produto.
     * @return "tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade]"
     */
    public abstract String gerarDadosTexto();

    public static Produto criarDoTexto(String linha) {
        if (linha == null) return null;

        String[] partes = linha.split(";");
        if (partes.length < 4) return null;

        int tipo = Integer.parseInt(partes[0].trim());
        String desc = partes[1].trim();

        double preco = Double.parseDouble(partes[2].trim().replace(",", "."));
        double margem = Double.parseDouble(partes[3].trim().replace(",", "."));

        if (tipo == 1) {
            return new ProdutoNaoPerecivel(desc, preco, margem);
        }

        if (tipo == 2) {
            if (partes.length < 5) return null;
            String dataStr = partes[4].trim();
            LocalDate validade = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return new ProdutoPerecivel(desc, preco, margem, validade, false);
        }

        return null;
    }

    protected static String fmt2(double v) {
        return String.format(Locale.US, "%.2f", v);
    }
}