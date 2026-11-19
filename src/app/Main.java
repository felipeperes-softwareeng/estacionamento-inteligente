
package app;

import aplicacao.CadastroEstacionamentos;

/**
 * R12 - Ponto de entrada do sistema.
 * Inicializa o cadastro vazio e inicia o menu.
 */
public final class Main {

    public static void main(String[] args) {

        // Cria o cadastro sem estacionamentos.
        CadastroEstacionamentos cadastro =
                new CadastroEstacionamentos();

        // Inicia o menu interativo.
        new MenuConsole(cadastro).executar();
    }
}
