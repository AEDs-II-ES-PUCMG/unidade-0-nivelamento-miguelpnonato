public class ProdutoNaoPerecivel extends Produto {

    public ProdutoNaoPerecivel(String descricao, double precoCusto, double margemLucro) {
        super(descricao, precoCusto, margemLucro);
    }

    public ProdutoNaoPerecivel(String descricao, double precoCusto) {
        super(descricao, precoCusto);
    }

    @Override
    public double valorDeVenda() {
        return (precoCusto * (1.0 + margemLucro));
    }

    @Override
    public String gerarDadosTexto() {
        return "1;" + getDescricao() + ";" + Produto.fmt2(precoCusto) + ";" + Produto.fmt2(margemLucro);
    }
}