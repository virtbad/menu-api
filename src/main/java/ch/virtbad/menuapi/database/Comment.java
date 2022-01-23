package ch.virtbad.menuapi.database;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Setter
@Entity
public class Comment {

    @Id
    @GeneratedValue()
    @Type(type="uuid-char")
    @Setter(AccessLevel.NONE)
    private UUID id;

    public Comment(User user, Menu menu, String title, String content, Float rating) {
        this.user = user;
        this.menu = menu;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.created = new Date();
    }

    @JoinColumn(name = "user")
    @ManyToOne
    private User user;

    @JoinColumn(name = "menu")
    @ManyToOne
    private Menu menu;

    @Column(length = 64)
    private String title;

    @Column(length = 255)
    private String content;

    private Float rating;

    private Date created;

}
