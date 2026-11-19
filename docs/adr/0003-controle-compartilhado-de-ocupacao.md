# ADR-0003 — Controle compartilhado de ocupação entre unidades

**Status:** Aceito  
**Data:** 19/11/2026

## Contexto

A RN02 proíbe um veículo em duas vagas simultaneamente. Ao introduzir a RN09 (múltiplas unidades), checar placas somente na lista de vagas de cada estacionamento deixaria a mesma placa entrar em unidades distintas.

## Decisão

`CadastroEstacionamentos` cria um único `ControleDeOcupacao` e o injeta nos estacionamentos que cadastra. `Estacionamento.registrarEntrada()` reserva a placa no controle antes de ocupar a vaga e desfaz a reserva se a operação falhar. `liberarCancela()` encerra o ticket, libera a vaga e retira a placa do controle compartilhado.

## Consequências

- **Positiva:** a restrição RN02 vale entre todas as unidades cadastradas pelo mesmo serviço, inclusive se seus objetos forem usados diretamente.
- **Positiva:** entradas com erro não deixam placa presa no controle global.
- **Limitação:** o controle é em memória e não implementa exclusão mútua para acessos concorrentes de múltiplas threads; o escopo do AV2 é um menu de terminal de um único operador.
