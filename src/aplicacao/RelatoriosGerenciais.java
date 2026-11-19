package aplicacao;

import dominio.Estacionamento;
import dominio.StatusTicket;
import dominio.StatusVaga;
import dominio.Ticket;
import dominio.Vaga;
import dominio.VagaCarro;
import dominio.VagaCarroEletrico;
import dominio.VagaMoto;
import dominio.VagaPcd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** R11/Desafio D - 3 relatórios com Streams: filter/map/sorted/groupingBy. */
public final class RelatoriosGerenciais {
    private final CadastroEstacionamentos cadastro;
    public RelatoriosGerenciais(CadastroEstacionamentos cadastro) {
        if (cadastro == null) throw new IllegalArgumentException("Cadastro obrigatório.");
        this.cadastro = cadastro;
    }
    private Estacionamento unidade() { return cadastro.unidadeSelecionada(); }

    // 1) Ocupação por tipo de vaga, somente as atualmente ocupadas.
    public List<String> ocupacaoPorTipo() {
        Map<String, Long> ocupadas = unidade().getVagas().stream()
                .filter(v -> v.getStatus() == StatusVaga.OCUPADA)
                .collect(Collectors.groupingBy(RelatoriosGerenciais::categoria,
                        TreeMap::new, Collectors.counting()));
        return ocupadas.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + ": " + e.getValue() + " ocupada(s)")
                .toList();
    }

    // 2) Receita efetivamente registrada em tickets PAGO ou ENCERRADO.
    // Mensalidades não entram aqui: são valores contratuais, sem histórico de caixa.
    public List<String> receitaPorTipo() {
        Map<String, Double> somas = unidade().getTickets().stream()
                .filter(t -> t.getStatus() != StatusTicket.ABERTO)
                .collect(Collectors.groupingBy(t -> categoria(t.getVaga()),
                        TreeMap::new, Collectors.summingDouble(Ticket::getValorPago)));
        return somas.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format(Locale.forLanguageTag("pt-BR"),
                        "%s: R$ %.2f recebidos", e.getKey(), e.getValue()))
                .toList();
    }

    // 3) Quantidade de tickets por estado (inclui ABERTO, PAGO e ENCERRADO).
    public List<String> ticketsPorSituacao() {
        Map<StatusTicket, Long> grupos = unidade().getTickets().stream()
                .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));
        return grupos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + ": " + e.getValue() + " ticket(s)")
                .toList();
    }

    // R11: ordenação natural por código (Comparable) e alternativa por estado (Comparator).
    public List<Vaga> listarVagasOrdenadas(boolean porStatus) {
        List<Vaga> vagas = new ArrayList<>(unidade().getVagas());
        if (porStatus) {
            vagas.sort(Comparator.comparing(Vaga::getStatus)
                    .thenComparing(Vaga::getCodigo));
        } else {
            vagas.sort(null); // usa o método compareTo de Vaga.
        }
        return List.copyOf(vagas);
    }

    public List<Ticket> listarTicketsPorEntrada() {
        return unidade().getTickets().stream()
                .sorted(Comparator.comparing(Ticket::getEntrada).reversed())
                .toList();
    }

    public static String categoria(Vaga vaga) {
        if (vaga instanceof VagaCarroEletrico) return "ELETRICO";
        if (vaga instanceof VagaMoto) return "MOTO";
        if (vaga instanceof VagaPcd) return "PCD";
        if (vaga instanceof VagaCarro) return "CARRO";
        return "OUTRO";
    }
}
