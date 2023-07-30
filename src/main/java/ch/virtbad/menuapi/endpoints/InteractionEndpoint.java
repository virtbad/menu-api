package ch.virtbad.menuapi.endpoints;

import ch.virtbad.menuapi.auth.LoginManager;
import ch.virtbad.menuapi.database.Comment;
import ch.virtbad.menuapi.database.Menu;
import ch.virtbad.menuapi.database.User;
import ch.virtbad.menuapi.database.Vote;
import ch.virtbad.menuapi.database.repositories.CommentRepository;
import ch.virtbad.menuapi.database.repositories.MenuRepository;
import ch.virtbad.menuapi.database.repositories.VoteRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This endpoint handles specific requests regarding the interactions with a menu
 */
@RequestMapping("/menu/{id}")
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(value = "custom.interactions.enabled", havingValue = "true")
public class InteractionEndpoint {

    private final LoginManager login;

    private final MenuRepository menus;
    private final VoteRepository votes;
    private final CommentRepository comments;

    @AllArgsConstructor
    @NoArgsConstructor
    private static class VotingObject {
        private int direction;
    }

    /**
     * This request allows one to see whether he voted on a menu.
     */
    @GetMapping("/vote")
    public VotingObject hasVoted(@RequestHeader HttpHeaders headers, @PathVariable UUID id) {
        User user = login.login(headers);
        Menu menu = menus.findById(id).orElseThrow(MenuEndpoint.MenuNotFound::new);

        Optional<Vote> optVote = votes.findByIdMenuAndIdUser(menu, user);
        return optVote.map(vote -> new VotingObject(vote.isDirection() ? 1 : -1)).orElseGet(() -> new VotingObject(0));
    }

    /**
     * With this request, a menu can be voted for.
     */
    @PutMapping("/vote")
    public void vote(@RequestBody VotingObject body, @RequestHeader HttpHeaders headers, @PathVariable UUID id) {
        User user = login.login(headers);
        if (user.isBanned()) throw new Banned();

        Menu menu = menus.findById(id).orElseThrow(MenuEndpoint.MenuNotFound::new);

        if (votes.existsByIdMenuAndIdUser(menu, user)) {
            if (body.direction == 0) votes.removeByIdMenuAndIdUser(menu, user);
            else {
                Vote vote = votes.findByIdMenuAndIdUser(menu, user).get();

                if (vote.isDirection() != (body.direction > 0)) {
                    votes.removeByIdMenuAndIdUser(menu, user);
                    votes.save(new Vote(user, menus.findById(id).get(), body.direction > 0));
                }
            }
        } else if (body.direction != 0) {
            votes.save(new Vote(user, menus.findById(id).get(), body.direction > 0));
        }

    }

    /**
     * With this request, all comments for a menu can be fetched.
     */
    @GetMapping("/comment")
    public List<Comment> getComments(@RequestHeader HttpHeaders headers, @PathVariable UUID id) {
        login.login(headers);
        if (!menus.existsById(id)) throw new MenuEndpoint.MenuNotFound();

        return comments.findCommentsByMenu_IdOrderByCreated(id);
    }

    /**
     * With this request, a comment can be deleted.
     */
    @DeleteMapping("/comment/{cid}")
    public void deleteComment(@RequestHeader HttpHeaders headers, @PathVariable UUID id, @PathVariable UUID cid) {
        User user = login.login(headers);
        if (user.isBanned()) throw new Banned();

        Comment comment = comments.findById(cid).orElseThrow(CommentNotFound::new);

        if (!user.isAdmin() && !user.getTag().equals(comment.getUser().getTag())) throw new NoRightToChange();

        comments.delete(comment);
    }

    /**
     * With this request a comment can be posted.
     */
    @PostMapping("/comment")
    public CommentResponse addComment(@RequestBody CommentRequest comment, @RequestHeader HttpHeaders headers, @PathVariable UUID id) {
        User user = login.login(headers);
        if (user.isBanned()) throw new Banned();

        Menu menu = menus.findById(id).orElseThrow(MenuEndpoint.MenuNotFound::new);

        if(!comment.validate()) throw new NotAllProvided();

        Comment target = new Comment(user, menu, comment.title, comment.content, comment.rating);
        target = comments.save(target);

        return new CommentResponse(target.getId());
    }
    public static class CommentRequest {
        private float rating;
        private String title;
        private String content;

        private boolean validate() {
            return rating >= 1 && rating <= 5 && title != null && title.length() <= 64 && content != null && content.length() <= 256;
        }
    }
    @AllArgsConstructor
    public static class CommentResponse {
        private UUID id;
    }

    /**
     * With this request, a comment can be edited.
     */
    @PutMapping("/comment/{cid}")
    public void changeComment(@RequestBody CommentRequest commentReq, @RequestHeader HttpHeaders headers, @PathVariable UUID id, @PathVariable UUID cid) {
        User user = login.login(headers);
        if (user.isBanned()) throw new Banned();

        Comment comment = comments.findById(cid).orElseThrow(CommentNotFound::new);

        if (!user.getTag().equals(comment.getUser().getTag())) throw new NoRightToChange();
        if(!commentReq.validate()) throw new NotAllProvided();

        comment.setContent(commentReq.content);
        comment.setTitle(commentReq.title);
        comment.setRating(commentReq.rating);
        comment.setEdited(true);

        comments.save(comment);
    }

    /**
     * This exception is thrown when not all features of a body are provided.
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Not all fields were provided or they were too large.")
    private static class NotAllProvided extends RuntimeException { }

    /**
     * This exception is thrown when a comment couldn't be found.
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "This comment was not found.")
    private static class CommentNotFound extends RuntimeException { }

    /**
     * This exception is thrown when a user has not the required rights to do sth.
     */
    @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "This user is not allowed to do that.")
    private static class NoRightToChange extends RuntimeException { }

    /**
     * This exception is thrown when a user attempts to write, but is banned.
     */
    @ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Sorry, but you appear to be banned.")
    private static class Banned extends RuntimeException { }
}
