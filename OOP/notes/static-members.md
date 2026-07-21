# Membros Estáticos em Java

## 1. Definição
Membros estáticos (atributos ou métodos) são aqueles que fazem sentido independentemente da criação de objetos. 
* Eles **não precisam** de um objeto instanciado para serem chamados.
* São invocados diretamente a partir do **próprio nome da classe** (ex: `Classe.metodo()`).
* Também são conhecidos como **membros de classe**, em oposição aos membros de instância (que pertencem a um objeto específico).

## 2. Aplicações Comuns
* **Classes utilitárias:** Agrupam métodos e operações comuns que não dependem de estado.
* **Declaração de constantes.**

## 3. Classes Estáticas
Uma classe que possui somente membros estáticos pode ser considerada/criada como uma classe estática. A principal característica nesse cenário é que **esta classe não poderá ser instanciada** (não faz sentido utilizar o `new`).

## 4. O Método `main`
O método principal (`main`) de um programa em Java é **sempre estático**.
* **Regra importante:** Dentro de contextos ou classes estáticas, só é possível chamar/criar diretamente métodos que também sejam estáticos.

```java
public static void main(String[] args) {
    // Ponto de entrada do programa
}
```

## 5. Constantes
Constantes são comumente definidas como membros estáticos.
* Como o valor de uma constante não pode ser modificado ao longo do programa, deve-se adicionar a palavra-chave `final`.
* Por convenção, o identificador de uma constante é escrito inteiramente em letras MAIÚSCULAS.

```java
public static final double PI = 3.14159;
```