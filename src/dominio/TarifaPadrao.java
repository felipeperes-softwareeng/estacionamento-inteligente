package dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * RN03: 15 minutos gratuitos, hora ou fração e teto por período de 24 horas.
 */
public final class TarifaPadrao implements PoliticaDeTarifa {

    private static final long MINUTOS_POR_DIA = 24L * 60;
    private static final long MINUTOS_POR_HORA = 60;
    private static final long TOLERANCIA_MINUTOS = 15;

    private final double valorHora;
    private final double tetoDiaria;

    public TarifaPadrao(double valorHora, double tetoDiaria) {
        if (!Double.isFinite(valorHora) || valorHora <= 0) {
            throw new IllegalArgumentException(
                    "O valor da hora deve ser maior que zero e finito."
            );
        }
        if (!Double.isFinite(tetoDiaria) || tetoDiaria <= 0) {
            throw new IllegalArgumentException(
                    "O teto da diária deve ser maior que zero e finito."
            );
        }

        this.valorHora = valorHora;
        this.tetoDiaria = tetoDiaria;
    }

    @Override
    public double calcularValor(long minutos) {
        if (minutos < 0) {
            throw new IllegalArgumentException(
                    "O tempo de permanência não pode ser negativo."
            );
        }

        // A tolerância é aplicada uma única vez no início da permanência.
        if (minutos <= TOLERANCIA_MINUTOS) {
            return 0.0;
        }

        long diasCompletos = minutos / MINUTOS_POR_DIA;
        long minutosRestantes = minutos % MINUTOS_POR_DIA;

        // Toda fração de hora é cobrada como uma hora completa.
        long horasRestantes = (minutosRestantes + MINUTOS_POR_HORA - 1)
                / MINUTOS_POR_HORA;

        BigDecimal precoHora = BigDecimal.valueOf(valorHora);
        BigDecimal limiteDiaria = BigDecimal.valueOf(tetoDiaria);

        // Em cada bloco completo de 24 h, cobra-se no máximo uma diária.
        BigDecimal custoDia = precoHora.multiply(BigDecimal.valueOf(24))
                .min(limiteDiaria);
        BigDecimal custoDiasCompletos = custoDia.multiply(
                BigDecimal.valueOf(diasCompletos)
        );
        BigDecimal custoHorasRestantes = precoHora.multiply(
                BigDecimal.valueOf(horasRestantes)
        ).min(limiteDiaria);

        BigDecimal total = custoDiasCompletos.add(custoHorasRestantes)
                .setScale(2, RoundingMode.HALF_UP);

        if (total.compareTo(BigDecimal.valueOf(Double.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException(
                    "Valor calculado excede o limite permitido."
            );
        }

        return total.doubleValue();
    }

    @Override
    public String getDescricao() {
        return String.format(
                Locale.forLanguageTag("pt-BR"),
                "Tarifa padrão: R$ %.2f/h; teto de R$ %.2f por 24 h; 15 min de tolerância.",
                valorHora,
                tetoDiaria
        );
    }

    public double getValorHora() {
        return valorHora;
    }

    public double getTetoDiaria() {
        return tetoDiaria;
    }
}
