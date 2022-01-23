package ch.virtbad.menuapi.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
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
    private String title;

    @Column(length = 512)
    private String description;

    private Date date;

    private int channel;

    private int label;

    @OneToMany
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
}
