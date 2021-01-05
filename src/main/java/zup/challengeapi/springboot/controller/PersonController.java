package zup.challengeapi.springboot.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import zup.challengeapi.springboot.exception.ConstraintException;
import zup.challengeapi.springboot.exception.ResourceNotFoundException;
import zup.challengeapi.springboot.model.Person;
import zup.challengeapi.springboot.service.PersonService;
import zup.challengeapi.springboot.validation.PostValidation;
import zup.challengeapi.springboot.validation.PutValidation;

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
