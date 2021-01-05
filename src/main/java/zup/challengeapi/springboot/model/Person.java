package zup.challengeapi.springboot.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.br.CPF;

import zup.challengeapi.springboot.validation.PostValidation;
import zup.challengeapi.springboot.validation.PutValidation;

@Entity
@Table(name = "clients")
public class Person {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "firstName")
	@NotBlank(message = "First name is required", groups = PostValidation.class)
	private String firstName;

	@Column(name = "lastName")
	@NotBlank(message = "Last name is required", groups = PostValidation.class)
	private String lastName;

	@Column(name = "email", unique = true)
	@NotBlank(message = "Email is required", groups = PostValidation.class)
	@Email(message = "Email should be valid", groups = {PostValidation.class, PutValidation.class})
	private String email;
	
	@Column(name = "cpf", unique = true)
	@NotBlank(message = "CPF is required", groups = PostValidation.class)
	@CPF(message = "CPF should be valid", groups = PostValidation.class)
	private String cpf;

	public Person(String firstName, String lastName, String email, String cpf) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.cpf = cpf;
	}
	
	public Person(){
		super();
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

}
