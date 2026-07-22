# Switch Expressions (Java 14+)

## O que é

Quando você precisa escolher entre **mais de dois valores**, pode usar uma **expressão `switch`** (introduzida no Java 14). Ela se parece com isto:

```java
int diaDaSemana = 3;

String nome = switch (diaDaSemana) {
    case 1 -> "Segunda";
    case 2 -> "Terça";
    case 3 -> "Quarta";
    default -> "Dia inválido";
};
```

- A expressão que vem depois de `switch` é chamada de **selector expression**, e seu valor é o **selector** (no exemplo acima, `diaDaSemana`).
- Por enquanto, selector e labels só podem ser:
  - Tipos integrais (`byte`, `short`, `char`, `int`)
  - `String`
  - Constantes de um **tipo enumerado** (`enum`)
- No Capítulo 5, mostra-se como usar `switch` com outros tipos para **pattern matching**.

> 💡 **Nota:** assim como toda expressão, a expressão `switch` **tem um valor** — repare na **seta (`->`)** que precede o valor em cada ramo.

> 💡 **Nota:** a partir do Java 14, existem **quatro formas diferentes** de `switch`. Esta seção foca na mais útil (a expressão com `->`). A discussão completa de todas as formas (statements e expressions) fica em outra seção do livro.

---

## Labels (rótulos)

Um **label** precisa ser uma constante em tempo de compilação cujo tipo combine com o do selector. É possível ter **múltiplos labels** no mesmo `case`, separados por vírgula:

```java
char nota = 'B';

int pontos = switch (nota) {
    case 'A' -> 10;
    case 'B', 'C' -> 7;
    case 'D', 'F' -> 3;
    default -> 0;
};
// pontos = 7
```

Também funciona com `String`:

```java
String comando = "iniciar";

String acao = switch (comando) {
    case "iniciar" -> "Sistema iniciado";
    case "parar" -> "Sistema parado";
    default -> "Comando desconhecido";
};
```

---

## Uso com `enum`

Quando você usa a expressão `switch` com constantes de um `enum`, **não precisa escrever o nome do enum** em cada label — ele é deduzido a partir do valor:

```java
enum Tamanho { PEQUENO, MEDIO, GRANDE }

Tamanho t = Tamanho.MEDIO;

String descricao = switch (t) {
    case PEQUENO -> "Serve para 1 pessoa";
    case MEDIO   -> "Serve para 2 pessoas";
    case GRANDE  -> "Serve para 4 pessoas";
};
// descricao = "Serve para 2 pessoas"
```

Neste exemplo, foi legal **omitir o `default`**, já que havia um `case` para **cada valor possível** do enum.

---

## Cuidado: `default` obrigatório com tipos "abertos"

Quando o selector é um `int` (ou outro tipo integral) e você **não** cobre todos os valores possíveis, é obrigatório ter um `default`:

```java
int codigo = 99;

String status = switch (codigo) {
    case 200 -> "OK";
    case 404 -> "Não encontrado";
    case 500 -> "Erro interno";
    default -> "Código desconhecido"; // obrigatório!
};
```

Uma expressão `switch` com selector inteiro **sempre precisa de um `default`** — diferente do caso do `enum`, aqui é impossível cobrir todos os valores possíveis do tipo.

---

## Cuidado: selector `null` (Java 21)

Se o selector for `null`, uma **`NullPointerException`** é lançada. Para evitar isso, é possível adicionar um `case null` explícito:

```java
String valor = null;

String resultado = switch (valor) {
    case null -> "Valor era nulo";
    case "sim" -> "Confirmado";
    default -> "Valor desconhecido";
};
// resultado = "Valor era nulo"
```

Esse recurso é do **Java 21**. Importante: `case null` **não é igual** a `default` — o `default` sozinho não captura o caso `null`.

