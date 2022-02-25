package ch.virtbad.menuapi.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Indexed
@Entity
public class Menu {

    @Id
    @GeneratedValue()
    @Type(type="uuid-char")
    @Setter(AccessLevel.NONE)
    private UUID id;

    public Menu(String title, String description, Date date, int channel, int label, List<Price> prices) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.channel = channel;
        this.label = label;
        this.prices = prices;
    }

    @Column(length = 512)
    @Field
    private String title;

    @Column(length = 512)
    @Field
    private String description;

    private Date date;

    private int channel;

    private int label;

    @ManyToMany
    private List<Price> prices;

    @JsonProperty
    public int getVoteBalance() {
        return votes.stream().mapToInt((vote) -> vote.isDirection() ? 1 : -1).sum();
    }

    @JsonIgnore
    @OneToMany(mappedBy = "id.menu", cascade = CascadeType.REMOVE)
    private List<Vote> votes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "menu", cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Menu menu = (Menu) o;

        return
            channel == menu.channel &&
            label == menu.label &&
            title.equals(menu.title) &&
            description.equals(menu.description) &&
            date.getTime() == menu.date.getTime() &&
            prices.size() == menu.prices.size() && prices.containsAll(menu.prices);
    }
}
