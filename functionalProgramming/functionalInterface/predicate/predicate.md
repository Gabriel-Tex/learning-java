# Predicate

## Definição

`Predicate<T>` é uma interface funcional do pacote `java.util.function` que representa uma condição, um teste lógico aplicado a um valor. Sua assinatura é simples:

```java
public interface Predicate<T> {
    boolean test(T t);
}
```

Ou seja, um `Predicate<T>` recebe um valor de tipo `T` e devolve um `boolean`, indicando se aquele valor satisfaz ou não determinada condição. É a ferramenta natural sempre que a lógica do programa envolve a pergunta "esse elemento atende a este critério?" — filtrar, validar, remover.

## Problema exemplo

Suponha uma lista de produtos e o objetivo de remover dela apenas os produtos cujo preço seja inferior a 100:

```java
List<Product> list = new ArrayList<>();
list.add(new Product("Tv", 900.00));
list.add(new Product("Mouse", 50.00));
list.add(new Product("Tablet", 350.50));
list.add(new Product("HD Case", 80.90));
```

O método `removeIf`, presente na interface `Collection` (e portanto disponível em `List`), resolve exatamente esse tipo de problema. Sua assinatura recebe justamente um `Predicate`:

```java
boolean removeIf(Predicate<? super E> filter)
```

Todo elemento para o qual o `Predicate` retornar `true` é removido da coleção.

## As diferentes formas de fornecer o Predicate

Assim como aconteceu com `Comparator`, existe uma progressão de formas de implementar o `Predicate`, da mais verbosa à mais concisa — e vale a pena conhecer todas.

**Implementação explícita da interface**

```java
public class PriceLowerThanPredicate implements Predicate<Product> {
    @Override
    public boolean test(Product product) {
        return product.getPrice() < 100.0;
    }
}
```

```java
list.removeIf(new PriceLowerThanPredicate());
```

**Method reference com método estático**

Se a lógica do teste já existir (ou fizer sentido existir) como um método estático em alguma classe utilitária:

```java
public class Program {
    public static boolean testPriceLowerThan100(Product product) {
        return product.getPrice() < 100.0;
    }
}
```

```java
list.removeIf(Program::testPriceLowerThan100);
```

**Method reference com método não estático**

O método também pode ser de instância, desde que exista um objeto para o qual a referência apontará:

```java
public class Product {
    // ...
    public boolean priceLowerThan100() {
        return price < 100.0;
    }
}
```

```java
Product product = new Product();
list.removeIf(product::priceLowerThan100);
```

Repare que aqui `product` é usado apenas como referência para "emprestar" o método — não é o objeto real que será testado; o Java aplica o método de instância a cada elemento da lista automaticamente, seguindo o mesmo padrão que já apareceu com `System.out::println`.

**Expressão lambda declarada**

Se preferir não criar um método separado, o teste pode ser escrito diretamente como uma variável do tipo `Predicate`:

```java
Predicate<Product> predicate = p -> p.getPrice() < 100.0;
list.removeIf(predicate);
```

**Expressão lambda inline**

E, finalmente, a forma mais direta: a lambda é escrita no próprio momento da chamada, sem passar por uma variável intermediária:

```java
list.removeIf(p -> p.getPrice() < 100.0);
```

Essa costuma ser a forma mais usada no dia a dia, por unir clareza e concisão — dá para ler quase como uma frase: "remova da lista se o preço do produto for menor que 100".

## Combinando Predicates (complemento)

Um detalhe que a nota original não cobre, mas que é bastante útil na prática, é que `Predicate` oferece métodos `default` para compor condições sem precisar escrever `&&` e `||` manualmente dentro da lambda: `and`, `or` e `negate`.

```java
Predicate<Product> isExpensive = p -> p.getPrice() >= 100.0;
Predicate<Product> startsWithT = p -> p.getName().startsWith("T");

// remove produtos caros E cujo nome começa com "T"
list.removeIf(isExpensive.and(startsWithT));

// remove produtos que NÃO são caros
list.removeIf(isExpensive.negate());
```

Isso permite montar regras mais complexas a partir de regras pequenas e reutilizáveis, um dos benefícios centrais de tratar lógica como um objeto de primeira classe.

## Onde mais o Predicate aparece

Vale destacar que `Predicate` não se limita ao `removeIf`. Ele é a base do método `filter` das Streams (assunto de uma próxima anotação), usado exatamente com o mesmo espírito: manter apenas os elementos que satisfazem determinada condição. Entender bem `Predicate` aqui facilita bastante entender `filter` mais adiante, já que a lógica é idêntica — a diferença é que `removeIf` modifica a coleção original (efeito colateral), enquanto `filter` gera uma nova stream, sem alterar a fonte de dados.