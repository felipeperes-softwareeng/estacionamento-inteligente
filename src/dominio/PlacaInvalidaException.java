package dominio;
public class PlacaInvalidaException extends IllegalArgumentException {
    public PlacaInvalidaException(String valor) {
        super("Placa inválida: " + valor);
    }
}
