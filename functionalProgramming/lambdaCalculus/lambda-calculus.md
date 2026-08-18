# Programação Funcional e Cálculo Lambda

## De onde vem a ideia

O paradigma funcional não é uma invenção recente: sua base é o Cálculo Lambda, um formalismo matemático criado por Alonzo Church em 1930, muito antes de existirem computadores como os conhecemos. Linguagens como Haskell, Clojure, Clean e Erlang são construídas em torno desse paradigma desde o início. Java, por outro lado, nasceu fortemente orientado a objetos e só passou a incorporar características funcionais a partir da versão 8, com a introdução das expressões lambda.

É útil situar o funcional entre os demais paradigmas de programação: o imperativo (C, Pascal, Fortran, Cobol), o orientado a objetos (C++, Object Pascal, Java anterior à versão 8, C# anterior à versão 3), o funcional propriamente dito (Haskell, Clojure, Clean, Erlang), o lógico (Prolog) e o multiparadigma, categoria em que se encaixam linguagens modernas como JavaScript, Python, Ruby, Go, e também o próprio Java a partir da versão 8 e o C# a partir da versão 3. Ou seja: hoje em dia é raro encontrar uma linguagem "pura" em um único paradigma; a tendência é combinar características de vários.

## Imperativo vs Funcional: as diferenças que importam

A forma mais direta de entender programação funcional é compará-la, característica por característica, com a programação imperativa.

**Como se descreve algo a ser computado.** Na programação imperativa, você escreve *comandos*: uma sequência de passos que descrevem *como* chegar ao resultado (um `for`, um `if`, atribuições de variáveis). Na programação funcional, você escreve *expressões*: descreve *o que* deseja obter, e deixa que a linguagem decida como executar. É a diferença entre "percorra a lista, some cada elemento, guarde em uma variável" e "reduza a lista somando seus elementos".

**Transparência referencial.** Uma função possui transparência referencial quando, para os mesmos dados de entrada, ela sempre produz o mesmo resultado — sem depender de, nem alterar, nenhum estado externo. Isso é o mesmo que dizer que a função não tem efeitos colaterais (*side effects*). Na programação funcional essa propriedade é levada a sério; na imperativa, costuma ser tratada de forma mais frouxa. Veja um exemplo de função que **não** é referencialmente transparente:

```java
public class Program {
    public static int globalValue = 3;

    public static void main(String[] args) {
        int[] vect = new int[] {3, 4, 5};
        changeOddValues(vect);
        System.out.println(Arrays.toString(vect));
    }

    public static void changeOddValues(int[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] % 2 != 0) {
                numbers[i] += globalValue;
            }
        }
    }
}
```

Note dois problemas aqui: o método depende de uma variável externa (`globalValue`), e ele modifica o array recebido como parâmetro em vez de retornar um novo array. Se `globalValue` mudar entre duas chamadas, o mesmo array de entrada pode gerar resultados diferentes — isso é exatamente o que a transparência referencial proíbe. Os benefícios de evitar esse tipo de efeito colateral são simplicidade e previsibilidade: uma função referencialmente transparente pode ser entendida, testada e até substituída pelo seu próprio resultado (isso é chamado de *equational reasoning*) sem se preocupar com o "resto do programa".

**Objetos imutáveis.** Na programação imperativa, mutar o estado de um objeto é comum e natural. Na funcional, a preferência é por criar novos objetos a cada transformação, em vez de alterar os existentes — o que, entre outras coisas, facilita muito a escrita de código *thread-safe*, já que dados imutáveis não sofrem condições de corrida.

**Funções como objetos de primeira ordem (ou primeira classe).** Na programação funcional, funções podem ser tratadas como qualquer outro valor: podem ser passadas como argumento para outras funções, retornadas como resultado, ou atribuídas a variáveis. Isso não acontecia no Java pré-8. Um bom exemplo é o uso de *method references* (operador `::`):

```java
public class Program {
    public static int compareProducts(Product p1, Product p2) {
        return p1.getPrice().compareTo(p2.getPrice());
    }

    public static void main(String[] args) {
        List<Product> list = new ArrayList<>();
        list.add(new Product("TV", 900.00));
        list.add(new Product("Notebook", 1200.00));
        list.add(new Product("Tablet", 450.00));

        list.sort(Program::compareProducts);
        list.forEach(System.out::println);
    }
}
```

Aqui, `compareProducts` é passado como um argumento para `sort`, exatamente como se passaria um número ou uma string. A sintaxe `Classe::método` (operador `::`) é a forma de referenciar um método existente sem precisar reescrevê-lo como lambda.

**Tipagem dinâmica / inferência de tipos.** Linguagens funcionais costumam favorecer tipagem dinâmica ou inferência de tipos, evitando a verbosidade de declarar tipos explicitamente toda vez. O Java, por ser fortemente tipado, não vira dinâmico com as lambdas, mas ganha inferência de tipos nesse contexto — repare como não é mais necessário declarar o tipo dos parâmetros:

```java
list.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
```

O compilador infere que `p1` e `p2` são do tipo `Product`, a partir do tipo genérico da lista.

**Expressividade / código conciso.** Como consequência de tudo isso — expressões em vez de comandos, funções de primeira ordem, inferência de tipos — o código funcional tende a ser mais curto e mais direto ao ponto. Um exemplo clássico é somar os elementos de uma lista:

```java
// Estilo imperativo
Integer sum = 0;
for (Integer x : list) {
    sum += x;
}

// Estilo funcional
Integer sum = list.stream().reduce(0, Integer::sum);
```

A versão imperativa descreve o *processo* de somar (inicializar um acumulador, iterar, adicionar); a versão funcional descreve o *resultado* desejado (reduzir a lista a um único valor, usando a soma como operação).

## O que é, de fato, uma expressão lambda

Em programação funcional, uma **expressão lambda** corresponde a uma **função anônima de primeira classe**: anônima porque não tem nome (não é declarada como um método de uma classe), e de primeira classe porque pode ser tratada como qualquer outro objeto — passada como parâmetro, atribuída a uma variável, retornada por outro método.

Veja o mesmo exemplo de comparação de produtos escrito de duas formas equivalentes, uma com method reference e outra com expressão lambda:

```java
public class Program {
    public static int compareProducts(Product p1, Product p2) {
        return p1.getPrice().compareTo(p2.getPrice());
    }

    public static void main(String[] args) {
        // ...
        list.sort(Program::compareProducts);
        // é equivalente a:
        list.sort((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()));
        // ...
    }
}
```

Ambas as chamadas fazem exatamente a mesma coisa: passam para `sort` uma função que sabe comparar dois produtos. A diferença é que, na primeira, essa função já existia como método nomeado (`compareProducts`) e foi referenciada; na segunda, a função foi criada "na hora", sem nome, diretamente no local onde é usada — esse é o traço definidor de uma expressão lambda.

## Fechando a ideia

Cálculo Lambda é o formalismo matemático que serve de base teórica para toda a programação funcional. Expressão lambda, no contexto prático de uma linguagem como Java, é a materialização dessa ideia: uma função anônima de primeira classe, que pode ser passada, armazenada e combinada como qualquer outro dado. Entender essas duas definições — e a tabela comparativa entre imperativo e funcional — é o alicerce para tudo que vem a seguir: interfaces funcionais, `Predicate`, `Consumer`, `Function` e Streams são, no fundo, apenas ferramentas concretas do Java para explorar essas ideias.