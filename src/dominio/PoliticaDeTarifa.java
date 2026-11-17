package dominio;

/**
 * Contrato para calcular o valor da permanência de um veículo.
 * A implementação pode ser trocada sem alterar quem utiliza a interface.
 */
public interface PoliticaDeTarifa {

    double calcularValor(long minutos);

    String getDescricao();
}
