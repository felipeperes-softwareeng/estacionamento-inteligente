# 🅿️ Sistema de Estacionamento Inteligente

Sistema backend desenvolvido em **Java** com foco em Orientação a Objetos e modelagem de dados, criado para automatizar e otimizar o fluxo, controle de vagas e cálculo de permanência em um estacionamento.

---

## 👥 Equipe de Desenvolvimento
* **Felipe Peres d'Oliveira**
* **Eduardo**
* **Kauan**
* **Karina**
* **Daniele**
* **Naiara**

**Orientação:** Prof. Jessé Guimarães  
**Instituição / Contexto:** Bacharelado em Engenharia de Software  

---

## 📋 Regras de Negócio e Desafios do Projeto

Durante o desenvolvimento deste sistema, foram estabelecidas regras de negócio fundamentais e superados desafios técnicos específicos para garantir a robustez da aplicação:

### ⚙️ Regras de Negócio Principais
1. **Controle de Vagas e Lotação:** O sistema impede a entrada de novos veículos caso o estacionamento esteja com 100% de ocupação, registrando uma mensagem de alerta.
2. **Cálculo Dinâmico de Permanência:** O valor da estadia é calculado proporcionalmente ao tempo de permanência, aplicando tarifas diferenciadas conforme o tipo de veículo.
3. **Validação de Placas e Status:** Apenas veículos com formato de placa válido podem realizar o check-in, impedindo duplicidade de veículos ativos na mesma vaga.

### 🧠 Desafios Escolhidos e Superados
1. **Modelagem Orientada a Objetos (POO):** Estruturar classes coesas e desacopladas aplicando os pilares da POO (Encapsulamento, Herança e Polimorfismo).
2. **Persistência e Gerenciamento de Dados:** Garantir que o estado das vagas seja atualizado com precisão no sistema.
3. **Tratamento de Exceções Robustas:** Implementar validações preventivas para cenários de erro comuns (como tentar dar saída em um veículo que não está no estacionamento ou liberar uma vaga já vazia) sem quebrar a aplicação.

---

## 🛠️ Tecnologias Utilizadas
* **Linguagem:** Java
* **Conceitos:** Programação Orientada a Objetos (POO), Estruturas de Dados, Tratamento de Exceções
* **Controle de Versão:** Git e GitHub

---

## 🚀 Como Executar o Projeto
*(Instruções de como rodar a aplicação na sua máquina)*

1. Clone o repositório:
   ```bash
   git clone [https://github.com/felipeperes-softwareeng/estacionamento-inteligente.git](https://github.com/felipeperes-softwareeng/estacionamento-inteligente.git)
