# ADR-0001 — Vaga como classe abstrata

**Status:** Aceito  
**Data:** 16/11/2026

## Contexto

Todas as vagas compartilham código, estado e regras de ocupação, mas cada categoria aceita tipos diferentes de veículo. Repetir validações em várias classes aumentaria o risco de esquecer uma regra.

## Decisão

`Vaga` será uma **classe abstrata**, com campos privados e métodos concretos `ocupar()`, `liberar()`, `bloquear()` e `desbloquear()`. O método abstrato `aceita(Veiculo)` será especializado por `VagaCarro`, `VagaMoto`, `VagaCarroEletrico` e `VagaPcd`; `ocupar()` chama esse método.

## Consequências

- **Positiva:** regra de ocupação fica centralizada e protege as invariantes RN01, RN02 e RN07.
- **Positiva:** o código demonstra despacho dinâmico e hierarquia polimórfica (R04).
- **Limitação:** novas categorias de vaga exigem nova subclasse; o projeto aceita essa extensão explícita.
