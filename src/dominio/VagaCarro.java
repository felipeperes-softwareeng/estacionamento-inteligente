package dominio;

public class VagaCarro extends Vaga {

    public VagaCarro(String codigo) {
        super(codigo);
    }

    @Override
    public boolean aceita(Veiculo veiculo) {
        return veiculo != null
                && veiculo.getTipo() == TipoVeiculo.CARRO;
    }
}
