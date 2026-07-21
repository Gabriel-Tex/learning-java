# Programação Orientada a Objetos (POO) em Java

## 1. Declaração e Convenções de Classes
Por convenção, os identificadores de classes em Java são escritos com iniciais maiúsculas (**PascalCase**).

```java
public class Name {
    // Corpo da classe
}
```

## 2. Instanciação e Atributos de um Objeto
A criação de instâncias e a atribuição de valores ocorrem através da utilização de variáveis de referência e do operador `new`.

```java
// Instanciação de um objeto
Classe novo_objeto = new Classe();

// Definição de atributos do objeto
novo_objeto.atributo1 = x;
novo_objeto.atributo2 = y;
```

## 3. Vetores (Arrays) de Objetos
Em vez de reservar uma variável simples para cada objeto individualmente, é possível utilizar uma variável composta (vetor) para gerenciar múltiplos objetos da mesma classe de forma organizada.

```java
Classe v[] = new Classe[x];
v[0] = new Classe();
v[1] = new Classe();
// ...
v[x - 1] = new Classe();
```

## 4. Classes e Métodos Abstratos (`abstract`)
* **Classe Abstrata:** Pode conter tanto métodos abstratos quanto métodos concretos. Ela **não pode ser instanciada** diretamente e serve exclusivamente como classe progenitora (superclasse).
* **Método Abstrato:** É declarado, mas não implementado na classe progenitora. Um método abstrato **só pode** estar contido dentro de uma classe abstrata, obrigando as subclasses a fornecerem uma implementação.

### Estrutura de uma Classe Abstrata
```java
public abstract class Name {
    // Atributos
    private tipo atributo1;
    private tipo atributo2;
    
    // Métodos getters e setters
    
    // Métodos personalizados (finais ou abstratos)
    public abstract void metodoAbstrato();
}
```

## 5. Classes e Métodos Finais (`final`)
* **Classe Final:** Não pode ser herdada por nenhuma outra classe. Ela é obrigatoriamente uma classe "folha" na hierarquia de herança.
* **Método Final:** Não pode ser sobrescrito pelas suas subclasses. Ele é obrigatoriamente herdado mantendo seu comportamento inalterado.