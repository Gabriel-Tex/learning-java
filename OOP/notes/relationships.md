# Relacionamento entre classes

## Agregação

- Relação “tem-um”, isto é, indica que uma classe possui outra
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
        }

    	// método construtor
    	public Luta(Lutador d, Lutador de, int ro) {
    		setDesafiado(d);
    		setDesafiante(de);
    		setRounds(ro);
    	}
    	
    	/* Percebe-se que a classe Luta utiliza o tipo abstrado Lutador, 
    	pertencente à classe Lutador */
    ```