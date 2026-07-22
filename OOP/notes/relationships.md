# Relacionamento entre classes

## Agregação
- Relação "tem-um", isto é, indica que uma classe possui outra
- Os objetos são independentes
- Relaciona as classes utilizando tipo abstrato de dados, isto é, uma classe utiliza o tipo referente a outra classe
- É possível, daí, utilizar todos os métodos do objeto da classe externa na classe interna

```java
    public class Luta {
    	// atributos	
    	private Lutador desafiado;
    	private Lutador desafiante;
    	private int rounds;
    	private boolean aprovada;

    	// método construtor
    	public Luta(Lutador d, Lutador de, int ro) {
    		setDesafiado(d);
    		setDesafiante(de);
    		setRounds(ro);
    	}
    	
    	/* Percebe-se que a classe Luta utiliza o tipo abstrado Lutador, 
    	pertencente à classe Lutador */
    }
```

- Como os objetos são independentes, o **ciclo de vida** de um não depende do outro: se o objeto `Luta` for destruído, os objetos `Lutador` continuam existindo normalmente (eles podem ter sido criados fora da classe `Luta` e apenas referenciados nela)
- Em UML, a agregação é representada por um losango **vazio** (não preenchido) do lado da classe "todo"

## Composição
- Relação "tem-um" mais forte que a agregação: indica **posse exclusiva**
- Os objetos **não são independentes** — o objeto interno só existe enquanto o objeto externo existir
- O **ciclo de vida** do objeto interno é totalmente dependente do objeto externo: se o "todo" é destruído, as "partes" também são
- Geralmente, o objeto interno é criado **dentro** do próprio construtor da classe externa (e não recebido via parâmetro)

```java
    public class Carro {
    	// atributos
    	private Motor motor;
    	
    	// método construtor
    	public Carro() {
    		motor = new Motor(); // o Motor é criado aqui dentro,
    		                      // não existe fora de um Carro
    	}
    	
    	/* Percebe-se que o objeto Motor não faz sentido existir
    	independentemente de um Carro: ele é uma parte inseparável dele */
    }
```

- Em UML, a composição é representada por um losango **preenchido** (sólido) do lado da classe "todo"

