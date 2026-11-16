package dominio;

public class TransicaoEstadoTicketException extends IllegalStateException {

    public TransicaoEstadoTicketException(
            String idTicket,
            StatusTicket estado,
            String operacao
    ) {
        super("Não é possível " + operacao
                + " o ticket " + idTicket
                + " no estado " + estado + ".");
    }
}