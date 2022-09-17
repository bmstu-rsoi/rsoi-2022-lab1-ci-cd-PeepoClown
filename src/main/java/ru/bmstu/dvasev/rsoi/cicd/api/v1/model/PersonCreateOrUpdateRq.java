package ru.bmstu.dvasev.rsoi.cicd.api.v1.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@Accessors(chain = true)
public class PersonCreateOrUpdateRq
        implements Serializable {

    private static final long serialVersionUID = 5537633871696312617L;

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
