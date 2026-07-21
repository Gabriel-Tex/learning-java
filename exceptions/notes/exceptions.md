# Tratamento de Exceções em Java

## 1. O que é uma exceção

Uma **exceção** é qualquer condição de erro ou comportamento inesperado que ocorre durante a execução de um programa e que interrompe o fluxo normal de instruções. Em Java, exceções são **objetos** — instâncias de classes que descrevem o tipo de erro ocorrido, contendo informações como mensagem, causa e a pilha de chamadas (*stack trace*) no momento da falha.

O tratamento de exceções existe para separar a lógica de "o que fazer quando tudo dá certo" da lógica de "o que fazer quando algo dá errado", evitando que erros sejam ignorados silenciosamente ou que o programa quebre de forma abrupta e sem explicação.

---

## 2. Hierarquia de exceções

Todas as exceções em Java derivam de `java.lang.Throwable`. A partir dela, a hierarquia se divide em dois ramos principais:

```
Throwable
├── Error                     (erros graves da JVM, não tratáveis na prática)
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
└── Exception
    ├── RuntimeException      (unchecked)
    │   ├── NullPointerException
    │   ├── ArithmeticException
    │   ├── ArrayIndexOutOfBoundsException
    │   ├── ClassCastException
    │   ├── IllegalArgumentException
    │   └── ...
    └── (checked exceptions)
        ├── IOException
        ├── SQLException
        ├── ClassNotFoundException
        └── ...
```

### 2.1 `Error`

Representa problemas graves, geralmente relacionados à própria JVM (falta de memória, estouro de pilha etc.), que **não devem ser tratados** pela aplicação, pois normalmente indicam um estado do qual o programa não pode se recuperar.

### 2.2 `Exception` — Checked Exceptions

Subclasses diretas de `Exception` (que não herdam de `RuntimeException`) são chamadas **checked exceptions**. O compilador **obriga** o desenvolvedor a tratá-las (com `try-catch`) ou declará-las como propagáveis (com `throws`) na assinatura do método.

São usadas para condições **previsíveis e recuperáveis**, geralmente ligadas a fatores externos ao programa: leitura de arquivo inexistente, falha de conexão com banco de dados, timeout de rede etc.

```java
public void lerArquivo(String caminho) throws IOException {
    FileReader arquivo = new FileReader(caminho); // pode lançar IOException
}
```

### 2.3 `RuntimeException` — Unchecked Exceptions

Subclasses de `RuntimeException` são **unchecked exceptions**. O compilador **não obriga** tratamento nem declaração via `throws`. Geralmente representam **erros de programação** (bugs) — situações que, em teoria, poderiam ter sido evitadas com validações adequadas no código.

```java
int[] vetor = new int[5];
System.out.println(vetor[10]); // ArrayIndexOutOfBoundsException (unchecked)
```

> **Regra prática:** checked = "algo externo pode falhar, o chamador precisa saber disso e decidir o que fazer"; unchecked = "isso é um bug, conserte o código".

---

## 3. Propagação (Stack Unwinding)

Quando uma exceção é lançada (`throw`), a JVM interrompe o fluxo normal e passa a procurar, subindo pela **pilha de chamadas** (call stack), um bloco `catch` compatível com o tipo da exceção lançada. Esse processo é chamado de **stack unwinding**.

- Se um `catch` compatível é encontrado em algum método da pilha, a exceção é capturada ali e a execução continua a partir do bloco `catch`.
- Se a exceção percorre toda a pilha sem ser capturada, ela chega até a *thread* principal, que imprime o *stack trace* no console (`System.err`) e o programa é encerrado (ou a thread correspondente é finalizada).

```
main() chama metodoA()
  metodoA() chama metodoB()
    metodoB() lança exceção
  metodoA() não trata → propaga
main() trata (catch) ou também propaga → programa encerra se ninguém tratar
```

---

## 4. Estrutura `try-catch-finally`

### 4.1 Sintaxe geral

```java
try {
    // código que pode lançar exceção
} catch (TipoDeExcecaoA e) {
    // tratamento específico para TipoDeExcecaoA
} catch (TipoDeExcecaoB e) {
    // tratamento específico para TipoDeExcecaoB
} finally {
    // executado sempre, com ou sem exceção
}
```

- O bloco `try` deve conter apenas o código que pode falhar — não deve ser usado como um bloco genérico envolvendo toda a lógica do método.
- Múltiplos blocos `catch` são avaliados **em ordem**, de cima para baixo. O primeiro `catch` cujo tipo seja compatível com a exceção lançada é executado.
- **Upcasting é permitido**: um `catch` pode capturar uma superclasse da exceção lançada (ex: `catch (Exception e)` captura qualquer subtipo de `Exception`).

> ⚠️ **Ordem importa**: subclasses devem ser capturadas **antes** das superclasses. Colocar `catch (Exception e)` antes de `catch (IOException e)` gera erro de compilação (*"exception has already been caught"*), pois o segundo `catch` se tornaria inatingível.

```java
try {
    metodoQuePodeFalhar();
} catch (FileNotFoundException e) {   // mais específico primeiro
    System.out.println("Arquivo não encontrado");
} catch (IOException e) {             // mais genérico depois
    System.out.println("Erro de I/O");
} catch (Exception e) {               // fallback geral por último
    System.out.println("Erro inesperado");
}
```

### 4.2 Multi-catch (Java 7+)

Quando o tratamento é idêntico para vários tipos de exceção, é possível uni-los em uma única cláusula `catch` usando `|`:

```java
try {
    metodoQuePodeFalhar();
} catch (IOException | SQLException e) {
    log.error("Falha de I/O ou banco de dados", e);
}
```

Restrição: os tipos combinados não podem ter relação de herança direta entre si (não pode combinar uma classe e sua superclasse).

### 4.3 Bloco `finally`

Executa **sempre**, independentemente de a exceção ter ocorrido, ter sido capturada, ou até mesmo se houver um `return` dentro do `try` ou do `catch`. As únicas exceções a essa regra são:
- Chamada explícita a `System.exit()`.
- Encerramento anormal da JVM (queda de energia, `kill -9` etc.).

Uso clássico: liberar recursos (fechar arquivos, conexões de banco, sockets, streams).

```java
FileReader arquivo = null;
try {
    arquivo = new FileReader("dados.txt");
    // processamento
} catch (IOException e) {
    System.out.println("Erro ao ler o arquivo: " + e.getMessage());
} finally {
    if (arquivo != null) {
        try {
            arquivo.close();
        } catch (IOException e) {
            System.out.println("Erro ao fechar o arquivo");
        }
    }
}
```

> ⚠️ Cuidado: um `return` dentro do `finally` **sobrescreve** qualquer `return` ou exceção lançada no `try`/`catch`. Isso é considerado **má prática** e deve ser evitado.

### 4.4 `try-with-resources` (Java 7+) — forma moderna e preferida

Em vez de fechar recursos manualmente no `finally`, qualquer classe que implemente a interface `AutoCloseable` (ou `Closeable`) pode ser declarada dentro dos parênteses do `try`, sendo fechada **automaticamente** ao final do bloco, mesmo em caso de exceção.

```java
try (FileReader arquivo = new FileReader("dados.txt");
     BufferedReader leitor = new BufferedReader(arquivo)) {

    String linha;
    while ((linha = leitor.readLine()) != null) {
        System.out.println(linha);
    }

} catch (IOException e) {
    System.out.println("Erro ao ler o arquivo: " + e.getMessage());
}
// arquivo e leitor são fechados automaticamente aqui, na ordem inversa de abertura
```

Vantagens sobre o `finally` manual:
- Elimina código repetitivo (*boilerplate*) de fechamento.
- Evita esquecimentos de fechar recursos.
- Fecha múltiplos recursos na ordem correta (inverso da declaração).
- Preserva corretamente exceções "suprimidas" (`getSuppressed()`), caso tanto o corpo do `try` quanto o `close()` lancem exceções.

Esta é a abordagem **recomendada atualmente** sempre que se trabalha com recursos (arquivos, conexões JDBC, sockets, streams etc.).

---

## 5. `throw` vs `throws`

| | `throw` | `throws` |
|---|---|---|
| Uso | Dentro do corpo do método | Na assinatura do método |
| Função | **Lança** (instância) uma exceção no momento em que ocorre um erro | **Declara** que o método pode propagar determinado(s) tipo(s) de exceção, delegando o tratamento ao chamador |
| Quantidade | Uma exceção por vez (um objeto) | Pode listar vários tipos, separados por vírgula |

```java
public void validarIdade(int idade) throws IllegalArgumentException {
    if (idade < 0) {
        throw new IllegalArgumentException("Idade não pode ser negativa");
    }
}

public void lerArquivo(String caminho) throws IOException, FileNotFoundException {
    // ...
}
```

---

## 6. Criando exceções personalizadas

Em sistemas reais, é comum criar exceções específicas do domínio da aplicação, ao invés de usar apenas exceções genéricas do Java. Isso torna o código mais expressivo e permite tratamento diferenciado por tipo de erro de negócio.

```java
// Checked: obriga o chamador a tratar
public class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}

// Unchecked: representa erro de programação/validação
public class UsuarioInvalidoException extends RuntimeException {
    public UsuarioInvalidoException(String mensagem) {
        super(mensagem);
    }
}
```

Uso:

```java
public void sacar(double valor) throws SaldoInsuficienteException {
    if (valor > saldo) {
        throw new SaldoInsuficienteException(
            "Saldo insuficiente: saldo atual R$" + saldo + ", valor solicitado R$" + valor
        );
    }
    saldo -= valor;
}
```

**Convenção de nomenclatura**: exceções personalizadas devem terminar com o sufixo `Exception` (ex: `SaldoInsuficienteException`, `PedidoNaoEncontradoException`).

Boa prática: sempre fornecer construtores que aceitem `String message`, `Throwable cause`, e ambos combinados — espelhando os construtores de `Exception`:

```java
public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(String message) {
        super(message);
    }
    public PedidoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

---

## 7. Encadeamento de exceções (Exception Chaining)

Ao capturar uma exceção de baixo nível e relançar uma exceção mais significativa para o contexto da aplicação, é importante **preservar a causa original**, para não perder informação de depuração. Isso é feito via o parâmetro `cause`.

```java
try {
    conectarBancoDeDados();
} catch (SQLException e) {
    throw new RuntimeException("Falha ao processar pedido do cliente", e); // 'e' é a causa original
}
```

Ao imprimir o *stack trace* dessa nova exceção, a causa original aparece encadeada, na seção `Caused by:`, preservando o contexto completo do erro.

---

## 8. Métodos úteis da classe `Throwable`/`Exception`

| Método | Descrição |
|---|---|
| `getMessage()` | Retorna a mensagem descritiva associada à exceção |
| `getCause()` | Retorna a exceção que causou esta (encadeamento), ou `null` |
| `printStackTrace()` | Imprime a pilha de chamadas no console (`System.err`) |
| `getStackTrace()` | Retorna a pilha como um array de `StackTraceElement`, permitindo análise programática |
| `toString()` | Retorna nome da classe + mensagem (ex: `java.lang.NullPointerException: ...`) |
| `getSuppressed()` | Retorna exceções suprimidas (relevante em `try-with-resources`) |

---

## 9. Boas práticas

1. **Nunca "engolir" exceções silenciosamente.**
   ```java
   // Ruim
   try {
       processar();
   } catch (Exception e) {}  // erro desaparece sem deixar rastro

   // Melhor
   try {
       processar();
   } catch (Exception e) {
       log.error("Falha ao processar", e);
   }
   ```

2. **Evite capturar `Exception` ou `Throwable` genericamente**, a menos que seja em uma camada muito alta da aplicação (ex: um *handler* global). Capturar exceções específicas torna o tratamento mais preciso e evita mascarar bugs.

3. **Não use exceções para controle de fluxo normal** (ex: usar exceção para "sair de um loop" em vez de uma condição). Lançar e capturar exceções tem custo de performance (principalmente por causa da construção do *stack trace*) e prejudica a legibilidade.

4. **Prefira `try-with-resources`** a fechar recursos manualmente em `finally`.

5. **Mantenha o bloco `try` o menor possível**, contendo apenas as instruções que realmente podem lançar a exceção tratada — isso facilita saber exatamente qual código pode falhar.

6. **Prefira exceções específicas a genéricas.** Lançar `new Exception("erro")` dificulta o tratamento diferenciado pelo chamador. Prefira tipos como `IllegalArgumentException`, `IllegalStateException` ou exceções personalizadas.

7. **Sempre preserve a causa original ao encadear exceções** (`new MinhaException(msg, causaOriginal)`), nunca descarte a exceção original silenciosamente.

8. **Documente exceções lançadas com Javadoc `@throws`:**
   ```java
   /**
    * Realiza o saque de um valor da conta.
    *
    * @param valor valor a ser sacado
    * @throws SaldoInsuficienteException se o valor exceder o saldo disponível
    */
   public void sacar(double valor) throws SaldoInsuficienteException { ... }
   ```

9. **Evite lançar `RuntimeException` genérica.** Prefira subtipos mais específicos (`IllegalArgumentException`, `IllegalStateException`, ou exceções de domínio próprias) para que o chamador possa diferenciar o problema.

10. **Cuidado com exceções em construtores e blocos `static`** — podem deixar o objeto ou a classe em estado inconsistente se mal tratadas.

11. **Use `finally` (ou `try-with-resources`) apenas para liberação de recursos** — não para lógica de negócio, já que sua execução independe do sucesso do `try`.

12. **Em APIs/camadas de serviço, considere um tratamento centralizado** (ex: `@ControllerAdvice` no Spring) em vez de espalhar `try-catch` por todos os *endpoints* — isso concentra a política de erro em um único lugar.

---

## 10. Exemplo completo e integrado

```java
public class ContaBancaria {

    private double saldo;

    public ContaBancaria(double saldoInicial) {
        this.saldo = saldoInicial;
    }

    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de saque deve ser positivo");
        }
        if (valor > saldo) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficiente: disponível R$%.2f, solicitado R$%.2f", saldo, valor)
            );
        }
        saldo -= valor;
    }

    public static void main(String[] args) {
        ContaBancaria conta = new ContaBancaria(100.0);

        try {
            conta.sacar(50.0);
            conta.sacar(200.0); // deve lançar SaldoInsuficienteException
        } catch (IllegalArgumentException e) {
            System.out.println("Entrada inválida: " + e.getMessage());
        } catch (SaldoInsuficienteException e) {
            System.out.println("Operação não realizada: " + e.getMessage());
        } finally {
            System.out.println("Processamento de saque finalizado.");
        }
    }
}

class SaldoInsuficienteException extends Exception {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem);
    }
}
```

---

## 11. Resumo mental (cheat sheet)

- `Throwable` → raiz de tudo; `Error` (não tratável) e `Exception` (tratável).
- **Checked** (`Exception` "pura") → compilador obriga tratar/propagar → erros externos recuperáveis.
- **Unchecked** (`RuntimeException`) → compilador não obriga → geralmente bugs de programação.
- `try` → código arriscado; `catch` → tratamento (do mais específico ao mais genérico); `finally` → sempre executa.
- `try-with-resources` → forma moderna de fechar recursos automaticamente (`AutoCloseable`).
- `throw` → lança uma instância; `throws` → declara na assinatura.
- Exceções personalizadas → sufixo `Exception`, herdam de `Exception` (checked) ou `RuntimeException` (unchecked).
- Sempre preserve a `cause` ao encadear exceções.
- Nunca capture e ignore silenciosamente.