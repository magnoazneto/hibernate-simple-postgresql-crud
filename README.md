# Construindo uma API RESTful com Spring, Hibernate (em contexto meio fantasioso) :)

Então você estava lá, como um bom desenvolvedor Java, com seu notebook em umas das mãos e o elixir da vida na outra - aquele que os povos antigos chamavam de café - aguardando uma quest para pôr em prática tudo o que aprendeu ao longo das suas incríveis noites viradas treinando Spring. Você está no seu primeiro dia. Você vai se alistar em uma das maiores guildas do reino: a Zup. Mas bem na porta de entrada... algo acontece.

Um espirito da natureza aparece em sua frente, com um sabre de luz marcado com runas antigas, mas por algum motivo você consegue decifrar naquele emaranhado de palavras ancestrais, uma marcação: BUG.


>_Para nessa guilda entrar, em uma provação terá que passar. Diferentes métodos uma API REST deverá suportar. Nome, email, CPF e nascimento, não deixe nada faltar. As informações corretas, em um banco de dados relacional terá que salvar. Um Status certo o cliente poderá desejar. Mas se algo não funcionar, não esqueça das exceções tratar, e a resposta adequada enviar._
<img src="https://s2.glbimg.com/oGOP1N5kCTMEZa35A7OE1zNZsiA=/e.glbimg.com/og/ed/f/original/2020/01/08/baby-yoda.jpg" style="width:350px;"/>

O espirito some da sua frente, mas o seu interior sabe: ele vai voltar. Você tem até o pôr do sol para desenvolver a API. Com um sorriso no rosto e uma tomada ao lado, você abre seu notebook, estala seus dedos, toma um gole de café e ao som de uma live do lofi do youtube você começa a programar!

## GET READY!

Para não perder tempo, você já começa a imaginar as camadas necessárias da sua aplicação. É uma quest simples, a princípio. Sua API vai receber as requests por um Controller. Esse Controller irá guiar as requests para os lugares corretos dependendo de que endpoint for chamado, e sua aplicação terá que persistir os dados em um banco de dados relacional.

Com sua experiência, você já imagina uma Stack que funciona bem para isso: JPA e PostgreSql

>_O JPA (Java Persistence API) servirá para persistir os objetos Java no PostgreSql (no nosso caso), realizar consultas e outras operações de CRUD de forma mais legal dentro da nossa aplicação :) você vai ver na prática._

Você escolhe alguma IDE que já está habituado, e embora outros aventureiros ao seu lado estejam usando Intellij (que também é ótimo), você decide usar o Eclipse, e lembra que já ouviu falar de uma magia muito interessante para essa quest: 

> Spring Boot - Spring Starter Project

para aprendê-la, você lembra das palavras:

> Help > Eclipse Marketplace > Find for "Spring Starter Project"

A magia "Spring tools" aparece em sua frente, e tudo que você tem que fazer é aprendê-la. 

_(Ou seja: clicar em install)_

Com isso instalado, seus dedos magicamente guiam o cursor do mouse para iniciar um novo projeto Spring usando essa nova skill, e você usa configurações que julga adequadas, como:

~~~java
{
    "Name": "springboot-hibernate-crud",
    "Type": "Maven",
    "Java Version": "8",
    "Packing": "Jar",
    "Language": "Java",
    "Group": "zup.challengeapi",
    "Artifact": "springboot-hibernate-crud",
    "Package": "zup.challengeapi.springboot",
    "Description": "Challenge API to join Zup Guild"
}
~~~

Ao clicar em Next, você para e pensa em tudo que irá precisar para esse projeto, e já seleciona essas dependências, como se fossem equipamentos que o ajudarão a enfrentar o Boss:
1. Spring Web
2. PostgreSQL Driver
3. Spring Data JPA
4. Validation (Bean and Hibernate Validation)

>Nota: enquanto faz isso, você lembra que também poderia ter feito todo esse processo de configuração inicial pelo site https://start.spring.io, e em seguida apenas importado o projeto no Eclipse ou outra IDE. _Anyway_, você apenas lembra disso porque um amigo do seu lado perguntou e você o ajudou. _Porque zupper ajuda zupper (:_

Uma vez que seu projeto foi criado, você dá o golpe final nessa primeira etapa acessando o arquivo:

> src/main/resources/application.properties

E ajusta o que JPA precisa para conectar na sua instalação do postgreSql, depois de criar uma database chamada "clients" nele:

~~~properties
spring.datasource.url=jdbc:postgresql://localhost:5432/clients
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.show-sql=true

# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto = update

~~~

_E a mágica acontece..._

![Inside code world](https://res.cloudinary.com/practicaldev/image/fetch/s--StRkI7Ze--/c_imagga_scale,f_auto,fl_progressive,h_420,q_auto,w_1000/https://codesandtags.github.io/blog/static/0c42bdee6c2a7e213cacc2b33ac3039c/a0304/hero.webp)

## HANDS ON!

Depois que sua mente aqueceu e você já entrou no flow, já sentiu a energia do binário te chamar para o mundo dos bytes, sua mente já está a mil. Você cria todos os Packages que serão necessários, porque você já sabe onde quer chegar. Sua estrutura de Pastas será a seguinte:

> \>src/main/java
>> \>zup.challengeapi.springboot
>>> \>zup.challengeapi.springboot.controller
>>> \>zup.challengeapi.springboot.exception
>>> \>zup.challengeapi.springboot.model
>>> \>zup.challengeapi.springboot.repository
>>> \>zup.challengeapi.springboot.service
>>> \>zup.challengeapi.springboot.validation

E você começa pela base da coisa: O Model!

## MODEL

Sua aplicação precisará receber os dados de Nome, CPF, email e data de nascimento. O contexto é que esses dados serão usados para abrir uma conta no banco.

Com isso em mente, sua Classe Model deverá ter uma cara de _Pessoa._ Com esses atributos declarados, prontos para serem usados em outras camadas da aplicação:

~~~Java
@Entity
@Table(name = "clients")
public class Person {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "firstName")
	@NotBlank(message = "Name is required", groups = PostValidation.class)
	private String name;

	@Column(name = "email", unique = true)
	@NotBlank(message = "Email is required", groups = PostValidation.class)
	@Email(message = "Email should be valid", groups = {PostValidation.class, PutValidation.class})
	private String email;
	
	@Column(name = "cpf", unique = true)
	@NotBlank(message = "CPF is required", groups = PostValidation.class)
	@CPF(message = "CPF should be valid", groups = PostValidation.class)
	private String cpf;
	
	
	@Column(name = "birthday")
	@Past(message = "Birthday should be valid", groups = {PostValidation.class, PutValidation.class})
	@NotNull(message = "Birthday is required", groups = PostValidation.class)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthday;

	public Person(String name, String email, String cpf, LocalDate birthday) {
		super();
		this.name = name;
		this.email = email;
		this.cpf = cpf;
		this.birthday = birthday;
	}
	
	public Person(){
		super();
	}

    // here comes necessary getters and setters...
}
~~~

_Mas o que são todos esses @ espalhados na classe?_

Eles são anotações. Algumas vem do JPA, outras vem das nossas dependências de validação. Elas tem diferentes funções:

> @Entity

Essa vem do JPA. Ela é anotada nas classes que devem ser persistidas no banco de dados. É uma forma de dizer para o JPA que aquela classe também representa uma entidade que irá ser armazenada, no nosso caso, no postgreSql. Se você não estivesse usando o JPA para mapear suas classes que também representam entidades, teria-se que realizar algum outro tipo de tratamento para converter o Objeto (sua classe Java), e seu modelo relacional entendível pelo banco de dados.

_Enfim, você como um bom Ninja, vai utilizar o JPA e focar sua mana em outras partes do código :)_

A anotação @Entity está sendo usada em combinação com a @Table para demarcar o nome da nossa tabela do banco de dados. E se você olhar bem para as anotações feitas em cima dos atributos, perceberá a @Column. Já consegue perceber o que ela faz, né? Isso mesmo! Ela mapeia o nome da coluna da tabela (marcada com o @Table) em que aquele atributo deverá ser persistido.

>Nota: não se preocupe com criar previamente essa tabela com essas colunas no banco de dados. O spring fará isso para você :) _just like magic._

Dentro da nossa classe, cada atributo tem um conjunto de anotações para mapear que magias queremos invocar do Spring:

>@Id

Cada linha do nosso banco de dados relacional vai precisar de uma _Primary Key._ Essa anotação em combinação com @GeneratedValue(strategy = GenerationType.IDENTITY) vai cuidar de gerar esses Ids para nós e lidar com o que for necessário para deixá-los numa sequência lógica e válida.


>@JsonFormat

Essa anotação serve para controlar o formato da nossa data de nascimento. Quando a API receber uma data válida, o ideal é que para um nascimento, fique registrado no banco apenas o dia, o mês e o ano. Não precisamos gravar a hora, os minutos e os segundos. 

_Ah não ser que o baby Yoda peça para nós, aí ninguém consegue negar :c_

E todas as outras anotações ainda não mencionadas são parte do _Bean Validation._ A função delas já é realmente validar se algum campo segue as regras convencionais. Por exemplo, você sabia que existe uma fórmula matemática para verificar os 11 digitos do CPF? Mesmo se souber, não precisa se preocupar em implementar: o @CPF da nossa classe vai fazer isso com maestria, assim como o @NotBlank não vai deixar passar campos nulos ou em branco, e o @Past só vai aceitar datas anteriores a data atual. Alguns bancos (financeiros mesmo), não aceitavam contas sendo abertas por menores de 18 anos, mas nos tempos atuais isso pode ser feito pelos pais para começar a acumular moedas de ouro para um jovem padawan em crescimento. Por isso, nossa API não vai precisar verificar a maioridade.

_E com isso, nosso Model está pronto para ser usado em outros lugares :)_ 
<img src="https://www.pngfind.com/pngs/m/49-499330_naruto-clipart-happy-transparent-naruto-chibi-png-png.png" style="width:50px;"/>

## REPOSITORY

Essa interface tem uma implementação bem simples, mas fundamental para o bom funcionamento da nossa aplicação:

~~~~Java
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

}

~~~~

Ela vai extender o JpaRepository, passando o tipo Person e o tipo Long do Id sendo usado. Usaremos ela para acessar os métodos responsáveis pelas operações CRUD da API. E a anotação @Repository é uma especialização de @Component. Ela é necessária para tornar nossa classe um Bean Spring. E isso vai tornar possível trabalhar com o conceito de Injeção de dependência, que veremos logo mais. _Por enquanto, sem spoilers :)_

## VALIDATION

Nessa pasta, teremos apenas dois arquivos: PostValidation e PutValidation. Eles só vão servir para agrupar certas validações diferentes para os métodos POST e PUT logo mais. O motivo? Imagine que no método POST, a API realmente não deve aceitar nenhum atributo nulo ou vazio. Mas isso não precisa acontecer no PUT. A operação pode querer alterar apenas o email de um cliente, mas o nome dela se manteve o mesmo, por exemplo. E tudo bem com isso.

A combinação é a seguinte:

~~~Java
public interface PostValidation {

}
~~~
~~~Java
public interface PutValidation {

}
~~~

Com as duas interfaces criadas em arquivos separados, basta separar as validações com o atributo "groups", na anotação de validação do nosso Model. Se você foi um discipulo de Yoda atencioso, percebeu isso no Model lá em cima:

> groups = PostValidation.class

ou

> groups = PutValidation.class

Falta apenas uma anotação para fazer tudo isso funcionar: a @Validated, que usaremos no Controller. _Lembre-se disso._

## EXCEPTION

Ansioso para ver as camadas Controller e Service, não é? Estamos quase lá. Antes, precisamos apenas entender nossas exceções. Porque queremos proteger nossa API e enviar o tipo de Status code adequado para cada situação. E essas classes serão de grande utilidade nisso.

> ErrorDetails

Essa é uma classe simples, que usaremos para construir as mensagens de erro que serão lançadas em certos pontos críticos da nossa API. É interessante fazê-la para que a aplicação de front-end receba as exceções tratadas em um formato padronizado e mais amigável, e assim consiga mostrar ao usuário final caso algum dado inserido não esteja 100% válido.

~~~Java

public class ErrorDetails {
	private Date timestamp;
	private String message;
	private String details;
	
	
	public ErrorDetails(Date timestamp, String message, String details) {
		super();
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
	}
	
    // here comes getters and setters...
	
}
~~~
Ela tem apenas 3 atributos, um para marcar o momento que o Erro foi encontrado, uma para a mensagem, e outro para detalhes sobre o erro.

> ConstraintException

Aqui já temos uma exceção que extende a nossa famosa RuntimeException. Ela será usada, como o nome sugere, em momentos em que os dados enviados pela aplicação cliente quebrem alguma regra do nosso model. O que causaria um problema na hora de inserir esse dados no postgreSql. E na nossa API, o banco de dados vai receber apenas dados que ele realmente deverá e conseguirá armazenar :)

~~~Java
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ConstraintException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	public ConstraintException(String msg) {
		super(msg);
	}
	
	public ConstraintException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
~~~
Note que temos algo novo aqui. Vindo diretamente do nosso Spring Framework, a anotação @ResponseStatus vai indicar o Http Status Code para "Bad Request" quando esse tipo de Exceção for lançada. Assim, a aplicação cliente saberá de forma mais clara o que não conversou bem com nossas regras de API.

>ResourceNotFoundException

Essa exceção será lançada em casos onde um ID será passado na requisição para encontrar algum valor, mas não haverá tal valor no banco de dados. É uma forma técnica de dizer _"Não achei nada para esse ID que você me pediu. Tem certeza que é esse?"_

~~~Java
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String message) {
		super(message);
	}
}

~~~

O Status code aqui vai ser bem intuitivo: Not Found. Ou seja, não encontrei nada. Zero. Tente novamente por favor. Vê se tu digitou certo isso aí :(

>GlobalExceptionHandler

Por fim, temos o carinha que vai cuidar de pegar cada uma dessas classes de Exceção, montar a mensagem de erro, e devolver para a aplicação cliente. Nosso GlobalExceptionHandler é uma classe com 3 métodos, um para cada uma das nossas exceções personalizadas, e um extra para tratar exceções gerais.

~~~Java
@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request){
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(ConstraintException.class)
	public ResponseEntity<?> constraintException(ConstraintException ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
	}
	
}
~~~

O que torna possível manipular três anotações de @ExpectionHandler na mesma classe é a anotação @ControllerAdvice. A principal vantagem dessa anotação é controlar todos os nossos ExceptionHandlers em um mesmo arquivo, de forma clara, centrada e organizada.

## CONTROLLER

Com os passos anteriores prontos para uso, podemos finalmente olhar para o no nosso mapeamento de endpoints  dentro da classe PersonController:

~~~Java
@RestController
@RequestMapping("/api/v1/")
public class PersonController {
	
	@Autowired
	private PersonService personService;
	
	@GetMapping("clients")
	public List<Person> getAllClients(){
		return this.personService.findAll();
	}
	
	@GetMapping("/clients/{id}")
	public ResponseEntity<Person> getPersonById(@PathVariable(value = "id") Long personId) throws ResourceNotFoundException {
		Person person = personService.findById(personId);
		return ResponseEntity.ok().body(person);
	}
	
	@PostMapping("clients")
	@ResponseStatus(HttpStatus.CREATED)
	public Person createPerson(@Validated(PostValidation.class) @RequestBody Person person, BindingResult br) throws ConstraintException, Exception {
		if (br.hasErrors())
			throw new ConstraintException(br.getAllErrors().get(0).getDefaultMessage());
		return this.personService.createPerson(person);
			
	}
	
	@PutMapping("clients/{id}")
	public ResponseEntity<Person> updatePerson(@PathVariable(value = "id") Long personId, 
			@Validated(PutValidation.class) @RequestBody Person personDetails, BindingResult br) throws ResourceNotFoundException, ConstraintException, Exception {
		if (br.hasErrors())
			throw new ConstraintException(br.getAllErrors().get(0).getDefaultMessage());
		Person person = personService.updatePerson(personId, personDetails);
		return ResponseEntity.ok(person);
	}
	
	@DeleteMapping("clients/{id}")
	public Map<String, Boolean> deletePerson(@PathVariable(value = "id") Long personId) throws ResourceNotFoundException{
		return personService.deletePerson(personId);

	}

}
~~~

Começamos anotando-a com @RestController e criando a base do mapeamento "api/v1", que pode ajudar nossa API em atualizações futuras.

O @RestController faz a função combinada da anotação @Controller e @ResponseBody. Em suma, com o @RequestMapping("/api/v1") elas servem para dizer para nossa aplicação que essa é nossa classe Controller que vai responder as requisições direcionadas para esse endpoint.

E como nossa API vai ser RESTful, temos que nos preocupar com os 4 pilares de um CRUD: Create, Read, Update e Delete.

>READ

A funcionalidade "Read" da nossa API é implementada nas funções getAllClients() e getPersonById(). A primeira deve retornar todos os clientes do banco cadastrados no postgreSql. A segunda, o cliente com o Id que receberemos por parâmetro.

~~~Java
@GetMapping("clients")
	public List<Person> getAllClients(){
		return this.personService.findAll();
	}
	
@GetMapping("/clients/{id}")
public ResponseEntity<Person> getPersonById(@PathVariable(value = "id") Long personId) throws ResourceNotFoundException {
    Person person = personService.findById(personId);
    return ResponseEntity.ok().body(person);
}
~~~

Existe um carinha ainda não abordado aqui: personService. Ele será abordado no próximo tópico. Mas quer um spoiler? É ele quem manipula nosso PersonRepository, chamando os métodos herdados do JPA de criar, ler, atualizar ou deleter registros do nosso banco de dados, depois de se certificar que os dados repassados pelo Controller atendem as regras necessárias.

A única anotação nova aqui é a @PathVariable. Ela serve para ensinar o Spring que nesse endpoint esperamos um parâmetro do tipo Long chamado personId, e ele vai vir no final do Path "api/v1/clientes/id".

>CREATE

A função "Create" convencional de qualquer CRUD é representada aqui pela função createPerson(). Nela acontecem algumas coisas interessantes:

~~~Java
@PostMapping("clients")
@ResponseStatus(HttpStatus.CREATED)
public Person createPerson(@Validated(PostValidation.class) @RequestBody Person person, BindingResult br) throws ConstraintException, Exception {
    if (br.hasErrors())
        throw new ConstraintException(br.getAllErrors().get(0).getDefaultMessage());
    return this.personService.createPerson(person);
        
}
~~~

A primeira, é que diferente do nosso GET, apenas para mostrar que existem dois modos de fazer a mesma coisa, ao invés de enviar o status da resposta no return com:

~~~Java
return ResponseEntity.ok().body(person);
~~~

Nós declaramos o tipo de retorno padrão Created(201) com a anotação @ResponseStatus. 

Em seguida, a anotação @Validated(PostValidation.class) diz para o Spring que o @RequestBody, que vai ser do tipo Person, deve passar pelas nossas validações definidas no Model. Lembra? E capturamos o resultado dessas validações no objeto do tipo BidingResult :)

Com isso, basta verificar se algo ocorreu de errado logo no inicio do método. Se houver algum erro de validação, lançaremos uma das nossas exceções personalizadas com a mensagem de erro que virá das anotações de validação definidas no model. Por exemplo, se nosso RequestBody não tiver o campo CPF, definimos que a mensagem a ser retornada deve ser "CPF should be valid".

Por fim, se tudo ocorreu bem nas validações, usamos nosso personService para invocar o método createPerson, e no fim das contas, se tudo ocorrer bem na camada de serviço também, isso vai parar no banco de dados.

>UPDATE

Nosso Update, implementado pelo método updatePerson, é bem similar ao createPerson. Para ser honesto, ele é como uma junção do GET e do POST. Nada novo por aqui. O que muda mesmo é na camada de serviço.

~~~Java
@PutMapping("clients/{id}")
public ResponseEntity<Person> updatePerson(@PathVariable(value = "id") Long personId, 
        @Validated(PutValidation.class) @RequestBody Person personDetails, BindingResult br) throws ResourceNotFoundException, ConstraintException, Exception {
    if (br.hasErrors())
        throw new ConstraintException(br.getAllErrors().get(0).getDefaultMessage());
    Person person = personService.updatePerson(personId, personDetails);
    return ResponseEntity.ok(person);
}
~~~

>DELETE

E o delete é o mais simples de todos.

~~~Java
@DeleteMapping("clients/{id}")
public Map<String, Boolean> deletePerson(@PathVariable(value = "id") Long personId) throws ResourceNotFoundException{
    return personService.deletePerson(personId);
}
~~~

Revisando, o Controller é o responsável por redirecionar as requisições recebidas em cada endpoint para o método certo da nossa camada Service. Lá, vamos ver agora o que acontece.

## SERVICE

Muita coisa acontece aqui. Começamos anotando o que essa nossa classe representa para o Spring: @Service.

A anotação @AutoWired feita para o nosso PersonRepository traz para nossa aplicação o conceito de injeção de dependência. Lembra da anotação @Repository que transforma nossa classe PersonRepository em um Bean Spring? A @Autowired faz o equivalente a criar uma instância desse objeto para uso dentro do nosso PersonService. De fora para dentro, estamos tornando as funções do PersonRepository usáveis dentro da nossa camada de serviço. Porque ela depende disso para salvar os dados no postgreSql. Se é de fora para dentro, imagine como se o Spring usasse uma grande injeção(AutoWired), com uma maravilhosa vacina(JPA) sendo aplicada no corpo de alguém. Essa vacina será usada pelo organismo dessa pessoa. Você acabou de entender o necessário sobre o conceito de _injeção de dependência_ :)

~~~Java
@Service
public class PersonService {
	
	@Autowired
	private PersonRepository personRepository;
	
	public List<Person> findAll() {
		return personRepository.findAll();
	}
	
	public Person findById(Long personId) throws ResourceNotFoundException {
		Person person = personRepository.findById(personId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found for this id: " + personId));
		return person;
	}
	
	public Person createPerson(Person person) throws ConstraintException, Exception {
		try {
			return this.personRepository.save(person);
		}
		catch(DataIntegrityViolationException e) {
			throw new ConstraintException("Constraint Problem - " + e.getMostSpecificCause().getMessage());
		}
		catch(Exception e) {
			throw new Exception("Unknown error :( but i know some details that could help: " + e.getMessage()); 
		}
	}
	
	public Person updatePerson(Long personId, Person personDetails) throws Exception {
		Person person = personRepository.findById(personId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found for this id: " + personId));
		
		try {
			if(personDetails.getCpf() != null) {
				throw new DataIntegrityViolationException("CPF can not be updated");
			}
			if(personDetails.getEmail() != null && personDetails.getEmail() != "") {
				person.setEmail(personDetails.getEmail());
			}
			if(personDetails.getName() != null && personDetails.getName() != "") {
				person.setName(personDetails.getName());
			}
			if(personDetails.getBirthday() != null) {
				person.setBirthday(personDetails.getBirthday());
			}

			return this.personRepository.save(person);
			
		}
		catch(DataIntegrityViolationException e) {
			throw new ConstraintException("Constraint Problem - " + e.getMostSpecificCause().getMessage());
		}
		catch(Exception e) {
			throw new Exception("Unknown error :( but i know some details that could help: " + e.getMessage()); 
		}
	}
	
	public Map<String, Boolean> deletePerson(Long personId) throws ResourceNotFoundException{
		Person person = personRepository.findById(personId)
				.orElseThrow(() -> new ResourceNotFoundException("Client not found for this id :: " + personId));
		
		this.personRepository.delete(person);
		
		Map<String, Boolean> response = new HashMap<>();
		response.put("deleted", Boolean.TRUE);
		
		return response;
	}

}
~~~

Os métodos do nosso PersonService são declarados de forma muito semelhante aos que vimos no Controller. Vamos partir para as peculiaridades.

O método findAll() é exatamente igual ao nosso getAllClients() do Controller. E se nosso Service tivesse apenas ele, não seria necessária uma camada de serviço. Poderíamos ter chamado o método FindAll() do JPA direto no controlador.

Mas observe o método findById():


~~~Java
public Person findById(Long personId) throws ResourceNotFoundException {
    Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found for this id: " + personId));
    return person;
}
~~~

Apesar de bem parecido, é aqui que nossa ResourceNotFoundException é lançada, caso não exista nenhuma entrada no banco de dados para o _personId_ fornecido.

A diferença se torna ainda mais visível no createPerson():

~~~Java
public Person createPerson(Person person) throws ConstraintException, Exception {
    try {
        return this.personRepository.save(person);
    }
    catch(DataIntegrityViolationException e) {
        throw new ConstraintException("Constraint Problem - " + e.getMostSpecificCause().getMessage());
    }
    catch(Exception e) {
        throw new Exception("Unknown error :( but i know some details that could help: " + e.getMessage()); 
    }
}
~~~

Aqui, nosso try/catch cuida de capturar algum DataIntegrityViolationException e lançar isso como nosso ConstraintException, informando o problema para a aplicação cliente. Isso será lançado, por exemplo, em CPFs ou Emails duplicados. Para enviar algum tipo de mensagem mais elegante para a aplicação cliente quando não conhecermos o erro, Uma exception é também lançada apenas com o getMessage(), que na maioria das vezes é o suficiente para o front-end entender o que aconteceu.

O método responsável pelo update também tem alguns pontos que merecem atenção:

~~~Java
public Person updatePerson(Long personId, Person personDetails) throws Exception {
    Person person = personRepository.findById(personId)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found for this id: " + personId));
    
    try {
        if(personDetails.getCpf() != null) {
            throw new DataIntegrityViolationException("CPF can not be updated");
        }
        if(personDetails.getEmail() != null && personDetails.getEmail() != "") {
            person.setEmail(personDetails.getEmail());
        }
        if(personDetails.getName() != null && personDetails.getName() != "") {
            person.setName(personDetails.getName());
        }
        if(personDetails.getBirthday() != null) {
            person.setBirthday(personDetails.getBirthday());
        }

        return this.personRepository.save(person);
        
    }
    catch(DataIntegrityViolationException e) {
        throw new ConstraintException("Constraint Problem - " + e.getMostSpecificCause().getMessage());
    }
    catch(Exception e) {
        throw new Exception("Unknown error :( but i know some details that could help: " + e.getMessage()); 
    }
}
~~~

De diferente do Post, temos apenas a verificação de nulo desse lado para que os dados que já foram salvos de forma correta no banco de dados não sejam substituidos por campos nulos ou vazios, já que o Bean Validation não olha para isso no método PUT. Caso a requisição tenha esses campos não nulos nem vazios, então eles passarão pela validação do Bean Validation :)

E o CPF dispara um erro dizendo que CPFs não podem ser alterados. Porque já que o baby yoda não disse que na terra dele as pessoas mudam de CPF regularmente, seguimos a regra convencional de que isso é um número que as pessoas criam uma vez só durante toda a vida.

_Mas nunca se sabe quando o Yodinha vai usar a força para mudar os CPFs dos seres humanos, não é?_

Por último, mas não menos importante, nossa função de Deletar não traz nada novo: apenas devolvemos um HashMap informando a aplicação cliente que o delete foi executado, caso o personId seja realmente encontrado no banco de dados. 

## MISSION COMPLETED!

E com isso, temos uma API RESTful funcional, pronta para ser usada por alguma aplicação front-end, seja ela web ou mobile :)

