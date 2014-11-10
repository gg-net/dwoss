package eu.ggnet.dwoss.util.interactiveresult;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.util.UserInfoException;

import lombok.*;

/**
 * A Result for afterwards questioning of the result.
 *
 * @author oliver.guenther
 */
public class Result<T> implements Serializable {

    @Setter
    @Getter
    private T payload;

    private final List<YesNoQuestion> questions = new ArrayList<>();

    public Result(T payload) {
        this.payload = payload;
    }

    public void add(YesNoQuestion q) {
        if (q == null) return;
        questions.add(q);
    }

    public T request(InteractionListener listener) throws UserInfoException {
        for (YesNoQuestion question : questions) {
            question.ask(listener);
        }
        return payload;
    }
}
