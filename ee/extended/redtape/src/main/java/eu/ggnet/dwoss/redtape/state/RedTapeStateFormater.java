package eu.ggnet.dwoss.redtape.state;

import eu.ggnet.statemachine.State;
import eu.ggnet.statemachine.StateCharacteristic;
import eu.ggnet.statemachine.StateFormater;

/**
 * A Formatter for an aggregated View of all Characteristics of a State.
 * <p/>
 * @author oliver.guenther
 */
public class RedTapeStateFormater implements StateFormater<CustomerDocument> {

    @Override
    public String toHtml(State<CustomerDocument> state) {
        String pre = "<div><center><u>";
        String suf = "</u></center></div>";
        if ( state.getType() == State.Type.START ) {
            pre = pre + "<b>";
            suf = "</b>" + suf;
        }
        if ( state.getType() == State.Type.END ) {
            pre = pre + "<i>";
            suf = "</i>" + suf;
        }
        String result = "<html>" + pre + state.getName() + suf;
        RedTapeStateCharacteristicElementCollection intersection = RedTapeStateCharacteristicElementCollection.intersection(state.getCharacteristics());
        RedTapeStateCharacteristicElementCollection union = RedTapeStateCharacteristicElementCollection.union(state.getCharacteristics());
        union.removeAll(intersection);
        result += (intersection.getFormatedDispatches().isEmpty() ? "" : intersection.getFormatedDispatches())
                + (!intersection.getFormatedDispatches().isEmpty() && !union.getFormatedDispatches().isEmpty() ? " + " : "")
                + (union.getFormatedDispatches().isEmpty() ? "" : unionFormat(union.getFormatedDispatches().toString())) + " - ";
        result += (intersection.getTypes().isEmpty() ? "" : intersection.getTypes())
                + (!intersection.getTypes().isEmpty() && !union.getTypes().isEmpty() ? " + " : "")
                + (union.getTypes().isEmpty() ? "" : unionFormat(union.getTypes().toString())) + "<br />";
        result += (intersection.getPaymentMethods().isEmpty() ? "" : intersection.getPaymentMethods())
                + (!intersection.getPaymentMethods().isEmpty() && !union.getPaymentMethods().isEmpty() ? " + " : "")
                + (union.getPaymentMethods().isEmpty() ? "" : unionFormat(union.getPaymentMethods().toString())) + "<br />";
        result += (intersection.getCustomerFlags().isEmpty() ? "[*] + " : intersection.getCustomerFlags())
                + (!intersection.getCustomerFlags().isEmpty() && !union.getCustomerFlags().isEmpty() ? " + " : "")
                + (union.getCustomerFlags().isEmpty() ? "" : unionFormat(union.getCustomerFlags().toString())) + "<br />";
        result += (intersection.getConditions().isEmpty() ? "[*] + " : intersection.getConditions())
                + (!intersection.getConditions().isEmpty() && !union.getConditions().isEmpty() ? " + " : "")
                + (union.getConditions().isEmpty() ? "" : unionFormat(union.getConditions().toString())) + "<br />";
        result += (intersection.getDirectives().isEmpty() ? "" : intersection.getDirectives())
                + (!intersection.getDirectives().isEmpty() && !union.getDirectives().isEmpty() ? " + " : "")
                + (union.getDirectives().isEmpty() ? "" : unionFormatNl(union.getDirectives().toString()));
        result += "</html>";
        return result;
    }

    private String unionFormat(String in) {
        return in.replaceAll("\\[", "\\(").replaceAll("\\]", "\\)").replaceAll(",", " |");
    }

    private String unionFormatNl(String in) {
        return in.replaceAll("\\[", "\\(").replaceAll("\\]", "\\)").replaceAll(",", "<br /> |");
    }

    
    @Override
    public String toToolTipHtml(State<CustomerDocument> state) {
        String pre = "<div><center><u>";
        String suf = "</u></center></div>";
        if ( state.getType() == State.Type.START ) {
            pre = pre + "<b>";
            suf = "</b>" + suf;
        }
        if ( state.getType() == State.Type.END ) {
            pre = pre + "<i>";
            suf = "</i>" + suf;
        }
        String result = "<html>" + pre + state.getName() + suf;
        for (StateCharacteristic sc : state.getCharacteristics()) {
            RedTapeStateCharacteristic dc = (RedTapeStateCharacteristic)sc;
            result += (dc.isDispatch() ? "DISPATCH - " : "PICKUP - ") + "<b>" + dc.getType() + "</b>," + dc.getPaymentMethod() + ", " + dc.getDirective()
                    + (dc.getConditions().isEmpty() ? "" : "<br />" + dc.getConditions())
                    + (dc.getCustomerFlags().isEmpty() ? "" : "<br />" + dc.getCustomerFlags()) + "<br />";
        }
        result += "</html>";
        return result;
    }
}
