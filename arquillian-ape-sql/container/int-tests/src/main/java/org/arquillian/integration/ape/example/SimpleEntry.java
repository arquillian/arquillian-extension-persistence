package org.arquillian.integration.ape.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "simple")
public class SimpleEntry implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

}
