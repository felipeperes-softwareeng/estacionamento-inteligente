package dominio;
public final class VagaMoto extends Vaga {
    public VagaMoto(String codigo) { super(codigo); }
    @Override public boolean aceita(Veiculo v) { return v != null && v.getTipo() == TipoVeiculo.MOTO; }
}
