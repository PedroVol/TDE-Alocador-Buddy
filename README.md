# TDE – Alocador Buddy Binário

## Implementação do Alocador Buddy Binário com Estruturas de Dados Auxiliares

Projeto desenvolvido para o TDE da disciplina **Resolução de Problemas Estruturados em Computação**, com o objetivo de implementar um simulador de gerenciamento de memória utilizando o método **Buddy System**.

O programa foi desenvolvido em **Java** e utiliza estruturas de dados implementadas manualmente com nós encadeados, integrando:

- fila de requisições pendentes;
- pilha de histórico para desfazer operações;
- listas encadeadas de blocos livres;
- árvore binária para representação da memória.

---

## Sumário

1. [Descrição do projeto](#descrição-do-projeto)
2. [Conceito do Buddy System](#conceito-do-buddy-system)
3. [Configurações da memória](#configurações-da-memória)
4. [Estruturas de dados implementadas](#estruturas-de-dados-implementadas)
5. [Classes do projeto](#classes-do-projeto)
6. [Funcionamento das operações](#funcionamento-das-operações)
7. [Menu do sistema](#menu-do-sistema)
8. [Arquivo dataset.txt](#arquivo-datasettxt)
9. [Como compilar e executar](#como-compilar-e-executar)
10. [Exemplo de uso](#exemplo-de-uso)
11. [Análise de fragmentação interna](#análise-de-fragmentação-interna)
12. [Observações sobre a implementação](#observações-sobre-a-implementação)
13. [Integrantes](#integrantes)

---

## Descrição do projeto

Este projeto simula o funcionamento de um **alocador de memória Buddy Binário**. O alocador recebe pedidos de alocação de memória, arredonda o tamanho solicitado para a próxima potência de 2 e procura um bloco livre adequado.

Quando não existe um bloco livre exatamente do tamanho necessário, o sistema divide blocos maiores em dois blocos menores, processo chamado de **split**. Quando um bloco é liberado, o sistema verifica se o seu bloco parceiro, ou **buddy**, também está livre. Caso esteja, os dois blocos são unidos novamente por meio do **merge**.

A memória é representada por uma **árvore binária**, em que:

- a raiz representa toda a memória disponível;
- cada divisão cria dois filhos de mesmo tamanho;
- cada nó representa um bloco de memória;
- blocos livres podem ser divididos;
- blocos ocupados armazenam um identificador.

O sistema também mantém uma fila de requisições pendentes, uma pilha de histórico para desfazer operações e listas de blocos livres separadas por tamanho.

---

## Conceito do Buddy System

O **Buddy System** é uma técnica de gerenciamento de memória em que todos os blocos possuem tamanho baseado em potências de 2.

Exemplo:

| Tamanho solicitado | Tamanho realmente alocado | Fragmentação interna |
|---:|---:|---:|
| 5 KB | 8 KB | 3 KB |
| 100 KB | 128 KB | 28 KB |
| 3000 KB | 4096 KB | 1096 KB |
| 5000 KB | 8192 KB | 3192 KB |

Esse arredondamento facilita a divisão e a união de blocos, pois cada bloco pode ser dividido em dois blocos de mesmo tamanho.

---

## Configurações da memória

O projeto utiliza as seguintes configurações:

| Configuração | Valor |
|---|---:|
| Memória total | 32768 KB |
| Memória total em MB | 32 MB |
| Buddy mínimo | 4 KB |
| Quantidade de listas livres | 14 |
| Menor bloco possível | 4 KB |
| Maior bloco possível | 32768 KB |

Os tamanhos possíveis de blocos são:

```text
4 KB, 8 KB, 16 KB, 32 KB, 64 KB, 128 KB, 256 KB,
512 KB, 1024 KB, 2048 KB, 4096 KB, 8192 KB,
16384 KB, 32768 KB
```

---

## Estruturas de dados implementadas

Todas as estruturas foram feitas manualmente, sem uso de classes prontas como `ArrayList`, `LinkedList`, `Queue` ou `Stack`.

### 1. Árvore binária

A árvore binária representa a memória principal.

Cada nó possui:

- tamanho do bloco;
- estado do bloco;
- identificador da alocação;
- referência para o pai;
- referência para o filho esquerdo;
- referência para o filho direito.

Estados possíveis:

```text
LIVRE
OCUPADO
DIVIDIDO
```

### 2. Fila de pendentes

A fila armazena requisições de alocação que não puderam ser atendidas no momento do pedido.

Ela segue o comportamento **FIFO**:

```text
First In, First Out
Primeiro a entrar, primeiro a sair
```

Quando uma liberação ou merge acontece, o programa tenta atender novamente as requisições pendentes. Se uma requisição ainda não puder ser atendida, ela volta para o final da fila.

Operações implementadas:

- enfileirar;
- desenfileirar;
- espiar;
- estaVazia;
- tamanho;
- exibir.

### 3. Pilha de histórico

A pilha registra operações bem-sucedidas para permitir a opção **Desfazer**.

Ela segue o comportamento **LIFO**:

```text
Last In, First Out
Último a entrar, primeiro a sair
```

No desfazer, a última operação registrada é removida da pilha e o estado anterior da árvore é restaurado.

Operações implementadas:

- empilhar;
- desempilhar;
- topo;
- estaVazia;
- tamanho.

### 4. Listas encadeadas de blocos livres

O projeto possui listas separadas para cada tamanho de bloco livre. Cada posição do vetor `listasLivres` representa uma lista encadeada de blocos livres daquele tamanho.

Exemplo:

| Índice | Tamanho representado |
|---:|---:|
| 0 | 4 KB |
| 1 | 8 KB |
| 2 | 16 KB |
| 3 | 32 KB |
| 4 | 64 KB |
| 5 | 128 KB |
| 6 | 256 KB |
| 7 | 512 KB |
| 8 | 1024 KB |
| 9 | 2048 KB |
| 10 | 4096 KB |
| 11 | 8192 KB |
| 12 | 16384 KB |
| 13 | 32768 KB |

Essas listas permitem visualizar a quantidade de blocos livres disponíveis em cada nível, semelhante ao conceito de `buddyinfo`.

---

## Classes do projeto
Observação: neste projeto, as classes Fila, Pilha e Lista representam os nós encadeados, enquanto NoFila, NoPilha e NoLista representam as estruturas que manipulam esses nós.
### `Main.java`

Classe principal do sistema.

Responsabilidades:

- criar o objeto `AlocadorBuddy`;
- exibir o menu de opções;
- ler as entradas do usuário;
- chamar as operações de alocação, liberação, desfazer e exibição;
- carregar e processar o arquivo `dataset.txt`.

---

### `AlocadorBuddy.java`

Classe responsável pela lógica principal do alocador.

Responsabilidades:

- criar a memória inicial de 32768 KB;
- controlar a árvore binária;
- realizar alocações;
- realizar liberações;
- executar splits;
- executar merges;
- atualizar listas livres;
- controlar a fila de pendentes;
- registrar operações na pilha de histórico;
- desfazer a última operação;
- exibir a árvore de memória;
- exibir fila e listas livres.

Principais atributos:

```java
private No raiz;
private final int MEMORIA_TOTAL = 32768;
private final int BUDDY_MINIMO = 4;
private NoFila filaPendentes;
private NoPilha historico;
private NoLista[] listasLivres;
```

---

### `No.java`

Representa um nó da árvore binária.

Cada objeto `No` representa um bloco de memória.

Atributos:

```java
int tamanho;
String estado;
String id;
No pai;
No esquerdo;
No direito;
```

Também possui o método `naoDividido()`, que verifica se o nó não possui filhos.

---

### `Fila.java`

Representa o nó da fila de requisições pendentes.

Atributos:

```java
String id;
int tamanho;
Fila proximo;
```

Cada nó guarda o identificador da requisição, o tamanho solicitado e a referência para o próximo item da fila.

---

### `NoFila.java`

Representa a estrutura da fila encadeada.

Responsabilidades:

- inserir requisições no final da fila;
- remover requisições do início;
- consultar o primeiro item;
- verificar se a fila está vazia;
- informar o tamanho da fila;
- exibir os itens pendentes.

---

### `Pilha.java`

Representa o nó da pilha de histórico.

Atributos:

```java
String operacao;
String id;
int tamanho;
No estadoAnterior;
Pilha proximo;
```

Cada nó armazena uma operação realizada e, quando utilizado o desfazer por cópia, guarda também o estado anterior da árvore.

---

### `NoPilha.java`

Representa a estrutura da pilha encadeada.

Responsabilidades:

- empilhar operações bem-sucedidas;
- desempilhar a última operação;
- consultar o topo;
- verificar se a pilha está vazia;
- informar o tamanho da pilha.

---

### `Lista.java`

Representa o nó da lista encadeada de blocos livres.

Atributos:

```java
No bloco;
Lista proximo;
```

Cada nó aponta para um bloco livre da árvore.

---

### `NoLista.java`

Representa uma lista encadeada de blocos livres.

Responsabilidades:

- inserir bloco livre;
- remover bloco livre;
- buscar o primeiro bloco disponível;
- verificar se a lista está vazia;
- verificar se um bloco já está na lista;
- informar o tamanho da lista;
- exibir os blocos livres.

---

## Funcionamento das operações

## Alocação

A operação de alocação recebe um identificador e um tamanho em KB.

Etapas:

1. Verifica se já existe um bloco com o mesmo identificador.
2. Calcula a próxima potência de 2 maior ou igual ao tamanho solicitado.
3. Garante que o tamanho real não seja menor que 4 KB.
4. Procura um bloco livre adequado nas listas livres.
5. Se necessário, divide blocos maiores usando split.
6. Marca o bloco encontrado como `OCUPADO`.
7. Armazena o identificador no bloco.
8. Atualiza as listas de blocos livres.
9. Calcula e exibe a fragmentação interna.
10. Registra a operação na pilha de histórico.

Exemplo:

```text
Solicitado: 5000 KB
Bloco usado: 8192 KB
Fragmentação interna: 3192 KB
```

---

## Split

O split ocorre quando existe um bloco livre maior que o necessário.

Exemplo:

```text
32768 KB é dividido em 16384 KB + 16384 KB
16384 KB é dividido em 8192 KB + 8192 KB
```

A divisão continua até chegar ao menor bloco suficiente para atender a requisição.

Durante o split:

- o bloco maior é removido da lista livre correspondente;
- o bloco é marcado como `DIVIDIDO`;
- dois filhos são criados;
- os filhos são inseridos na lista do novo tamanho.

---

## Liberação

A liberação recebe o identificador de um bloco ocupado.

Etapas:

1. Procura o bloco pelo identificador.
2. Marca o bloco como `LIVRE`.
3. Remove o identificador do bloco.
4. Tenta realizar merge com o buddy.
5. Atualiza as listas livres.
6. Registra a operação na pilha de histórico.
7. Tenta atender a fila de pendentes.

---

## Merge

O merge ocorre quando um bloco livre possui um buddy também livre.

Exemplo:

```text
4096 KB + 4096 KB = 8192 KB
8192 KB + 8192 KB = 16384 KB
16384 KB + 16384 KB = 32768 KB
```

O merge pode acontecer em cascata, subindo pela árvore até a raiz.

Durante o merge:

- o programa verifica o pai do bloco;
- confere se os dois filhos estão livres;
- remove os filhos;
- marca o pai como `LIVRE`;
- tenta repetir o processo no nível acima.

---

## Desfazer

A opção desfazer utiliza a pilha de histórico.

Como a pilha segue LIFO, a última operação realizada é a primeira a ser desfeita.

Na implementação com restauração de estado, antes de cada operação bem-sucedida é feita uma cópia da árvore. Ao selecionar a opção de desfazer, o programa restaura a árvore anterior e atualiza as listas livres.

Esse método evita inconsistências, pois não tenta apenas executar a operação contrária. Em vez disso, ele retorna exatamente ao estado anterior da memória.

---

## Fila de pendentes

Quando uma alocação falha por falta de bloco livre adequado, a requisição é enviada para a fila de pendentes.

Exemplo:

```text
Falha ao alocar backup01. Enviado para fila de pendentes.
```

Depois de uma liberação ou merge, o sistema tenta atender novamente os itens da fila.

Se a requisição puder ser atendida, ela é alocada e removida da fila.

Se não puder, volta para o final da fila.

---

## Menu do sistema

Ao executar o programa, o usuário visualiza o seguinte menu:

```text
===== ALOCADOR BUDDY BINARIO =====
1 - Alocar
2 - Liberar
3 - Desfazer
4 - Exibir memoria
5 - Exibir fila de pendentes
6 - Exibir listas de blocos livres
7 - Carregar dataset
8 - Sair
```

Descrição das opções:

| Opção | Função |
|---:|---|
| 1 | Aloca um bloco de memória informando ID e tamanho |
| 2 | Libera um bloco a partir do ID |
| 3 | Desfaz a última operação bem-sucedida |
| 4 | Exibe a árvore hierarquizada da memória |
| 5 | Exibe a fila de requisições pendentes |
| 6 | Exibe as listas de blocos livres por tamanho |
| 7 | Carrega as operações do arquivo `dataset.txt` |
| 8 | Encerra o programa |

---

## Arquivo `dataset.txt`

O arquivo `dataset.txt` contém operações automáticas para testar o alocador.

Formato aceito:

```text
ALOCAR id tamanho_em_KB
LIBERAR id
```

Exemplos:

```text
ALOCAR img01 8
ALOCAR dados01 5120
LIBERAR img01
ALOCAR backup01 12288
```

Linhas vazias e comentários iniciados por `#` são ignorados pelo programa.

O dataset testa:

- alocações pequenas;
- alocações médias;
- alocações grandes;
- divisões sucessivas;
- liberações intercaladas;
- merge em cascata;
- fila de pendentes;
- fragmentação interna;
- exibição do estado da memória após cada operação.

---

## Como compilar e executar

### Requisitos

- Java JDK instalado.
- Terminal, Prompt de Comando ou IntelliJ IDEA.

### Compilar pelo terminal

Na pasta do projeto, execute:

```bash
javac *.java
```

### Executar

Depois da compilação, execute:

```bash
java Main
```

### Executar pelo IntelliJ IDEA

1. Abra o projeto no IntelliJ IDEA.
2. Verifique se o JDK está configurado.
3. Abra o arquivo `Main.java`.
4. Clique em **Run**.
5. Use o menu exibido no console.

---

## Exemplo de uso

Entrada:

```text
1
Digite o ID da alocacao: img01
Digite o tamanho em KB: 8
```

Saída esperada:

```text
Alocado: img01
Solicitado: 8 KB
Bloco usado: 8 KB
Fragmentacao interna: 0 KB
```

Outro exemplo:

```text
1
Digite o ID da alocacao: dados01
Digite o tamanho em KB: 5120
```

Saída esperada:

```text
Alocado: dados01
Solicitado: 5120 KB
Bloco usado: 8192 KB
Fragmentacao interna: 3072 KB
```

Exemplo de liberação:

```text
2
Digite o ID para liberar: dados01
```

Saída esperada:

```text
Liberado: dados01
```

Exemplo de falha de alocação:

```text
Falha ao alocar backup01. Enviado para fila de pendentes.
```

---

## Análise de fragmentação interna

A fragmentação interna acontece quando o tamanho solicitado é menor que o tamanho realmente alocado.

Como o Buddy System trabalha com potências de 2, o programa arredonda cada requisição para o menor bloco possível capaz de armazená-la.

Fórmula utilizada:

```text
Fragmentação interna = tamanho do bloco alocado - tamanho solicitado
```

Exemplos:

| Solicitação | Bloco alocado | Fragmentação interna |
|---:|---:|---:|
| 8 KB | 8 KB | 0 KB |
| 12 KB | 16 KB | 4 KB |
| 24 KB | 32 KB | 8 KB |
| 64 KB | 64 KB | 0 KB |
| 5120 KB | 8192 KB | 3072 KB |
| 12288 KB | 16384 KB | 4096 KB |

Apesar de gerar desperdício interno, essa estratégia facilita a organização da memória e permite que os blocos sejam unidos novamente quando seus buddies estiverem livres.

---

## Observações sobre a implementação

- A memória total é fixa em 32768 KB.
- O menor bloco permitido é 4 KB.
- O programa não divide blocos abaixo do buddy mínimo.
- As estruturas de fila, pilha e lista foram implementadas manualmente.
- A árvore binária é manipulada por referências entre nós.
- O split cria dois filhos de mesmo tamanho.
- O merge remove os filhos e restaura o bloco pai como livre.
- A fila de pendentes é tentada novamente após liberações e merges.
- As listas livres são organizadas por tamanho de bloco.
- A opção de dataset permite testar várias operações automaticamente.

---

## Organização dos arquivos

```text
TDE - AlocadorBuddy/
│
├── AlocadorBuddy.java
├── Main.java
├── No.java
├── Fila.java
├── NoFila.java
├── Pilha.java
├── NoPilha.java
├── Lista.java
├── NoLista.java
├── dataset.txt
└── README.md
```

---

## Integrantes
Pedro Muller Volpe - BCC


---

## Conclusão

O projeto implementa um simulador funcional do Alocador Buddy Binário, utilizando árvore binária para representar a memória e estruturas auxiliares para controlar pendências, histórico e blocos livres.

A implementação permite observar, na prática, os principais comportamentos do Buddy System: alocação por potência de 2, fragmentação interna, split, merge em cascata, fila de pendentes e visualização hierárquica da memória.
