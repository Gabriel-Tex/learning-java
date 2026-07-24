# UML para Programação Orientada a Objetos

## 1. O que é UML e por que usar

**UML** (*Unified Modeling Language*) é uma linguagem visual padronizada para representar a estrutura e o comportamento de sistemas orientados a objetos, antes ou durante a escrita do código. O objetivo é comunicar decisões de design de forma independente de linguagem de programação.

Esta nota foca no **diagrama de classes**, que é o mais usado para modelar orientação a objetos, mas a seção final também cobre brevemente outros tipos de diagrama UML (sequência, casos de uso).

---

## 2. Notação básica de uma classe

Uma classe é representada por um **retângulo dividido em três compartimentos**:

```
┌─────────────────────────┐
│         Employee        │  ← nome da classe
├─────────────────────────┤
│ - name : String         │  ← atributos
│ - salary : Double       │
├─────────────────────────┤
│ + getName() : String    │  ← métodos
│ + getSalary() : Double  │
│ + raiseSalary(pct: Double) : void 
└─────────────────────────┘
```

Código correspondente:

```java
public class Employee {
    private String name;
    private Double salary;

    public String getName() {
        return name;
    }

    public Double getSalary() {
        return salary;
    }

    public void raiseSalary(Double pct) {
        salary += salary * pct / 100;
    }
}
```

### 2.1 Símbolos de visibilidade

| Símbolo | Visibilidade | Equivalente em Java |
|---|---|---|
| `+` | Público | `public` |
| `-` | Privado | `private` |
| `#` | Protegido | `protected` |
| `~` | Pacote (padrão) | *sem modificador* |

### 2.2 Formato de atributos e métodos

```
nome : Tipo                          → atributo
nome(param1 : Tipo, param2 : Tipo) : TipoRetorno   → método
```

```
- salary : Double
+ raiseSalary(pct : Double) : void
```

### 2.3 Atributos/métodos estáticos e abstratos

- **Estático** → texto **sublinhado**.
- **Abstrato** → texto em ***itálico***.

```
┌───────────────────────────┐
│        Shape (abstract)   │  ← nome em itálico = classe abstrata
├───────────────────────────┤
│ # color : Color           │
├───────────────────────────┤
│ + area() : Double         │  ← em itálico = método abstrato
│ + getColor() : Color      │  ← normal = método concreto
└───────────────────────────┘
```

```java
public abstract class Shape {
    protected Color color;

    public abstract Double area(); // itálico no diagrama = sem corpo

    public Color getColor() { // normal no diagrama = já implementado
        return color;
    }
}
```

---

## 3. Interfaces

Interfaces são representadas com o estereótipo `<<interface>>` acima do nome, e todos os métodos são implicitamente abstratos (sem sublinhado de estático, sem itálico necessário, já que tudo ali é contrato):

```
┌───────────────────────────┐
│      <<interface>>        │
│       TaxService          │
├───────────────────────────┤
│ + tax(amount : Double) : Double 
└───────────────────────────┘
```

```java
public interface TaxService {
    Double tax(Double amount);
}
```

---

## 4. Relacionamentos entre classes

Esta é a parte mais importante — e a que costuma gerar confusão. Existem seis tipos principais de relacionamento em um diagrama de classes, cada um com uma linha e uma "ponta" diferentes.

### 4.1 Associação (Association)

**O que representa:** uma classe **conhece** e se relaciona com outra, mas cada uma existe de forma independente. É o relacionamento mais genérico — "tem uma referência para".

**Notação:** linha simples, podendo ter seta indicando direção de navegação, além de multiplicidade e nomes de papel (*role*) em cada ponta.

```
┌─────────┐  1        0..*  ┌─────────┐
│ Client  │◀────────────────│  Order  │
└─────────┘  - client       │ -orders │
                            └─────────┘
```

```java
public class Client {
    private String name;
    // Client pode ou não conhecer seus pedidos
}

public class Order {
    private Client client; // Order CONHECE Client — associação
}
```

**Característica-chave:** nenhuma das classes é "parte" da outra — `Client` e `Order` têm ciclos de vida totalmente independentes. Um `Client` pode existir sem nenhum `Order`, e um `Order` referencia um `Client`, mas não o "possui" no sentido estrutural.

#### Associação unidirecional vs. bidirecional

- **Unidirecional** (seta em uma ponta só): apenas uma classe conhece a outra.
```
  Order ───────────▶ Client
```
```java
  class Order {
      private Client client; // Order conhece Client
  }
  class Client {
      // Client NÃO tem referência para Order
  }
```

- **Bidirecional** (sem seta, ou seta nas duas pontas): as duas classes se conhecem mutuamente.
```
  Order ◀──────────▶ Client
```
```java
  class Order {
      private Client client;
  }
  class Client {
      private List<Order> orders; // Client também conhece seus Orders
  }
```

### 4.2 Multiplicidade

Indica **quantas instâncias** de uma classe se relacionam com quantas instâncias de outra. Fica escrita perto de cada ponta da linha.

| Notação | Significado |
|---|---|
| `1` | exatamente um |
| `0..1` | zero ou um (opcional) |
| `0..*` ou `*` | zero ou muitos |
| `1..*` | um ou muitos (pelo menos um) |
| `3..5` | intervalo específico |

```
┌─────────┐  1                 ┌───────────┐
│  Order  │────────────────────│  Invoice  │
└─────────┘   - invoice   0..1 └───────────┘
```

Isso significa: um `Order` tem no máximo um `Invoice` (pode não ter nenhum ainda), e um `Invoice` pertence a exatamente um `Order`.

```java
public class Order {
    private Invoice invoice; // pode ser null → 0..1
}
```

```
┌─────────┐   1              *  ┌───────────┐
│  Order  │─────────────────────│ OrderItem │
└─────────┘      - items        └───────────┘
```

Um `Order` tem muitos `OrderItem` (representado como coleção):

```java
public class Order {
    private List<OrderItem> items = new ArrayList<>(); // multiplicidade "*"
}
```

### 4.3 Agregação (Aggregation)

**O que representa:** uma relação **todo-parte fraca** — "tem um", mas a parte **pode existir independentemente** do todo. Se o todo for destruído, a parte continua existindo.

**Notação:** linha com **losango vazio (branco)** na ponta do "todo".

```
┌───────────┐         ┌──────────┐
│  Team     │◇────────│  Player  │
└───────────┘  1    * └──────────┘
              - players
```

```java
public class Team {
    private List<Player> players = new ArrayList<>();

    public Team(List<Player> players) {
        this.players = players; // os Players são criados FORA e passados para o Team
    }
}

public class Player {
    private String name;
    // Player existe de forma independente — pode trocar de time, ou existir sem time algum
}
```

**Teste mental para reconhecer agregação:** "se eu apagar o `Team`, o `Player` continua existindo (talvez em outro time, ou sem time)?" Se sim, é agregação.

### 4.4 Composição (Composition)

**O que representa:** uma relação **todo-parte forte** — a parte **não tem sentido de existir** fora do todo. Se o todo for destruído, as partes são destruídas junto (ciclo de vida acoplado).

**Notação:** linha com **losango preenchido (preto)** na ponta do "todo".

```
┌───────────┐         ┌──────────┐
│  Order    │◆────────│OrderItem │
└───────────┘  1    * └──────────┘
              - items
```

```java
public class Order {
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item); // OrderItem é criado e gerenciado PELO Order
    }
    // não existe um construtor público de OrderItem chamado fora do contexto de Order
}

public class OrderItem {
    private Integer quantity;
    private Double price;
    // um OrderItem sem um Order "dono" não faz sentido no domínio
}
```

**Teste mental:** "se eu apagar o `Order`, faz sentido o `OrderItem` continuar existindo sozinho?" Se não, é composição.

> 💡 Na prática em Java, **a diferença entre agregação e composição raramente aparece na sintaxe do código** (as duas usam uma referência ou coleção normal) — é uma diferença de **intenção de design e de ciclo de vida**, geralmente reforçada por como os objetos são criados/destruídos (ex: `OrderItem` só é instanciado dentro de métodos de `Order`, nunca de forma solta) e, em sistemas com banco de dados, por regras de cascade delete.

### 4.5 Generalização / Herança (Generalization)

**O que representa:** relação **é-um** entre uma classe mais genérica (superclasse) e uma mais específica (subclasse), com reuso de implementação.

**Notação:** linha com **seta triangular vazia (branca)**, apontando para a superclasse.

```
        ┌───────────┐
        │   Shape   │
        └───────────┘
              △
      ┌───────┴───────┐
┌───────────┐   ┌───────────┐
│ Rectangle │   │  Circle   │
└───────────┘   └───────────┘
```

```java
public abstract class Shape {
    protected Color color;
    public abstract Double area();
}

public class Rectangle extends Shape {
    private Double width, height;
    public Double area() { return width * height; }
}

public class Circle extends Shape {
    private Double radius;
    public Double area() { return Math.PI * radius * radius; }
}
```

### 4.6 Realização / Implementação (Realization)

**O que representa:** uma classe **cumpre o contrato** de uma interface — parecido com generalização, mas sem reuso de implementação (ver nossa nota sobre interfaces para a diferença conceitual completa).

**Notação:** linha **tracejada** com **seta triangular vazia**, apontando para a interface.

```
      ┌────────────────────┐
      │   <<interface>>    │
      │     TaxService     │
      └────────────────────┘
                △
                ╎ (tracejado)
      ┌────────────────────┐
      │  BrazilTaxService  │
      └────────────────────┘
```

```java
public interface TaxService {
    Double tax(Double amount);
}

public class BrazilTaxService implements TaxService {
    public Double tax(Double amount) {
        return amount <= 100.0 ? amount * 0.20 : amount * 0.15;
    }
}
```

### 4.7 Dependência (Dependency)

**O que representa:** a relação **mais fraca** de todas — uma classe **usa** outra momentaneamente (geralmente como parâmetro de método, tipo de retorno, ou variável local), sem guardar uma referência permanente como atributo.

**Notação:** linha **tracejada** com seta simples (aberta), apontando para a classe usada.

```
┌──────────────┐            ┌────────────┐
│ OrderService │╌╌╌╌╌╌╌╌╌╌▶ │  Invoice   │
└──────────────┘            └────────────┘
```

```java
public class OrderService {
    // Invoice NÃO é atributo de OrderService — é usada só de passagem
    public Invoice generateInvoice(Order order) {
        Invoice invoice = new Invoice(/* ... */);
        return invoice;
    }
}
```

**Diferença entre dependência e associação:** associação é um relacionamento **estrutural e duradouro** (atributo da classe); dependência é **temporária e local** (parâmetro, variável local, tipo de retorno). Se você apagar o método, a dependência desaparece; a associação, não — ela é parte da estrutura da classe.

---

## 5. Comparativo visual rápido das pontas de linha

```
──────────▶     Associação direcionada (navegação)
──────────      Associação bidirecional / não direcionada
◇────────       Agregação (losango vazio no "todo")
◆────────       Composição (losango preenchido no "todo")
──────△         Generalização/Herança (seta vazia, linha cheia)
╌╌╌╌╌△          Realização/Implementação (seta vazia, linha tracejada)
╌╌╌╌╌▶          Dependência (seta aberta, linha tracejada)
```

## 6. Comparativo de intensidade do acoplamento

Da relação **mais fraca** para a **mais forte**:

```
Dependência  <  Associação  <  Agregação  <  Composição  <  Herança
(mais fraco)                                              (mais forte)
```

- **Dependência** — uso pontual, sem guardar referência.
- **Associação** — referência guardada, ciclos de vida independentes.
- **Agregação** — referência guardada, relação todo-parte, mas partes sobrevivem ao todo.
- **Composição** — referência guardada, relação todo-parte, partes **não sobrevivem** ao todo.
- **Herança** — o acoplamento mais forte de todos, porque a subclasse depende inclusive da estrutura interna (protegida) da superclasse.

> Esse é o mesmo princípio por trás da recomendação **"favoreça composição a herança"**: quanto mais fraco o acoplamento entre duas classes, mais fácil é modificar uma sem quebrar a outra.

---

## 7. Exemplo completo: modelando um sistema de pedidos

Juntando várias notações em um único diagrama coerente, no estilo do que costuma aparecer em provas e projetos de Engenharia de Software:

```
┌─────────────────┐
│     Client      │
├─────────────────┤
│ - name : String │
│ - email : String│
└─────────────────┘
         △ 1
         │
         │ - client
         ◇ 1
┌─────────────────┐                    ┌──────────────────┐
│      Order      │  1               * │    OrderItem     │
│                 │ ◆───────────────── │                  │
├─────────────────┤    - items         ├──────────────────┤
│ - moment : Date │                    │ - quantity : Int │
│ - status : OrderStatus               │ - price : Double │
├─────────────────┤                    ├──────────────────┤
│ + addItem() : void                   │ + subTotal() : Double 
│ + total() : Double                   └──────────────────┘
└─────────────────┘                              │
         │                                       │ - product
         │ - invoice  0..1                       ▽ 1
         ▽                             ┌──────────────────┐
┌─────────────────┐                    │      Product     │
│     Invoice     │                    ├──────────────────┤
├─────────────────┤                    │ - name : String  │
│ - basicPayment : Double              │ - price : Double │
│ - tax : Double  │                    └──────────────────┘
└─────────────────┘
```

Análise dos relacionamentos:
- `Client` ↔ `Order` → **associação** (um cliente existe independente de ter pedidos; multiplicidade 1 para *).
- `Order` ◆ `OrderItem` → **composição** (um item de pedido não existe fora de um pedido).
- `OrderItem` → `Product` → **associação** (um item referencia um produto, mas o produto existe independentemente, no catálogo).
- `Order` → `Invoice` → **associação opcional** (`0..1`, o pedido pode ainda não ter fatura gerada).

```java
public class Client {
    private String name;
    private String email;
}

public class Order {
    private Date moment;
    private OrderStatus status;
    private Client client;                  // associação
    private List<OrderItem> items = new ArrayList<>(); // composição
    private Invoice invoice;                 // associação opcional (0..1)

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public Double total() {
        double sum = 0.0;
        for (OrderItem item : items) sum += item.subTotal();
        return sum;
    }
}

public class OrderItem {
    private Integer quantity;
    private Double price;
    private Product product; // associação

    public Double subTotal() {
        return price * quantity;
    }
}

public class Product {
    private String name;
    private Double price;
}

public class Invoice {
    private Double basicPayment;
    private Double tax;
}

public enum OrderStatus {
    PENDING_PAYMENT, PROCESSING, SHIPPED, DELIVERED
}
```

---

## 8. Estereótipos comuns

Além de `<<interface>>`, existem outros estereótipos padronizados úteis ao modelar arquitetura em camadas (ver nossa nota sobre Entities vs. Services):

| Estereótipo | Significado |
|---|---|
| `<<interface>>` | Interface (contrato) |
| `<<abstract>>` | Classe abstrata (alternativa a escrever o nome em itálico) |
| `<<enumeration>>` | Enum |
| `<<entity>>` | Classe que representa dado persistente (seção "Entities" da nota anterior) |
| `<<service>>` | Classe de lógica de negócio/orquestração |
| `<<repository>>` | Classe de acesso a dados |
| `<<controller>>` | Classe de entrada de requisições (ex: REST controller) |

```
┌──────────────────┐
│ <<enumeration>>  │
│    OrderStatus   │
├──────────────────┤
│ PENDING_PAYMENT  │
│ PROCESSING       │
│ SHIPPED          │
│ DELIVERED        │
└──────────────────┘
```

```java
public enum OrderStatus {
    PENDING_PAYMENT, PROCESSING, SHIPPED, DELIVERED
}
```

---

## 9. Classes genéricas (Generics) em UML

Representadas com um pequeno retângulo pontilhado no canto superior direito, contendo o(s) parâmetro(s) de tipo:

```
┌──────────────────┐
│  CrudRepository  │┄┄┄┐
├──────────────────┤   ┊ T, ID
│ + save(obj:T) : T│┄┄┄┘
│ + findById(id:ID):T 
└──────────────────┘
```

```java
public interface CrudRepository<T, ID> {
    T save(T obj);
    void delete(T obj);
    T findById(ID id);
    List<T> findAll();
}
```

---

## 10. Boas práticas de modelagem

1. **Prefira composição a herança** quando a relação não for genuinamente "é-um" — herança cria o acoplamento mais forte entre todas as relações (seção 6).
2. **Nomeie os papéis (roles) nas associações** quando o nome do atributo não for óbvio a partir do tipo (ex: `- client`, `- items`), facilitando a geração de código a partir do diagrama.
3. **Sempre inclua multiplicidade** — "um pedido tem itens" é ambíguo; "um pedido tem de 1 a N itens" (`1..*`) é preciso.
4. **Não modele todos os métodos** — diagramas de classe para comunicação de design geralmente omitem getters/setters óbvios, focando nos métodos que carregam regra de negócio.
5. **Use interface + composição para reduzir acoplamento**, revisitando a distinção entre interface (contrato) e classe abstrata/composição (reuso) já discutida nas notas anteriores.
6. **Distinga agregação de composição pela pergunta do ciclo de vida**: "a parte sobrevive à destruição do todo?" — se sim, agregação; se não, composição.
