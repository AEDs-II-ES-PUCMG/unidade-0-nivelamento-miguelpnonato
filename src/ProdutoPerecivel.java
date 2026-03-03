import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ProdutoPerecivel extends Produto {

    private LocalDate dataValidade;
    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;

    public ProdutoPerecivel(String descricao, double precoCusto, double margemLucro, LocalDate validade) {
        this(descricao, precoCusto, margemLucro, validade, true);
    }

    ProdutoPerecivel(String descricao, double precoCusto, double margemLucro, LocalDate validade, boolean validarData) {
        super(descricao, precoCusto, margemLucro);
        if (validarData && validade.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O produto está vencido!");
        }
        dataValidade = validade;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    @Override
    public double valorDeVenda() {
        if (LocalDate.now().isAfter(dataValidade)) {
            throw new IllegalStateException("Não é possível vender: produto vencido.");
        }

        long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), dataValidade);
        double desconto = (diasRestantes <= PRAZO_DESCONTO) ? DESCONTO : 0.0;

        return (precoCusto * (1 + margemLucro)) * (1 - desconto);
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dados = super.toString();
        dados += "\nVálido até: " + formato.format(dataValidade);
        return dados;
    }

    @Override
    public String gerarDadosTexto() {
        String dataFormatada = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(dataValidade);
        return "2;" + getDescricao() + ";" + Produto.fmt2(precoCusto) + ";" + Produto.fmt2(margemLucro) + ";" + dataFormatada;
    }
}