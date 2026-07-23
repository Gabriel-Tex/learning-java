# Manipulação de Arquivos em Java

## 1. O que é um arquivo, do ponto de vista da linguagem

Um **arquivo** é uma abstração que uniformiza a interação entre o ambiente de execução e os dispositivos externos (disco, rede, etc.), permitindo que o programa trabalhe com dados persistentes sem precisar conhecer os detalhes de baixo nível do sistema operacional ou do hardware.

Toda interação de um programa com um dispositivo por meio de arquivos passa, conceitualmente, por três etapas:

1. **Abertura ou criação** do arquivo
2. **Transferência de dados** (leitura e/ou escrita)
3. **Fechamento** do arquivo (liberação do recurso)

Java oferece duas grandes APIs para isso:

- **java.io** — API clássica (desde o Java 1.0), baseada em `File`, `FileReader`, `FileWriter`, `Scanner` etc. Trabalha por *streams* de leitura/escrita.
- **java.nio.file** — API moderna (**NIO.2**, introduzida no Java 7), baseada em `Path`, `Paths` e na classe utilitária `Files`. Mais flexível, com melhor tratamento de erros, suporte a atributos de arquivo, symlinks, e operações atômicas.

Este material cobre ambas, já que a API clássica ainda é amplamente usada e ensinada, mas a moderna é a recomendada para código novo.

---

## 2. A classe `File` (java.io)

`File` é uma **representação abstrata** de um caminho de arquivo ou diretório no sistema de arquivos. É importante entender que:

- `File` **não lê nem escreve** dados — ela apenas representa o **caminho** e oferece metadados/operações sobre ele.
- A existência de um objeto `File` **não implica** que o arquivo ou diretório correspondente exista de fato no disco.
- Contém métodos utilitários como: testar existência (`exists()`), verificar se é arquivo ou diretório (`isFile()`, `isDirectory()`), obter permissões, apagar (`delete()`), criar diretórios (`mkdir()`, `mkdirs()`), listar conteúdo de diretórios (`listFiles()`), entre outros.

Referência: [`java.io.File`](https://docs.oracle.com/javase/10/docs/api/java/io/File.html)

```java
File file = new File("C:\\temp\\in.txt");

System.out.println(file.exists());       // o arquivo existe no disco?
System.out.println(file.isFile());       // é um arquivo (não diretório)?
System.out.println(file.isDirectory());  // é um diretório?
System.out.println(file.length());       // tamanho em bytes
System.out.println(file.getAbsolutePath()); // caminho absoluto
System.out.println(file.canRead());      // tem permissão de leitura?
System.out.println(file.canWrite());     // tem permissão de escrita?
```

> 💡 **Dica de portabilidade**: evite caminhos "hardcoded" com barra invertida (`\\`), que só funcionam no Windows. Prefira `File.separator` ou, melhor ainda, a API `Path`/`Paths` (ver seção 8), que abstrai isso automaticamente.

---

## 3. Leitura de texto com `File` + `Scanner`

O `Scanner` é o real responsável por **abrir e ler** o arquivo; ele usa o objeto `File` apenas para localizar o arquivo no disco.

Referências: [`Scanner`](https://docs.oracle.com/javase/10/docs/api/java/util/Scanner.html) · [`IOException`](https://docs.oracle.com/javase/10/docs/api/java/io/IOException.html)

```java
package application;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) {

        File file = new File("C:\\temp\\in.txt");
        Scanner sc = null;

        try {
            sc = new Scanner(file); // Scanner abre e lê o arquivo referenciado por file
            while (sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
        }
        catch (IOException e) { // ex: arquivo não encontrado
            System.out.println("Error: " + e.getMessage());
        }
        finally {
            if (sc != null) {
                sc.close(); // libera o recurso
            }
        }
    }
}
```

> Note que `Scanner(File)` na verdade lança `FileNotFoundException`, que é uma subclasse de `IOException` — por isso capturar `IOException` também funciona aqui.

---

## 4. `FileReader` e `BufferedReader`

- **`FileReader`** — leitor de caracteres que se conecta diretamente ao arquivo. É a "ponte" inicial que dá acesso aos dados brutos, caractere por caractere. Referência: [`FileReader`](https://docs.oracle.com/javase/10/docs/api/java/io/FileReader.html)
- **`BufferedReader`** — um **decorador** (padrão *Decorator*) que adiciona um *buffer* de memória ao leitor subjacente. Em vez de ler caractere por caractere, lê blocos maiores de uma vez, reduzindo drasticamente o número de acessos físicos ao disco — o que o torna muito mais eficiente para arquivos grandes. Também oferece o método conveniente `readLine()`. Referência: [`BufferedReader`](https://docs.oracle.com/javase/10/docs/api/java/io/BufferedReader.html)

Mais sobre a diferença entre os dois: [Stack Overflow — BufferedReader vs FileReader](https://stackoverflow.com/questions/9648811/specific-difference-betweenbufferedreader-and-filereader)

### 4.1 Forma manual (fechamento explícito em `finally`)

```java
package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Program {

    public static void main(String[] args) {

        String path = "C:\\temp\\in.txt";
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(path);
            br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        finally {
            try {
                if (br != null) br.close();
                if (fr != null) fr.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
```

Esse padrão — fechar recursos manualmente dentro de um `finally` aninhado com seu próprio `try-catch` — era necessário antes do Java 7, mas hoje é considerado verboso e propenso a erro (é fácil esquecer de fechar um dos recursos, ou fechar na ordem errada).

---

## 5. `try-with-resources` — forma moderna e recomendada

Introduzido no Java 7, o `try-with-resources` permite declarar um ou mais recursos **dentro dos parênteses do `try`**. Ao final do bloco (com ou sem exceção), todos os recursos declarados são **fechados automaticamente**, na ordem inversa à de declaração — sem necessidade de `finally` manual.

Qualquer classe que implemente a interface `AutoCloseable` (ou sua especialização `Closeable`) pode ser usada dessa forma — o que inclui `FileReader`, `BufferedReader`, `FileWriter`, `BufferedWriter`, conexões JDBC, sockets etc.

```java
package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Program {

    public static void main(String[] args) {

        String path = "C:\\temp\\in.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

> ⚠️ Correção em relação à versão anterior desta anotação: a classe correta é **`BufferedReader`**, não `BufferReader` — atenção ao digitar, pois o compilador não reconhece `BufferReader`.

### 5.1 Múltiplos recursos no mesmo `try`

É possível declarar mais de um recurso, separando-os por `;`. Eles serão fechados na ordem **inversa** à declaração:

```java
try (FileReader fr = new FileReader(path);
     BufferedReader br = new BufferedReader(fr)) {
    // br é fechado primeiro, depois fr
}
```

### 5.2 Java 9+: reaproveitando variáveis `effectively final`

A partir do Java 9, não é mais necessário declarar a variável dentro do `try` — um recurso já declarado como `effectively final` fora do bloco pode ser referenciado diretamente:

```java
BufferedReader br = new BufferedReader(new FileReader(path));
try (br) { // apenas referencia o recurso já existente
    System.out.println(br.readLine());
}
```

---

## 6. `FileWriter` e `BufferedWriter`

- **`FileWriter`** — escreve caracteres diretamente em um arquivo.
  - Cria (ou recria, apagando o conteúdo anterior) o arquivo: `new FileWriter(path)`
  - Acrescenta ao final do arquivo existente (*append*): `new FileWriter(path, true)`
  - Referência: [`FileWriter`](https://docs.oracle.com/javase/10/docs/api/java/io/FileWriter.html)
- **`BufferedWriter`** — decorador que adiciona *buffer* de escrita, reduzindo o número de gravações físicas no disco, e oferecendo o método `newLine()` para quebra de linha portável. Referência: [`BufferedWriter`](https://docs.oracle.com/javase/10/docs/api/java/io/BufferedWriter.html)

```java
package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Program {

    public static void main(String[] args) {

        String[] lines = new String[] { "Good morning", "Good afternoon", "Good night" };
        String path = "C:\\temp\\out.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) {
                bw.write(line);   // escreve a linha no arquivo
                bw.newLine();     // quebra de linha portável (equivalente a \n)
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

> Para **acrescentar** conteúdo a um arquivo já existente, sem apagar o que já está nele:
> ```java
> try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
>     bw.write("Nova linha adicionada ao final");
>     bw.newLine();
> }
> ```

### 6.1 `PrintWriter` — alternativa mais conveniente para escrita

`PrintWriter` oferece métodos como `println()`, `printf()` e `format()`, tornando a escrita mais parecida com `System.out`:

```java
try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
    pw.println("Good morning");
    pw.printf("Total de itens: %d%n", 10);
}
catch (IOException e) {
    e.printStackTrace();
}
```

---

## 7. Manipulando pastas (diretórios) com `File`

A classe `File` também representa diretórios, e oferece métodos específicos para criá-los, listá-los e navegá-los.

```java
File dir = new File("C:\\temp\\meusArquivos");

// Criar diretório
boolean criado = dir.mkdir();       // cria apenas o diretório final (falha se pais não existirem)
boolean criados = dir.mkdirs();     // cria o diretório e todos os diretórios pais necessários

// Verificar se é diretório
if (dir.isDirectory()) {
    System.out.println("É um diretório");
}

// Listar conteúdo (nomes)
String[] nomes = dir.list();
if (nomes != null) {
    for (String nome : nomes) {
        System.out.println(nome);
    }
}

// Listar conteúdo (objetos File completos)
File[] arquivos = dir.listFiles();
if (arquivos != null) {
    for (File f : arquivos) {
        System.out.println(f.getName() + " - " + (f.isDirectory() ? "diretório" : "arquivo"));
    }
}

// Filtrar arquivos por extensão, usando lambda (FilenameFilter é uma interface funcional)
File[] txtFiles = dir.listFiles((d, nome) -> nome.endsWith(".txt"));

// Apagar (só funciona se o diretório estiver vazio)
dir.delete();

// Renomear/mover
File novo = new File("C:\\temp\\arquivosRenomeados");
dir.renameTo(novo);
```

> ⚠️ **Limitações de `File`**: os métodos `delete()`, `mkdir()` e `renameTo()` retornam apenas um `boolean` indicando sucesso ou falha, **sem informar o motivo** do erro. Além disso, `File` não lida bem com *links simbólicos* nem oferece operações atômicas. Por esses motivos, a API `java.nio.file` (seção 8) é preferida em código novo.

---

## 8. A API moderna: `java.nio.file` (NIO.2)

Desde o Java 7, a API **NIO.2** oferece uma forma mais robusta, expressiva e portável de trabalhar com arquivos, por meio das classes `Path`, `Paths`/`Path.of(...)` e do utilitário estático `Files`.

### 8.1 `Path` e `Paths`

`Path` substitui conceitualmente o `File`, representando um caminho de forma independente de sistema operacional.

```java
import java.nio.file.Path;

Path caminho = Path.of("C:", "temp", "in.txt");      // Java 11+ (forma recomendada)
// ou, em versões anteriores:
// Path caminho = Paths.get("C:\\temp\\in.txt");

System.out.println(caminho.getFileName());  // in.txt
System.out.println(caminho.getParent());    // C:\temp
System.out.println(caminho.toAbsolutePath());
```

### 8.2 A classe utilitária `Files`

Concentra praticamente todas as operações de leitura, escrita e manipulação de arquivos/diretórios, com métodos estáticos que lançam exceções mais informativas (`NoSuchFileException`, `FileAlreadyExistsException`, `DirectoryNotEmptyException` etc.), todas subclasses de `IOException`.

```java
import java.nio.file.*;
import java.util.List;

Path path = Path.of("C:", "temp", "in.txt");

// Verificações
Files.exists(path);
Files.isDirectory(path);
Files.isReadable(path);
Files.size(path);

// Leitura simples de um arquivo pequeno (lê tudo em memória de uma vez)
List<String> linhas = Files.readAllLines(path); // usa UTF-8 por padrão
String conteudo = Files.readString(path);       // Java 11+

// Escrita simples
Files.writeString(path, "conteúdo do arquivo");           // Java 11+ (sobrescreve)
Files.write(path, List.of("linha 1", "linha 2"));          // sobrescreve
Files.writeString(path, "\nmais conteúdo", StandardOpenOption.APPEND); // acrescenta

// Cópia, movimentação e exclusão
Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);
Files.delete(path);            // lança exceção se não existir
Files.deleteIfExists(path);     // não lança exceção se já não existir

// Diretórios
Files.createDirectory(path);    // cria um único nível
Files.createDirectories(path);  // cria todos os níveis necessários

// Streaming de leitura para arquivos grandes (não carrega tudo em memória)
try (Stream<String> stream = Files.lines(path)) {
    stream.filter(l -> l.contains("erro"))
          .forEach(System.out::println);
}

// Listar conteúdo de um diretório (retorna um Stream<Path>)
try (Stream<Path> stream = Files.list(dirPath)) {
    stream.forEach(System.out::println);
}

// Percorrer uma árvore de diretórios recursivamente
try (Stream<Path> stream = Files.walk(dirPath)) {
    stream.filter(Files::isRegularFile)
          .filter(p -> p.toString().endsWith(".java"))
          .forEach(System.out::println);
}
```

### 8.3 Por que preferir NIO.2 em código novo

| Aspecto | `java.io.File` | `java.nio.file` (`Path` + `Files`) |
|---|---|---|
| Tratamento de erro | `boolean` sem detalhes | Exceções específicas e informativas |
| Portabilidade | Manual (separador de path) | Nativa, independente de SO |
| Links simbólicos | Sem suporte real | Suporte completo |
| Operações em lote/streaming | Limitado | `Files.lines()`, `Files.walk()`, integração com Stream API |
| Metadados (atributos POSIX, ACL etc.) | Muito limitado | `Files.readAttributes()`, granular |
| Atomicidade (mover/copiar) | Não garantida | `StandardCopyOption.ATOMIC_MOVE` |

> Para código legado ou integração com bibliotecas antigas que exigem `File`, é possível converter facilmente entre as duas APIs: `path.toFile()` e `file.toPath()`.

### 8.4 Monitorando diretórios em tempo real: `WatchService`

Além de ler, escrever e listar, a NIO.2 permite **observar mudanças** em um diretório (arquivo criado, apagado ou modificado) sem precisar ficar checando manualmente em loop (*polling*). Isso é feito com `WatchService`, registrado em um `Path`.

```java
import java.nio.file.*;

public class Monitor {
    public static void main(String[] args) throws IOException, InterruptedException {

        Path dir = Path.of("C:\\temp");

        // 1. cria o serviço de observação
        WatchService watcher = FileSystems.getDefault().newWatchService();

        // 2. registra o diretório e os tipos de evento de interesse
        dir.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        // 3. loop de observação (bloqueante)
        while (true) {
            WatchKey key = watcher.take(); // bloqueia até haver um evento

            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(event.kind() + ": " + event.context());
            }

            // reseta a key para continuar recebendo eventos; se retornar false,
            // o diretório ficou inacessível e o loop deve ser interrompido
            boolean valid = key.reset();
            if (!valid) break;
        }
    }
}
```

> Uso típico: *hot reload* de configuração, sincronização de pastas, gatilhos de build (ex: recompilar quando um `.java` muda). Referência: [`WatchService`](https://docs.oracle.com/javase/10/docs/api/java/nio/file/WatchService.html)

### 8.5 Percorrendo árvores de diretório: `Files.walkFileTree` e `FileVisitor`

Já vimos `Files.walk()` (seção 8.2), que retorna um `Stream<Path>` simples — ótimo para filtros rápidos. Quando é preciso um controle mais fino sobre a travessia (ex: agir diferente ao entrar/sair de cada pasta, ou abortar a busca antecipadamente), usa-se `Files.walkFileTree` com a interface `FileVisitor`.

```java
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

Path start = Path.of("C:\\temp\\projeto");

Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        System.out.println("Entrando na pasta: " + dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        System.out.println("Arquivo: " + file + " (" + attrs.size() + " bytes)");
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        System.out.println("Falha ao acessar: " + file);
        return FileVisitResult.CONTINUE; // não aborta a travessia inteira
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Saindo da pasta: " + dir);
        return FileVisitResult.CONTINUE;
    }
});
```

`SimpleFileVisitor` é uma implementação padrão de `FileVisitor` onde todos os métodos apenas retornam `CONTINUE` — basta sobrescrever os que interessam. Os retornos possíveis (`FileVisitResult`) permitem controle fino:
- `CONTINUE` — segue normalmente
- `SKIP_SUBTREE` — pula toda a subárvore da pasta atual (útil em `preVisitDirectory`)
- `SKIP_SIBLINGS` — pula os irmãos restantes no mesmo nível
- `TERMINATE` — aborta a travessia inteira

> Use `Files.walk()` quando bastar um `Stream` para filtrar/coletar; use `Files.walkFileTree` quando precisar de lógica de entrada/saída de diretório ou de abortar a busca cedo.

---

## 9. Leitura/escrita de dados binários: streams de byte

Quando o conteúdo não é texto (imagens, arquivos compactados, serialização de objetos etc.), usam-se *streams* de **bytes** em vez de caracteres:

```java
import java.io.*;

// Leitura binária
try (FileInputStream fis = new FileInputStream("imagem.png");
     BufferedInputStream bis = new BufferedInputStream(fis)) {

    int b;
    while ((b = bis.read()) != -1) {
        // processar byte a byte (ou usar bis.readAllBytes() para ler tudo de uma vez)
    }
}

// Escrita binária
try (FileOutputStream fos = new FileOutputStream("copia.png");
     BufferedOutputStream bos = new BufferedOutputStream(fos)) {

    byte[] dados = Files.readAllBytes(Path.of("imagem.png"));
    bos.write(dados);
}
```

> Regra geral: `Reader`/`Writer` (e suas subclasses) trabalham com **caracteres** (texto); `InputStream`/`OutputStream` (e suas subclasses) trabalham com **bytes** (dados binários).

---

## 10. Boas práticas

1. **Sempre use `try-with-resources`** para qualquer classe `AutoCloseable` (`FileReader`, `BufferedReader`, `FileWriter`, `InputStream`, conexões etc.) — evita vazamento de recursos e reduz *boilerplate*.
2. **Prefira a API `Path`/`Files` (NIO.2)** a `File` em código novo, pelos motivos listados na seção 8.3.
3. **Não leia arquivos grandes inteiros em memória** (`readAllLines`, `readAllBytes`) sem necessidade — para arquivos grandes, prefira leitura em *stream* (`Files.lines()`, `BufferedReader.readLine()` em loop).
4. **Sempre trate `IOException`** de forma específica quando possível (`FileNotFoundException`, `NoSuchFileException` etc.), fornecendo mensagens úteis ao usuário — evite apenas repassar a *stack trace* crua em aplicações de produção.
5. **Defina o charset explicitamente** ao ler/escrever texto, especialmente em sistemas que trocam arquivos entre plataformas diferentes:
```java
   Files.readString(path, StandardCharsets.UTF_8);
```
6. **Verifique existência e permissões antes de operar**, mas esteja ciente de condições de corrida (*race conditions* — o arquivo pode ser removido entre a verificação e o uso); por isso, capturar a exceção resultante da operação real ainda é necessário mesmo após checar `exists()`.
7. **Feche recursos na ordem correta** — com `try-with-resources` isso é automático (ordem inversa à declaração); se fechar manualmente, feche o *wrapper* (`BufferedReader`) antes do recurso interno (`FileReader`), embora fechar o wrapper já feche a cadeia internamente na maioria dos casos.
8. **Use caminhos relativos ou configuráveis** (não *hardcoded*) sempre que possível, para portabilidade entre ambientes de desenvolvimento, teste e produção.
9. **Ao mover/copiar arquivos importantes, use `StandardCopyOption.ATOMIC_MOVE`** quando a atomicidade for crítica (ex: evitar arquivo corrompido em caso de falha no meio da operação).
10. **Prefira `Files.createDirectories()` a `mkdir()`/`mkdirs()`** quando precisar tratar falhas com exceções específicas em vez de apenas um `boolean`.
