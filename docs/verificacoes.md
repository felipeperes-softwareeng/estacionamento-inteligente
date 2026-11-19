# Verificações executáveis (Java 21, sem dependências)

`verificacao/VerificarRegras.java` executa **41 verificações** com `AssertionError` se falhar alguma regra. Não usa JUnit e portanto **não equivale** ao desafio A da Trilha Vá Além.

## Cobertura

- Placas Mercosul/antigas e rejeição de formato inválido.
- Tarifa de 0, 15, 16, 60, 61, 1.440 e 1.441 minutos, diária e desconto promocional.
- Compatibilidade de vagas e bloqueio por manutenção.
- Entrada em vaga incompatível e restrição de placa em duas unidades.
- Mensalista em dia com recarga (cobra só kWh) e inadimplente que volta a ser isento após regularização.
- Pagamento abaixo do devido, pagamento duplicado, liberação sem pagamento, saída duplicada.
- Liberação da vaga após encerramento e imutabilidade da lista de tickets.
- `List<Cobravel>` contendo `Ticket` e `Mensalista`.
- Relatórios e ordenações com Comparable/Comparator.
- Troca real de Strategy.
- Menu captura uma `PlacaInvalidaException` e continua até a saída normal.

`Clock` injetável permite simular permanência de 61 minutos sem atraso no teste. Essa injeção é usada apenas na verificação; a aplicação normal usa o relógio do computador.

## Executar

Veja os comandos em `README.md`. Saída esperada:

```text
SUCESSO: 41 verificações passaram.
```

## Verificação manual antes da apresentação

1. Execute o menu e selecione `Centro`.
2. Demonstre uma entrada válida e recuse uma placa inválida.
3. Tente liberar cancela antes do pagamento (exceção de domínio).
4. Consulte o valor, confirme o pagamento correto e libere a cancela.
5. Mostre uma listagem por código e por status.
6. Mostre os três relatórios (podem aparecer vazios se ainda não houver dados).
7. Troque tarifa na opção 10 e explique o padrão Strategy.
8. Tente ocupar a mesma placa em `Shopping` sem antes liberá-la em `Centro`.
