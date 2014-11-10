package eu.ggnet.dwoss.mandator.api.service;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Set;

import lombok.Data;

/**
 *
 * @author pascal.perau
 */
@Data
public class ClientLocation implements Serializable{

    private final Set<InetAddress> inetAdresses;

}
