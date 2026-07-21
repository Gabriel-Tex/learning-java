# Enumerações (Enums) em Java

## 1. Definição e Vantagens
Uma **enumeração** (`enum`) é um tipo especial em Java que serve para especificar de forma literal e segura um conjunto de constantes relacionadas.

### Vantagens
* **Melhor semântica:** Deixa claro o domínio de valores aceitos para determinada propriedade.
* **Código mais legível:** Evita o uso de "magic numbers" ou strings soltas.
* **Auxílio do compilador:** Garante a segurança de tipos, impedindo que valores inválidos sejam atribuídos acidentalmente.

## 2. Exemplo de Definição e Uso
### Declaração do Enum
```java
package entities.enums;

public enum OrderStatus {
    PENDING_PAYMENT,
    PROCESSING,
    SHIPPED,
    DELIVERED
}
```

### Utilização em uma Classe
O enum pode ser utilizado como tipo de atributo em outras classes do sistema.
```java
package entities;

import java.util.Date;
import entities.enums.OrderStatus;

public class Order {
    private Integer id;
    private Date moment;
    private OrderStatus status;
    
    // Construtores, getters, setters e métodos...
}
```

## 3. Conversão de String para Enum
Para converter uma `String` (com o nome exato da constante) para o tipo enumerado correspondente, utiliza-se o método `valueOf`.

```java
OrderStatus os1 = OrderStatus.DELIVERED;

// Converte a string informada para o enum correspondente
OrderStatus os2 = OrderStatus.valueOf("DELIVERED");
```