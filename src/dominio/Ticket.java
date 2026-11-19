package dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RN03/RN04/RN05 - Registro de entrada, pagamento e saída.
 * Só Estacionamento, no pacote dominio, pode criar ou pagar tickets.
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

    Ticket(String id, Veiculo veiculo, Vaga vaga) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O identificador do ticket é obrigatório.");
        }
        if (veiculo == null || vaga == null) {
            throw new IllegalArgumentException("Veículo e vaga são obrigatórios.");
        }
        if (vaga.getStatus() != StatusVaga.OCUPADA
                || vaga.getVeiculoAtual() == null
                || !vaga.getVeiculoAtual().getPlaca().equals(veiculo.getPlaca())) {
            throw new IllegalStateException(
                    "O ticket deve corresponder ao veículo que ocupa a vaga."
            );
        }

        this.id = id;
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.entrada = LocalDateTime.now();
        this.status = StatusTicket.ABERTO;
        this.valorPago = 0.0;
        this.valorPermanencia = 0.0;
        this.valorRecarga = 0.0;
        this.consumoKwh = 0.0;
        this.isencaoMensalista = false;
        this.politicaAplicada = "Não calculada";
    }

    // Sem public: o menu não pode marcar um ticket como pago por conta própria.
    void pagar(double permanencia, double recarga, double kwh,
               boolean isento, String descricaoPolitica) {
        if (status != StatusTicket.ABERTO) {
            throw new TransicaoEstadoTicketException(id, status, "pagar");
        }
        validarDinheiro(permanencia, "tarifa de permanência");
        validarDinheiro(recarga, "valor de recarga");

        if (!Double.isFinite(kwh) || kwh < 0) {
            throw new IllegalArgumentException("Consumo em kWh inválido.");
        }
        if (vaga instanceof VagaCarroEletrico eletrica) {
            double custoEsperado = eletrica.calcularCustoRecarga(kwh);
            if (BigDecimal.valueOf(recarga).compareTo(
                    BigDecimal.valueOf(custoEsperado)) != 0) {
                throw new IllegalArgumentException(
                        "O valor da recarga não corresponde ao consumo informado."
                );
            }
        } else if (kwh > 0 || recarga > 0) {
            throw new IllegalArgumentException(
                    "Não é possível registrar recarga elétrica nesta vaga."
            );
        }
        if (isento && permanencia != 0.0) {
            throw new IllegalArgumentException(
                    "Mensalista isento não pode ter tarifa de permanência."
            );
        }
        if (descricaoPolitica == null || descricaoPolitica.isBlank()) {
            throw new IllegalArgumentException("A política aplicada é obrigatória.");
        }

        BigDecimal total = BigDecimal.valueOf(permanencia)
                .add(BigDecimal.valueOf(recarga));
        if (total.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Valor da cobrança excede o limite permitido.");
        }

        this.valorPermanencia = permanencia;
        this.valorRecarga = recarga;
        this.consumoKwh = kwh;
        this.isencaoMensalista = isento;
        this.politicaAplicada = descricaoPolitica;
        this.valorPago = total.doubleValue();
        this.status = StatusTicket.PAGO;
    }

    private void validarDinheiro(double valor, String campo) {
        if (!Double.isFinite(valor) || valor < 0
                || BigDecimal.valueOf(valor).scale() > 2) {
            throw new IllegalArgumentException(
                    "O " + campo + " deve ser não negativo e ter até duas casas decimais."
            );
        }
    }

    void encerrar() {
        if (status == StatusTicket.ABERTO) {
            throw new TicketNaoPagoException(id);
        }
        if (status != StatusTicket.PAGO) {
            throw new TransicaoEstadoTicketException(id, status, "encerrar");
        }
        this.status = StatusTicket.ENCERRADO;
    }

    @Override
    public double getValorCobrado() {
        // Antes do pagamento, ainda não existe cobrança final registrada.
        return valorPago;
    }

    @Override
    public String getDescricaoCobranca() {
        return "Ticket " + id + " - placa " + veiculo.getPlaca().valor()
                + " - " + status;
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
