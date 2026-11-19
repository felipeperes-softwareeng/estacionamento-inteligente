package dominio;
/** R06/Strategy: contrato para tarifa de permanência. */
public interface PoliticaDeTarifa {
    double calcularValor(long minutos);
    String getDescricao();
}
