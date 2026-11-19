package dominio;
public final class VagaCarro extends Vaga {
    public VagaCarro(String codigo) { super(codigo); }
    @Override public boolean aceita(Veiculo v) { return v != null && v.getTipo() == TipoVeiculo.CARRO; }
}
