package zup.challengeapi.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zup.challengeapi.springboot.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

}
