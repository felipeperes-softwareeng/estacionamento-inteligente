package dominio;
public class TicketNaoPagoException extends IllegalStateException {
    public TicketNaoPagoException(String idTicket) {
        super("O ticket " + idTicket + " não está PAGO. Operação recusada.");
    }
}
