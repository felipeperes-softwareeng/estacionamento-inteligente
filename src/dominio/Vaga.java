
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

    // R04 - Cada tipo de vaga define sua compatibilidade
    public abstract boolean aceita(Veiculo veiculo);

    // RN01 e RN02 - Ocupação controlada pelo domínio
    void ocupar(Veiculo veiculo) {

        if (veiculo == null) {
            throw new IllegalArgumentException(
                    "O veículo não pode ser nulo."
            );
        }

        if (this.status == StatusVaga.BLOQUEADA) {
            throw new VagaBloqueadaException(codigo);
        }

        if (this.status == StatusVaga.OCUPADA) {
            throw new VagaOcupadaException(codigo);
        }

        if (!this.aceita(veiculo)) {
            throw new VagaIncompativelException(codigo);
        }

        this.veiculoAtual = veiculo;
        this.status = StatusVaga.OCUPADA;
    }

    // RN05 - Liberação coordenada pelo estacionamento
    void liberar() {

        if (this.status != StatusVaga.OCUPADA) {
            throw new IllegalStateException(
                    "A vaga " + codigo + " não está ocupada."
            );
        }

        this.veiculoAtual = null;
        this.status = StatusVaga.LIVRE;
    }

    // RN07 - Bloqueio para manutenção
    public void bloquear() {

        if (this.status == StatusVaga.OCUPADA) {
            throw new VagaOcupadaException(codigo);
        }

        if (this.status == StatusVaga.BLOQUEADA) {
            throw new VagaBloqueadaException(codigo);
        }

        this.status = StatusVaga.BLOQUEADA;
    }

    // RN07 - Desbloqueio da vaga
    public void desbloquear() {

        if (this.status != StatusVaga.BLOQUEADA) {
            throw new IllegalStateException(
                    "A vaga " + codigo + " não está bloqueada."
            );
        }

        this.status = StatusVaga.LIVRE;
    }

    // R11 - Ordenação natural pelo código
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
