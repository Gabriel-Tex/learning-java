# Comparator em Java

## O problema que motiva o Comparator

Imagine uma classe `Product`, com os atributos `name` e `price`. Em algum momento você vai precisar ordenar uma lista desses produtos — por exemplo, por preço. A primeira ideia costuma ser fazer `Product` implementar `Comparable<Product>`, definindo dentro da própria classe como dois produtos devem ser comparados.

O problema é que essa abordagem viola o princípio open/closed (SOLID): se amanhã o critério de comparação mudar (agora quero ordenar por nome, depois por price, depois por outra regra qualquer), você é obrigado a alterar a classe `Product` toda vez. A classe deveria estar aberta para extensão, mas fechada para modificação — e `Comparable` não permite isso, porque o critério de comparação fica "grudado" no objeto.

A solução é separar a lógica de comparação da classe do objeto, usando a interface `Comparator`. Ela permite criar quantos critérios de comparação diferentes você quiser, sem tocar em `Product`.

## Onde o Comparator entra

A interface `List` possui um *default method* chamado `sort`, que recebe justamente um `Comparator`:

```java
default void sort(Comparator<? super E> c)
```

Ou seja, você passa para o método `sort` um objeto que sabe comparar dois elementos, e a lista se ordena de acordo com essa lógica externa.

`Comparator<T>` é uma interface funcional (possui um único método abstrato):

```java
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

O contrato do `compare` segue a mesma lógica do `compareTo` do `Comparable`: retorna um valor negativo se `o1` for "menor" que `o2`, zero se forem "iguais" para fins de ordenação, e um valor positivo se `o1` for "maior" que `o2`.

## As várias formas de fornecer um Comparator

Existe uma progressão natural de como se pode implementar um `Comparator`, cada uma mais concisa que a anterior. É interessante conhecer todas, porque em código real (e em entrevistas técnicas) você vai encontrar qualquer uma delas.

**Classe separada implementando Comparator**

A forma mais "clássica" e explícita: cria-se uma classe à parte, implementando a interface.

```java
public class MyComparator implements Comparator<Product> {
    @Override
    public int compare(Product p1, Product p2) {
        return p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase());
    }
}
```

```java
list.sort(new MyComparator());
```

Essa abordagem resolve o problema do open/closed (a classe `Product` não precisa mudar), mas ainda é verbosa: cria-se um arquivo inteiro só para uma regra de comparação.

**Classe anônima**

Em vez de criar um arquivo separado, você pode instanciar a interface na hora, com uma implementação anônima:

```java
list.sort(new Comparator<Product>() {
    @Override
    public int compare(Product p1, Product p2) {
        return p1.getPrice().compareTo(p2.getPrice());
    }
});
```

Já elimina a necessidade de um arquivo extra, mas ainda carrega bastante sintaxe repetitiva (a assinatura do método, o `@Override`, etc.) só para expressar uma lógica de uma linha.

**Expressão lambda com chaves**

Como `Comparator` é uma interface funcional, o compilador consegue inferir todo o "esqueleto" (nome do método, tipos dos parâmetros) a partir do contexto. Isso permite substituir a classe anônima por uma expressão lambda:

```java
list.sort((Product p1, Product p2) -> {
    return p1.getPrice().compareTo(p2.getPrice());
});
```

**Expressão lambda sem chaves**

Se o corpo da lambda é uma única expressão (um `return` implícito), as chaves e a palavra `return` podem ser removidas:

```java
list.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
```

Aqui também já aparece a inferência de tipos: não é mais necessário declarar `Product p1, Product p2`, o compilador deduz os tipos a partir do tipo genérico da lista.

**Method reference**

Quando a lógica de comparação já existe como um método (estático ou de instância) em algum lugar, não é preciso nem escrever uma lambda: basta referenciar o método diretamente, usando o operador `::`.

```java
public class Program {
    public static int compareProducts(Product p1, Product p2) {
        return p1.getPrice().compareTo(p2.getPrice());
    }

    public static void main(String[] args) {
        List<Product> list = new ArrayList<>();
        list.add(new Product("TV", 900.00));
        list.add(new Product("Notebook", 1200.00));
        list.add(new Product("Tablet", 450.00));

        list.sort(Program::compareProducts);
        list.forEach(System.out::println);
    }
}
```

A sintaxe `Classe::método` é chamada de *method reference*. Ela reforça uma ideia central da programação funcional: em Java, métodos podem ser tratados como objetos de primeira classe, ou seja, podem ser passados como argumento, atribuídos a variáveis (via interfaces funcionais) ou retornados por outros métodos — exatamente como fizemos aqui, passando `compareProducts` como argumento de `sort`.

## Comparando por múltiplos critérios (complemento)

Vale a pena registrar aqui algo que a nota original não cobre, mas que é extremamente comum na prática: encadear critérios de comparação usando os métodos default da própria interface `Comparator`, como `thenComparing`, e os helpers estáticos `comparing`, `reverseOrder` e `naturalOrder`.

```java
list.sort(
    Comparator.comparing(Product::getName)
              .thenComparing(Product::getPrice)
);

// ordem decrescente de preço
list.sort(Comparator.comparing(Product::getPrice).reversed());
```

Isso evita ter que escrever manualmente a lógica de desempate (primeiro compara por nome; se empatar, compara por preço), tornando o código bem mais declarativo — o que é justamente o espírito da programação funcional.

## Comparable vs Comparator, em resumo

`Comparable` define uma ordenação natural e única, embutida na própria classe (via `compareTo`), e é usado quando o objeto tem "uma" forma óbvia e canônica de ser comparado. `Comparator` define uma ordenação externa, podendo existir vários critérios diferentes para o mesmo tipo de objeto, e é a escolha certa quando o critério de ordenação pode variar ou não deveria acoplar-se à classe do objeto.

Na prática, com expressões lambda e method references, `Comparator` se tornou a ferramenta natural do dia a dia: é mais flexível que `Comparable` e, ao mesmo tempo, muito mais enxuto do que era antes do Java 8 — a evolução de "classe separada" para "method reference" mostrada acima é basicamente um retrato de como o Java foi incorporando o paradigma funcional a partir da versão 8.