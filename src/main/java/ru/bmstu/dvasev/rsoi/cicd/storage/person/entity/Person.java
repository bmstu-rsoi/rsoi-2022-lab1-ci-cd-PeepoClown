package ru.bmstu.dvasev.rsoi.cicd.storage.person.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@Accessors(chain = true)
@Entity(name = "person")
@Table(schema = "public", name = "t_person")
public class Person
        implements Serializable {

    private static final long serialVersionUID = -167072508838291289L;

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "t_person_id_seq", strategy = SEQUENCE)
    @SequenceGenerator(name = "t_person_id_seq", sequenceName = "t_person_id_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "address")
    private String address;

    @Column(name = "work")
    private String work;
}
