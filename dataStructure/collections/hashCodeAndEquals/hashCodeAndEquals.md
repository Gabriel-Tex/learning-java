# hashCode e equals em Java

## O que são

`hashCode()` e `equals()` são operações herdadas da classe `Object`, usadas para comparar se um objeto é igual a outro.

- **`equals`**: comparação mais lenta, porém com resposta 100% confiável (verifica igualdade "de verdade").
- **`hashCode`**: comparação mais rápida (retorna um número inteiro), porém uma resposta positiva **não é 100% garantida** — pode haver colisão.

Tipos comuns do Java (`String`, `Date`, `Integer`, `Double`, etc.) já possuem implementações prontas dessas operações. **Classes personalizadas precisam sobrescrevê-las** quando se deseja comparação por conteúdo (e não por referência).

---

## equals

Compara se um objeto é igual a outro, retornando `true` ou `false`.

```java
String a = "Maria";
String b = "Alex";
System.out.println(a.equals(b)); // false
```

Por padrão (sem sobrescrever), `equals` de `Object` compara **referências** (mesmo endereço de memória), não conteúdo.

---

## hashCode

Retorna um número inteiro representando um código gerado a partir das informações do objeto.

```java
String a = "Maria";
String b = "Alex";
System.out.println(a.hashCode());
System.out.println(b.hashCode());
```

---

## Regra de ouro do hashCode

- Se o `hashCode` de dois objetos for **diferente**, então os dois objetos são **certamente diferentes**.
- Se o `hashCode` de dois objetos for **igual**, muito provavelmente os objetos são iguais (mas **pode haver colisão**).

```
"Alex Larry Brown" -> -242670543
"Alex Larry Brown" -> 880483901   // isso nunca acontece (mesmo objeto/conteúdo deveria gerar o mesmo hash)
```

> Importante: **objetos iguais (`equals` true) DEVEM ter o mesmo `hashCode`**. O inverso não é garantido (hashes iguais não implicam objetos iguais — por isso `equals` é usado para confirmar).

---

## hashCode e equals personalizados

Ao criar uma classe própria, se quisermos que dois objetos sejam considerados "iguais" com base em seus atributos (e não pela referência), precisamos sobrescrever `equals` e `hashCode`.

```java
public class Client {
    private String name;
    private String email;

    // hashCode e equals devem ser gerados/sobrescritos
    // considerando os atributos relevantes (ex: name e email)
}
```

Na prática (IDEs geram automaticamente), a implementação segue o padrão:

```java
@Override
public int hashCode() {
    return Objects.hash(name, email);
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Client other = (Client) obj;
    return Objects.equals(name, other.name) && Objects.equals(email, other.email);
}
```

---

## Por que isso importa?

Coleções baseadas em hash (`HashSet`, `HashMap`, etc.) usam `hashCode` + `equals` para:
- Verificar se um elemento já existe (`contains`, `add`).
- Localizar rapidamente um valor a partir de uma chave (`get`).

**Se `equals` e `hashCode` não forem implementados** na classe personalizada, a coleção usa a comparação padrão (por **referência/ponteiro**), o que geralmente não é o comportamento desejado ao comparar objetos "iguais" por conteúdo.

> Este tópico é a base para entender o funcionamento de `Set` e `Map` (próximas anotações).