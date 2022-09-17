package ru.bmstu.dvasev.rsoi.cicd.storage.person.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Validated
@Accessors(chain = true)
public class PersonModel
        implements Serializable {

    private static final long serialVersionUID = -3864465451041362329L;

    @Min(1)
    @NotNull
    private Integer id;

    @NotEmpty
    @Size(max = 255)
    private String name;
    @Min(0)
    private Integer age;
    @Size(max = 255)
    private String address;
    @Size(max = 255)
    private String work;
}
