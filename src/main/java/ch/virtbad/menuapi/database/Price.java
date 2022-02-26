package ch.virtbad.menuapi.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;

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

    // Must use DECIMAL type because it does not work with float or whatever
    @Column(columnDefinition = "decimal(8,2)")
    private float price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return Float.compare(price1.price, price) == 0 && tag.equals(price1.tag);
    }
}
