# Polimorfismo em Java

## 1. Conceito de Polimorfismo
É a capacidade de um mesmo método ou objeto se comportar de diferentes formas dependendo do contexto.

## 2. Sobreposição (Overriding)
**Definição Formal:** Acontece quando substituímos um método de uma superclasse em uma subclasse, utilizando a **mesma assinatura**.

Na prática, ocorre quando se redefine em uma subclasse um método já definido anteriormente (seja de forma abstrata ou não), mudando sua implementação. Para que a assinatura seja considerada a mesma, a **quantidade e os tipos dos parâmetros** devem ser obrigatoriamente os mesmos.

* Um método pode ser sobreposto mais de uma vez ao longo da hierarquia, desde que não seja um método `final`.
* Só pode haver uma sobreposição do mesmo método em cada classe.

**Exemplo de Sobreposição:**
```java
/* Considere que existe um método chamado "metodo" em uma superclasse 
   da classe abaixo, recebendo o parâmetro x */

public class Classe {
    @Override
    public void metodo(Tipo x) {
        // implementação em código
    }
}
```

## 3. Sobrecarga (Overloading)
Permite que vários métodos com o **mesmo nome** existam em uma **mesma classe**, desde que tenham parâmetros diferentes (em tipo, número ou ordem).

Dependendo do argumento que for passado para o método chamado, uma implementação diferente será executada.

**Exemplo de Sobrecarga:**
```java
public class Calculadora {

    // Somar dois inteiros
    int somar(int a, int b) {
        return a + b;
    }

    // Somar três inteiros
    int somar(int a, int b, int c) {
        return a + b + c;
    }

    // Somar dois números reais
    double somar(double a, double b) {
        return a + b;
    }

    // Somar um inteiro com um real
    double somar(int a, double b) {
        return a + b;
    }
}
```

## 4. Em Suma
* **Sobreposição:** Mesma assinatura, **classes diferentes** (relação superclasse/subclasse).
* **Sobrecarga:** Assinaturas diferentes, **mesma classe**.