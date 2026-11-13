package dominio;

public abstract class Vaga implements Comparable<Vaga> {
    private final String codigo;
    private StatusVaga status;
    private Veiculo veiculoAtual;

    protected Vaga(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código da vaga é obrigatório.");
        }
        this.codigo = codigo;
        this.status = StatusVaga.LIVRE;
        this.veiculoAtual = null;
    }

    // Método abstrato que cada subclasse concreta vai implementar (R04)
    public abstract boolean aceita(Veiculo veiculo);

    // Método concreto que utiliza o método abstrato (Template Method - R04)
    public void ocupar(Veiculo veiculo) {
        if (this.status == StatusVaga.BLOQUEADA) {
            throw new IllegalStateException("A vaga " + codigo + " está bloqueada para manutenção.");
        }
        if (this.status == StatusVaga.OCUPADA) {
            throw new IllegalStateException("A vaga " + codigo + " já está ocupada.");
        }
        if (!this.aceita(veiculo)) {
            throw new IllegalArgumentException("Veículo incompatível com o tipo desta vaga.");
        }
        this.veiculoAtual = veiculo;
        this.status = StatusVaga.OCUPADA;
    }

    public void liberar() {
        this.veiculoAtual = null;
        this.status = StatusVaga.LIVRE;
    }

    public void bloquear() {
        if (this.status == StatusVaga.OCUPADA) {
            throw new IllegalStateException("Não é permitido bloquear uma vaga ocupada.");
        }
        this.status = StatusVaga.BLOQUEADA;
    }

    public void desbloquear() {
        if (this.status == StatusVaga.BLOQUEADA) {
            this.status = StatusVaga.LIVRE;
        }
    }

    @Override
    public int compareTo(Vaga outra) {
        return this.codigo.compareTo(outra.codigo); // Ordenação natural por código (R11)
    }

    public String getCodigo() { return codigo; }
    public StatusVaga getStatus() { return status; }
    public Veiculo getVeiculoAtual() { return veiculoAtual; }
}