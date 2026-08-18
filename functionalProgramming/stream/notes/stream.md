# Stream

## O que é uma Stream

Uma **Stream** é uma sequência de elementos vinda de uma fonte de dados, que oferece suporte a operações agregadas — operações que processam o conjunto de elementos como um todo, em vez de manipular índice por índice como se faz tradicionalmente com um `for`. A fonte de dados pode ser uma coleção, um array, uma função de iteração, ou até um recurso de entrada/saída.

É importante deixar claro desde já: Stream **não é** uma estrutura de dados. Ela não armazena elementos, não tem estado persistente, e não modifica a fonte de onde os dados vieram. Uma Stream é, na verdade, um "cano" (*pipeline*) por onde os dados passam, sofrendo transformações ao longo do caminho, até chegar a um resultado final.

## Características

Streams foram desenhadas para processar sequências de dados seguindo os princípios da programação funcional discutidos nas anotações anteriores, e isso se reflete em várias características centrais.

O processamento é **declarativo**: você descreve *o que* quer fazer (filtrar, transformar, somar), e a forma como a iteração de fato acontece fica escondida do programador — diferente de um `for` tradicional, em que você controla manualmente cada passo da iteração.

Streams são **parallel-friendly**: como trabalham sobre estruturas imutáveis (a Stream em si não guarda estado mutável compartilhado), é seguro paralelizar seu processamento sem se preocupar com condições de corrida, bastando trocar `.stream()` por `.parallelStream()`.

O processamento é feito **sem efeitos colaterais** — pelo menos, é essa a expectativa. As operações de uma Stream, especialmente as intermediárias, devem ser funções puras (relembrando a ideia de transparência referencial), o que é justamente o que garante que a paralelização funcione de forma segura e previsível.

Streams operam sob demanda, em regime de **lazy evaluation** (avaliação preguiçosa): nenhuma operação intermediária é de fato executada até que uma operação terminal seja chamada. Isso permite montar pipelines complexos sem custo de processamento algum, até o momento em que o resultado é efetivamente solicitado.

O **acesso é sequencial**, sem índices — não existe algo como `stream.get(2)`. Você não acessa elementos por posição; você os processa em fluxo, um após o outro (ou, no caso de streams paralelas, em partições que são recombinadas ao final).

E, talvez a característica mais importante na prática do dia a dia: uma Stream é **single-use**, ou seja, só pode ser usada (percorrida) uma única vez. Depois que uma operação terminal é chamada sobre ela, a Stream é considerada consumida — qualquer tentativa de reutilizá-la lança uma exceção (`IllegalStateException`). Se for necessário processar os mesmos dados de novo, é preciso criar uma nova Stream a partir da fonte original.

Por fim, Streams seguem o padrão de **pipeline**: operações realizadas sobre uma Stream retornam uma nova Stream, o que permite encadear várias operações em sequência, formando um fluxo contínuo de processamento — exatamente como vimos nos exemplos com `filter`, `map` e `collect` combinados em uma única expressão.

## Operações intermediárias e terminais

Todo pipeline de Stream é composto por zero ou mais operações intermediárias, seguidas obrigatoriamente por uma operação terminal.

Uma **operação intermediária** produz uma nova Stream (permitindo o encadeamento), e só é de fato executada quando alguma operação terminal é invocada — reflexo direto da *lazy evaluation* mencionada acima. Chamar `.filter(...)` sozinho, sem nenhuma operação terminal depois, não processa absolutamente nada.

As operações intermediárias mais comuns são: `filter` (mantém apenas os elementos que satisfazem um `Predicate`), `map` (transforma cada elemento usando uma `Function`), `flatMap` (parecido com `map`, mas "achata" streams de streams em uma única stream), `peek` (permite espiar/inspecionar os elementos sem alterá-los, geralmente para depuração), `distinct` (remove duplicatas), `sorted` (ordena os elementos), `skip` (pula os *n* primeiros elementos) e `limit` (mantém apenas os *n* primeiros elementos).

Uma **operação terminal** produz um objeto que não é uma Stream (uma coleção, um valor, ou nenhum resultado, como no caso de `void`), e determina o fim do processamento — é o "gatilho" que efetivamente dispara a execução de todo o pipeline montado anteriormente.

As operações terminais mais comuns são: `forEach` (executa uma ação para cada elemento, via `Consumer`), `forEachOrdered` (como `forEach`, mas garantindo a ordem mesmo em streams paralelas), `toArray` (converte a stream para um array), `reduce` (combina todos os elementos em um único valor), `collect` (acumula os elementos em uma coleção ou outra estrutura, geralmente via `Collectors`), `min` e `max` (menor e maior elemento, segundo um `Comparator`), `count` (quantidade de elementos), e um grupo de operações de teste rápido: `anyMatch`, `allMatch` e `noneMatch`, além de `findFirst` e `findAny`.

Vale destacar `limit`, `anyMatch`, `allMatch`, `noneMatch`, `findFirst` e `findAny` como operações *short-circuit*: elas podem interromper o processamento assim que o resultado já é conhecido, sem percorrer a stream inteira — por exemplo, `anyMatch` para de processar assim que encontra o primeiro elemento que satisfaz a condição, sem necessidade de checar os demais.

## Criando uma Stream

A forma mais comum de obter uma Stream é chamar o método `stream()` (ou `parallelStream()`, para processamento paralelo) a partir de qualquer objeto que implemente `Collection`.

Além disso, existem formas alternativas de criar Streams diretamente, sem partir de uma coleção já existente: `Stream.of`, que cria uma stream a partir de valores fornecidos diretamente; `Stream.ofNullable`, que cria uma stream com zero ou um elemento, tratando `null` de forma segura; e `Stream.iterate`, que gera uma stream infinita (ou limitada, se combinada com `limit`) aplicando repetidamente uma função sobre um valor inicial.

```java
List<Integer> list = Arrays.asList(3, 4, 5, 10, 7);
Stream<Integer> st1 = list.stream();
System.out.println(Arrays.toString(st1.toArray()));

Stream<String> st2 = Stream.of("Maria", "Alex", "Bob");
System.out.println(Arrays.toString(st2.toArray()));

Stream<Integer> st3 = Stream.iterate(0, x -> x + 2);
System.out.println(Arrays.toString(st3.limit(10).toArray()));

Stream<Long> st4 = Stream.iterate(new long[]{0L, 1L}, p -> new long[]{p[1], p[0] + p[1]})
                          .map(p -> p[0]);
System.out.println(Arrays.toString(st4.limit(10).toArray()));
```

O último exemplo é particularmente interessante: `Stream.iterate` gera pares consecutivos da sequência de Fibonacci, e `.map(p -> p[0])` extrai apenas o primeiro elemento de cada par — uma boa demonstração de como `iterate` pode ser combinado com `map` para gerar sequências matemáticas não triviais, de forma preguiçosa (só os 10 primeiros valores são de fato calculados, por causa do `.limit(10)`).

## Pipeline: colocando tudo junto

Com operações intermediárias e terminais combinadas, é possível montar pipelines completos de processamento de dados de forma bastante declarativa.

```java
List<Integer> list = Arrays.asList(3, 4, 5, 10, 7);

// map: multiplica cada elemento por 10
Stream<Integer> st1 = list.stream().map(x -> x * 10);
System.out.println(Arrays.toString(st1.toArray()));

// reduce: soma todos os elementos, partindo de um valor inicial (0)
int sum = list.stream().reduce(0, (x, y) -> x + y);
System.out.println("Sum = " + sum);

// pipeline completo: filtra os pares, multiplica por 10, coleta em uma nova lista
List<Integer> newList = list.stream()
    .filter(x -> x % 2 == 0)
    .map(x -> x * 10)
    .collect(Collectors.toList());
System.out.println(Arrays.toString(newList.toArray()));
```

Vale prestar atenção especial ao `reduce`: ele recebe um valor inicial (o "acumulador" de partida, `0` no exemplo) e uma função que combina o acumulador com cada elemento da stream, produzindo um único valor final. É a forma funcional de expressar o que, em código imperativo, seria escrito com uma variável externa sendo incrementada dentro de um `for` — a mesma comparação já feita na anotação sobre programação funcional, ao contrastar `sum += x` dentro de um laço com `list.stream().reduce(0, Integer::sum)`.

## Encaixando com o que já vimos

Esse é o momento em que `Predicate`, `Function` e `Consumer` — vistos nas anotações anteriores — se revelam como as peças que sustentam toda a API de Streams: `filter` é literalmente construído em cima de `Predicate`, `map` em cima de `Function`, e `forEach`/`peek` em cima de `Consumer`. Entender bem essas três interfaces isoladamente é o que torna natural ler e escrever qualquer pipeline de Stream, por mais longo que seja — no fundo, é sempre a mesma lógica de pequenas funções sendo encadeadas, uma alimentando a próxima.