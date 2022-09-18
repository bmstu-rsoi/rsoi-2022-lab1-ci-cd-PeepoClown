package ru.bmstu.dvasev.rsoi.cicd.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import ru.bmstu.dvasev.rsoi.cicd.api.v1.model.PersonCreateOrUpdateRq;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.dao.PersonRepository;
import ru.bmstu.dvasev.rsoi.cicd.storage.person.entity.Person;

import static java.util.Comparator.comparingInt;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class PersonsControllerTest {

    private static final String BASE_PATH = "/api/v1/persons";
    private static int createPersonRqCounter = 0;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final PersonRepository personRepository;

    @Autowired
    public PersonsControllerTest(MockMvc mockMvc, PersonRepository personRepository) {
        this.mockMvc = mockMvc;
        this.objectMapper = new ObjectMapper();
        this.personRepository = personRepository;
    }

    @Test
    @Order(1)
    public void should_return_bad_request_when_person_create_request_is_invalid() throws Exception {
        var invalidCreatePersonRequest = new PersonCreateOrUpdateRq()
                .setName("")
                .setAge(-1)
                .setAddress("478 Smith Expressway")
                .setWork("Green Inc");

        mockMvc.perform(post(BASE_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCreatePersonRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(0);
    }

    @Test
    @Order(2)
    @SqlGroup(@Sql(value = "classpath:sql_scripts/reset.sql", executionPhase = BEFORE_TEST_METHOD))
    public void should_return_created_when_valid_person_create_request_received() throws Exception {
        var createPersonRequest = new PersonCreateOrUpdateRq()
                .setName("Neoma.Bartoletti51")
                .setAge(31)
                .setAddress("478 Smith Expressway")
                .setWork("Green Inc");

        createPersonRqCounter++;
        mockMvc.perform(post(BASE_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPersonRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, BASE_PATH + "/" + createPersonRqCounter))
                .andExpect(jsonPath("$").doesNotExist());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
        assertEquals(createPersonRqCounter, savedPersons.stream().map(Person::getId).max(comparingInt(Integer::intValue)).get());
    }

    @Test
    @Order(3)
    public void should_return_ok_when_request_for_all_persons_received() throws Exception {
        should_return_created_when_valid_person_create_request_received();
        should_return_created_when_valid_person_create_request_received();
        should_return_created_when_valid_person_create_request_received();
        should_return_created_when_valid_person_create_request_received();

        mockMvc.perform(get(BASE_PATH)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
    }

    @Test
    @Order(4)
    public void should_return_bad_request_when_request_for_get_one_person_invalid() throws Exception {
        final String invalidId = "invalidId";
        mockMvc.perform(get(BASE_PATH + "/" + invalidId)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
    }

    @Test
    @Order(5)
    public void should_return_not_found_request_when_person_not_exist() throws Exception {
        final int invalidId = createPersonRqCounter + 1;
        mockMvc.perform(get(BASE_PATH + "/" + invalidId)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
    }

    @Test
    @Order(6)
    public void should_return_ok_found_request_when_person_exist() throws Exception {
        mockMvc.perform(get(BASE_PATH + "/" + createPersonRqCounter)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createPersonRqCounter));

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
    }

    @Test
    @Order(7)
    public void should_return_bad_request_when_request_for_update_one_person_invalid() throws Exception {
        var invalidUpdatePersonRequest = new PersonCreateOrUpdateRq()
                .setName("")
                .setAge(-1)
                .setAddress("478 Smith Expressway")
                .setWork("Green Inc");

        mockMvc.perform(patch(BASE_PATH + "/" + createPersonRqCounter)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdatePersonRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
        assertNotEquals(-1, savedPersons.get(createPersonRqCounter - 1).getAge());
    }

    @Test
    @Order(8)
    public void should_return_not_found_request_when_update_received_and_person_not_exist() throws Exception {
        var invalidUpdatePersonRequest = new PersonCreateOrUpdateRq()
                .setName("Neoma.Bartoletti51")
                .setAge(41);
        final int invalidId = createPersonRqCounter + 1;

        mockMvc.perform(patch(BASE_PATH + "/" + invalidId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdatePersonRequest)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
        assertNotEquals(41, savedPersons.get(createPersonRqCounter - 1).getAge());
    }

    @Test
    @Order(9)
    public void should_return_ok_request_when_update_received_and_person_exist() throws Exception {
        var invalidUpdatePersonRequest = new PersonCreateOrUpdateRq()
                .setName("Neoma.Bartoletti52")
                .setAge(41);

        mockMvc.perform(patch(BASE_PATH + "/" + createPersonRqCounter)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdatePersonRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
        assertEquals(41, savedPersons.get(createPersonRqCounter - 1).getAge());
        assertEquals("Neoma.Bartoletti52", savedPersons.get(createPersonRqCounter - 1).getName());
    }

    @Test
    @Order(10)
    public void should_return_no_content_when_person_deleted() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + createPersonRqCounter)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").isNotEmpty());

        createPersonRqCounter--;
        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(createPersonRqCounter);
    }

    @Test
    @Order(11)
    @SqlGroup(@Sql(value = "classpath:sql_scripts/reset.sql", executionPhase = BEFORE_TEST_METHOD))
    public void should_return_no_content_when_person_not_deleted() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/" + createPersonRqCounter)
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").isNotEmpty());

        var savedPersons = personRepository.findAll();
        assertThat(savedPersons).hasSize(0);
    }
}
