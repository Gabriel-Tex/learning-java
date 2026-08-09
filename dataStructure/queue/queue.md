# Queue em Java

## O que é

`Queue<T>` representa uma **fila**, estrutura de dados que segue (por padrão) a ordem **FIFO** (*First In, First Out* — o primeiro elemento inserido é o primeiro a ser removido).

Características:
- Elementos são inseridos em uma extremidade (**fim da fila**) e removidos em outra (**início da fila**).
- Interface faz parte do pacote `java.util` e estende `Collection<T>`.
- Muito usada em cenários como: filas de processamento, filas de impressão, mensagens (produtor/consumidor), busca em largura (BFS) em grafos/árvores, escalonamento de tarefas.

---

## Principais implementações

| Implementação | Estrutura interna | Ordenação | Observações |
|---|---|---|---|
| `LinkedList` | Lista duplamente encadeada | Ordem de inserção (FIFO) | Implementa tanto `Queue` quanto `Deque` e `List` |
| `ArrayDeque` | Array redimensionável | Ordem de inserção (FIFO) | Mais eficiente que `LinkedList` na maioria dos casos; **implementação recomendada** para pilha/fila |
| `PriorityQueue` | Heap binário | **Não é FIFO** — ordena pelo `compareTo`/`Comparator` (menor prioridade sai primeiro) | Útil quando a ordem de saída depende de prioridade, não de chegada |

> A documentação oficial recomenda `ArrayDeque` em vez de `LinkedList` para implementar pilhas e filas, por ter melhor desempenho e não gerar lixo de objetos (nós encadeados) desnecessário.

---

## Métodos principais

A interface `Queue` oferece dois "estilos" de métodos: um que lança exceção em caso de falha, e outro que retorna um valor especial (`null` ou `false`).

| Operação | Lança exceção | Retorna valor especial |
|---|---|---|
| Inserir | `add(e)` | `offer(e)` |
| Remover | `remove()` | `poll()` |
| Examinar (sem remover) | `element()` | `peek()` |

- **`add(e)` / `offer(e)`**: insere um elemento no fim da fila. `offer` é preferível em filas com capacidade limitada, pois retorna `false` em vez de lançar exceção quando não há espaço.
- **`remove()` / `poll()`**: remove e retorna o elemento do início da fila. `remove()` lança `NoSuchElementException` se a fila estiver vazia; `poll()` retorna `null`.
- **`element()` / `peek()`**: retorna (sem remover) o elemento do início da fila. `element()` lança exceção se vazia; `peek()` retorna `null`.

> **Boa prática:** em código de produção, prefira `offer`, `poll` e `peek` — evitam exceções e deixam o fluxo mais previsível.

---

## Demo — fila simples com `LinkedList`

```java
package application;

import java.util.LinkedList;
import java.util.Queue;

public class Program {
    public static void main(String[] args) {
        Queue<String> queue = new LinkedList<>();

        queue.offer("Cliente 1");
        queue.offer("Cliente 2");
        queue.offer("Cliente 3");

        System.out.println("Próximo da fila: " + queue.peek()); // Cliente 1

        while (!queue.isEmpty()) {
            System.out.println("Atendendo: " + queue.poll());
        }
        // Atendendo: Cliente 1
        // Atendendo: Cliente 2
        // Atendendo: Cliente 3
    }
}
```

---

## Demo — fila com `ArrayDeque` (implementação recomendada)

```java
package application;

import java.util.ArrayDeque;
import java.util.Queue;

public class Program {
    public static void main(String[] args) {
        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(10);
        queue.offer(20);
        queue.offer(30);

        System.out.println(queue.poll()); // 10
        System.out.println(queue.peek()); // 20
        System.out.println(queue.size()); // 2
    }
}
```

---

## Deque — fila de duas extremidades

`Deque<T>` (*Double Ended Queue*) permite inserir e remover elementos em **ambas as extremidades**, funcionando tanto como fila (FIFO) quanto como pilha (LIFO — *Last In, First Out*).

Principais métodos:

| Operação | Início da fila | Fim da fila |
|---|---|---|
| Inserir | `addFirst(e)` / `offerFirst(e)` | `addLast(e)` / `offerLast(e)` |
| Remover | `removeFirst()` / `pollFirst()` | `removeLast()` / `pollLast()` |
| Examinar | `peekFirst()` | `peekLast()` |

Uso como **pilha (Stack)**:

```java
Deque<Integer> stack = new ArrayDeque<>();
stack.push(1);   // equivalente a addFirst
stack.push(2);
stack.push(3);
System.out.println(stack.pop()); // 3 (LIFO)
```

> A classe legada `Stack` (de 1998) é considerada obsoleta; a documentação recomenda usar `Deque` (via `ArrayDeque`) no lugar dela.

---

## PriorityQueue — fila com prioridade

Ao contrário de `LinkedList`/`ArrayDeque`, a `PriorityQueue` **não** segue a ordem de inserção: o elemento removido é sempre o de **maior prioridade** (por padrão, o "menor" segundo `compareTo`, ou seja, comportamento de *min-heap*).

```java
package application;

import java.util.PriorityQueue;
import java.util.Queue;

public class Program {
    public static void main(String[] args) {
        Queue<Integer> pq = new PriorityQueue<>();

        pq.offer(50);
        pq.offer(10);
        pq.offer(30);

        while (!pq.isEmpty()) {
            System.out.println(pq.poll());
        }
        // 10
        // 30
        // 50
    }
}
```

Para ordem decrescente (maior prioridade primeiro), usar um `Comparator`:

```java
Queue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
```

Com objetos personalizados, a classe deve implementar `Comparable<T>` ou receber um `Comparator<T>` no construtor (mesmo princípio já visto em `TreeSet`/`TreeMap`).

---

## Comparativo rápido: List vs Set vs Queue vs Map

| Coleção | Permite duplicatas? | Possui posição/índice? | Ordem de acesso |
|---|---|---|---|
| `List` | Sim | Sim | Pela posição (índice) |
| `Set` | Não | Não | Depende da implementação (hash, ordenada, inserção) |
| `Queue` | Sim (em geral) | Não | FIFO (ou por prioridade, no caso de `PriorityQueue`) |
| `Map` | Não (chaves) | Não | Pela chave |
