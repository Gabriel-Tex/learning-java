# Set em Java

## O que é

`Set<T>` representa um **conjunto de elementos**, similar ao conceito de conjunto da Álgebra.

Características:
- **Não admite repetições** de elementos.
- Elementos **não possuem posição** (não há índice, como em `List`).
- Acesso, inserção e remoção de elementos são **rápidos**.
- Oferece operações eficientes de conjunto: **interseção, união, diferença**.

Referência: https://docs.oracle.com/javase/10/docs/api/java/util/Set.html

---

## Principais implementações

| Implementação | Desempenho | Ordenação |
|---|---|---|
| `HashSet` | Mais rápido — operações O(1) em tabela hash | **Não ordenado** |
| `TreeSet` | Mais lento — operações O(log n) em árvore rubro-negra | Ordenado pelo `compareTo` do objeto (ou `Comparator`) |
| `LinkedHashSet` | Velocidade intermediária | Elementos na **ordem em que foram adicionados** |

---

## Métodos importantes

- `add(obj)`, `remove(obj)`, `contains(obj)` — baseados em `equals` e `hashCode`.
  - Se `equals`/`hashCode` não estiverem implementados na classe do objeto, é usada **comparação de referência** (ponteiros).
- `clear()` — remove todos os elementos.
- `size()` — quantidade de elementos.
- `removeIf(predicate)` — remove elementos que satisfazem uma condição.
- `addAll(other)` — **união**: adiciona os elementos do outro conjunto, sem repetição.
- `retainAll(other)` — **interseção**: remove os elementos não contidos em `other`.
- `removeAll(other)` — **diferença**: remove os elementos contidos em `other`.

---

## Demo 1 — HashSet básico

```java
package application;

import java.util.HashSet;
import java.util.Set;

public class Program {
    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        set.add("TV");
        set.add("Notebook");
        set.add("Tablet");

        System.out.println(set.contains("Notebook"));

        for (String p : set) {
            System.out.println(p);
        }
    }
}
```

---

## Demo 2 — Operações de conjunto (união, interseção, diferença)

```java
package application;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class Program {
    public static void main(String[] args) {
        Set<Integer> a = new TreeSet<>(Arrays.asList(0, 2, 4, 5, 6, 8, 10));
        Set<Integer> b = new TreeSet<>(Arrays.asList(5, 6, 7, 8, 9, 10));

        // union
        Set<Integer> c = new TreeSet<>(a);
        c.addAll(b);
        System.out.println(c); // [0, 2, 4, 5, 6, 7, 8, 9, 10]

        // intersection
        Set<Integer> d = new TreeSet<>(a);
        d.retainAll(b);
        System.out.println(d); // [5, 6, 8, 10]

        // difference
        Set<Integer> e = new TreeSet<>(a);
        e.removeAll(b);
        System.out.println(e); // [0, 2, 4]
    }
}
```

---

## Como o Set testa igualdade?

Depende se a classe dos elementos implementa `hashCode`/`equals`:

- **Se `hashCode` e `equals` estão implementados:**
  1. Primeiro compara `hashCode`.
  2. Se os hashes forem iguais, usa `equals` para confirmar a igualdade.
  3. Tipos comuns (`String`, `Integer`, `Double`, etc.) já possuem essas implementações prontas.
- **Se `hashCode` e `equals` NÃO estão implementados:**
  - A comparação é feita pelas **referências** (ponteiros) dos objetos — ou seja, só é "igual" se for exatamente o mesmo objeto na memória.

### Exemplo — sem `equals`/`hashCode` sobrescritos

```java
package application;

import java.util.HashSet;
import java.util.Set;
import entities.Product;

public class Program {
    public static void main(String[] args) {
        Set<Product> set = new HashSet<>();
        set.add(new Product("TV", 900.0));
        set.add(new Product("Notebook", 1200.0));
        set.add(new Product("Tablet", 400.0));

        Product prod = new Product("Notebook", 1200.0);
        System.out.println(set.contains(prod)); // false, se Product não sobrescrever hashCode/equals
    }
}
```

```java
package entities;

public class Product {
    private String name;
    private Double price;

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
```

> Para que `contains` retorne `true` com um objeto "equivalente" (mesmo `name` e `price`, mas instância diferente), a classe `Product` precisaria sobrescrever `hashCode` e `equals` (ver anotação sobre **hashCode e equals**).

---

## Como o TreeSet ordena os elementos?

`TreeSet` mantém os elementos **ordenados**, usando o `compareTo` do objeto (via interface `Comparable`) ou um `Comparator` fornecido.

```java
package application;

import java.util.Set;
import java.util.TreeSet;
import entities.Product;

public class Program {
    public static void main(String[] args) {
        Set<Product> set = new TreeSet<>();
        set.add(new Product("TV", 900.0));
        set.add(new Product("Notebook", 1200.0));
        set.add(new Product("Tablet", 400.0));

        for (Product p : set) {
            System.out.println(p);
        }
    }
}
```

```java
package entities;

public class Product implements Comparable<Product> {
    private String name;
    private Double price;

    public Product(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    // (... get / set / hashCode / equals)

    @Override
    public String toString() {
        return "Product [name=" + name + ", price=" + price + "]";
    }

    @Override
    public int compareTo(Product other) {
        return name.toUpperCase().compareTo(other.getName().toUpperCase());
    }
}
```

Nesse exemplo, os produtos são ordenados alfabeticamente pelo `name` (ignorando maiúsculas/minúsculas).

> **Atenção:** `TreeSet` exige que os elementos implementem `Comparable` (ou que seja fornecido um `Comparator`), caso contrário lança `ClassCastException` em tempo de execução.

---

## Exercício resolvido — contagem de usuários distintos

**Problema:** um site registra um log de acessos (usuário + timestamp ISO 8601). Ler o log a partir de um arquivo e informar quantos usuários **distintos** acessaram o site.

```
amanda 2018-08-26T20:45:08Z
alex86 2018-08-26T21:49:37Z
bobbrown 2018-08-27T03:19:13Z
amanda 2018-08-27T08:11:00Z
jeniffer3 2018-08-27T09:19:24Z
alex86 2018-08-27T22:39:52Z
amanda 2018-08-28T07:42:19Z
```

Execução:

```
Enter file full path: c:\temp\in.txt
Total users: 4
```

**Ideia da solução:** usar um `Set<String>` para armazenar apenas os nomes de usuário (sem repetição); o `size()` do set ao final é a quantidade de usuários distintos.

> Exemplo completo: https://github.com/acenelio/set1-java

---

## Exercício proposto — total de alunos de um instrutor

Um instrutor possui vários cursos; um mesmo aluno pode estar matriculado em mais de um curso. O total de alunos **não** é a soma simples dos alunos de cada curso (pode haver repetição entre cursos).

**Ideia:** usar um `Set<Integer>` (códigos dos alunos) e usar `addAll` para unir os conjuntos de cada curso, aproveitando que `Set` elimina duplicatas automaticamente.

Exemplo de execução:

```
How many students for course A? 3
21
35
22
How many students for course B? 2
21
50
How many students for course C? 3
42
35
13
Total students: 6
```

> Exemplo completo: https://github.com/acenelio/set2-java
