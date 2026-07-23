# Exceções em Java

## 1. Fundamentos: `Throwable`, `Error` e `Exception`

Toda condição excepcional em Java deriva de `java.lang.Throwable`, que se divide em duas ramificações principais:

```
Throwable
├── Error → problemas graves, geralmente irrecuperáveis (JVM, memória, etc.)
└── Exception
    ├── RuntimeException → exceções NÃO verificadas (unchecked)
    └── (demais subclasses) → exceções verificadas (checked)
```

- **`Error`** — representa falhas sérias do ambiente de execução (ex: `OutOfMemoryError`, `StackOverflowError`). Não são feitas para serem capturadas ou tratadas pela aplicação; geralmente indicam que algo está profundamente errado (falta de memória, recursão infinita).
- **`Exception`** — representa condições que a aplicação pode querer capturar e tratar. Se divide em:
  - **Checked (verificadas)** — o compilador **obriga** a tratar (`try-catch`) ou declarar (`throws`). Representam falhas *previsíveis e recuperáveis*, geralmente ligadas a recursos externos (arquivo, rede, banco). Ex: `IOException`, `SQLException`.
  - **Unchecked (não verificadas)** — subclasses de `RuntimeException`. O compilador **não exige** tratamento. Representam, em geral, **erros de programação** (uso incorreto de uma API, lógica falha) que deveriam ser corrigidos no código, não "tratados" em produção. Ex: `NullPointerException`, `ArrayIndexOutOfBoundsException`.

> 💡 Regra prática: se o erro pode acontecer mesmo com código correto (ex: arquivo apagado por outro processo, rede caiu), é checked. Se o erro só acontece por bug do programador (índice inválido, cast errado), é unchecked.

Referência: [`Throwable`](https://docs.oracle.com/javase/10/docs/api/java/lang/Throwable.html)

---

## 2. Exceções unchecked mais comuns (`RuntimeException`)

### 2.1 `NullPointerException` (NPE)

**Causa:** tentar acessar um método, campo ou índice de uma referência que é `null`.

```java
String texto = null;
System.out.println(texto.length()); // NPE — texto é null
```

**Como tratar/evitar:**
- Verificar `null` antes de usar, ou usar `Optional<T>` para representar ausência de valor de forma explícita.
- Usar `Objects.requireNonNull(obj, "mensagem")` para falhar cedo e com mensagem clara.
- Desde o Java 14+, as mensagens de NPE (com `-XX:+ShowCodeDetailsInExceptionMessages`, padrão desde Java 15) já indicam **qual variável** era nula, facilitando o debug.

```java
Objects.requireNonNull(usuario, "usuário não pode ser nulo");
if (texto != null) {
    System.out.println(texto.length());
}
```

### 2.2 `ArrayIndexOutOfBoundsException`

**Causa:** acessar uma posição de array fora do intervalo válido (`0` a `length - 1`).

```java
int[] numeros = new int[5];
System.out.println(numeros[5]); // índice válido vai de 0 a 4
```

**Como tratar:** validar o índice antes de acessar (`if (i >= 0 && i < array.length)`), ou usar estruturas como `List` com métodos que falham de forma mais previsível. Não é recomendado usar `try-catch` para "esconder" esse erro — corrija a lógica do índice.

### 2.3 `StringIndexOutOfBoundsException`

**Causa:** análoga à anterior, mas em operações de `String` (`charAt()`, `substring()`) com índice inválido.

```java
String s = "abc";
s.charAt(10); // só existem índices 0, 1, 2
```

### 2.4 `ClassCastException`

**Causa:** tentar converter (*cast*) um objeto para um tipo incompatível em tempo de execução.

```java
Object obj = "texto";
Integer numero = (Integer) obj; // ClassCastException
```

**Como tratar/evitar:** usar `instanceof` (ou *pattern matching for instanceof*, Java 16+) antes de fazer o cast:

```java
if (obj instanceof Integer numero) {
    System.out.println(numero + 1);
}
```

### 2.5 `NumberFormatException`

**Causa:** subclasse de `IllegalArgumentException`; ocorre ao tentar converter uma `String` malformada para número (`Integer.parseInt`, `Double.parseDouble` etc.).

```java
int valor = Integer.parseInt("abc"); // não é um número válido
```

**Como tratar:** capturar especificamente e informar o usuário, especialmente em entradas vindas de fora (formulário, arquivo, `Scanner`):

```java
try {
    int valor = Integer.parseInt(entrada);
} catch (NumberFormatException e) {
    System.out.println("Entrada inválida, esperado um número: " + entrada);
}
```

### 2.6 `ArithmeticException`

**Causa:** operação aritmética inválida — o caso clássico é divisão de inteiros por zero (`int / 0`). **Não** ocorre com `double` (que produz `Infinity` ou `NaN` em vez de lançar exceção).

```java
int resultado = 10 / 0; // ArithmeticException: / by zero
double resultadoDouble = 10.0 / 0; // Infinity, NÃO lança exceção
```

**Como tratar:** validar o divisor antes da operação quando ele vier de entrada externa.

### 2.7 `IllegalArgumentException` / `IllegalStateException`

**Causa:** lançadas deliberadamente por métodos de bibliotecas (ou pelo seu próprio código) quando um argumento é inválido para aquele contexto, ou quando um objeto está em um estado que não permite a operação solicitada.

```java
public void setIdade(int idade) {
    if (idade < 0) {
        throw new IllegalArgumentException("Idade não pode ser negativa: " + idade);
    }
    this.idade = idade;
}
```

**Como tratar:** normalmente cabe a quem *chama* o método tratar, ou corrigir a chamada. São ótimas para você lançar nas suas próprias validações — comunicam a intenção com muito mais clareza do que deixar um NPE "estourar" mais adiante.

### 2.8 `ConcurrentModificationException`

**Causa:** modificar uma coleção (`add`/`remove`) enquanto ela está sendo percorrida por um `for-each` ou `Iterator`, fora do próprio iterador.

```java
List<String> lista = new ArrayList<>(List.of("a", "b", "c"));
for (String item : lista) {
    if (item.equals("b")) {
        lista.remove(item); // ConcurrentModificationException
    }
}
```

**Como tratar/evitar:** usar `Iterator.remove()`, ou `removeIf()`, ou percorrer uma cópia da coleção:

```java
lista.removeIf(item -> item.equals("b")); // forma correta e mais idiomática
```

### 2.9 `UnsupportedOperationException`

**Causa:** tentar modificar uma coleção **imutável** (ex: retornada por `List.of()`, `Collections.unmodifiableList()`, ou o resultado de `.stream().toList()` no Java 16+).

```java
List<String> lista = List.of("a", "b");
lista.add("c"); // UnsupportedOperationException
```

**Como tratar:** se precisar modificar, crie uma cópia mutável: `new ArrayList<>(lista)`.

---

## 3. Exceções checked mais comuns

### 3.1 `IOException` (e subclasses)

**Causa:** erro genérico de entrada/saída — falha ao ler, escrever, abrir ou fechar um recurso externo (arquivo, socket, stream). É a superclasse de várias exceções mais específicas:
- `FileNotFoundException` — arquivo não encontrado ao tentar abri-lo para leitura (ou já existe/está protegido, ao abrir para escrita, dependendo da API).
- `EOFException` — fim de arquivo/stream alcançado inesperadamente durante uma leitura estruturada.
- `NoSuchFileException` / `FileAlreadyExistsException` — específicas da API `java.nio.file` (mais informativas que as equivalentes de `java.io`).

```java
try (BufferedReader br = new BufferedReader(new FileReader("dados.txt"))) {
    String linha = br.readLine();
} catch (FileNotFoundException e) {
    System.out.println("Arquivo não existe: " + e.getMessage());
} catch (IOException e) {
    System.out.println("Erro de E/S: " + e.getMessage());
}
```

**Como tratar:** sempre com `try-with-resources` (ver nota anterior sobre manipulação de arquivos) e captura específica antes da genérica — `FileNotFoundException` deve vir **antes** de `IOException` no `catch`, já que é subclasse dela e o compilador exige a ordem do mais específico para o mais geral.

### 3.2 `SQLException`

**Causa:** qualquer erro ao interagir com um banco de dados via JDBC — conexão perdida, SQL inválido, violação de constraint, timeout etc.

```java
try (Connection conn = DriverManager.getConnection(url, user, senha);
     Statement stmt = conn.createStatement()) {
    stmt.executeUpdate("INSERT INTO usuario (nome) VALUES ('Gabriel')");
} catch (SQLException e) {
    System.out.println("Erro no banco: " + e.getMessage() + " (código: " + e.getErrorCode() + ")");
}
```

**Como tratar:** capturar e logar o `getErrorCode()`/`getSQLState()` para diagnóstico; em aplicações com transações, usar o `catch` para fazer `rollback()`.

### 3.3 `ParseException`

**Causa:** erro ao converter texto para um formato estruturado — o exemplo clássico é `SimpleDateFormat.parse()` recebendo uma data em formato inesperado.

```java
SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
try {
    Date data = sdf.parse("31-12-2025"); // formato errado, esperava dd/MM/yyyy
} catch (ParseException e) {
    System.out.println("Data inválida: " + e.getMessage());
}
```

> 💡 Nota: nas APIs modernas de data (`java.time`, Java 8+), `LocalDate.parse()` lança `DateTimeParseException`, que é **unchecked** — outro bom exemplo de como o design mudou ao longo do tempo, preferindo unchecked para erros de parsing.

### 3.4 `InterruptedException`

**Causa:** lançada quando uma thread que está bloqueada (em `Thread.sleep()`, `wait()`, `join()` etc.) é interrompida por outra thread.

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // restaura o status de interrupção!
    System.out.println("Thread interrompida");
}
```

**Como tratar:** regra de ouro — **nunca engula silenciosamente**. Se não for relançar, chame `Thread.currentThread().interrupt()` para não "perder" o sinal de interrupção para o resto do sistema.

### 3.5 `CloneNotSupportedException`

**Causa:** chamar `clone()` em uma classe que não implementa a interface marcadora `Cloneable`. Pouco usada hoje em dia (prefira construtores de cópia ou padrões como *Builder*), mas ainda aparece em código legado e provas.
