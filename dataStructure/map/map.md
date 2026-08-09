# Map em Java

## O que é

`Map<K,V>` é uma **coleção de pares chave/valor**.

Características:
- **Não admite repetições** do objeto chave (cada chave é única).
- Os elementos são **indexados pelo objeto chave** (não possuem posição, como em `List`).
- Acesso, inserção e remoção de elementos são **rápidos**.
- Uso comum: **cookies**, **local storage**, e qualquer modelo do tipo **chave-valor**.

Referência: https://docs.oracle.com/javase/10/docs/api/java/util/Map.html

---

## Principais implementações

| Implementação | Desempenho | Ordenação |
|---|---|---|
| `HashMap` | Mais rápido — operações O(1) em tabela hash | **Não ordenado** |
| `TreeMap` | Mais lento — operações O(log n) em árvore rubro-negra | Ordenado pelo `compareTo` do objeto chave (ou `Comparator`) |
| `LinkedHashMap` | Velocidade intermediária | Elementos na **ordem em que foram adicionados** |

---

## Métodos importantes

- `put(key, value)` — insere ou atualiza o valor associado à chave.
- `remove(key)` — remove o par pela chave.
- `containsKey(key)` — verifica se a chave existe.
- `get(key)` — retorna o valor associado à chave (ou `null` se não existir).
- `clear()` — remove todos os elementos.
- `size()` — quantidade de pares chave/valor.
- `keySet()` — retorna um `Set<K>` com todas as chaves.
- `values()` — retorna uma `Collection<V>` com todos os valores.

> Assim como em `Set`, as operações de busca/comparação de chaves (`containsKey`, `get`, `put`) são baseadas em `equals` e `hashCode`. Se a classe da chave não implementar esses métodos, é usada **comparação de referência** (ponteiros).

---

## Demo 1 — Map de Strings (cookies)

```java
package application;

import java.util.Map;
import java.util.TreeMap;

public class Program {
    public static void main(String[] args) {
        Map<String, String> cookies = new TreeMap<>();
        cookies.put("username", "maria");
        cookies.put("email", "maria@gmail.com");
        cookies.put("phone", "99771122");

        cookies.remove("email");
        cookies.put("phone", "99771133"); // atualiza valor existente

        System.out.println("Contains 'phone' key: " + cookies.containsKey("phone"));
        System.out.println("Phone number: " + cookies.get("phone"));
        System.out.println("Email: " + cookies.get("email")); // null (foi removido)
        System.out.println("Size: " + cookies.size());

        System.out.println("ALL COOKIES:");
        for (String key : cookies.keySet()) {
            System.out.println(key + ": " + cookies.get(key));
        }
    }
}
```

Como é `TreeMap`, as chaves aparecem **ordenadas alfabeticamente** na iteração.

---

## Demo 2 — Map com objeto personalizado como chave

```java
package application;

import java.util.HashMap;
import java.util.Map;
import entities.Product;

public class Program {
    public static void main(String[] args) {
        Map<Product, Double> stock = new HashMap<>();

        Product p1 = new Product("Tv", 900.0);
        Product p2 = new Product("Notebook", 1200.0);
        Product p3 = new Product("Tablet", 400.0);

        stock.put(p1, 10000.0);
        stock.put(p2, 20000.0);
        stock.put(p3, 15000.0);

        Product ps = new Product("Tv", 900.0);
        System.out.println("Contains 'ps' key: " + stock.containsKey(ps));
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

    // getters, setters, equals, hashCode
}
```

> Assim como em `Set`, para que `stock.containsKey(ps)` retorne `true` com uma instância diferente mas "equivalente", a classe `Product` **precisa sobrescrever `equals` e `hashCode`** (ver anotação sobre **hashCode e equals**). Sem isso, a comparação usa referência de memória, e o resultado seria `false`.

---

## Exercício proposto — relatório de votos

**Problema:** em uma eleição, são gerados registros de votação em formato `.csv`, contendo o nome do candidato e a quantidade de votos que ele obteve em uma urna. Ler os registros a partir de um arquivo e gerar um relatório consolidado com o **total de votos de cada candidato**.

Arquivo de entrada (exemplo):

```
Alex Blue,15
Maria Green,22
Bob Brown,21
Alex Blue,30
Bob Brown,15
Maria Green,27
Maria Green,22
Bob Brown,25
Alex Blue,31
```

Execução esperada:

```
Enter file full path: c:\temp\in.txt
Alex Blue: 76
Maria Green: 71
Bob Brown: 61
```

**Ideia da solução:** usar um `Map<String, Integer>` onde a chave é o nome do candidato e o valor é o total acumulado de votos. Para cada linha lida:
- Se o candidato já existe no map, somar os votos ao valor atual (`map.put(nome, map.get(nome) + votos)`).
- Se não existe, inserir o candidato com o valor inicial (`map.put(nome, votos)`).

> Solução completa: https://github.com/acenelio/map1-java

