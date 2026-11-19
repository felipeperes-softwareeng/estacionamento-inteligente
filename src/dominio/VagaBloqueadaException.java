package dominio;
public class VagaBloqueadaException extends IllegalStateException {
    public VagaBloqueadaException(String codigo) {
        super("A vaga " + codigo + " está bloqueada para manutenção.");
    }
}
