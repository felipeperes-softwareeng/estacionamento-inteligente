package dominio;

import java.math.BigDecimal;

/**
 * RN03/RN08 - Cliente mensalista vinculado a uma placa.
 * Mensalidade em dia isenta a tarifa de permanência, não a recarga elétrica.
 *
 * Nesta versão, o atraso é registrado explicitamente, sem verificação
 * automática de datas de vencimento.
 */
public final class Mensalista implements Cobravel {

    private final String nome;
    private final Placa placa;
    private final double valorMensalidade;
    private boolean emDia;

    public Mensalista(String nome, Placa placa,
                      double valorMensalidade, boolean emDia) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do mensalista é obrigatório.");
        }
        if (placa == null) {
            throw new IllegalArgumentException(
                    "A placa do mensalista é obrigatória.");
        }
        if (!Double.isFinite(valorMensalidade) || valorMensalidade <= 0) {
            throw new IllegalArgumentException(
                    "A mensalidade deve ter valor positivo e finito.");
        }
        if (BigDecimal.valueOf(valorMensalidade).scale() > 2) {
            throw new IllegalArgumentException(
                    "A mensalidade pode ter no máximo duas casas decimais.");
        }

        this.nome = nome.trim();
        this.placa = placa;
        this.valorMensalidade = valorMensalidade;
        this.emDia = emDia;
    }

    /** RN08: somente mensalista em dia tem isenção da permanência. */
    public boolean temIsencao() {
        return emDia;
    }

    /** Registra a perda de regularidade da mensalidade. */
    public void registrarAtraso() {
        if (!emDia) {
            throw new IllegalStateException(
                    "A mensalidade de " + nome + " já está em atraso.");
        }
        emDia = false;
    }

    /**
     * Regulariza a mensalidade apenas com o valor integral contratado.
     * Não há parcelamento, crédito ou troco neste modelo acadêmico.
     */
    public void regularizarPagamento(double valorRecebido) {
        if (emDia) {
            throw new IllegalStateException(
                    "A mensalidade de " + nome + " já está em dia.");
        }
        if (!Double.isFinite(valorRecebido) || valorRecebido <= 0) {
            throw new IllegalArgumentException(
                    "O valor recebido deve ser positivo e finito.");
        }
        if (BigDecimal.valueOf(valorRecebido).compareTo(
                BigDecimal.valueOf(valorMensalidade)) != 0) {
            throw new IllegalArgumentException(
                    "O pagamento da mensalidade deve corresponder a R$ "
                            + BigDecimal.valueOf(valorMensalidade).toPlainString() + ".");
        }
        emDia = true;
    }

    public String getNome() {
        return nome;
    }

    public Placa getPlaca() {
        return placa;
    }

    public double getValorMensalidade() {
        return valorMensalidade;
    }

    /** Valor contratual da mensalidade, independentemente de estar paga. */
    @Override
    public double getValorCobrado() {
        return valorMensalidade;
    }

    @Override
    public String getDescricaoCobranca() {
        return "Mensalidade de " + nome + " - placa " + placa.valor();
    }
}
