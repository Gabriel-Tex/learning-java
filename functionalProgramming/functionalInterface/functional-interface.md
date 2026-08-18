# Interface Funcional

## Definição

Uma **interface funcional** é uma interface que possui **um único método abstrato**. É justamente essa restrição — apenas um método abstrato — que permite ao compilador Java associar uma expressão lambda (ou uma *method reference*) a essa interface: como só existe um método para implementar, a lambda pode representar diretamente o corpo desse método, sem precisar dizer explicitamente qual método está sendo implementado.

Em outras palavras: interfaces funcionais são a "ponte" entre o mundo orientado a objetos do Java (tudo é interface, classe, método) e o estilo funcional das expressões lambda. `Comparator`, visto anteriormente, é o exemplo mais natural: possui um único método abstrato, `compare`, o que a torna uma interface funcional.

## Do jeito tradicional até a lambda

Vale relembrar o caminho percorrido com o exemplo do `Comparator`, porque ele ilustra bem o papel de uma interface funcional. Antes das lambdas, para implementar uma interface funcional era preciso criar uma classe (separada ou anônima) implementando-a explicitamente:

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

Como `Comparator` tem apenas um método abstrato (`compare`), o compilador sabe exatamente qual método está sendo "preenchido" quando você escreve uma lambda no lugar dessa implementação. É por isso que toda essa classe pode ser substituída por:

```java
list.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));
```

Se `Comparator` tivesse dois ou mais métodos abstratos, essa substituição seria ambígua — o compilador não saberia a qual método a lambda corresponde. É exatamente essa ambiguidade que a regra "um único método abstrato" elimina.

## A anotação @FunctionalInterface

Java oferece a anotação `@FunctionalInterface`, opcional, mas recomendada, para marcar explicitamente a intenção de que uma interface seja funcional:

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
```

Ela não muda o comportamento da interface, mas faz o compilador verificar a regra em tempo de compilação: se alguém adicionar um segundo método abstrato por engano, o código deixa de compilar, com uma mensagem de erro clara, em vez de gerar um problema sutil mais adiante. É uma forma de documentar a intenção e proteger a interface contra alterações acidentais.

Vale notar que uma interface funcional pode ter métodos `default` e `static` à vontade, além do único método abstrato — o que conta para a regra é exclusivamente a quantidade de métodos **abstratos**. É por isso que, por exemplo, `List` continua sendo uma interface comum (não funcional), mas ainda assim consegue oferecer o método `default void sort(Comparator<? super E> c)` sem qualquer conflito.

## Outras interfaces funcionais comuns do Java

O pacote `java.util.function` reúne uma série de interfaces funcionais genéricas, prontas para uso, que cobrem os padrões mais comuns de manipulação de dados. As três mais usadas no dia a dia são:

`Predicate<T>`, que representa uma função que recebe um valor e devolve um `boolean` — usada tipicamente para testar uma condição (por exemplo, filtrar elementos de uma lista).

`Function<T, R>`, que representa uma função que recebe um valor de tipo `T` e devolve um valor de tipo `R` — usada para transformar dados de um tipo em outro.

`Consumer<T>`, que representa uma função que recebe um valor e não devolve nada (`void`) — usada para executar alguma ação com o dado recebido, como imprimir ou salvar.

Uma observação importante: diferente de `Predicate` e `Function`, que idealmente devem manter a transparência referencial (sem efeitos colaterais), o `Consumer` é a exceção esperada dessa regra — sua própria razão de existir é justamente produzir efeitos colaterais (como imprimir na tela, gravar em um arquivo ou alterar algo fora de si mesmo), já que ele não retorna nenhum valor que poderia representar um resultado "puro".

Essas três interfaces, junto com `Comparator`, formam a base sobre a qual são construídas as operações de Streams (como `filter`, que usa `Predicate`; `map`, que usa `Function`; e `forEach`, que usa `Consumer`) — assunto das próximas anotações.