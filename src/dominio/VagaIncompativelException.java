package dominio;

public class VagaIncompativelException extends IllegalArgumentException {

    public VagaIncompativelException(String codigo) {
        super("Veículo incompatível com a vaga " + codigo + ".");
    }
}