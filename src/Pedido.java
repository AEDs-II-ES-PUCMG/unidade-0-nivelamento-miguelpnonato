import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Pedido {

    private static final int MAX_PRODUTOS = 10;

    private static final double DESCONTO_PG_A_VISTA = 0.15;

    private static final double DESCONTO_ITEM_GRANDE = 0.05;

    private ItemDePedido[] itens;

    private LocalDate dataPedido;

    private int quantProdutos = 0;

    private int formaDePagamento;

    
    public Pedido(LocalDate dataPedido, int formaDePagamento) {
        this.itens = new ItemDePedido[MAX_PRODUTOS];
        this.quantProdutos = 0;
        this.dataPedido = dataPedido;
        this.formaDePagamento = formaDePagamento;
    }

    
    public boolean incluirProduto(Produto novo) {
        if (quantProdutos < MAX_PRODUTOS) {
            itens[quantProdutos++] = new ItemDePedido(novo, 1, novo.valorDeVenda());
            return true;
        }
        return false;
    }

    
    public boolean incluirProduto(Produto novo, int quantidade) {
        if (quantProdutos < MAX_PRODUTOS) {
            itens[quantProdutos++] = new ItemDePedido(novo, quantidade, novo.valorDeVenda());
            return true;
        }
        return false;
    }

   
    private int buscarItem(ItemDePedido item) {
        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] != null && itens[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    
    public double valorFinal() {
        double valorPedido = 0;

        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] != null) {
                valorPedido += itens[i].calcularSubtotal();
            }
        }

        if (formaDePagamento == 1) {
            valorPedido *= (1.0 - DESCONTO_PG_A_VISTA);
        }

        return valorPedido;
    }

    
    public void mesclarPedido(Pedido outroPedido) {
        if (outroPedido == null) {
            return;
        }

        int novosItensNecessarios = 0;

        
        for (int i = 0; i < outroPedido.quantProdutos; i++) {
            ItemDePedido itemOutro = outroPedido.itens[i];
            if (itemOutro != null && buscarItem(itemOutro) == -1) {
                novosItensNecessarios++;
            }
        }

        if (this.quantProdutos + novosItensNecessarios > MAX_PRODUTOS) {
            throw new IllegalStateException("Não há espaço suficiente para mesclar os pedidos.");
        }

        
        for (int i = 0; i < outroPedido.quantProdutos; i++) {
            ItemDePedido itemOutro = outroPedido.itens[i];

            if (itemOutro != null) {
                int posicaoExistente = buscarItem(itemOutro);

                if (posicaoExistente != -1) {
                    
                    itens[posicaoExistente].adicionarQuantidade(itemOutro.getQuantidade());

                    
                    if (itemOutro.getPrecoVenda() < itens[posicaoExistente].getPrecoVenda()) {
                        itens[posicaoExistente].setPrecoVenda(itemOutro.getPrecoVenda());
                    }
                } else {
                    
                    itens[quantProdutos++] = itemOutro;
                }
            }
        }

        
        for (int i = 0; i < outroPedido.quantProdutos; i++) {
            outroPedido.itens[i] = null;
        }
        outroPedido.quantProdutos = 0;
    }

    
    public void imprimirRecibo() {
        System.out.println("=========== RECIBO ===========");
        System.out.println("Data do pedido: " + dataPedido.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println();

        double totalGeral = 0;

        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] != null) {
                double subtotal = itens[i].calcularSubtotal();

                if (itens[i].getQuantidade() > 10) {
                    subtotal *= (1.0 - DESCONTO_ITEM_GRANDE);
                }

                System.out.println("Produto: " + itens[i].getProduto());
                System.out.println("Quantidade: " + itens[i].getQuantidade());
                System.out.println("Preço unitário: R$ " + String.format("%.2f", itens[i].getPrecoVenda()));
                System.out.println("Subtotal: R$ " + String.format("%.2f", subtotal));
                System.out.println("--------------------------------");

                totalGeral += subtotal;
            }
        }

        if (formaDePagamento == 1) {
            System.out.println("Pagamento: à vista");
            System.out.println("Desconto do pedido: " + String.format("%.2f", DESCONTO_PG_A_VISTA * 100) + "%");
            totalGeral *= (1.0 - DESCONTO_PG_A_VISTA);
        } else {
            System.out.println("Pagamento: parcelado");
        }

        System.out.println("TOTAL GERAL: R$ " + String.format("%.2f", totalGeral));
        System.out.println("==============================");
    }

    @Override
    public String toString() {
        StringBuilder stringPedido = new StringBuilder();
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        stringPedido.append("Data do pedido: ").append(formatoData.format(dataPedido)).append("\n");
        stringPedido.append("Pedido com ").append(quantProdutos).append(" itens.\n");
        stringPedido.append("Itens no pedido:\n");

        for (int i = 0; i < quantProdutos; i++) {
            if (itens[i] != null) {
                stringPedido.append(itens[i].toString()).append("\n");
            }
        }

        stringPedido.append("Pedido pago ");
        if (formaDePagamento == 1) {
            stringPedido.append("à vista. Percentual de desconto: ")
                        .append(String.format("%.2f", DESCONTO_PG_A_VISTA * 100))
                        .append("%\n");
        } else {
            stringPedido.append("parcelado.\n");
        }

        stringPedido.append("Valor total do pedido: R$ ")
                    .append(String.format("%.2f", valorFinal()));

        return stringPedido.toString();
    }

    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Pedido outro = (Pedido) obj;
        return this.dataPedido.equals(outro.dataPedido);
    }
}