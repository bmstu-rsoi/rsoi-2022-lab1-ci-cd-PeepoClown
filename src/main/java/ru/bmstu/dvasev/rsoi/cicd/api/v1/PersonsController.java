package ru.bmstu.dvasev.rsoi.cicd.api.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.bmstu.dvasev.rsoi.cicd.api.v1.model.ErrorResponse;
import ru.bmstu.dvasev.rsoi.cicd.api.v1.model.PersonCreateOrUpdateRq;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.PersonService;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.model.PersonModel;

import javax.validation.Valid;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        path = "api/v1/persons",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE
)
public class PersonsController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<List<PersonModel>> getAllPersons() {
        log.debug("Received new getAllPersons request");
        var response = personService.getAll();
        log.debug("Return getAllPersons response: {}", response);
        return ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonCreateOrUpdateRq request) {
        log.debug("Received new createPerson request: {}", request);
        var response = personService.create(toPersonModel(request));
        log.debug("Return createPerson response id: '{}'", response);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response)
                .toUri();
        return created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> findPersonById(@PathVariable(name = "id") String id) {
        log.debug("Received new findPersonById request by id: '{}'", id);
        var response = personService.findById(parseInt(id));
        log.debug("Return findPersonById response: {}", response.orElse(null));
        if (response.isEmpty()) {
            var errorResponse = new ErrorResponse()
                    .setMessage(NOT_FOUND.getReasonPhrase())
                    .setDescription(format("Failed to find person with id '%s'", id));
            return status(NOT_FOUND).body(errorResponse);
        }
        return ok().body(response.get());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deletePersonById(@PathVariable(name = "id") String id) {
        log.debug("Received new deletePersonById request by id: '{}'", id);
        personService.deleteById(parseInt(id));
        log.debug("Return deletePersonById response");
        return status(NO_CONTENT).body(format("Success to delete person with id '%s'", id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updatePersonById(@PathVariable(name = "id") String id,
                                              @Valid @RequestBody PersonCreateOrUpdateRq request) {
        log.debug("Received new updatePersonById request with id '{}' and body: {}", id, request);
        var response = personService.updateById(parseInt(id), toPersonModel(request));
        log.debug("Return updatePersonById response: {}", response.orElse(null));
        if (response.isEmpty()) {
            var errorResponse = new ErrorResponse()
                    .setMessage(NOT_FOUND.getReasonPhrase())
                    .setDescription(format("Failed to find person with id '%s'", id));
            return status(NOT_FOUND).body(errorResponse);
        }
        return ok().body(response.get());
    }

    @NonNull
    private PersonModel toPersonModel(@NonNull PersonCreateOrUpdateRq request) {
        return new PersonModel()
                .setName(request.getName())
                .setAge(request.getAge())
                .setAddress(request.getAddress())
                .setWork(request.getWork());
    }
}
