package eu.ggnet.dwoss.misc.ee;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.assist.Customers;
import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.progress.*;
import eu.ggnet.dwoss.redtape.ee.assist.RedTapes;

import com.querydsl.jpa.impl.JPAQuery;

import static eu.ggnet.dwoss.common.api.values.AddressType.INVOICE;
import static eu.ggnet.dwoss.common.api.values.AddressType.SHIPPING;
import static eu.ggnet.dwoss.customer.ee.entity.QCustomer.customer;
import static eu.ggnet.dwoss.redtape.ee.entity.QDossier.dossier;

/**
 *
 * @author pascal.perau
 */
@Stateless
public class CustomerAdressLabelMergeOperation implements Serializable {

    private final static Logger L = LoggerFactory.getLogger(CustomerAdressLabelMergeOperation.class);

    //needed... damnit
    @Inject
    @Customers
    private EntityManager customerEm;

    @Inject
    @RedTapes
    private EntityManager redTapeEm;

    public List<Long> findNonAddressLabelCustomers() {
        return new JPAQuery<>(customerEm)
                .select(customer.id)
                .from(customer)
                .where(customer.addressLabels.isEmpty()).fetch();
    }

    public List<Long> findNonDossierCustomers() {
        List<Long> dossierCustomers = new JPAQuery<>(redTapeEm)
                .select(dossier.customerId).distinct()
                .from(dossier).fetch();

        return new JPAQuery<>(customerEm)
                .select(customer.id)
                .from(customer)
                .where(customer.id.notIn(dossierCustomers))
                .fetch();
    }

    public Map<String, List<PicoCustomer>> mergeCustomerAfterAddressLabel(Collection<Long> customerIds, IMonitor m) {

        Map<String, List<PicoCustomer>> violations = new HashMap<>();
        String noDossiers = "No Dossiers for Customer";
        violations.put(noDossiers, new ArrayList<>());

        for (Long customerId : customerIds) {
            Customer customer = customerEm.find(Customer.class, customerId);

            L.info("--Merging Customer: " + customer.getId());
            m.message("Merging Customer: " + customer.getId());

            //safety if addresslabels are already present
            if ( customer.getAddressLabels().isEmpty() ) {
                L.debug("---No Label present, try creating from old customer");
                Company company = customer.getCompanies().stream().filter(Company::isPrefered).findFirst().orElse(null);
                Contact contact = customer.getContacts().stream().filter(Contact::isPrefered).findFirst().orElse(null);

                L.debug("---Customer company: " + (company == null ? company : company.getId() + " - " + company.getName()));
                L.debug("---Customer contact: " + (contact == null ? contact : contact.getId() + " - " + contact.toFullName()));

                Address invoice = (contact == null
                                   ? company.getAddresses().stream().reduce((first, second) -> second).orElse(null)
                                   : contact.prefered(INVOICE));

                //build invoice label
                AddressLabel invoiceLabel = new AddressLabel(company, contact, invoice, INVOICE);
                L.debug("---Created Invoice Label: " + invoiceLabel);

                //build shipping label only if neccesary
                AddressLabel shippingLabel = null;
                Address shipping = (contact == null ? invoice : contact.prefered(SHIPPING));
                if ( shipping != null ) {
                    shippingLabel = new AddressLabel(company, contact, shipping, SHIPPING);
                    L.debug("---Created Shipping Label: " + invoiceLabel);
                } else {
                    L.debug("---Shipping address was null");
                }

                //add violation info if neccesary and continue to next customer
                if ( invoiceLabel.getViolationMessage() != null || (shippingLabel != null && shippingLabel.getViolationMessage() != null) ) {
                    String violation = invoiceLabel.getViolationMessage();
                    //add violation vor invoice label
                    L.info("----Invoice violation: " + violation);
                    if ( violations.get(violation) == null ) violations.put(violation, new ArrayList<>());
                    violations.get(violation).add(customer.toPico());

                    //check for and add violation for shipping label
                    if ( shippingLabel != null ) {
                        violation = shippingLabel.getViolationMessage();
                        if ( violations.get(violation) == null ) violations.put(violation, new ArrayList<>());
                        L.info("----Shipping violation: " + violation);
                        violations.get(violation).add(customer.toPico());
                    }
                    m.worked(1, "Failed merge for Customer: " + customer.getId());
                    continue;
                }

                //add addresslabel and update customer
                customer.getAddressLabels().add(invoiceLabel);
                if ( shippingLabel != null ) customer.getAddressLabels().add(shippingLabel);
                customerEm.merge(customer);
                L.info("---Updated Customer: " + customer.getId());
                m.worked(1, "Merged Customer: " + customer.getId());
            } else {
                L.info("---Label already present");
                m.worked(1, "Skipped Customer: " + customer.getId());
            }
        }

        m.finish();
        return violations;
    }

}
