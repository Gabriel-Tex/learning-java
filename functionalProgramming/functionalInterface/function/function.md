# Function

## Definição

`Function<T, R>` é uma interface funcional do pacote `java.util.function` que representa uma transformação: recebe um valor de um tipo e devolve um valor de outro tipo (ou do mesmo tipo, se fizer sentido). Sua assinatura é:

```java
public interface Function<T, R> {
    R apply(T t);
}
```

Aqui `T` é o tipo de entrada e `R` é o tipo de saída. Diferente do `Predicate` (que sempre devolve `boolean`) e do `Consumer` (que não devolve nada), `Function` é genérica também no tipo de retorno — o que a torna a interface funcional mais flexível das três, própria para qualquer situação em que se precise converter, mapear ou derivar um valor a partir de outro.

## Problema exemplo

Suponha uma lista de produtos e o objetivo de gerar uma nova lista contendo apenas os nomes dos produtos, em caixa alta:

```java
List<Product> list = new ArrayList<>();
list.add(new Product("Tv", 900.00));
list.add(new Product("Mouse", 50.00));
list.add(new Product("Tablet", 350.50));
list.add(new Product("HD Case", 80.90));
```

Esse é exatamente o tipo de problema que `Function` resolve: transformar cada `Product` (tipo `T`) em uma `String` (tipo `R`). A ferramenta usada para aplicar essa transformação a uma coleção inteira é o método `map` das Streams, que recebe justamente uma `Function`:

```java
List<String> names = list.stream()
    .map(p -> p.getName().toUpperCase())
    .collect(Collectors.toList());
```

## As diferentes formas de fornecer a Function

Seguindo o mesmo padrão já visto em `Comparator`, `Predicate` e `Consumer`, existem várias formas de implementar a `Function`, da mais explícita à mais concisa.

**Implementação explícita da interface**

```java
public class UpperCaseNameFunction implements Function<Product, String> {
    @Override
    public String apply(Product product) {
        return product.getName().toUpperCase();
    }
}
```

```java
List<String> names = list.stream()
    .map(new UpperCaseNameFunction())
    .collect(Collectors.toList());
```

**Method reference com método estático**

```java
public class Program {
    public static String upperCaseName(Product product) {
        return product.getName().toUpperCase();
    }
}
```

```java
List<String> names = list.stream()
    .map(Program::upperCaseName)
    .collect(Collectors.toList());
```

**Method reference com método não estático**

```java
public class Product {
    // ...
    public String getUpperCaseName() {
        return name.toUpperCase();
    }
}
```

```java
List<String> names = list.stream()
    .map(Product::getUpperCaseName)
    .collect(Collectors.toList());
```

**Expressão lambda declarada**

```java
Function<Product, String> function = p -> p.getName().toUpperCase();
List<String> names = list.stream()
    .map(function)
    .collect(Collectors.toList());
```

**Expressão lambda inline**

```java
List<String> names = list.stream()
    .map(p -> p.getName().toUpperCase())
    .collect(Collectors.toList());
```

Assim como nos casos anteriores, a forma inline costuma ser a mais usada no dia a dia, por unir clareza a poucas linhas de código.

## Nota sobre o map

Vale um esclarecimento importante, já que o nome gera confusão com frequência: a função `map`, usada acima, **não tem relação** com a estrutura de dados `Map` (aquela de pares chave-valor, como `HashMap`). O `map` das Streams é uma operação que aplica uma `Function` a cada elemento de uma stream, produzindo uma nova stream com os valores transformados — o nome vem do conceito matemático de "mapear" um conjunto de valores para outro, elemento a elemento.

Para trabalhar com `map`, é comum precisar converter entre `List` e `Stream`, e vale destacar essa via de mão dupla: de `List` para `Stream`, usa-se `.stream()`; de `Stream` de volta para `List`, usa-se `.collect(Collectors.toList())`.

## Compondo Functions (complemento)

Assim como `Predicate` tem `and`/`or` e `Consumer` tem `andThen`, `Function` também oferece métodos `default` para composição: `andThen` e `compose`. Ambos permitem encadear duas transformações, mas em ordens diferentes.

```java
Function<Product, String> getName = Product::getName;
Function<String, String> toUpperCase = String::toUpperCase;

// executa getName, depois toUpperCase (a mesma coisa que Product::getUpperCaseName)
Function<Product, String> nameToUpperCase = getName.andThen(toUpperCase);

System.out.println(nameToUpperCase.apply(new Product("Tv", 900.00))); // "TV"
```

Em `f.andThen(g)`, a função `f` é aplicada primeiro, e o resultado é passado para `g`. Já em `f.compose(g)`, a ordem se inverte: `g` é aplicada primeiro, e o resultado vai para `f`. É útil pensar em `andThen` como "e depois faça isso" e em `compose` como "mas antes faça isso".

## Function como base do map em Streams

Assim como `Predicate` é a base do `filter` e `Consumer` é a base do `forEach`, `Function` é a base do `map` — e é interessante notar como essas três interfaces funcionais cobrem, juntas, praticamente todas as operações fundamentais de uma pipeline de Stream: filtrar elementos (`Predicate`), transformar elementos (`Function`) e consumir/agir sobre elementos (`Consumer`). Entender bem essas três interfaces isoladamente é o que torna natural compreender, na próxima anotação, como elas se encaixam dentro de uma pipeline completa de Stream.