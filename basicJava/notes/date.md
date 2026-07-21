# Data, Hora e Duração em Java

## 1. Conceitos Importantes
* **Data-Hora Local (`LocalDate` / `LocalDateTime`):** Ano, mês, dia e opcionalmente hora **sem fuso horário**. Útil para sistemas de região única ou quando o momento exato não importa para outros fusos (ex: datas de nascimento, vendas locais).
* **Data-Hora Global (`Instant`):** Ano, mês, dia e hora **com fuso horário** (referência UTC). Essencial para sistemas multi-região e web (ex: agendamentos, posts, logs de transações).
* **Duração (`Duration`):** Tempo decorrido entre duas data-horas.

## 2. Fusos Horários (Timezones)
* **GMT (Greenwich Mean Time):** Horário de Londres / Padrão UTC (Coordinated Universal Time), também chamado de tempo "Z" (Zulu).
* **Deslocamentos:** Outros fusos são relativos ao UTC (ex: São Paulo é `GMT-3`, Portugal é `GMT+1`).
* **Identificadores:** As tecnologias utilizam nomes padronizados como `"US/Pacific"` ou `"America/Sao_Paulo"`.

## 3. Padrão ISO 8601
* **Data-Hora Local:** 
  `2022-07-21` | `2022-07-21T14:52` | `2022-07-22T14:52:09.4073`
* **Data-Hora Global:** 
  `2022-07-23T14:52:09Z` | `2022-07-23T14:52:09-03:00`

## 4. Principais Tipos Java (Versão 8+)
* **Data-Hora Local:** `LocalDate`, `LocalDateTime`
* **Data-Hora Global:** `Instant`
* **Duração:** `Duration`
* **Outros:** `ZoneId`, `ChronoUnit`

> **Boa Prática:** Armazene sempre em UTC (`Instant`) no banco de dados e exiba formatado no fuso horário local do usuário.

## 5. Instanciação de Data-Hora
### A partir do momento atual (Agora)
```java
LocalDate d1 = LocalDate.now();
LocalDateTime d2 = LocalDateTime.now();
Instant d3 = Instant.now();
```

### A partir de texto no formato ISO 8601
```java
LocalDate d1 = LocalDate.parse("2022-07-20");
LocalDateTime d2 = LocalDateTime.parse("2022-07-20T01:30:26");
Instant d3 = Instant.parse("2022-07-20T01:30:26Z");
Instant d4 = Instant.parse("2022-07-20T01:30:26-03:00");
```

### A partir de formato customizado (`DateTimeFormatter`)
```java
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
LocalDate d = LocalDate.parse("20/07/2022", fmt);
```

### A partir de dados isolados (Ano, Mês, Dia)
```java
LocalDate d = LocalDate.of(2022, 7, 20);
```

## 6. Formatação de Data-Hora
Para exibir data-horas em formatos personalizados, utiliza-se a classe `DateTimeFormatter`.

```java
LocalDate d = LocalDate.parse("2022-07-20");
DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

System.out.println("d = " + d.format(fmt));

// Para Instant (global), é necessário associar um ZoneId para formatá-lo para a localidade
Instant instant = Instant.parse("2022-07-20T01:30:26Z");
DateTimeFormatter fmtInstant = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());

System.out.println("d = " + fmtInstant.format(instant));
```

## 7. Conversões e Extração de Dados
### Converter Data-Hora Global para Local
```java
Instant d = Instant.parse("2022-07-20T01:30:26Z");
LocalDate r = LocalDate.ofInstant(d, ZoneId.systemDefault());
```

### Obter dados de uma Data-Hora Local
```java
LocalDate d = LocalDate.now();
int dia = d.getDayOfMonth();
int mes = d.getMonthValue();
int ano = d.getYear();
```

## 8. Cálculos com Data-Hora
### Adicionar ou Subtrair Tempo
```java
LocalDate pastWeek = d.minusDays(7);
LocalDate nextWeek = d.plusDays(7);
```

### Calcular Duração entre duas Data-Horas
```java
Duration t = Duration.between(data1, data2);
long diasTotais = t.toDays();
```