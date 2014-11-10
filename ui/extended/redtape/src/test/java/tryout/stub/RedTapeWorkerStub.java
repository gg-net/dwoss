package tryout.stub;

import eu.ggnet.dwoss.redtape.entity.SalesProduct;
import eu.ggnet.dwoss.redtape.entity.Dossier;
import eu.ggnet.dwoss.redtape.entity.Document;
import eu.ggnet.dwoss.redtape.entity.Address;

import java.util.ArrayList;
import java.util.List;

import eu.ggnet.dwoss.event.AddressChange;
import eu.ggnet.dwoss.redtape.RedTapeWorker;
import eu.ggnet.dwoss.redtape.state.CustomerDocument;
import eu.ggnet.statemachine.StateTransition;

import eu.ggnet.dwoss.util.UserInfoException;

/**
 *
 * @author pascal.perau
 */
public class RedTapeWorkerStub implements RedTapeWorker {

    @Override
    public Dossier create(long customerId, boolean dispatch, String arranger) {
        return null;
    }

    @Override
    public Document update(Document doc, Integer destination, String arranger) throws RuntimeException {
        throw new RuntimeException("Not Yet implemented");
    }

    @Override
    public Document revertCreate(Document doc) {
        throw new RuntimeException("Not Yet implemented");
    }

    @Override
    public Addresses requestAdressesByCustomer(long customerId) {
        throw new RuntimeException("Not Yet implemented");
    }

    @Override
    public void delete(Dossier dos) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<StateTransition<CustomerDocument>> getPossibleTransitions(CustomerDocument cdoc) {
        return new ArrayList<>();
    }

    @Override
    public Address requestAddressByDescription(String description) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Document stateChange(CustomerDocument cdoc, StateTransition<CustomerDocument> transition, String arranger) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dossier updateComment(Dossier dossier, String comment) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SalesProduct createSalesProduct(String partNo) throws UserInfoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateAllDocumentAdresses(AddressChange event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toDetailedHtml(long dossierId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
