package dominio;

public class VagaOcupadaException extends IllegalStateException {

    public VagaOcupadaException(String codigo) {
        super("A vaga " + codigo + " já está ocupada.");
    }
}