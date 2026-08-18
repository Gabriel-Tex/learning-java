# Consumer

## Definição

`Consumer<T>` é uma interface funcional do pacote `java.util.function` que representa uma ação a ser executada sobre um valor, sem produzir nenhum resultado de retorno. Sua assinatura é:

```java
public interface Consumer<T> {
    void accept(T t);
}
```

Um `Consumer<T>` recebe um valor de tipo `T` e simplesmente "faz algo" com ele — imprime, salva, modifica, envia — sem devolver nada. É por isso que, das interfaces funcionais mais comuns do Java (`Predicate`, `Function`, `Consumer`), o `Consumer` é a única em que efeitos colaterais são esperados e até desejados: como não existe valor de retorno, a única forma de o `Consumer` ser útil é produzindo algum efeito observável fora de si mesmo. Isso o coloca em contraste direto com a ideia de transparência referencial discutida na programação funcional — e é uma exceção assumida, não um descuido.

## Problema exemplo

Suponha uma lista de produtos e o objetivo de aumentar o preço de todos eles em 10%:

```java
List<Product> list = new ArrayList<>();
list.add(new Product("Tv", 900.00));
list.add(new Product("Mouse", 50.00));
list.add(new Product("Tablet", 350.50));
list.add(new Product("HD Case", 80.90));
```

O método `forEach`, presente na interface `Iterable` (e, portanto, disponível em qualquer `List`), foi feito exatamente para esse tipo de tarefa: aplicar uma ação a cada elemento da coleção. Sua assinatura recebe justamente um `Consumer`:

```java
void forEach(Consumer<? super T> action)
```

## As diferentes formas de fornecer o Consumer

Seguindo a mesma progressão já vista em `Comparator` e `Predicate`, existem várias formas de implementar o `Consumer`, da mais explícita até a mais concisa.

**Implementação explícita da interface**

```java
public class IncreasePriceConsumer implements Consumer<Product> {
    @Override
    public void accept(Product product) {
        product.setPrice(product.getPrice() * 1.1);
    }
}
```

```java
list.forEach(new IncreasePriceConsumer());
```

**Method reference com método estático**

```java
public class Program {
    public static void increasePrice(Product product) {
        product.setPrice(product.getPrice() * 1.1);
    }
}
```

```java
list.forEach(Program::increasePrice);
```

**Method reference com método não estático**

```java
public class Product {
    // ...
    public void increasePrice(double percentage) {
        price += price * percentage / 100.0;
    }
}
```

Quando o método de instância tem parâmetros próprios além do elemento em si, uma lambda costuma ser mais prática do que uma method reference direta, já que a assinatura do `Consumer` só permite um argumento (o elemento). Ainda assim, para um método sem parâmetros extras, a referência funciona normalmente:

```java
Product product = new Product();
list.forEach(product::someAction);
```

**Expressão lambda declarada**

```java
Consumer<Product> consumer = p -> p.setPrice(p.getPrice() * 1.1);
list.forEach(consumer);
```

**Expressão lambda inline**

```java
list.forEach(p -> p.setPrice(p.getPrice() * 1.1));
```

Essa última forma é, na prática, a mais comum: direta, curta, e fácil de ler — "para cada produto da lista, aumente o preço em 10%".

## Um uso muito comum: method reference com println

Um caso do `Consumer` que aparece o tempo todo em código Java, inclusive nos exemplos das anotações anteriores sobre `Comparator`, é imprimir cada elemento de uma lista:

```java
list.forEach(System.out::println);
```

Aqui `System.out::println` é uma *method reference* para um método de instância (`println`, do objeto `System.out`), que se encaixa perfeitamente na assinatura de `Consumer<Product>`: recebe um produto, imprime, não retorna nada.

## Encadeando Consumers (complemento)

Assim como `Predicate` tem `and`/`or`/`negate`, `Consumer` oferece o método `default` `andThen`, que permite encadear duas ações em sequência sobre o mesmo elemento, sem precisar escrever tudo dentro de uma única lambda:

```java
Consumer<Product> increasePrice = p -> p.setPrice(p.getPrice() * 1.1);
Consumer<Product> printProduct = p -> System.out.println(p);

list.forEach(increasePrice.andThen(printProduct));
```

Isso executa `increasePrice` e, em seguida, `printProduct`, para cada produto da lista — útil quando se quer compor pequenas ações reutilizáveis em vez de escrever uma lambda grande fazendo tudo de uma vez.

## Cuidado: Consumer e efeitos colaterais em Streams

Vale reforçar um ponto de atenção: embora o `Consumer` seja "a exceção que pode ter efeito colateral", isso é aceitável no `forEach` de uma `List` porque ali a intenção é justamente essa (uma operação terminal, fora do contexto de Streams paralelas). Já dentro de uma pipeline de Stream (que veremos em uma próxima anotação), usar um `Consumer` com efeitos colaterais em operações como `peek` ou, principalmente, em Streams paralelas, é considerado uma má prática — pode gerar condições de corrida e resultados imprevisíveis, já que a stream não garante ordem de execução nem exclusão mútua entre as ações.