package dominio;
import java.math.BigDecimal;
/** RN08 - Isenção de permanência somente para mensalistas em dia. */
public final class Mensalista implements Cobravel {
    private final String nome;
    private final Placa placa;
    private final double valorMensalidade;
    private boolean emDia;
    public Mensalista(String nome, Placa placa, double valorMensalidade, boolean emDia) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome obrigatório.");
        if (placa == null) throw new IllegalArgumentException("Placa obrigatória.");
        if (!Double.isFinite(valorMensalidade) || valorMensalidade <= 0 ||
            BigDecimal.valueOf(valorMensalidade).scale() > 2) {
            throw new IllegalArgumentException("Mensalidade deve ser positiva e ter até duas casas.");
        }
        this.nome = nome.trim();
        this.placa = placa;
        this.valorMensalidade = valorMensalidade;
        this.emDia = emDia;
    }
    public boolean temIsencao() { return emDia; }
    public void registrarAtraso() {
        if (!emDia) throw new IllegalStateException("Mensalidade já está em atraso: " + nome);
        emDia = false;
    }
    public void regularizarPagamento(double recebido) {
        if (emDia) throw new IllegalStateException("Mensalidade já está em dia: " + nome);
        if (!Double.isFinite(recebido) || BigDecimal.valueOf(recebido)
                .compareTo(BigDecimal.valueOf(valorMensalidade)) != 0) {
            throw new IllegalArgumentException("Pagamento deve ser integral: R$ " + valorMensalidade);
        }
        emDia = true;
    }
    @Override public double getValorCobrado() { return valorMensalidade; }
    @Override public String getDescricaoCobranca() { return "Mensalidade " + nome + " - " + placa.valor(); }
    public String getNome() { return nome; }
    public Placa getPlaca() { return placa; }
    public double getValorMensalidade() { return valorMensalidade; }
}
