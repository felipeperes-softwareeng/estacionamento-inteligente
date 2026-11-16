
package dominio;

public abstract class Vaga implements Comparable<Vaga> {

    private final String codigo;
    private StatusVaga status;
    private Veiculo veiculoAtual;

    // Construtor
    protected Vaga(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "Código da vaga é obrigatório."
            );
        }

        this.codigo = codigo;
        this.status = StatusVaga.LIVRE;
        this.veiculoAtual = null;
    }

    // Método abstrato: cada tipo de vaga define sua compatibilidade (R04)
    public abstract boolean aceita(Veiculo veiculo);

    // Ocupa a vaga somente se estiver livre e aceitar o veículo
    public void ocupar(Veiculo veiculo) {

        if (veiculo == null) {
            throw new IllegalArgumentException(
                    "O veículo não pode ser nulo."
            );
        }

        if (this.status == StatusVaga.BLOQUEADA) {
            throw new IllegalStateException(
                    "A vaga " + codigo + " está bloqueada para manutenção."
            );
        }

        if (this.status == StatusVaga.OCUPADA) {
            throw new IllegalStateException(
                    "A vaga " + codigo + " já está ocupada."
            );
        }

        if (!this.aceita(veiculo)) {
            throw new IllegalArgumentException(
                    "Veículo incompatível com a vaga " + codigo + "."
            );
        }

        this.veiculoAtual = veiculo;
        this.status = StatusVaga.OCUPADA;
    }

    // Libera somente uma vaga ocupada
    public void liberar() {

        if (this.status != StatusVaga.OCUPADA) {
            throw new IllegalStateException(
                    "A vaga " + codigo + " não está ocupada."
            );
        }

        this.veiculoAtual = null;
        this.status = StatusVaga.LIVRE;
    }

    // Bloqueia somente uma vaga livre
    public void bloquear() {

        if (this.status != StatusVaga.LIVRE) {
            throw new IllegalStateException(
                    "A vaga " + codigo +
                            " não pode ser bloqueada porque não está livre."
            );
        }

        this.status = StatusVaga.BLOQUEADA;
    }

    // Desbloqueia somente uma vaga bloqueada
    public void desbloquear() {

        if (this.status != StatusVaga.BLOQUEADA) {
            throw new IllegalStateException(
                    "A vaga " + codigo + " não está bloqueada."
            );
        }

        this.status = StatusVaga.LIVRE;
    }

    // Ordenação natural pelo código da vaga (R11)
    @Override
    public int compareTo(Vaga outra) {
        return this.codigo.compareTo(outra.codigo);
    }

    // Métodos de consulta
    public String getCodigo() {
        return codigo;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public Veiculo getVeiculoAtual() {
        return veiculoAtual;
    }
}
