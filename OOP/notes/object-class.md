# A Classe Object em Java

## 1. O que é a Classe `Object`?
Em Java, a classe `Object` (do pacote `java.lang`) é a **raiz da hierarquia de classes**. Toda classe desenvolvida em Java herda, direta ou indiretamente, de `Object`. Isso significa que todo e qualquer objeto instanciado (incluindo vetores/arrays) possui os métodos definidos nesta classe.

## 2. Principais Métodos

### 2.1. `toString()`
Retorna uma representação em formato de `String` do objeto.
* **Comportamento padrão:** Retorna uma string composta pelo nome da classe seguido de um arroba (`@`) e o código hash em hexadecimal (ex: `Pessoa@15db9742`).
* **Uso comum:** É uma boa prática sobrescrever (`@Override`) este método para retornar os dados dos atributos de forma legível. O Java chama esse método automaticamente quando tentamos imprimir o objeto (`System.out.println(objeto)`).

```java
@Override
public String toString() {
    return "Nome: " + this.nome + ", Idade: " + this.idade;
}
```

### 2.2. `equals(Object obj)`
Verifica se o objeto atual é "igual" a outro objeto passado como parâmetro.
* **Comportamento padrão:** Compara apenas as **referências de memória** (funciona exatamente como o operador `==`). Ou seja, só retorna `true` se ambas as variáveis apontarem para o mesmo objeto na memória.
* **Uso comum:** Deve ser sobrescrito (`@Override`) para realizar a comparação dos **valores dos atributos** (igualdade lógica/semântica), verificando se dois objetos distintos possuem os mesmos dados.

```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true; // Mesma referência na memória
    if (obj == null || this.getClass() != obj.getClass()) return false; // Tipos diferentes ou nulo
    
    MinhaClasse outro = (MinhaClasse) obj; // Faz o casting
    return this.id == outro.id; // Compara pelo atributo identificador
}
```

### 2.3. `hashCode()`
Retorna um número inteiro (código hash) gerado a partir do objeto. É amplamente utilizado por estruturas de dados baseadas em tabelas de espalhamento (Hash), como `HashSet` e `HashMap`, para buscas rápidas.
* **Regra de Ouro (Contrato equals/hashCode):** Se você sobrescrever o método `equals()`, você **obrigatoriamente deve** sobrescrever o `hashCode()`. Se dois objetos são iguais segundo o método `equals()`, eles **devem** retornar o mesmo valor no `hashCode()`.

### 2.4. `getClass()`
Retorna um objeto do tipo `Class` que representa a classe do objeto em tempo de execução (*runtime*).
* **Uso comum:** Diferente do `instanceof`, que verifica se um objeto "é um" tipo de classe (incluindo superclasses), o `getClass()` retorna a classe **exata** a qual o objeto pertence. Não pode ser sobrescrito, pois é um método `final`.

```java
System.out.println(novo_objeto.getClass().getSimpleName()); // Imprime apenas o nome da classe
```

## 3. Outros Métodos da Classe Object
* **`clone()`:** Cria e retorna uma cópia do objeto (para usá-lo, a classe deve implementar a interface `Cloneable`).
* **`wait()`, `notify()`, `notifyAll()`:** Métodos finais utilizados exclusivamente para controle de concorrência e sincronização de *threads*.
* **`finalize()`:** Método chamado pelo *Garbage Collector* (Coletor de Lixo) antes de o objeto ser destruído da memória. (Nota: Está depreciado nas versões mais recentes do Java).