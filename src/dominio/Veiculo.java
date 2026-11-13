package dominio;

public class Veiculo {
    private final Placa placa;
    private final TipoVeiculo tipo;

    public Veiculo(Placa placa, TipoVeiculo tipo) {
        if (placa == null || tipo == null) {
            throw new IllegalArgumentException("Placa e tipo de veículo são obrigatórios.");
        }
        this.placa = placa;
        this.tipo = tipo;
    }

    public Placa getPlaca() {
        return placa;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }
}