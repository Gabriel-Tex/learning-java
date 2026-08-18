# Criando Funções que Recebem Funções como Argumento

## Relembrando

Nas anotações anteriores vimos três interfaces funcionais consumidas por métodos prontos do Java: `removeIf` recebe um `Predicate`, `forEach` recebe um `Consumer`, e `map` recebe uma `Function`. Em todos esses casos, quem definiu "a função que recebe uma função como argumento" foi o próprio Java, através das classes `List` e `Stream`. A pergunta natural que segue é: como faço isso eu mesmo, dentro do meu próprio código?

A resposta é simples: uma interface funcional pode ser usada como tipo de parâmetro em qualquer método que você escrever, exatamente da mesma forma como `removeIf`, `forEach` e `map` fazem internamente. Isso é o que permite, de fato, tratar funções como objetos de primeira classe em Java — não apenas usar as funções prontas da API, mas também criar suas próprias funções que aceitam outras funções como entrada.

## Problema exemplo

Suponha uma lista de produtos e o objetivo de calcular a soma dos preços apenas dos produtos cujo nome começa com "T":

```java
List<Product> list = new ArrayList<>();
list.add(new Product("Tv", 900.00));
list.add(new Product("Mouse", 50.00));
list.add(new Product("Tablet", 350.50));
list.add(new Product("HD Case", 80.90));
```

Resultado esperado: `1250.50` (soma dos preços de "Tv" e "Tablet").

O ponto interessante deste problema é que o critério de filtragem ("nome começa com T") é apenas um exemplo específico de um critério mais geral ("nome começa com uma letra qualquer"). Em vez de escrever um método fixo, específico para a letra "T", faz muito mais sentido escrever um método genérico que recebe, como argumento, a condição a ser aplicada — permitindo reutilizá-lo com qualquer critério, sem duplicar código.

## Escrevendo um método que recebe um Predicate

A forma mais direta de resolver isso é declarar um método próprio cujo parâmetro seja do tipo `Predicate<Product>`:

```java
public class Program {

    public static double sum(List<Product> list, Predicate<Product> predicate) {
        double sum = 0.0;
        for (Product product : list) {
            if (predicate.test(product)) {
                sum += product.getPrice();
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        List<Product> list = new ArrayList<>();
        list.add(new Product("Tv", 900.00));
        list.add(new Product("Mouse", 50.00));
        list.add(new Product("Tablet", 350.50));
        list.add(new Product("HD Case", 80.90));

        double result = sum(list, p -> p.getName().startsWith("T"));
        System.out.println(result); // 1250.5
    }
}
```

Note a estrutura: o método `sum` não sabe, e não precisa saber, qual é o critério de filtragem — ele apenas recebe um `Predicate<Product>` e delega a ele a decisão de "incluir ou não" cada produto na soma, chamando `predicate.test(product)`. Quem decide o critério é quem chama o método, no momento da chamada, passando a lambda `p -> p.getName().startsWith("T")`. Se amanhã o critério mudar (produtos com preço acima de determinado valor, produtos de um fornecedor específico, etc.), basta chamar `sum` com outra lambda — o método em si permanece intocado.

Essa é a mesma lógica de inversão de controle que já vimos em `removeIf`, `forEach` e `map`: o método genérico define *o que fazer com o resultado do teste* (somar, remover, imprimir, transformar), e quem o chama define *qual é o teste* em si.

## Generalizando para qualquer tipo

O mesmo método pode ser generalizado ainda mais, usando generics, para funcionar com qualquer tipo de lista, não apenas `List<Product>`:

```java
public static <T> double sum(List<T> list, Predicate<T> predicate, Function<T, Double> valueExtractor) {
    double sum = 0.0;
    for (T item : list) {
        if (predicate.test(item)) {
            sum += valueExtractor.apply(item);
        }
    }
    return sum;
}
```

```java
double result = sum(list, p -> p.getName().startsWith("T"), Product::getPrice);
```

Aqui entram em jogo duas interfaces funcionais ao mesmo tempo: o `Predicate<T>` decide quais elementos entram na soma, e a `Function<T, Double>` decide como extrair, de cada elemento, o valor numérico a ser somado. Esse tipo de composição — vários parâmetros funcionais no mesmo método — é bastante comum em bibliotecas mais sofisticadas, e é exatamente o mecanismo por trás de operações como `Collectors.summingDouble` das Streams, que veremos mais adiante.

## Por que isso importa

Esse é o ponto em que a ideia de "funções como objetos de primeira ordem", discutida na anotação sobre programação funcional, deixa de ser um conceito abstrato e vira uma ferramenta prática de design. Métodos que recebem outros métodos (via interfaces funcionais) como argumento são chamados de **funções de ordem superior** (*higher-order functions*). Elas permitem escrever código genérico e reutilizável, adiando a decisão de "qual comportamento exato executar" para quem usa o método, em vez de fixar esse comportamento dentro da implementação.

Essa é, na prática, a mesma engenharia por trás de `removeIf`, `forEach`, `map`, `sort`, e — como veremos na próxima anotação — de praticamente toda a API de Streams do Java: métodos genéricos e estáveis, parametrizados por pequenas funções fornecidas por quem os chama.