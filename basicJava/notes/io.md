# Entrada, Saída e Manipulação de Strings em Java

## 1. Saída de Dados
O `System.out` é o objeto de saída padrão. Ele permite que aplicativos Java exibam strings na janela de comando (prompt de comando).

### Impressão de Texto
```java
System.out.print("Texto");   // O próximo caractere aparecerá imediatamente após o último
// ou
System.out.println("Texto"); // Após imprimir, posiciona o cursor no começo da linha seguinte
```

### Formatação de Dados (`printf`)
Para imprimir dados formatados, utiliza-se o `printf` junto com especificadores de formato.

```java
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        String nome = "Maria";
        int idade = 30;
        double altura = 1.65;

        System.out.printf("Nome: %s\n", nome); // %n também quebra a linha
        System.out.printf("Idade: %d anos\n", idade);
        System.out.printf("Altura: %.2f metros\n", altura);
        
        // Para imprimir com ponto em vez de vírgula em números decimais:
        Locale.setDefault(Locale.US); 
    }
}
```
* **Especificadores comuns:** `%s` (String), `%d` (inteiro), `%f` (ponto flutuante). Pode-se usar `%.2f` para limitar a duas casas decimais, por exemplo.

### Saída de Dados no Java 25
No Java 25, foi introduzida uma forma simplificada para saída de dados:
```java
IO.println("Olá, mundo!");
```

## 2. Casting (Conversão Explícita de Tipos)
Utilizado para forçar a conversão de um tipo de dado em outro.
```java
void main() {       
    int a = 3, b = 2;   
    double x;   
    
    // Converte 'a' explicitamente para double antes da divisão
    x = (double) a / b; 
    System.out.println(x);
}
```

## 3. Entrada de Dados
A leitura de dados padrão (via teclado) é feita conectando a classe `Scanner` à entrada padrão do sistema (`System.in`).

### Utilização do Scanner
```java
import java.util.Scanner; // Importação necessária

// Declaração e instanciação
Scanner input = new Scanner(System.in); 

// Leitura
int number = input.nextInt(); 

// Fechamento (é preciso dar close quando não for mais necessário)
input.close(); 
```
*Dica: Para ler vários inputs na mesma linha, basta fazer uma sequência direta de métodos de leitura.*

### Métodos de Leitura
* **Inteiros:** `input.nextInt()`
* **Decimais:** `input.nextDouble()`
* **Palavra (sem espaços):** `input.next()`
* **Linha inteira (com espaços):** `input.nextLine()`
* **Único caractere:** `input.next().charAt(0)`

### O Problema da Quebra de Linha Pendente
Quando você usa um comando de leitura diferente de `nextLine()` (como `nextInt()`) e dá um `Enter`, a quebra de linha fica "pendente" na entrada padrão. Se você fizer um `nextLine()` logo em seguida, ele absorverá essa quebra de linha em vez de ler a string desejada.

**Solução:** Fazer um `nextLine()` extra antes do `nextLine()` de interesse para "limpar" o buffer.
```java
// PROBLEMA
int x = sc.nextInt(); 
String s1 = sc.nextLine(); // Vai ler a quebra de linha vazia do nextInt

// CORRETO
int x = sc.nextInt();
sc.nextLine(); // Absorve a quebra de linha pendente
String s1 = sc.nextLine();
String s2 = sc.nextLine();
```

## 4. Funções para o Tipo String
O Java possui diversos métodos prontos para manipulação de strings.

### Formatar
* `toLowerCase()`: Converte todas as letras para minúsculas.
* `toUpperCase()`: Converte todas as letras para maiúsculas.
* `trim()`: Remove espaços em branco nas extremidades da string.

### Recortar
* `substring(inicio)`: Retorna uma substring a partir do índice de `inicio` até o final.
* `substring(inicio, fim)`: Retorna uma substring começando em `inicio` e terminando em `fim - 1`.

### Substituir
* `replace(char1, char2)`: Substitui todas as ocorrências de um caractere por outro.
* `replace(string1, string2)`: Substitui todas as ocorrências de uma substring por outra.

### Buscar e Separar
* `indexOf("x")`: Retorna a posição da primeira ocorrência do argumento "x".
* `lastIndexOf("x")`: Retorna a posição da última ocorrência do argumento "x".
* `split(" ")`: Separa a string em um vetor de strings, utilizando o separador passado como argumento (neste caso, um espaço em branco).