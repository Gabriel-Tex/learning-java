# Herança em Java

## 1. Conceito de Herança
A herança permite basear uma nova classe na definição de outra classe previamente existente. 
Dessa forma, um objeto da subclasse (filha) pode utilizar todos os métodos e atributos definidos na superclasse (mãe), embora o contrário não seja possível.

### Sintaxe
Para definir que uma classe `A` herda de uma classe `B`, utiliza-se a palavra-chave `extends`:
```java
public class A extends B {
    // Corpo da classe A, que herda de B
}
```

## 2. Categorias de Herança
* **Herança Pobre (ou de Implementação):** Ocorre quando uma subclasse herda de outra sem definir novos métodos ou atributos próprios, limitando-se apenas a implementar o que foi herdado (como métodos de uma classe abstrata).
* **Herança para Diferença:** Ocorre quando, além de herdar os atributos e métodos da superclasse, a subclasse expande seu comportamento implementando novos métodos e/ou atributos.

## 3. Sobrescrita de Métodos e `super()`
### Sobrescrita (`@Override`)
Quando um método da superclasse está sendo sobreposto por uma subclasse — ou seja, sua assinatura permanece a mesma, mas o seu comportamento (código) muda —, a declaração do método deve ser antecedida pela anotação `@Override`.

### A palavra-chave `super()`
É utilizada dentro da subclasse para fazer referência direta à classe progenitora. O uso mais comum do `super()` é dentro do construtor da subclasse para invocar o construtor da superclasse, garantindo que os atributos herdados sejam inicializados corretamente.

## 4. Classes e Métodos Abstratos vs. Finais
### Modificador `abstract`
* **Classe Abstrata:** Não pode ser instanciada (não se pode usar `new`). Serve exclusivamente como progenitora. Pode conter tanto métodos abstratos quanto concretos.
* **Método Abstrato:** É declarado, mas não implementado na progenitora.
    * Só pode existir dentro de uma classe abstrata.
    * A subclasse é **obrigada** a desenvolver (implementar) todos os métodos abstratos herdados.

### Modificador `final`
* **Classe Final:** Não pode ser herdada por nenhuma outra classe. Ela é obrigatoriamente uma classe "folha" na hierarquia.
* **Método Final:** Não pode ser sobrescrito pelas suas subclasses. Ele é obrigatoriamente herdado mantendo a sua implementação original inalterada.