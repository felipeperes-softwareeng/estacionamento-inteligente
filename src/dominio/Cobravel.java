package dominio;

/**
 * Contrato para objetos que representam uma cobrança do estacionamento.
 * O valor cobrado e o valor efetivamente recebido são conceitos diferentes.
 */
public interface Cobravel {

    double getValorCobrado();

    String getDescricaoCobranca();
}
