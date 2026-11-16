
package dominio;

import java.time.LocalDateTime;

public class Ticket {

    private final String id;
    private final Veiculo veiculo;
    private final Vaga vaga;
    private final LocalDateTime entrada;

    private StatusTicket status;
    private double valorPago;

    // Construtor
    public Ticket(String id, Veiculo veiculo, Vaga vaga) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "O identificador do ticket é obrigatório."
            );
        }

        if (veiculo == null || vaga == null) {
            throw new IllegalArgumentException(
                    "Veículo e vaga são obrigatórios."
            );
        }

        if (vaga.getStatus() != StatusVaga.OCUPADA
                || vaga.getVeiculoAtual() == null
                || !vaga.getVeiculoAtual().getPlaca()
                .equals(veiculo.getPlaca())) {

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
    }

    // RN05 - Pagamento do ticket
    public void pagar(double valor) {

        if (this.status != StatusTicket.ABERTO) {
            throw new TransicaoEstadoTicketException(
                    id, status, "pagar"
            );
        }

        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException(
                    "O valor do pagamento é inválido."
            );
        }

        this.valorPago = valor;
        this.status = StatusTicket.PAGO;
    }

    // RN05 - Encerramento controlado pelo domínio
    void encerrar() {

        if (this.status == StatusTicket.ABERTO) {
            throw new TicketNaoPagoException(id);
        }

        if (this.status != StatusTicket.PAGO) {
            throw new TransicaoEstadoTicketException(
                    id, status, "encerrar"
            );
        }

        this.status = StatusTicket.ENCERRADO;
    }

    // Métodos de consulta
    public String getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public StatusTicket getStatus() {
        return status;
    }

    public double getValorPago() {
        return valorPago;
    }
}
