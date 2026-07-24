# Diagrama de Sequência em UML

## 1. O que é e para que serve

O **diagrama de sequência** é um diagrama de **comportamento** (diferente do diagrama de classes, que é **estrutural** — ver nossa nota anterior sobre UML). Ele mostra **como objetos trocam mensagens entre si, ao longo do tempo**, para realizar um fluxo específico do sistema.

Enquanto o diagrama de classes responde **"o que existe e como se relaciona"**, o diagrama de sequência responde **"o que acontece, e em que ordem"**.

**Quando usar:** fluxos com lógica não-trivial, múltiplos participantes (serviços, banco, sistemas externos), ou processos assíncronos — exatamente o que discutimos ser o uso mais comum no mercado hoje (autenticação, integrações, pagamentos, webhooks).

**Quando não usar:** fluxos simples de CRUD, onde a sequência é óbvia (`Controller → Service → Repository → banco`, sem lógica condicional relevante) — nesse caso, o diagrama de sequência é *over-engineering*.

---

## 2. Elementos principais

### 2.1 Ator (Actor)

Representa um usuário ou sistema externo que **inicia** a interação. Desenhado como um "bonequinho" (a mesma figura usada em diagrama de casos de uso).

```
   ○
  /|\    Client
  / \
```

### 2.2 Participante / Objeto (Lifeline)

Cada participante do fluxo (um objeto, classe, serviço, ou componente) é representado por uma **caixa no topo**, com uma **linha vertical tracejada** descendo dela — essa linha é chamada de ***lifeline*** e representa a existência do objeto ao longo do tempo.

```
┌─────────────┐
│ :OrderService 
└──────┬──────┘
       ┊
       ┊  ← lifeline (linha de vida)
       ┊
```

> Convenção de nomenclatura: `nomeDoObjeto : NomeDaClasse`. Se o nome do objeto não for relevante para o diagrama, usa-se só `:NomeDaClasse` (como no exemplo acima) — comum quando você quer enfatizar o **tipo**, não uma instância específica.

### 2.3 Mensagens (Messages)

As setas horizontais entre lifelines representam **chamadas de método** (mensagens). Existem tipos diferentes, cada um com notação própria:

| Tipo | Notação | Significado |
|---|---|---|
| Síncrona | `───▶` (seta preenchida) | Chamada de método comum; quem chama **espera** a resposta antes de continuar |
| Assíncrona | `╌╌╌▷` (seta aberta/linha tracejada) | Chamada que **não bloqueia**; quem chama continua sem esperar (ex: publicar em fila) |
| Retorno | `◁╌╌╌` (tracejada, seta aberta, sentido inverso) | Resposta de uma chamada síncrona |
| Criação (`create`) | `───▶` apontando para o **topo** de uma nova lifeline | Um objeto cria outro (equivalente a um `new`) |
| Destruição (`destroy`) | `X` no fim da lifeline | O objeto deixa de existir a partir daquele ponto |

```
Client          OrderService          PaymentGateway
  │                   │                       │
  │  save(order)      │                       │
  │──────────────────▶│                       │  ← síncrona
  │                   │    charge(amount)     │
  │                   │──────────────────────▶│  ← síncrona
  │                   │◁──────────────────────│  ← retorno
  │                   │      (result)         │
  │◁──────────────────│                       │  ← retorno
  │   (confirmation)  │                       │
```

### 2.4 Ativação (Activation Bar / Focus of Control)

Um **retângulo estreito** sobre a lifeline indica que o objeto está **ativamente executando** algo (processando a chamada recebida). Toda mensagem síncrona geralmente gera uma barra de ativação no destino, que só termina quando a resposta é enviada de volta.

```
Client          OrderService
  │                   │
  │  save(order)      │
  │──────────────────▶│
  │                  ┌┴┐  ← ativação (OrderService está "trabalhando")
  │                  │ │
  │◁─────────────────└┬┘
  │   (confirmation)  │
```

### 2.5 Mensagem para si mesmo (Self-message / Self-call)

Quando um objeto chama um método de si mesmo (ou um método privado interno), a seta sai e volta para a **mesma** lifeline, formando um pequeno laço:

```
OrderService
    │
   ┌┴┐
   │ │ calculateTotal()
   │ │──┐
   │ │◀─┘
   └┬┘
```

```java
public class OrderService {
    public void processOrder(Order order) {
        Double total = calculateTotal(order); // self-message
        // ...
    }

    private Double calculateTotal(Order order) {
        // ...
        return 0.0;
    }
}
```

### 2.6 Nota (Note)

Um retângulo com o canto dobrado, usado para adicionar comentários explicativos em qualquer ponto do diagrama, sem afetar a semântica do fluxo:

```
┌──────────────────────┐
│ Nota: token expira   
│ em 15 minutos        │
└──────────────────────┘
         ┊
   OrderService
```

---

## 3. Fragmentos combinados (Combined Fragments)

Essa é a parte mais poderosa (e mais esquecida por quem aprende só o básico) do diagrama de sequência: permite representar **lógica condicional, loops e paralelismo** — coisas que um fluxo puramente linear de setas não consegue expressar.

Um fragmento é desenhado como um **retângulo com uma etiqueta no canto superior esquerdo** (dentro de um pequeno pentágono), envolvendo a parte do diagrama à qual se aplica.

### 3.1 `alt` (Alternative) — equivalente a `if/else`

```
┌─ alt [pagamento aprovado] ──────────────────┐
│  OrderService -> Client : confirmação       │
├─ else [pagamento recusado] ─────────────────┤
│  OrderService -> Client : erro              │
└─────────────────────────────────────────────┘
```

```java
if (pagamentoAprovado) {
    notificarConfirmacao();
} else {
    notificarErro();
}
```

### 3.2 `opt` (Optional) — equivalente a `if` sem `else`

```
┌─ opt [cupom informado] ─────────────────────┐
│  OrderService -> DiscountService : applyCoupon()
└──────────────────────────────────────────────┘
```

```java
if (cupomInformado) {
    discountService.applyCoupon(order, coupon);
}
```

### 3.3 `loop` — equivalente a um laço de repetição

```
┌─ loop [para cada item do pedido] ───────────┐
│  OrderService -> StockService : reserveStock(item)
└──────────────────────────────────────────────┘
```

```java
for (OrderItem item : order.getItems()) {
    stockService.reserveStock(item);
}
```

### 3.4 `par` (Parallel) — execuções em paralelo

```
┌─ par ────────────────────────────────────────┐
│  OrderService -> EmailService : sendEmail()  │
│  ┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄   │
│  OrderService -> SmsService : sendSms()      │
└──────────────────────────────────────────────┘
```

Indica que as duas mensagens acontecem **simultaneamente**, sem uma depender de esperar a outra terminar — típico de chamadas assíncronas independentes.

### 3.5 `break` — interrompe o fluxo dentro do fragmento

```
┌─ break [estoque insuficiente] ──────────────┐
│  OrderService -> Client : erro "sem estoque"│
└─────────────────────────────────────────────┘
```

Sinaliza que, se a condição for verdadeira, o restante do fluxo **não continua** — parecido com um `return` antecipado ou `throw` (relembrando nossa nota sobre exceções: um `break` no diagrama de sequência é o equivalente visual de um `throw` interrompendo o fluxo normal).

### 3.6 Resumo dos fragmentos

| Fragmento | Equivalente em código |
|---|---|
| `alt` | `if / else if / else` |
| `opt` | `if` (sem alternativa) |
| `loop` | `for` / `while` |
| `par` | Execução concorrente/paralela |
| `break` | Interrupção antecipada do fluxo (exceção, `return`) |
| `critical` | Região crítica (só uma thread por vez — pouco usado no dia a dia) |

---

## 4. Exemplo completo: fluxo de autenticação com JWT

Um exemplo bem próximo do que você já trabalhou no NeoQuest (login com JWT), combinando vários elementos vistos acima:

```
Client         AuthController      AuthService        UserRepository
  │                  │                    │                   │
  │ login(user, pass)│                    │                   │
  │─────────────────▶│                    │                   │
  │                 ┌┴┐  authenticate()   │                   │
  │                 │ │──────────────────▶│                   │
  │                 │ │                  ┌┴┐ findByUsername() │
  │                 │ │                  │ │─────────────────▶│
  │                 │ │                  │ │◁─────────────────│
  │                 │ │                  │ │      (user)      │
  │                 │ │                  └┬┘                  │
  │                 │ │        ┌─ alt [senha correta] ───────┐│
  │                 │ │        │  generateToken()            ││
  │                 │ │        ├─ else [senha incorreta] ────┤│
  │                 │ │        │  throw AuthenticationException 
  │                 │ │        └──────────────────────────────┘
  │                 │ │◁──────────────────│                   │
  │                 │ │      (token)      │                   │
  │                 └┬┘                   │                   │
  │◁─────────────────│                    │                   │
  │   200 OK + token │                    │                   │
```

```java
public class AuthController {
    public ResponseEntity<String> login(String user, String pass) {
        String token = authService.authenticate(user, pass);
        return ResponseEntity.ok(token);
    }
}

public class AuthService {
    public String authenticate(String user, String pass) {
        User u = userRepository.findByUsername(user);
        if (u.getPassword().equals(pass)) {
            return generateToken(u); // self-message
        } else {
            throw new AuthenticationException("Senha incorreta");
        }
    }
}
```

---

## 5. Escrevendo diagramas de sequência como código (recomendado no mercado)

Como já comentei, na prática quase ninguém desenha diagramas de sequência à mão em ferramentas tipo draw.io — a lógica condicional e as barras de ativação ficam trabalhosas de ajustar manualmente. O padrão de mercado é escrever a notação **como texto** e deixar a ferramenta renderizar.

### 5.1 PlantUML

```
@startuml
actor Client
participant "AuthController" as AC
participant "AuthService" as AS
participant "UserRepository" as UR

Client -> AC: login(user, pass)
activate AC
AC -> AS: authenticate(user, pass)
activate AS
AS -> UR: findByUsername(user)
activate UR
UR --> AS: user
deactivate UR

alt senha correta
    AS -> AS: generateToken(user)
else senha incorreta
    AS --> AC: throw AuthenticationException
end

AS --> AC: token
deactivate AS
AC --> Client: 200 OK + token
deactivate AC
@enduml
```

Cole esse código em [plantuml.com/plantuml](https://www.plantuml.com/plantuml) para ver renderizado — é a forma mais rápida de praticar a notação.

### 5.2 Mermaid (renderiza direto no GitHub/Notion)

```
sequenceDiagram
    actor Client
    participant AC as AuthController
    participant AS as AuthService
    participant UR as UserRepository

    Client->>AC: login(user, pass)
    activate AC
    AC->>AS: authenticate(user, pass)
    activate AS
    AS->>UR: findByUsername(user)
    activate UR
    UR-->>AS: user
    deactivate UR

    alt senha correta
        AS->>AS: generateToken(user)
    else senha incorreta
        AS-->>AC: throw AuthenticationException
    end

    AS-->>AC: token
    deactivate AS
    AC-->>Client: 200 OK + token
    deactivate AC
```

> 💡 Vantagem prática do Mermaid: colar esse bloco dentro de um arquivo `.md` no GitHub já renderiza o diagrama automaticamente na visualização do repositório — sem precisar de imagem externa. Ótimo para README de projetos como o seu NeoQuest.

---

## 6. Diferença entre mensagem síncrona e assíncrona (armadilha comum)

Vale reforçar essa diferença, porque é onde mais gente erra a notação:

- **Síncrona** (`───▶`, seta preenchida): o chamador **fica bloqueado esperando** — é o equivalente a uma chamada de método comum em Java, tipo `authService.authenticate(...)`. A barra de ativação do chamador continua "aberta" até a resposta chegar.
- **Assíncrona** (`╌╌╌▷`, seta aberta): o chamador **dispara e continua** sem esperar — típico de publicar uma mensagem numa fila (RabbitMQ, Kafka), ou um `@Async` no Spring.

```
OrderService              MessageQueue
     │                           │
     │  publish(event) ╌╌╌╌╌╌╌╌╌▷│   ← assíncrona: não bloqueia
     │  (continua imediatamente) │
```

```java
@Async
public void notificarPedidoCriado(Order order) {
    messageQueue.publish(new OrderCreatedEvent(order)); // não bloqueia o chamador
}
```

---

## 7. Erros comuns ao modelar

1. **Confundir diagrama de sequência com fluxograma.** Fluxograma modela um **algoritmo** isolado; diagrama de sequência modela a **troca de mensagens entre objetos/sistemas diferentes**. Se você só tem um participante, provavelmente não precisa de diagrama de sequência — um pseudocódigo resolve melhor.
2. **Modelar fluxos triviais.** Como já comentamos, CRUDs simples não merecem esse esforço — reserve para fluxos com decisão, múltiplos serviços, ou operações assíncronas.
3. **Esquecer as barras de ativação.** Sem elas, fica difícil visualizar quem está "com a bola" em cada momento — principalmente em fluxos com múltiplas chamadas aninhadas.
4. **Misturar nível de abstração.** Não coloque, no mesmo diagrama, uma chamada de alto nível (`processOrder()`) ao lado de detalhes de implementação interna (`String.split(",")`) — mantenha o diagrama no mesmo nível de granularidade do início ao fim.
5. **Diagramas gigantes demais.** Se o fluxo tem mais de ~10-12 mensagens ou mais de 5-6 participantes, considere quebrar em diagramas menores, cada um cobrindo uma parte do processo.

---

## 8. Comparando com o diagrama de classes

| | Diagrama de classes | Diagrama de sequência |
|---|---|---|
| Tipo | Estrutural | Comportamental |
| Responde | "O que existe e como se relaciona" | "O que acontece, em que ordem" |
| Elemento central | Classe (atributos + métodos) | Lifeline (participante) + mensagens |
| Quando desenhar | Modelagem de domínio, antes de codificar | Documentar um fluxo específico e complexo |
| Ferramenta comum no mercado | draw.io, Lucidchart, dbdiagram.io | PlantUML, Mermaid (como código) |

Os dois se complementam: o diagrama de classes te diz **quais objetos existem** (`OrderService`, `PaymentGateway`); o diagrama de sequência te diz **como eles conversam** para realizar uma tarefa específica (`processPayment`).
