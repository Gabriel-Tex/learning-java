# Tipos Primitivos e Wrapper Classes em Java

## 1. Tipos Primitivos
Em Java, os tipos primitivos (como `int`, `double`, `char`, `boolean`, etc.) são os tipos de dados mais básicos. Eles armazenam valores puros e não são objetos, ou seja, não possuem métodos e não aceitam o valor `null`.

![primitive types](../../../assets/types.png)

## 2. Wrapper Classes
As Wrapper classes são classes equivalentes aos tipos primitivos (ex: `Integer` para `int`, `Double` para `double`, `Character` para `char`, etc.), permitindo que esses valores básicos sejam tratados como objetos.

![wrapper classes](../../../assets/wrapper.png)

### Características Principais
* **Boxing e Unboxing:** O processo de conversão de um tipo primitivo para sua respectiva Wrapper class (boxing) e o processo inverso (unboxing) ocorrem de forma natural e automática na linguagem Java (conhecido como *autoboxing* e *autounboxing*).
* **Uso Comum:** São amplamente utilizadas na definição de campos de entidades em sistemas de informação e banco de dados.

### Vantagens das Wrapper Classes
* Como são tipos de referência (classes), elas **aceitam o valor `null`**, o que é fundamental em bancos de dados para representar a ausência de valor.
* Elas usufruem de todos os recursos da Orientação a Objetos (OO), possuindo métodos úteis embutidos para conversão e manipulação de dados (ex: `Integer.parseInt()`).