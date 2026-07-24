# Interfaces em Java

## 1. O que é uma interface

Uma **interface** é um tipo que define um **conjunto de operações que uma classe deve implementar**. Ela estabelece um **contrato**: quem implementa a interface se compromete a fornecer aquele comportamento, mas a interface em si não diz *como* isso deve ser feito — apenas *o quê* deve existir.

```java
interface Shape {
    double area();
    double perimeter();
}
```

Toda classe que implementar `Shape` é obrigada a fornecer os métodos `area()` e `perimeter()`, cada uma com sua própria lógica interna.

**Para que servem interfaces?** Principalmente para criar sistemas com **baixo acoplamento** e mais **flexíveis** — uma classe pode depender apenas do *contrato* (a interface), sem conhecer os detalhes da implementação concreta por trás dele. Isso é o que permite trocar peças do sistema sem quebrar o resto.

---

## 2. Problema motivador: acoplamento forte vs. acoplamento fraco

Imagine uma locadora de carros que precisa calcular o imposto sobre o valor do aluguel. Sem interface, o serviço de locação (`RentalService`) dependeria diretamente de uma implementação concreta de cálculo de imposto:

### Acoplamento forte (sem interface)

```java
class RentalService {
    private double pricePerHour;
    private double pricePerDay;
    private BrazilTaxService taxService; // depende do CONCRETO

    // ...
}
```

**Problema:** se a classe concreta mudar — por exemplo, a locadora expande para outro país e precisa calcular imposto diferente — é preciso alterar `RentalService` diretamente, mexendo em uma classe que, a princípio, não deveria se importar com *qual* serviço de imposto está sendo usado, apenas que exista um.

### Acoplamento fraco (com interface)

```java
interface TaxService {
    double tax(double amount);
}

class BrazilTaxService implements TaxService {
    public double tax(double amount) {
        return amount <= 100.0 ? amount * 0.20 : amount * 0.15;
    }
}

class UsaTaxService implements TaxService {
    public double tax(double amount) {
        return amount * 0.10; // exemplo
    }
}

class RentalService {
    private double pricePerHour;
    private double pricePerDay;
    private TaxService taxService; // depende da INTERFACE

    // ...
}
```

**Vantagem:** `RentalService` não sabe (nem precisa saber) qual implementação concreta está por trás — se `BrazilTaxService` mudar, ou surgir uma `UsaTaxService` nova, `RentalService` continua exatamente igual. Essa é a essência do baixo acoplamento: trocar a peça sem afetar a estrutura em volta.

---

## 3. Inversão de controle e injeção de dependência

Só declarar `private TaxService taxService;` não resolve tudo sozinho — em algum momento, alguém precisa decidir *qual* implementação concreta vai ser usada e entregá-la para `RentalService`. É aí que entram dois conceitos que andam juntos:

- **Inversão de controle (IoC)** — um padrão de desenvolvimento que consiste em **retirar da classe a responsabilidade de instanciar suas próprias dependências**. Em vez de `RentalService` fazer `new BrazilTaxService()` internamente, ela recebe essa dependência já pronta de fora.
- **Injeção de dependência (DI)** — é a forma prática de realizar essa inversão de controle: um componente **externo** instancia a dependência e a **injeta** no objeto que precisa dela. Pode ser feita de algumas formas:
  - Via **construtor** (a mais comum e recomendada)
  - Via classe de instanciação (**builder**/**factory**)
  - Via **container/framework** (como o Spring, que automatiza esse processo)

### Exemplo: injeção via construtor

```java
class RentalService {
    private double pricePerHour;
    private double pricePerDay;
    private TaxService taxService;

    public RentalService(double pricePerHour, double pricePerDay, TaxService taxService) {
        this.pricePerHour = pricePerHour;
        this.pricePerDay = pricePerDay;
        this.taxService = taxService;
    }
}
```

```java
// Quem monta o objeto decide qual implementação concreta usar
RentalService rentalService = new RentalService(10.0, 130.0, new BrazilTaxService());
```

Repare que `new BrazilTaxService()` é atribuído a um parâmetro do tipo `TaxService` — isso é chamado de **upcasting**, e é possível porque `BrazilTaxService implements TaxService`. Toda instância de uma classe que implementa uma interface **é também** daquele tipo de interface (é a chamada relação **é-um**), então ela pode ser tratada como tal em qualquer lugar que espere o tipo da interface.

---

## 4. Herdar vs. cumprir contrato

Herança (`extends`) e interfaces (`implements`) têm coisas em comum:
- Relação **é-um**
- Generalização/especialização
- **Polimorfismo**

```java
abstract class Shape {
    Color color;
    abstract double area();
}
class Rectangle extends Shape {
    double width, height;
    double area() { return width * height; }
}
class Circle extends Shape {
    double radius;
    double area() { return Math.PI * radius * radius; }
}
```

```java
interface TaxService {
    double tax(double amount);
}
class BrazilTaxService implements TaxService {
    public double tax(double amount) { return amount * 0.15; }
}
class UsaTaxService implements TaxService {
    public double tax(double amount) { return amount * 0.10; }
}
```

Mas existe uma diferença fundamental de propósito entre as duas:

| | Propósito |
|---|---|
| **Herança** | **Reuso** de código/estrutura |
| **Interface** | **Contrato** a ser cumprido, sem impor implementação |

### Combinando os dois: contrato + reuso

Às vezes você quer os dois benefícios ao mesmo tempo: um contrato definido via interface, mas também uma estrutura comum reutilizável entre as implementações concretas. A solução clássica é uma **classe abstrata intermediária**, que implementa a interface e é estendida pelas classes concretas:

```java
interface Shape {
    double area();
}

abstract class AbstractShape implements Shape {
    protected Color color; // atributo comum, reutilizável por quem estender
    // métodos utilitários comuns também podem ficar aqui
}

class Rectangle extends AbstractShape {
    private double width, height;
    public double area() { return width * height; }
}

class Circle extends AbstractShape {
    private double radius;
    public double area() { return Math.PI * radius * radius; }
}
```

Esse padrão aparece bastante em código real — por exemplo, um `EmailService` (interface, o contrato) com uma `AbstractEmailService` (classe abstrata, o reuso) implementada por diferentes serviços concretos, como um `MockEmailService` (para testes) e um `SmtpEmailService` (para produção).

---

## 5. Herança múltipla e o problema do diamante

**Herança múltipla** — uma classe herdar de mais de uma superclasse — **não é permitida na maioria das linguagens**, incluindo Java. O motivo é o chamado **problema do diamante**: uma ambiguidade causada pela existência do mesmo método em mais de uma superclasse.

```
        Device
       /       \
  Scanner      Printer      ambos "herdariam" processDoc()
       \       /
     ComboDevice             qual dos dois processDoc() prevalece?
```

Se `Scanner` e `Printer` fossem ambos superclasses concretas de `ComboDevice`, e cada uma tivesse sua própria versão de `processDoc()`, o compilador não teria como decidir qual das duas implementações herdar — daí o nome "problema do diamante", pela forma do diagrama de herança.

### A saída: uma classe pode implementar múltiplas interfaces

```java
interface Scanner {
    String scan();
}

interface Printer {
    void print(String doc);
}

class ComboDevice implements Scanner, Printer {
    public String scan() { /* ... */ return ""; }
    public void print(String doc) { /* ... */ }
}
```

> ⚠️ **Atenção:** isso **não é** herança múltipla, porque **não há reuso** de implementação vindo das interfaces — `ComboDevice` não *herda* comportamento de `Scanner` ou `Printer`, ela apenas **implementa** (cumpre) cada contrato, escrevendo sua própria lógica para cada método. Como não há implementação sendo herdada, não existe ambiguidade possível — e é por isso que uma classe pode implementar quantas interfaces quiser, sem esbarrar no problema do diamante clássico.

---

## 6. A interface `Comparable<T>`

Uma das interfaces mais usadas da própria biblioteca padrão do Java é a `Comparable<T>`, usada para definir uma **ordem natural** entre objetos de uma classe.

```java
public interface Comparable<T> {
    int compareTo(T o);
}
```

**Contrato do retorno de `compareTo`:**
- Negativo → `this` é "menor que" `o`
- Zero → `this` é "igual a" `o`
- Positivo → `this` é "maior que" `o`

```java
System.out.println("maria".compareTo("alex"));  // positivo (m > a)
System.out.println("alex".compareTo("maria"));  // negativo (a < m)
System.out.println("maria".compareTo("maria")); // 0
```

### Por que isso importa: ordenar objetos com `Collections.sort`

Para tipos como `String`, `Collections.sort(list)` já funciona direto, porque `String` já implementa `Comparable<String>`. Mas para uma classe própria, é preciso implementar a interface explicitamente, dizendo **como** dois objetos devem ser comparados:

```java
public class Employee implements Comparable<Employee> {
    private String name;
    private Double salary;

    public Employee(String name, Double salary) {
        this.name = name;
        this.salary = salary;
    }

    public String getName() { return name; }
    public Double getSalary() { return salary; }

    @Override
    public int compareTo(Employee other) {
        return name.compareTo(other.getName()); // ordena por nome
    }
}
```

```java
List<Employee> list = new ArrayList<>();
// ... popula a lista, por exemplo lendo de um arquivo CSV
Collections.sort(list); // funciona, pois Employee agora é Comparable
```

Isso resolve o caso mais simples, mas tem uma limitação: `compareTo` define uma **única** ordem natural para a classe inteira. E se, em outro momento do programa, você quiser ordenar os mesmos funcionários por salário, em vez de por nome? Reescrever `compareTo` mudaria a ordem "padrão" da classe inteira, afetando qualquer outro código que já dependa dela.

Para isso, desde o Java 8, existe a interface `Comparator<T>`, que permite definir ordens **alternativas** sem tocar na classe `Employee`, geralmente combinada com expressões lambda:

```java
list.sort(Comparator.comparing(Employee::getName));
list.sort(Comparator.comparing(Employee::getSalary).reversed());

// combinando critérios: por salário, e em caso de empate, por nome
list.sort(Comparator.comparing(Employee::getSalary).thenComparing(Employee::getName));
```

Resumindo a diferença: `Comparable` mora **dentro** da classe e define sua ordem natural (só existe uma); `Comparator` mora **fora**, criado sob demanda, e permite quantas ordens alternativas forem necessárias.

---

## 7. Default methods (Java 8+)

Até aqui, interfaces só definiam contratos — nenhuma linha de implementação. Isso mudou a partir do Java 8: interfaces passaram a poder conter **métodos concretos**, chamados de **default methods** (ou *defender methods*), usando a palavra-chave `default`.

### Motivação

- Evitar **repetição de implementação** em toda classe que implementa a interface.
- Evitar a necessidade de criar uma **classe abstrata** só para prover reuso (a solução vista na seção 4).
- Manter **retrocompatibilidade**: bibliotecas conseguiram adicionar novos métodos a interfaces já existentes, sem quebrar todas as classes que já as implementavam antes dessa mudança.
- Permitir que **interfaces funcionais** (que devem ter só um método abstrato — mais sobre isso já já) ofereçam métodos utilitários extras, sem deixar de ser "funcionais".

### Exemplo

Imagine calcular o valor de um empréstimo com juros compostos, mas com uma taxa diferente por país:

```java
interface InterestService {
    double getInterestRate(); // cada país implementa sua própria taxa

    // default method: implementação compartilhada por TODAS as implementações
    default double payment(double amount, int months) {
        double interestRate = getInterestRate();
        return amount * Math.pow(1 + interestRate / 100, months);
    }
}

class BrazilInterestService implements InterestService {
    public double getInterestRate() { return 2.0; } // 2% ao mês
}

class UsaInterestService implements InterestService {
    public double getInterestRate() { return 1.0; } // 1% ao mês
}
```

```java
InterestService service = new BrazilInterestService();
System.out.println(service.payment(200.0, 3)); // 212.24

service = new UsaInterestService();
System.out.println(service.payment(200.0, 3)); // 206.06
```

Aqui, `payment()` é escrito **uma única vez** dentro da interface, e reaproveitado por qualquer implementação — sem precisar de uma classe abstrata intermediária como na seção 4.

### Duas evoluções que vieram logo depois

Uma vez que interfaces passaram a carregar implementação, fazia sentido também organizar esse código internamente. Duas adições vieram para resolver isso:

**Métodos `static`** (Java 8) — úteis como *factory methods* ou utilitários relacionados diretamente à interface, sem precisar de uma classe utilitária à parte:

```java
interface InterestService {
    double getInterestRate();

    default double payment(double amount, int months) {
        return amount * Math.pow(1 + getInterestRate() / 100, months);
    }

    static InterestService of(String country) {
        return switch (country) {
            case "BR" -> new BrazilInterestService();
            case "US" -> new UsaInterestService();
            default -> throw new IllegalArgumentException("País não suportado: " + country);
        };
    }
}
```

```java
InterestService service = InterestService.of("BR");
```

**Métodos `private`** (Java 9) — para evitar duplicação de código **entre os próprios** default/static methods da interface, sem expor esse código como parte do contrato público:

```java
interface InterestService {
    double getInterestRate();

    default double payment(double amount, int months) {
        return calculate(amount, months);
    }

    default double paymentWithFee(double amount, int months, double fee) {
        return calculate(amount, months) + fee;
    }

    private double calculate(double amount, int months) { // só usado internamente
        return amount * Math.pow(1 + getInterestRate() / 100, months);
    }
}
```

### Considerações importantes sobre default methods

Com essas mudanças, algumas afirmações que pareciam definitivas nas seções anteriores precisam de ajuste:

- **Agora interfaces podem prover reuso**, não só contrato — a distinção "herança = reuso / interface = contrato" (seção 4) deixou de ser absoluta.
- **Agora existe uma forma de herança múltipla de comportamento**, já que uma classe pode implementar várias interfaces, cada uma trazendo seus próprios default methods prontos.
- Justamente por isso, **o problema do diamante pode voltar a acontecer**: se uma classe implementa duas interfaces que têm um default method com a **mesma assinatura**, o compilador não sabe qual das duas usar e **obriga a classe a sobrescrever** o método explicitamente, resolvendo a ambiguidade manualmente.
- Apesar de tudo isso, interfaces continuam **bem diferentes** de classes abstratas: interfaces **não têm construtores** nem **atributos de instância** (podem ter apenas constantes `public static final`).

---

## 8. Interfaces funcionais e expressões lambda

Uma consequência direta dos default methods é que ficou possível ter interfaces com **um único método abstrato** e, ainda assim, vários métodos utilitários prontos (default/static) — esse tipo de interface ganhou um nome: **interface funcional**.

```java
@FunctionalInterface
interface TaxService {
    double tax(double amount);
}
```

Interfaces funcionais são a base das **expressões lambda**: em vez de criar uma classe inteira só para implementar um método, você pode fornecer a implementação diretamente como uma expressão:

```java
TaxService brazilTax = amount -> amount <= 100 ? amount * 0.20 : amount * 0.15;
System.out.println(brazilTax.tax(150.0)); // 22.5
```

A anotação `@FunctionalInterface` é opcional, mas recomendada: ela faz o compilador **avisar** caso, por engano, um segundo método abstrato seja adicionado à interface no futuro, quebrando a premissa de que ela é "funcional".

---

## 9. Sealed interfaces (Java 17+)

Uma limitação histórica das interfaces é que **qualquer classe** pode implementá-las — não há como restringir quem tem permissão de cumprir aquele contrato. Isso é útil na maioria dos casos, mas às vezes você quer modelar um conjunto **fechado e conhecido** de variações, algo parecido com um `enum`, mas com estado próprio em cada implementação (como o caso de `TaxService`, com exatamente `BrazilTaxService` e `UsaTaxService`, sem previsão de novas implementações externas).

Para isso, o Java 17 introduziu as **sealed interfaces**:

```java
sealed interface TaxService permits BrazilTaxService, UsaTaxService {
    double tax(double amount);
}

final class BrazilTaxService implements TaxService {
    public double tax(double amount) { return amount * 0.15; }
}

final class UsaTaxService implements TaxService {
    public double tax(double amount) { return amount * 0.10; }
}

// qualquer outra classe tentando "implements TaxService" não compila
```

Combinado com o *pattern matching* em `switch` (Java 21+), isso permite que o compilador verifique se **todos os casos possíveis** foram cobertos, sem precisar de uma cláusula `default` — o que reduz bugs de "esqueci de tratar esse caso" quando uma nova implementação é adicionada no futuro.
