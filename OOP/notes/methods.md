# Métodos e Construtores em Java

## 1. Métodos
### Formato (Exemplo)
```java
public static void name() {
    // Corpo do método
}
```

### Assinatura de um Método
A assinatura de um método é definida exclusivamente pela **quantidade e tipo dos parâmetros** de entrada.

### Métodos Prontos Úteis
* `Nome_do_Array.length`: Retorna o tamanho numérico de um array.
* `Nome_do_ArrayList.size()`: Retorna a quantidade de elementos de um ArrayList.
* `String.format("máscara", variavel)`: Formata o valor de uma variável para uma String com base na máscara definida.

## 2. A Palavra-chave `this` e Autorreferência
A palavra `this` é uma referência para o **próprio objeto** que chamou o método em questão.

**Usos comuns:**
1. **Diferenciar atributos:** Evita ambiguidade entre os atributos da classe e variáveis locais (ou parâmetros).
2. **Passar o objeto atual como argumento:** Utilizado na chamada de outro método ou construtor.

**Exemplo de Autorreferência:**
```java
void status() {
    System.out.println("Status: " + this.atributo1);
    System.out.println("Status: " + this.atributo2);
}

/* A palavra-chave "this" será substituída pelo objeto que chamou o método: */
novo_objeto.status();
```

**Exemplo passando o objeto como argumento:**
```java
public class ChessMatch {
    // ...
    placeNewPiece('e', 1, new King(board, Color.WHITE, this));
    // ...
}
```

## 3. Encapsulamento: Métodos Getters e Setters
* **Getter:** Responsável por retornar o valor de um atributo do objeto.
* **Setter:** Responsável por alterar o valor de um atributo do objeto.

```java
public tipo getAtributo() {
    return this.atributo;
}

public void setAtributo(tipo x) {
    this.atributo = x;
}
```

## 4. Método Construtor
O método construtor define os atributos padrões (e outras ações iniciais) dos objetos instanciados pela classe em questão.

```java
// Construtor Padrão
public NomeDaClasse() {
    this.atributo1 = x;
    this.atributo2 = y;
    this.metodo1();
}

// Construtor Parametrizado
public NomeDaClasse(String a, float b, String c) {
    this.atributo1 = a;
    this.atributo2 = b;
    this.metodo1(c);
}
```
*Observação: Deve-se passar os argumentos de acordo com a assinatura do construtor no momento de instanciar um novo objeto.*