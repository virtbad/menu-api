package ch.virtbad.menuapi.database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User {

    @Id
    private String tag;

    private String firstname;

    private String lastname;

    private Date joined;

    public User(String tag, String firstname, String lastname) {
        this.tag = tag;
        this.firstname = firstname;
        this.lastname = lastname;
        this.joined = new Date();
    }

    @OneToMany(mappedBy = "id.user", cascade = CascadeType.REMOVE)
    private List<Vote> votes = new ArrayList<>();

}
