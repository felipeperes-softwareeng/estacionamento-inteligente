# ADR-0002: Utilizar Composição e Interface para a Política de Tarifa (Strategy)

Status: Aceito · Data: 16/11/2026

## Contexto
O cálculo de tarifa do estacionamento exige tolerância de 15 minutos, cobrança por hora/fração e teto de diária, além de poder variar conforme a estratégia comercial do pátio. Colocar a fórmula fixa dentro da classe `Estacionamento` ou `Ticket` acoplaria a regra de cobrança e dificultaria mudanças.

## Decisão
Criamos a interface `PoliticaDeTarifa` e a injetamos por composição no construtor de `Estacionamento` (requisito R06), aplicando o padrão Strategy (Desafio C) para permitir a troca da regra de cálculo em tempo de execução.

## Consequências
+ Baixo acoplamento: o `Estacionamento` depende de uma abstração (interface) e não da implementação concreta.
+ Permite trocar ou criar novas regras de tarifação (`TarifaPadrao`, `TarifaPromocional`) sem alterar o código de `Estacionamento`.
- Aumenta ligeiramente o número de classes no pacote de domínio.