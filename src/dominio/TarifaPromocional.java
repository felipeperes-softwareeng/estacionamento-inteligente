package dominio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
/** Strategy alternativa: aplica desconto à tarifa padrão. */
public final class TarifaPromocional implements PoliticaDeTarifa {
    private final TarifaPadrao tarifaBase;
    private final double percentualDesconto;
    public TarifaPromocional(double valorHora, double tetoDiaria, double percentualDesconto) {
        if (!Double.isFinite(percentualDesconto) || percentualDesconto <= 0 || percentualDesconto >= 100) {
            throw new IllegalArgumentException("Desconto deve ser maior que 0 e menor que 100%.");
        }
        this.tarifaBase = new TarifaPadrao(valorHora, tetoDiaria);
        this.percentualDesconto = percentualDesconto;
    }
    @Override public double calcularValor(long minutos) {
        BigDecimal fator = BigDecimal.ONE.subtract(BigDecimal.valueOf(percentualDesconto)
            .divide(BigDecimal.valueOf(100)));
        return BigDecimal.valueOf(tarifaBase.calcularValor(minutos)).multiply(fator)
            .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
    @Override public String getDescricao() {
        return String.format(Locale.forLanguageTag("pt-BR"),
            "Tarifa promocional: %.2f%% de desconto sobre a tarifa padrão.", percentualDesconto);
    }
    public double getPercentualDesconto() { return percentualDesconto; }
}
