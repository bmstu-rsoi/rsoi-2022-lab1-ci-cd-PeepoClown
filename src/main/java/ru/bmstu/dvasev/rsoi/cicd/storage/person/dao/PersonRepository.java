package ru.bmstu.dvasev.rsoi.cicd.storage.person.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.entity.Person;

@Repository
public interface PersonRepository
        extends JpaRepository<Person, Integer> {
}
