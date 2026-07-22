# Manipulação de caminhos de arquivo em Java

## Contexto
Ao trabalhar com arquivos em Java, é comum precisar montar o **caminho (path)** de um arquivo de forma dinâmica, sem "chumbar" (hardcode) o caminho absoluto no código. Isso é essencial pra portabilidade — o programa deve funcionar tanto no seu PC quanto no de outra pessoa, independente do sistema operacional.

## `System.getProperty("user.dir")`
- Retorna o **diretório de trabalho atual** (working directory) do programa em execução
- Normalmente corresponde à raiz do projeto (de onde o `.jar` ou o comando `java` foi executado)
- Retorna uma `String`, ex: `/home/gabriel/meu-projeto`

> Cuidado: esse valor depende de **onde o programa foi rodado**, não de onde o `.java` está salvo. Se você rodar o programa a partir de outra pasta, o resultado muda.

## Classe `Paths` (pacote `java.nio.file`)
- Faz parte da API **NIO.2**, introduzida no Java 7 — é a forma moderna de trabalhar com arquivos, substituindo a antiga classe `File` (do pacote `java.io`) para esse propósito
- Método `Paths.get(String primeiro, String... resto)`:
    - Recebe uma ou mais partes do caminho como strings separadas
    - Monta um objeto do tipo `Path`, unindo as partes automaticamente com o **separador correto do sistema operacional**:
        - `/` no Linux/Mac
        - `\` no Windows
    - Evita bugs de caminho quebrado ao trocar de SO

```java
Paths.get("/home/gabriel/projeto", "assets", "in.txt")
// resultado: /home/gabriel/projeto/assets/in.txt (Linux)
// resultado: C:\...\projeto\assets\in.txt (Windows, se o base dir for Windows)
```

## Método `.toString()`
- Converte o objeto `Path` retornado por `Paths.get()` em uma `String` comum
- Necessário porque `Path` não é uma `String` — é um tipo próprio da NIO.2, com seus próprios métodos (`.getFileName()`, `.getParent()`, `.resolve()`, etc.)

## Exemplo completo
```java
String path = Paths.get(System.getProperty("user.dir"), "assets", "in.txt").toString();
File arquivo = new File(path);
Scanner scanner = new Scanner(arquivo);
```

Isso monta dinamicamente o caminho até um arquivo `in.txt` dentro de uma pasta `assets/` na raiz do projeto, e abre esse arquivo pra leitura.
