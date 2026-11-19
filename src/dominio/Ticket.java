package dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RN03/RN04/RN05 - Permanência do veículo, cobrança e ciclo ABERTO/PAGO/ENCERRADO.
 * Construtor e transições de estado não são acessíveis à camada de menu.
 */
public final class Ticket implements Cobravel {
    private final String id;
    private final Veiculo veiculo;
    private final Vaga vaga;
    private final LocalDateTime entrada;

    private StatusTicket status;
    private double valorPago;
    private double valorPermanencia;
    private double valorRecarga;
    private double consumoKwh;
    private boolean isencaoMensalista;
    private String politicaAplicada;

    Ticket(String id, Veiculo veiculo, Vaga vaga, LocalDateTime horarioEntrada) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Identificador obrigatório.");
        if (veiculo == null || vaga == null || horarioEntrada == null) throw new IllegalArgumentException("Veículo, vaga e horário obrigatórios.");
        if (vaga.getStatus() != StatusVaga.OCUPADA || vaga.getVeiculoAtual() == null ||
            !vaga.getVeiculoAtual().getPlaca().equals(veiculo.getPlaca())) {
            throw new IllegalStateException("Ticket deve corresponder ao veículo que ocupa a vaga.");
        }
        this.id = id;
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.entrada = horarioEntrada;
        this.status = StatusTicket.ABERTO;
        this.politicaAplicada = "Não calculada";
    }

    /** Somente o domínio deve confirmar a cobrança calculada pelo Estacionamento. */
    void pagar(double permanencia, double recarga, double kwh,
               boolean isento, String descricaoPolitica) {
        if (status != StatusTicket.ABERTO) {
            throw new TransicaoEstadoTicketException(id, status, "pagar");
        }
        validarValor(permanencia, "Tarifa de permanência");
        validarValor(recarga, "Valor da recarga");
        if (!Double.isFinite(kwh) || kwh < 0) {
            throw new IllegalArgumentException("Consumo em kWh inválido.");
        }
        if (vaga instanceof VagaCarroEletrico eletrica) {
            if (BigDecimal.valueOf(eletrica.calcularCustoRecarga(kwh))
                    .compareTo(BigDecimal.valueOf(recarga)) != 0) {
                throw new IllegalArgumentException("Recarga incompatível com o consumo registrado.");
            }
        } else if (kwh > 0 || recarga > 0) {
            throw new IllegalArgumentException("Vaga não permite recarga elétrica.");
        }
        if (isento && permanencia != 0.0) {
            throw new IllegalArgumentException("Mensalista isento não paga permanência.");
        }
        if (descricaoPolitica == null || descricaoPolitica.isBlank()) {
            throw new IllegalArgumentException("Descrição da política é obrigatória.");
        }
        BigDecimal total = BigDecimal.valueOf(permanencia).add(BigDecimal.valueOf(recarga));
        if (total.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Valor da cobrança excede o limite.");
        }
        this.valorPermanencia = permanencia;
        this.valorRecarga = recarga;
        this.consumoKwh = kwh;
        this.isencaoMensalista = isento;
        this.politicaAplicada = descricaoPolitica;
        this.valorPago = total.doubleValue();
        this.status = StatusTicket.PAGO;
    }

    void encerrar() {
        if (status == StatusTicket.ABERTO) throw new TicketNaoPagoException(id);
        if (status != StatusTicket.PAGO) {
            throw new TransicaoEstadoTicketException(id, status, "encerrar");
        }
        status = StatusTicket.ENCERRADO;
    }

    private static void validarValor(double valor, String nome) {
        if (!Double.isFinite(valor) || valor < 0 || BigDecimal.valueOf(valor).scale() > 2) {
            throw new IllegalArgumentException(nome + " deve ter até duas casas e não ser negativo.");
        }
    }

    @Override public double getValorCobrado() { return valorPago; }
    @Override public String getDescricaoCobranca() {
        return "Ticket " + id + " | placa " + veiculo.getPlaca().valor() + " | " + status;
    }
    public String getId() { return id; }
    public Veiculo getVeiculo() { return veiculo; }
    public Vaga getVaga() { return vaga; }
    public LocalDateTime getEntrada() { return entrada; }
    public StatusTicket getStatus() { return status; }
    public double getValorPago() { return valorPago; }
    public double getValorPermanencia() { return valorPermanencia; }
    public double getValorRecarga() { return valorRecarga; }
    public double getConsumoKwh() { return consumoKwh; }
    public boolean teveIsencaoMensalista() { return isencaoMensalista; }
    public String getPoliticaAplicada() { return politicaAplicada; }
}
