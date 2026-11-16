
package dominio;

public class VagaCarroEletrico extends Vaga {

    private final double precoPorKwh;

    public VagaCarroEletrico(String codigo, double precoPorKwh) {
        super(codigo);

        if (!Double.isFinite(precoPorKwh) || precoPorKwh <= 0) {
            throw new IllegalArgumentException(
                    "O preço por kWh deve ser maior que zero."
            );
        }

        this.precoPorKwh = precoPorKwh;
    }

    // RN01 - Compatibilidade entre veículo e vaga
    @Override
    public boolean aceita(Veiculo veiculo) {
        return veiculo != null
                && veiculo.getTipo() == TipoVeiculo.ELETRICO;
    }

    // RN04 - Cobrança adicional pela recarga elétrica
    public double calcularCustoRecarga(double kwh) {

        if (!Double.isFinite(kwh) || kwh < 0) {
            throw new IllegalArgumentException(
                    "O consumo de energia não pode ser negativo ou inválido."
            );
        }

        return kwh * precoPorKwh;
    }

    public double getPrecoPorKwh() {
        return precoPorKwh;
    }
}
