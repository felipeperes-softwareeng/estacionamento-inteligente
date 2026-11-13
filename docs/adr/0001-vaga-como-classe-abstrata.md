# ADR-0001: Modelar Vaga como classe abstrata, e não como interface

Status: Aceito · Data: 16/11/2026

## Contexto
O estacionamento possui quatro tipos de vagas (Carro, Moto, Elétrico e PcD). Todas compartilham estado (código identificador, status de ocupação/bloqueio e veículo estacionado) e regras comuns (não permitir dois veículos na mesma vaga e impedir bloqueio de vaga ocupada), mas cada tipo possui sua própria regra de compatibilidade de veículo e cobrança adicional (como recarga em kWh na vaga elétrica).

## Decisão
Modelamos `Vaga` como uma classe `abstract` que guarda os atributos e métodos concretos de ocupação/manutenção (`ocupar()`, `liberar()`, `bloquear()`), declarando o método abstrato `aceita(Veiculo)` para que cada subclasse concreta (`VagaCarro`, `VagaMoto`, `VagaEletrica`, `VagaPcd`) defina sua regra de compatibilidade.

## Consequências
+ Evita duplicação de código das regras de ocupação e bloqueio por manutenção (RN02 e RN07).
+ Garante o cumprimento do requisito R04 com polimorfismo real (Template Method), eliminando `if/else` de tipos de vaga.
- Como Java possui herança simples, as subclasses de `Vaga` não podem herdar de outra superclasse.