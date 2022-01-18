package ch.virtbad.menuapi.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Menu {

    @Id
    @GeneratedValue()
    @Type(type="uuid-char")
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Column(length = 512)
    private String title;

    @Column(length = 512)
    private String description;

    private Date date;

    private int channel;

    private int label;

    @OneToMany
    private List<Price> prices;
}
