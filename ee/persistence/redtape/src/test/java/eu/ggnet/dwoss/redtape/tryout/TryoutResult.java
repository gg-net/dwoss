package eu.ggnet.dwoss.redtape.tryout;

import eu.ggnet.dwoss.core.common.UserInfoException;
import eu.ggnet.dwoss.core.common.UserInfoException.Type;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.Result;
import eu.ggnet.dwoss.redtape.ee.interactiveresult.YesNoQuestion;

/**
 *
 * @author oliver.guenther
 */
public class TryoutResult {

    public static void main(String... args) {

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
