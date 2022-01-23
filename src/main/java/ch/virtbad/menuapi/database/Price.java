package ch.virtbad.menuapi.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private int id;

    public Price(String tag, float price) {
        this.tag = tag;
        this.price = price;
    }

    @Column(length = 4)
    private String tag;

    private float price;
}
