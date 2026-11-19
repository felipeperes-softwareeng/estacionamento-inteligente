package aplicacao;

import dominio.Estacionamento;
import dominio.Mensalista;
import dominio.Placa;
import dominio.PoliticaDeTarifa;
import dominio.Ticket;
import dominio.Vaga;
import dominio.Veiculo;
import java.util.List;

/** Caso de uso: a camada de aplicação coordena a unidade ativa sem imprimir no console. */
public final class OperacoesEstacionamento {
    private final CadastroEstacionamentos cadastro;
    public OperacoesEstacionamento(CadastroEstacionamentos cadastro) {
        if (cadastro == null) throw new IllegalArgumentException("Cadastro obrigatório.");
        this.cadastro = cadastro;
    }
    public Estacionamento selecionado() { return cadastro.unidadeSelecionada(); }
    public Ticket entrada(Veiculo v, String codigo) { return selecionado().registrarEntrada(v, codigo); }
    public double consultarCobranca(String id, double kwh) { return selecionado().calcularValorTicket(id, kwh); }
    public double receberPagamento(String id, double valor, double kwh) {
        return selecionado().pagarTicket(id, valor, kwh);
    }
    public void liberarCancela(String id) { selecionado().liberarCancela(id); }
    public void bloquear(String codigo) { selecionado().bloquearVaga(codigo); }
    public void desbloquear(String codigo) { selecionado().desbloquearVaga(codigo); }
    public void cadastrarMensalista(Mensalista mensalista) { selecionado().cadastrarMensalista(mensalista); }
    public void registrarAtraso(Placa placa) { selecionado().registrarAtrasoMensalista(placa); }
    public void regularizarMensalidade(Placa placa, double valor) {
        selecionado().regularizarMensalidade(placa, valor);
    }
    public void trocarTarifa(PoliticaDeTarifa politica) { selecionado().alterarPoliticaDeTarifa(politica); }
    public List<Vaga> vagas() { return selecionado().getVagas(); }
    public List<Ticket> tickets() { return selecionado().getTickets(); }
    public List<Mensalista> mensalistas() { return selecionado().getMensalistas(); }
    public List<String> cobrancas() { return selecionado().listarCobrancas(); }
}
