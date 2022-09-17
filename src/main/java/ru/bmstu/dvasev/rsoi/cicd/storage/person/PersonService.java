package ru.bmstu.dvasev.rsoi.cicd.storage.person;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.dao.PersonRepository;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.entity.Person;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.model.PersonModel;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public List<PersonModel> getAll() {
        var persons = personRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(toList());
        log.debug("Successfully found list of persons: {}", persons);
        return persons;
    }

    @NonNull
    @Transactional
    public Integer create(@NonNull PersonModel person) {
        var createdPerson = toModel(personRepository.save(toEntity(person)));
        log.debug("Successfully created person: {}", createdPerson);
        return createdPerson.getId();
    }

    @Transactional(readOnly = true)
    public Optional<PersonModel> findById(@NonNull Integer personId) {
        return findEntityById(personId)
                .map(this::toModel)
                .map(foundPerson -> {
                    log.debug("Successfully found person by id '{}': {}", personId, foundPerson);
                    return of(foundPerson);
                }).orElse(empty());
    }

    @Transactional
    public void deleteById(@NonNull Integer personId) {
        findEntityById(personId).ifPresent(personRepository::delete);
        log.debug("Successfully delete person by id '{}", personId);
    }

    @Transactional
    public Optional<PersonModel> updateById(@NonNull Integer personId, @NonNull PersonModel person) {
        return findEntityById(personId)
                .map(foundPerson -> {
                    ofNullable(person.getName()).ifPresent(foundPerson::setName);
                    ofNullable(person.getAge()).ifPresent(foundPerson::setAge);
                    ofNullable(person.getAddress()).ifPresent(foundPerson::setAddress);
                    ofNullable(person.getWork()).ifPresent(foundPerson::setWork);

                    var updatedPerson = toModel(personRepository.save(foundPerson));
                    log.debug("Successfully update person wit id '{}' and params '{}': {}", personId, person, updatedPerson);
                    return of(updatedPerson);
                }).orElse(empty());
    }

    private Optional<Person> findEntityById(@NonNull Integer personId) {
        var foundPerson = personRepository.findById(personId);
        if (foundPerson.isEmpty()) {
            log.warn("Failed to find person by id '{}", personId);
        }
        return foundPerson;
    }

    @NonNull
    private PersonModel toModel(@NonNull Person entity) {
        return new PersonModel()
                .setId(entity.getId())
                .setName(entity.getName())
                .setAge(entity.getAge())
                .setAddress(entity.getAddress())
                .setWork(entity.getWork());
    }

    @NonNull
    private Person toEntity(@NonNull PersonModel model) {
        return new Person()
                .setName(model.getName())
                .setAge(model.getAge())
                .setAddress(model.getAddress())
                .setWork(model.getWork());
    }
}
