package dominio;
/** R05: contrato comum para cobranças de permanência e mensalidade. */
public interface Cobravel {
    double getValorCobrado();
    String getDescricaoCobranca();
}
