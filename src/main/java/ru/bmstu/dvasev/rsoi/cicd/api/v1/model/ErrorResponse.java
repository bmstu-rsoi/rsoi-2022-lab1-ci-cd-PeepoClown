package ru.bmstu.dvasev.rsoi.cicd.api.v1.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = -9197020130204988178L;

    private String message;
    private String description;
}
