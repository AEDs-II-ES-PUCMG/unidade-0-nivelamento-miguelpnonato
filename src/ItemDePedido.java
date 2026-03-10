public class ItemDePedido {

    
    private Produto produto;
    private int quantidade;
    private double precoVenda;

    public ItemDePedido(Produto produto, int quantidade, double precoVenda) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoVenda = precoVenda;
    }

    public double calcularSubtotal() {
        return quantidade * precoVenda;
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public void adicionarQuantidade(int quantidade) {
        this.quantidade += quantidade;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ItemDePedido outro = (ItemDePedido) obj;
        return this.produto.equals(outro.produto);
    }

    @Override
    public String toString() {
        return produto.toString()
                + " | Quantidade: " + quantidade
                + " | Preço unitário: R$ " + String.format("%.2f", precoVenda)
                + " | Subtotal: R$ " + String.format("%.2f", calcularSubtotal());
    }
}