package dominio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
/** RN03: 15 min grátis, hora/fração, teto por período de 24 h. */
public final class TarifaPadrao implements PoliticaDeTarifa {
    private static final long MINUTOS_DIA = 1440L;
    private final double valorHora;
    private final double tetoDiaria;
    public TarifaPadrao(double valorHora, double tetoDiaria) {
        if (!Double.isFinite(valorHora) || valorHora <= 0 ||
            !Double.isFinite(tetoDiaria) || tetoDiaria <= 0) {
            throw new IllegalArgumentException("Hora e diária precisam ser positivas e finitas.");
        }
        this.valorHora = valorHora;
        this.tetoDiaria = tetoDiaria;
    }
    @Override public double calcularValor(long minutos) {
        if (minutos < 0) throw new IllegalArgumentException("Tempo negativo não é válido.");
        if (minutos <= 15) return 0;
        long dias = minutos / MINUTOS_DIA;
        long resto = minutos % MINUTOS_DIA;
        long horas = (resto + 59) / 60;
        BigDecimal hora = BigDecimal.valueOf(valorHora);
        BigDecimal teto = BigDecimal.valueOf(tetoDiaria);
        BigDecimal diaria = hora.multiply(BigDecimal.valueOf(24)).min(teto);
        BigDecimal total = diaria.multiply(BigDecimal.valueOf(dias))
             .add(hora.multiply(BigDecimal.valueOf(horas)).min(teto))
             .setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Tarifa calculada excede o limite.");
        }
        return total.doubleValue();
    }
    @Override public String getDescricao() {
        return String.format(Locale.forLanguageTag("pt-BR"),
            "Tarifa padrão: R$ %.2f/h; teto R$ %.2f/24h; 15 min de tolerância.",
            valorHora, tetoDiaria);
    }
    public double getValorHora() { return valorHora; }
    public double getTetoDiaria() { return tetoDiaria; }
}
