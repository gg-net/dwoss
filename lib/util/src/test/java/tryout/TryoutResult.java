package tryout;

import org.junit.Test;

import eu.ggnet.dwoss.util.UserInfoException;
import eu.ggnet.dwoss.util.UserInfoException.Type;
import eu.ggnet.dwoss.util.interactiveresult.Result;
import eu.ggnet.dwoss.util.interactiveresult.YesNoQuestion;

/**
 *
 * @author oliver.guenther
 */
public class TryoutResult {

    @Test
    public void testResult() {

        // Server
        String payload = "Hallo";
        Result<String> r = new Result<>(payload);
        r.add(new YesNoQuestion("Eine Frage", "Willst du wirklich", Type.INFO).onNoException("Schade"));

        try {
            // Client
            String result = r.request(new SwingInteraction());
            System.out.println("Normal:" + result);
        } catch (UserInfoException ex) {
            System.out.println("Error:" + ex.getMessage());
        }

    }

}
