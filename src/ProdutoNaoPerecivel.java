public class ProdutoNaoPerecivel extends Produto {
    

    public ProdutoNaoPerecivel(String descricao, double precoCusto, double margemLucro) {
        super(descricao, precoCusto, margemLucro);
    }

    public ProdutoNaoPerecivel(String descricao, double precoCusto) {
        super(descricao, precoCusto);
    }
    
    @Override
    public double valorVenda() {
	    return (precoCusto * (1.0 + margemLucro));
	}
}
