package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** RN01/RN04 - Vaga elétrica compatível e custo de recarga por kWh. */
public final class VagaCarroEletrico extends Vaga {

    private final double precoPorKwh;

    public VagaCarroEletrico(String codigo, double precoPorKwh) {
        super(codigo);
        if (!Double.isFinite(precoPorKwh) || precoPorKwh <= 0) {
            throw new IllegalArgumentException(
                    "O preço por kWh deve ser positivo e finito."
            );
        }
        this.precoPorKwh = precoPorKwh;
    }

    @Override
    public boolean aceita(Veiculo veiculo) {
        return veiculo != null && veiculo.getTipo() == TipoVeiculo.ELETRICO;
    }

    public double calcularCustoRecarga(double kwh) {
        if (!Double.isFinite(kwh) || kwh < 0) {
            throw new IllegalArgumentException(
                    "O consumo de energia deve ser não negativo e finito."
            );
        }

        BigDecimal total = BigDecimal.valueOf(kwh)
                .multiply(BigDecimal.valueOf(precoPorKwh))
                .setScale(2, RoundingMode.HALF_UP);

        if (total.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Valor da recarga excede o limite permitido.");
        }
        return total.doubleValue();
    }

    public double getPrecoPorKwh() { return precoPorKwh; }
}
