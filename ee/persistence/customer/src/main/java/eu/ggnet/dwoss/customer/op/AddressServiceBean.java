package eu.ggnet.dwoss.customer.op;

import eu.ggnet.dwoss.mandator.api.value.DefaultCustomerSalesdata;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

import javax.ejb.*;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import eu.ggnet.dwoss.customer.api.AddressService;
import eu.ggnet.dwoss.customer.eao.CustomerEao;
import eu.ggnet.dwoss.customer.priv.ConverterUtil;

import eu.ggnet.dwoss.event.AddressChange;
import eu.ggnet.dwoss.rules.AddressType;

import static eu.ggnet.dwoss.rules.AddressType.INVOICE;

/**
 * Really necessary?
 * <p>
 * @author pascal.perau
 */
@Stateless
public class AddressServiceBean implements AddressService {

    @Inject
    private CustomerEao customerEao;

    @Inject
    private Mandator mandator;

    @Inject
    private DefaultCustomerSalesdata salesData;

    @Inject
    private Event<AddressChange> adressChangeEvent;

    @Override
    public String defaultAddressLabel(long customerId, AddressType type) {
        if ( type == INVOICE )
            return ConverterUtil.convert(customerEao.findById(customerId), mandator.getMatchCode(), salesData).toInvoiceAddress();
        else
            return ConverterUtil.convert(customerEao.findById(customerId), mandator.getMatchCode(), salesData).toShippingAddress();
    }

    @Override
    public void notifyAddressChange(AddressChange changeEvent) {
        adressChangeEvent.fire(changeEvent);
    }

}
