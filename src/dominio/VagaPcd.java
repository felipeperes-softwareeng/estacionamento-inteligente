package dominio;
public final class VagaPcd extends Vaga {
    public VagaPcd(String codigo) { super(codigo); }
    @Override public boolean aceita(Veiculo v) { return v != null && v.getTipo() == TipoVeiculo.PCD; }
}
