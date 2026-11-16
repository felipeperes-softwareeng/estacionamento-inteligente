package dominio;

public class VagaMoto extends Vaga {

    public VagaMoto(String codigo) {
        super(codigo);
    }

    @Override
    public boolean aceita(Veiculo veiculo) {
        return veiculo != null
                && veiculo.getTipo() == TipoVeiculo.MOTO;
    }
}
