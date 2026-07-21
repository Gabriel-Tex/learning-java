# Encapsulamento e Interfaces em Java

## 1. Encapsulamento
O encapsulamento consiste em ocultar partes independentes da implementação, permitindo construir componentes que são invisíveis ao mundo exterior.

### Regra Geral Básica
* Um objeto **NÃO** deve expor nenhum de seus atributos (utiliza-se o modificador de acesso `private`).
* Os atributos devem ser acessados exclusivamente por meio de métodos de acesso (`get` e `set`).

### Vantagens
* **Tornar mudanças invisíveis:** Alterações internas não afetam o código que consome a classe.
* **Facilitar a reutilização de código:** Componentes independentes e bem encapsulados são mais fáceis de transportar para outros projetos.
* **Reduzir efeitos colaterais:** Garante um controle rígido sobre como e quando os dados internos são modificados.

## 2. Interface
Uma interface funciona como uma "lista de serviços" fornecidos por um componente. É o ponto de contato com o mundo exterior, definindo exatamente o que pode ser feito com um objeto, mas sem ditar como será feito.

### Características
* Contém apenas **métodos abstratos** (todos implicitamente públicos), que são declarados sem corpo (sem implementação).
* **Não possui atributos** de instância.

### Regras de Implementação
* Ao implementar uma interface, a classe correspondente deverá manter seus atributos como **privados**.
* Se uma classe implementa uma interface, ela é obrigada a desenvolver e implementar o código de **todos** os métodos abstratos contidos nela (mantendo a visibilidade pública).
* A classe implementadora conterá, além dos métodos da interface, seus próprios atributos privados, métodos getters e setters (geralmente públicos) e o método construtor.

### Definindo uma Interface
```java
public interface Interface {
    public abstract tipo metodo1();
    public abstract tipo metodo2();
}
```

### Implementando uma Interface
A palavra-chave `implements` é utilizada na declaração da classe.

```java
public class Classe implements Interface {
    // Atributos privados
    
    // Método construtor
    
    // Métodos getters e setters
    
    // Métodos da interface
    @Override
    public tipo metodo1() {
        // Corpo do método desenvolvido aqui
    }
    
    @Override
    public tipo metodo2() {
        // Corpo do método desenvolvido aqui
    }
}
```

> **Anotação `@Override`:** Deve anteceder os métodos implementados para indicar explicitamente que eles estão sobrescrevendo os métodos abstratos da interface.