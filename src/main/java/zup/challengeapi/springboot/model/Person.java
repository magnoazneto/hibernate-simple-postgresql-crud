package zup.challengeapi.springboot.model;

import java.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.br.CPF;

import com.fasterxml.jackson.annotation.JsonFormat;

import zup.challengeapi.springboot.validation.PostValidation;
import zup.challengeapi.springboot.validation.PutValidation;

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


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

}
