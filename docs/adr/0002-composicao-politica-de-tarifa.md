# ADR-0002 — Composição via interface para política de tarifa

**Status:** Aceito  
**Data:** 17/11/2026

## Contexto

O estacionamento deve calcular permanência usando tarifas diferentes sem misturar fórmulas ao fluxo de entrada e saída. O desafio C exige troca de estratégia em execução.

## Decisão

`Estacionamento` recebe `PoliticaDeTarifa` no construtor, mantém uma referência à interface e delega os cálculos ao método `calcularValor(long)`. As implementações concretas são `TarifaPadrao` e `TarifaPromocional`. `alterarPoliticaDeTarifa()` permite substituir a política enquanto o programa roda.

## Consequências

- **Positiva:** polimorfismo e injeção de dependência sem `if/else` para decidir fórmulas.
- **Positiva:** a mesma regra pode ser testada isoladamente e usada por várias unidades.
- **Limitação:** alterar a política afeta as próximas consultas/pagamentos de tickets ainda abertos; a política efetiva fica registrada no ticket após pagamento.
