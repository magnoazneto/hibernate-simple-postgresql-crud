package zup.challengeapi.springboot.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import zup.challengeapi.springboot.exception.ConstraintException;
import zup.challengeapi.springboot.exception.ResourceNotFoundException;
import zup.challengeapi.springboot.model.Person;
import zup.challengeapi.springboot.repository.PersonRepository;

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
			if(personDetails.getFirstName() != null && personDetails.getFirstName() != "") {
				person.setFirstName(personDetails.getFirstName());
			}
			if(personDetails.getLastName() != null && personDetails.getLastName() != "") {
				person.setLastName(personDetails.getLastName());
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
