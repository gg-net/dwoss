/*
 * Copyright (C) 2017 GG-Net GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.dwoss.server.web;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.ggnet.dwoss.customer.ee.entity.*;
import eu.ggnet.dwoss.customer.ee.entity.projection.PicoCustomer;
import eu.ggnet.dwoss.mandator.api.value.Mandator;

import lombok.Getter;
import lombok.Setter;

import eu.ggnet.dwoss.mandator.ee.Mandators;
import eu.ggnet.dwoss.misc.ee.CustomerAdressLabelMergeOperation;
import eu.ggnet.dwoss.progress.MonitorFactory;
import eu.ggnet.dwoss.progress.SubMonitor;

/**
 *
 * @author oliver.guenther
 */
@Named
@ManagedBean
@SessionScoped
public class Server implements Serializable {

    private final Logger L = LoggerFactory.getLogger(Server.class);

    @Getter
    @Setter
    private String prefix = "";

    @Inject
    private MonitorFactory monitorFactory;

    @Getter
    private List<Long> nonAddressLabelCustomers;

    @Getter
    private List<Long> nonDossierCustomers;

    @Inject
    private CustomerAdressLabelMergeOperation customerMergeOperation;

    @EJB
    private Mandators mandatorSupport;

    @Getter
    private SubMonitor monitor;

    @Getter
    private Map<String, List<PicoCustomer>> mergeViolations;

    @Getter
    @Setter
    private String selectedKey;

    @Getter
    private int maxWork = 0;

    @PostConstruct
    public void init() {
        System.out.println("Search for non addresslabeled customers");
        L.info("Search for non addresslabeled customers");
        nonAddressLabelCustomers = customerMergeOperation.findNonAddressLabelCustomers();
        System.out.println("Found " + nonAddressLabelCustomers.size() + " entries");
        L.info("Found {} entries", nonAddressLabelCustomers.size());

        L.info("Search for non dossier customers");
        nonDossierCustomers = customerMergeOperation.findNonDossierCustomers();
        L.info("Found {} entries", nonDossierCustomers.size());
        monitor = monitorFactory.newSubMonitor("Customer merge not in progress..");
        mergeViolations = new HashMap<>();
    }

    public String getCompanyName() {
        return mandatorSupport.loadMandator().getCompany().getName();
    }

    public Mandator getMandator() {
        return mandatorSupport.loadMandator();
    }

    public void mergeCustomerAfterAddressLabel() {

        Map<String, List<Customer>> violations = new HashMap<>();
        String noDossiers = "No Dossiers for Customer";
        violations.put(noDossiers, new ArrayList<>());

        L.info("-Start customer merge after AddressLabel implementation");
        monitor.message("Merging Customer after AddressLabel implementation...");

        List<Long> collect = new ArrayList<>(nonAddressLabelCustomers);
        collect.removeAll(nonDossierCustomers);

        maxWork = collect.size();

        monitor.setWorkRemaining(collect.size());
        monitor.start();
        mergeViolations = customerMergeOperation.mergeCustomerAfterAddressLabel(collect, monitor);

        monitor.finish();
    }

    public List<Entry<String, List<PicoCustomer>>> violationEntriesAsList() {
        return new ArrayList<>(mergeViolations.entrySet());
    }
}