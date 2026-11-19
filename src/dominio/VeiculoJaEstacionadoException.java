package dominio;
public class VeiculoJaEstacionadoException extends IllegalStateException {
    public VeiculoJaEstacionadoException(String placa) {
        super("O veículo de placa " + placa + " já está estacionado.");
    }
}
