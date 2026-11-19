package dominio;
public class TransicaoEstadoTicketException extends IllegalStateException {
    public TransicaoEstadoTicketException(String id, StatusTicket estado, String acao) {
        super("Não é possível " + acao + " o ticket " + id + " no estado " + estado + ".");
    }
}
