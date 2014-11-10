package eu.ggnet.dwoss.price;

import javax.ejb.Remote;

import eu.ggnet.dwoss.price.engine.PriceEngineResult;

import eu.ggnet.dwoss.util.FileJacket;
import eu.ggnet.dwoss.util.UserInfoException;

@Remote
public interface Importer {

    /**
     * Imports the Price Management from an XLS file with a defined form.
     * The Form is as follows
     * <ul>
     * <li>Column 1 (A) = Refurbished Id, Type:Integer</li>
     * <li>Column 2 (C) = Manufacturer PartNo, Type:String</li>
     * <li>Column 4 (E) = Retailer Price, Type:Double</li>
     * <li>Column 7 (H) = Customer Price without Tax, Type:Double</li>
     * <li>Column 9 (J) = Set/Unset PartNoFixed Price, Type:Integer</li>
     * <li>Column 10 (K) = Warranty Id, Type:Integer</li>
     * </ul>
     *
     * @param jacket  the file in a jacket
     * @param monitor an optional monitor
     * @throws UserInfoException
     */
    void fromXls(FileJacket jacket, String arranger) throws UserInfoException;

    /**
     * Uses the Engine in the Background, and imports all Prices direct.
     * <p/>
     * @param arranger the arranger.
     */
    void direct(String arranger);

    /**
     * Store one price result in the Price Engine.
     * <p/>
     * @param pers     the price result
     * @param comment  a optional comment
     * @param arranger the arranger
     */
    void store(PriceEngineResult pers, String comment, String arranger);
}
