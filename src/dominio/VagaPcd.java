package dominio;

public class VagaPcd extends Vaga {

    public VagaPcd(String codigo) {
        super(codigo);
    }

    @Override
    public boolean aceita(Veiculo veiculo) {
        return veiculo != null
                && veiculo.getTipo() == TipoVeiculo.PCD;
    }
}
