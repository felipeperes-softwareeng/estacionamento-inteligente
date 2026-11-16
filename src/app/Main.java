
package app;

import dominio.*;

public class Main {

    public static void main(String[] args) {

        // Criando um veículo
        Placa placa = new Placa("ABC1D23");
        Veiculo carro = new Veiculo(placa, TipoVeiculo.CARRO);

        // Criando uma vaga de carro
        Vaga vaga = new VagaCarro("A01");

        // Estado inicial
        System.out.println("Estado inicial: " + vaga.getStatus());

        // Ocupando a vaga
        vaga.ocupar(carro);
        System.out.println("Após ocupar: " + vaga.getStatus());

        // Liberando a vaga
        vaga.liberar();
        System.out.println("Após liberar: " + vaga.getStatus());

        // Bloqueando para manutenção
        vaga.bloquear();
        System.out.println("Após bloquear: " + vaga.getStatus());

        // Desbloqueando
        vaga.desbloquear();
        System.out.println("Após desbloquear: " + vaga.getStatus());
    }
}
