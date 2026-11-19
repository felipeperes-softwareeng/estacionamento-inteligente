package dominio;
/** R04/R11 - Hierarquia de vagas; comportamento abstrato usado em método concreto. */
public abstract class Vaga implements Comparable<Vaga> {
    private final String codigo;
    private StatusVaga status;
    private Veiculo veiculoAtual;

    protected Vaga(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código da vaga é obrigatório.");
        }
        this.codigo = codigo.trim();
        this.status = StatusVaga.LIVRE;
    }
    public abstract boolean aceita(Veiculo veiculo);

    // RN01/RN02 - A implementação concreta decide o tipo permitido.
    void ocupar(Veiculo veiculo) {
        if (veiculo == null) throw new IllegalArgumentException("O veículo é obrigatório.");
        if (status == StatusVaga.BLOQUEADA) throw new VagaBloqueadaException(codigo);
        if (status == StatusVaga.OCUPADA) throw new VagaOcupadaException(codigo);
        if (!aceita(veiculo)) throw new VagaIncompativelException(codigo);
        veiculoAtual = veiculo;
        status = StatusVaga.OCUPADA;
    }
    void liberar() {
        if (status != StatusVaga.OCUPADA) {
            throw new IllegalStateException("A vaga " + codigo + " não está ocupada.");
        }
        veiculoAtual = null;
        status = StatusVaga.LIVRE;
    }
    public void bloquear() {
        if (status == StatusVaga.OCUPADA) throw new VagaOcupadaException(codigo);
        if (status == StatusVaga.BLOQUEADA) throw new VagaBloqueadaException(codigo);
        status = StatusVaga.BLOQUEADA;
    }
    public void desbloquear() {
        if (status != StatusVaga.BLOQUEADA) {
            throw new IllegalStateException("A vaga " + codigo + " não está bloqueada.");
        }
        status = StatusVaga.LIVRE;
    }
    @Override public int compareTo(Vaga outra) { return codigo.compareTo(outra.codigo); }
    public String getCodigo() { return codigo; }
    public StatusVaga getStatus() { return status; }
    public Veiculo getVeiculoAtual() { return veiculoAtual; }
}
