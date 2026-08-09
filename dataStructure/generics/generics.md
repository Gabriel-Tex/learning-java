# Generics em Java

## O que são

Generics permitem parametrizar classes, interfaces e métodos por **tipo**. Em vez de trabalhar com `Object` e fazer casts manuais, o compilador passa a conhecer o tipo real que está sendo manipulado.

**Benefícios:**
- **Reuso** — a mesma classe/método serve para vários tipos.
- **Type safety** — erros de tipo são pegos em tempo de compilação, não em runtime.
- **Performance** — evita casts e boxing/unboxing desnecessários.

Uso mais comum: coleções.

```java
List<String> list = new ArrayList<>();
list.add("Maria");
String name = list.get(0); // sem necessidade de cast
```

---

## Motivação: o problema sem generics

Sem generics, uma classe de serviço genérica precisaria trabalhar com `Object`:

```java
public class PrintService {
    public void addValue(Object value) { ... }
    public Object first() { ... }
    public void print() { ... }
}
```

Problemas:
- **Reuso ruim**: precisaríamos de uma classe por tipo (`IntPrintService`, `StringPrintService`, etc.) ou usar `Object` e perder segurança de tipo.
- **Type safety ruim**: nada impede de misturar tipos incompatíveis dentro da mesma coleção.
- **Performance ruim**: uso de `Object` implica casts e boxing/unboxing.

### Solução com generics

```java
public class PrintService<T> {
    public void addValue(T value) { ... }
    public T first() { ... }
    public void print() { ... }
}
```

Agora `T` é definido na instanciação (`PrintService<Integer>`, `PrintService<String>`, etc.), com segurança de tipo garantida pelo compilador.

> Exemplo completo: https://github.com/acenelio/generics1-java

---

## Generics delimitados (bounded types)

Usa-se `extends` para restringir o tipo genérico a subtipos de uma classe/interface (ou implementações de uma interface).

```java
public static <T extends Comparable<T>> T max(List<T> list) {
    if (list.isEmpty()) {
        throw new IllegalStateException("List can't be empty");
    }
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) {
            max = item;
        }
    }
    return max;
}
```

- `T extends Comparable<T>` → `T` deve implementar `Comparable` comparando-se consigo mesmo.
- Nota: Java já possui `Collections.max(list)` pronto para isso.

### Versão alternativa (mais completa)

Usa `Comparable<? super T>` para aceitar hierarquias onde o `compareTo` está implementado numa superclasse:

```java
public static <T extends Comparable<? super T>> T max(List<T> list) {
    if (list.isEmpty()) {
        throw new IllegalStateException("List can't be empty");
    }
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) {
            max = item;
        }
    }
    return max;
}
```

Isso permite, por exemplo, uma hierarquia `A -> B -> C` onde só `A` implementa `Comparable<A>`; `max` ainda funciona para listas de `B` ou `C`.

> Exemplo completo: https://github.com/acenelio/generics2-java

---

## Tipos curinga (Wildcard types) — `?`

### Generics são invariantes

`List<Object>` **não** é supertipo de `List<Integer>`, mesmo `Integer` sendo subtipo de `Object`:

```java
List<Object> myObjs = new ArrayList<Object>();
List<Integer> myNumbers = new ArrayList<Integer>();
myObjs = myNumbers; // ERRO de compilação
```

O supertipo comum de qualquer `List<T>` é `List<?>` (wildcard):

```java
List<?> myObjs = new ArrayList<Object>();
List<Integer> myNumbers = new ArrayList<Integer>();
myObjs = myNumbers; // OK
```

### Uso de `?` em métodos ("qualquer tipo")

```java
public static void printList(List<?> list) {
    for (Object obj : list) {
        System.out.println(obj);
    }
}
```

### Limitação: não é possível inserir dados numa coleção com `?`

```java
List<?> list = new ArrayList<Integer>();
list.add(3); // ERRO de compilação
```

O compilador não sabe o tipo real da lista, então não pode garantir segurança ao inserir.

---

## Curingas delimitados (bounded wildcards)

### Problema motivador

Somar as áreas de uma lista de figuras (`Shape`, com subtipos `Rectangle` e `Circle`).

**Soluções impróprias:**

```java
public double totalArea(List<Shape> list)   // só aceita List<Shape> exata, não List<Rectangle>
public double totalArea(List<?> list)       // aceita qualquer tipo, perde type safety; não permite add
```

**Solução correta com `? extends`:**

```java
public double totalArea(List<? extends Shape> list) {
    double sum = 0.0;
    for (Shape s : list) {
        sum += s.area();
    }
    return sum;
}
```

Aceita `List<Shape>`, `List<Rectangle>`, `List<Circle>`, etc.

> Exemplo completo: https://github.com/acenelio/generics4-java

---

## Princípio Get/Put (PECS — Producer Extends, Consumer Super)

Regra prática para decidir entre `? extends` e `? super`:
- Use **`extends`** quando a coleção é uma **fonte de leitura** (produtora / "get").
- Use **`super`** quando a coleção é um **destino de escrita** (consumidora / "put").

### Covariância — `? extends T` (get OK, put ERRO)

```java
List<Integer> intList = new ArrayList<Integer>();
intList.add(10);
intList.add(5);

List<? extends Number> list = intList;
Number x = list.get(0);  // OK — pode ler como Number
list.add(20);             // ERRO de compilação — não sabe o tipo exato
```

### Contravariância — `? super T` (get ERRO, put OK)

```java
List<Object> myObjs = new ArrayList<Object>();
myObjs.add("Maria");
myObjs.add("Alex");

List<? super Number> myNums = myObjs;
myNums.add(10);    // OK
myNums.add(3.14);  // OK
Number x = myNums.get(0); // ERRO de compilação — só garante Object
```

### Exemplo combinando os dois: método `copy`

```java
public static void copy(List<? extends Number> source, List<? super Number> destiny) {
    for (Number number : source) {
        destiny.add(number);
    }
}
```

```java
List<Integer> myInts = Arrays.asList(1, 2, 3, 4);
List<Double> myDoubles = Arrays.asList(3.14, 6.28);
List<Object> myObjs = new ArrayList<Object>();

copy(myInts, myObjs);
copy(myDoubles, myObjs);
```

- `source` é produtor (`extends`) → lemos itens dele.
- `destiny` é consumidor (`super`) → escrevemos itens nele.

> Referência: https://stackoverflow.com/questions/1368166/what-is-a-difference-between-super-e-and-extends-e
