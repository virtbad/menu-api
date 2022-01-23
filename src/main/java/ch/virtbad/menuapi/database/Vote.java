package ch.virtbad.menuapi.database;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NoArgsConstructor
@AssociationOverrides({
        @AssociationOverride(name = "id.user", joinColumns = @JoinColumn(name = "user")),
        @AssociationOverride(name = "id.menu", joinColumns = @JoinColumn(name = "menu"))
})
public class Vote {

    @EmbeddedId
    private VoteJoinKey id;

    @Getter @Setter
    private boolean direction;
    @Getter @Setter
    private Date created;


    public Vote(User user, Menu menu) {
        this.id = new VoteJoinKey(user, menu);
    }

    public Vote(User user, Menu menu, boolean direction) {
        this(user, menu);
        this.direction = direction;
        this.created = new Date();
    }

    public User getUser() {
        return id.user;
    }

    public Menu getMenu() {
        return id.menu;
    }


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class VoteJoinKey implements Serializable {
        @ManyToOne
        private User user;
        @ManyToOne
        private Menu menu;
    }

}
