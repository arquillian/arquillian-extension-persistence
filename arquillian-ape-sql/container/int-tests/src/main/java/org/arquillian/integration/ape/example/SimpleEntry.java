package org.arquillian.integration.ape.example;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "simple")
public class SimpleEntry implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
}
