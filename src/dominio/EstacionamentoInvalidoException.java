package dominio;

public class EstacionamentoInvalidoException extends IllegalArgumentException {

    public EstacionamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}