package tryout.stub;

import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.redtapext.ee.UniversalSearcherOperation;
import eu.ggnet.dwoss.util.Tuple2;

/**
 *
 * @author pascal.perau
 */
public class UniversalSearcherStub extends UniversalSearcherOperation {

    @Override 
    public String findCustomer(int id) {
        throw new IllegalArgumentException("Not yet implemented");
    }

    @Override
    public List<Tuple2<Long, String>> searchCustomers(String search) {
        ArrayList<Tuple2<Long, String>> result = new ArrayList<>();
        result.add(new Tuple2<>(1l, "Customer One"));
        result.add(new Tuple2<>(2l, "Customer Two"));
        return result;
    }

    @Override
    public List<Tuple2<Long, String>> findCustomers(String firma, String vorname, String nachname, String email) {
        ArrayList<Tuple2<Long, String>> result = new ArrayList<>();
        result.add(new Tuple2<>(1l, "Id=1, Search was:<br />firma=" + firma + "<br />vorname=" + vorname + "<br />nachname=" + nachname + "<br />email=" + email));
        return result;
    }

    @Override
    public List<Tuple2<Long, String>> searchUnits(String search) {
        throw new IllegalArgumentException("Not yet implemented");
    }
}
