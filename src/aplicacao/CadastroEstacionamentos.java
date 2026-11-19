package aplicacao;

import dominio.ControleDeOcupacao;
import dominio.Estacionamento;
import dominio.EstacionamentoInvalidoException;
import dominio.PoliticaDeTarifa;
import dominio.Vaga;
import dominio.VagaCarro;
import dominio.VagaCarroEletrico;
import dominio.VagaMoto;
import dominio.VagaPcd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** RN09 - Cadastro/seleção de unidades e compartilhamento do controle de placas. */
public final class CadastroEstacionamentos {
    private static final int MAX_VAGAS_POR_UNIDADE = 5000;
    private final Map<String, Estacionamento> unidades = new LinkedHashMap<>();
    private final ControleDeOcupacao controle = new ControleDeOcupacao();
    private Estacionamento selecionado;

    public Estacionamento cadastrar(String nome, int carros, int motos, int eletricos,
                                  int pcd, double precoPorKwh, PoliticaDeTarifa politica) {
        if (nome == null || nome.isBlank()) {
            throw new EstacionamentoInvalidoException("Nome da unidade é obrigatório.");
        }
        if (politica == null) throw new EstacionamentoInvalidoException("Política obrigatória.");
        if (carros < 0 || motos < 0 || eletricos < 0 || pcd < 0) {
            throw new EstacionamentoInvalidoException("Quantidades não podem ser negativas.");
        }
        long quantidade = (long) carros + motos + eletricos + pcd;
        if (quantidade < 1 || quantidade > MAX_VAGAS_POR_UNIDADE) {
            throw new EstacionamentoInvalidoException(
                    "Quantidade total deve estar entre 1 e " + MAX_VAGAS_POR_UNIDADE + ".");
        }
        if (!Double.isFinite(precoPorKwh) || precoPorKwh <= 0) {
            throw new EstacionamentoInvalidoException("Preço por kWh deve ser positivo e finito.");
        }
        String chave = nome.trim().toLowerCase(Locale.ROOT);
        if (unidades.containsKey(chave)) {
            throw new EstacionamentoInvalidoException("Já existe unidade chamada " + nome.trim() + ".");
        }

        List<Vaga> vagas = new ArrayList<>();
        for (int i = 1; i <= carros; i++) vagas.add(new VagaCarro(String.format("C%03d", i)));
        for (int i = 1; i <= motos; i++) vagas.add(new VagaMoto(String.format("M%03d", i)));
        for (int i = 1; i <= eletricos; i++) vagas.add(new VagaCarroEletrico(String.format("E%03d", i), precoPorKwh));
        for (int i = 1; i <= pcd; i++) vagas.add(new VagaPcd(String.format("P%03d", i)));

        Estacionamento unidade = new Estacionamento(nome.trim(), vagas, politica, controle);
        unidades.put(chave, unidade);
        return unidade;
    }

    public void selecionar(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new EstacionamentoInvalidoException("Informe o nome da unidade.");
        }
        Estacionamento unidade = unidades.get(nome.trim().toLowerCase(Locale.ROOT));
        if (unidade == null) throw new EstacionamentoInvalidoException("Unidade não cadastrada: " + nome);
        selecionado = unidade;
    }

    public Estacionamento unidadeSelecionada() {
        if (selecionado == null) throw new EstacionamentoInvalidoException("Selecione um estacionamento.");
        return selecionado;
    }

    public List<Estacionamento> listar() { return List.copyOf(unidades.values()); }
    public void limparSelecao() { selecionado = null; }
    public boolean temSelecao() { return selecionado != null; }
}
