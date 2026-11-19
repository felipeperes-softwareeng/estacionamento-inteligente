package dominio;

import java.util.HashSet;
import java.util.Set;

/**
 * RN02 - Registro comum de placas presentes nas unidades do mesmo cadastro.
 * Instâncias de Estacionamento que compartilham este controle não admitem
 * a mesma placa simultaneamente em unidades diferentes.
 */
public final class ControleDeOcupacao {
    private final Set<Placa> placasPresentes = new HashSet<>();

    void registrarEntrada(Placa placa) {
        if (placa == null) throw new IllegalArgumentException("A placa é obrigatória.");
        if (!placasPresentes.add(placa)) {
            throw new VeiculoJaEstacionadoException(placa.valor());
        }
    }

    void desfazerEntrada(Placa placa) {
        placasPresentes.remove(placa);
    }

    void validarSaida(Placa placa) {
        if (!placasPresentes.contains(placa)) {
            throw new IllegalStateException("Não existe ocupação registrada para " + placa.valor() + ".");
        }
    }

    void registrarSaida(Placa placa) {
        validarSaida(placa);
        placasPresentes.remove(placa);
    }
}
