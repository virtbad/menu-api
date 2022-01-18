package ch.virtbad.menuapi.database;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class User {

    @Id
    private String tag;

    private String firstname;

    private String lastname;

}
