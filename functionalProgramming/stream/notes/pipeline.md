# Pipeline (de Streams)

## O que é um pipeline

Um pipeline é a sequência de operações encadeadas sobre uma Stream, formada por zero ou mais operações intermediárias seguidas de exatamente uma operação terminal. É essa estrutura que dá nome à própria ideia de "processamento em cano": os dados entram por uma ponta (a fonte, via `stream()` ou similar), passam por uma série de transformações intermediárias, e saem transformados na outra ponta, quando a operação terminal é finalmente executada.

A montagem de um pipeline é possível porque toda operação intermediária devolve uma nova Stream — é essa devolução que permite "pendurar" a próxima operação logo em seguida, encadeando quantas forem necessárias, até fechar com uma operação terminal.

## Lazy evaluation na prática

Um ponto que vale reforçar aqui, agora com exemplos concretos: nenhuma operação intermediária do pipeline é executada no momento em que é escrita no código. Elas são apenas "registradas" como parte da receita de processamento. É somente quando a operação terminal é chamada que o Java de fato percorre a Stream e executa cada etapa, elemento por elemento.

```java
List<Integer> list = Arrays.asList(3, 4, 5, 10, 7);

Stream<Integer> st1 = list.stream().map(x -> x * 10);
// até aqui, nada foi processado — map ainda não rodou para nenhum elemento

System.out.println(Arrays.toString(st1.toArray()));
// só agora, com toArray() (operação terminal), o map é de fato executado
```

Isso significa que é perfeitamente possível montar um pipeline inteiro, guardá-lo em uma variável do tipo `Stream`, e só decidir depois — ou até nunca decidir — qual operação terminal usar. Mas atenção: como visto na anotação anterior, uma Stream é *single-use*; assim que a operação terminal roda, aquela Stream específica não pode ser reaproveitada para um segundo pipeline.

## Reduce: combinando tudo em um único valor

Um exemplo clássico de operação terminal usada para fechar um pipeline é o `reduce`, que combina todos os elementos da Stream em um único resultado:

```java
int sum = list.stream().reduce(0, (x, y) -> x + y);
System.out.println("Sum = " + sum);
```

Aqui, `0` é o valor inicial do acumulador, e a lambda `(x, y) -> x + y` descreve como combinar o acumulador (`x`) com cada novo elemento (`y`) da Stream, um de cada vez, até sobrar um único valor. É a mesma lógica de "reduzir uma coleção a um valor" já mencionada nas anotações sobre programação funcional e sobre Stream — e vale lembrar que essa mesma soma poderia ser escrita de forma ainda mais direta com uma method reference: `list.stream().reduce(0, Integer::sum)`.

## Um pipeline completo, com várias operações intermediárias

O verdadeiro poder do conceito de pipeline aparece quando várias operações intermediárias são encadeadas antes da operação terminal, formando uma única expressão declarativa que descreve todo o processamento de uma vez:

```java
List<Integer> newList = list.stream()
    .filter(x -> x % 2 == 0)
    .map(x -> x * 10)
    .collect(Collectors.toList());

System.out.println(Arrays.toString(newList.toArray()));
```

Lendo de cima para baixo, o pipeline conta uma história: parta da lista original, mantenha apenas os números pares (`filter`), multiplique cada um por dez (`map`), e junte tudo de volta em uma nova `List` (`collect`, a operação terminal). Cada etapa recebe a Stream produzida pela etapa anterior, e devolve uma nova Stream para a próxima — exceto a última, que fecha o pipeline com um resultado concreto (aqui, uma `List<Integer>`).

Repare que esse pipeline consegue expressar, em quatro linhas bastante legíveis, algo que em estilo imperativo exigiria um `for`, um `if` dentro dele, e uma lista auxiliar sendo populada manualmente — a mesma economia de código já discutida na comparação entre programação imperativa e funcional.

## Por que pensar em "pipeline" e não em "vários comandos separados"

Vale reforçar a diferença de mentalidade em relação ao código imperativo tradicional. Em um `for`, cada iteração executa *todos* os passos (checar a condição, calcular o novo valor, adicionar à lista) para um elemento antes de passar ao próximo. Em um pipeline de Stream, embora o resultado final seja o mesmo, o modelo mental é o de estágios: pense em cada operação intermediária (`filter`, `map`, etc.) como uma estação por onde os elementos passam, um a um, numa esteira. Na prática, a implementação da JVM processa elemento por elemento através de todas as etapas antes de seguir ao próximo, exatamente para viabilizar a *lazy evaluation* e o *short-circuit* das operações que possuem esse comportamento — mas o modelo declarativo de "estágios encadeados" é o que torna o código fácil de ler e de estender: adicionar uma nova etapa ao processamento é, literalmente, inserir mais uma chamada de método na cadeia.

## Fechando a sequência de anotações

Com `Comparator`, o embasamento teórico de programação funcional e cálculo lambda, `Interface Funcional`, `Predicate`, `Consumer`, `Function`, a criação de métodos que recebem funções como argumento, `Stream` e, por fim, `Pipeline`, fica completo o percurso conceitual da programação funcional em Java: das motivações teóricas até a ferramenta prática mais usada no dia a dia — encadear pequenas operações puras e reutilizáveis para processar coleções de dados de forma declarativa, concisa e (quando necessário) paralelizável.