/*
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther
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
package eu.ggnet.dwoss.core.system.generator;

import java.io.*;
import java.util.*;

/**
 * Generates useful names.
 *
 * @author oliver.guenther
 */
public class NameGenerator {

    private List<String> businessEntities;

    private List<String> namesFemaleFirst;

    private List<String> namesMaleFirst;

    private List<String> namesLast;

    private List<String> streets;

    private List<String> towns;

    private final Random R;

    public NameGenerator() throws RuntimeException {
        R = new Random();
        businessEntities = new ArrayList<>();
        namesFemaleFirst = new ArrayList<>();
        namesMaleFirst = new ArrayList<>();
        namesLast = new ArrayList<>();
        streets = new ArrayList<>();
        towns = new ArrayList<>();
        Map<String, List<String>> sources = new HashMap<>();
        sources.put("de_businesses.txt", businessEntities);
        sources.put("de_names_female_first.txt", namesFemaleFirst);
        sources.put("de_names_male_first.txt", namesMaleFirst);
        sources.put("de_names_last.txt", namesLast);
        sources.put("de_streets.txt", streets);
        sources.put("de_towns.txt", towns);

        for (String resource : sources.keySet()) {
            // load txt files.
            try (InputStream in = this.getClass().getResourceAsStream(resource)) {
                String all = read(in, "UTF-8");
                List<String> data = sources.get(resource);
                for (StringTokenizer st = new StringTokenizer(all, "\n"); st.hasMoreTokens();) {
                    String s = st.nextToken();
                    s = s.replace("\r", ""); // safty net, if names file gets corrupted.
                    data.add(s);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void showSourceData(PrintStream out) {
        if ( out == null ) return;
        out.println("Business Enteties:");
        for (String string : businessEntities) {
            out.println(" " + string);
        }
        out.println("Names Female First:");
        for (String string : namesFemaleFirst) {
            out.println(" " + string);
        }
        out.println("Names Male First:");
        for (String string : namesMaleFirst) {
            out.println(" " + string);
        }
        out.println("Names Last:");
        for (String string : namesLast) {
            out.println(" " + string);
        }
        out.println("Streets:");
        for (String string : streets) {
            out.println(" " + string);
        }
        out.println("Towns:");
        for (String string : towns) {
            out.println(" " + string);
        }
    }

    public Name makeName() {
        boolean female = R.nextBoolean();
        Name.Gender gender = Name.Gender.MALE;
        if ( female ) gender = Name.Gender.FEMALE;
        List<String> first = (female ? namesFemaleFirst : namesMaleFirst);
        return new Name.Builder()
                .setFirst(first.get(R.nextInt(first.size())))
                .setLast(namesLast.get(R.nextInt(namesLast.size())))
                .setGender(gender).build();
    }

    public String makeCompanyName() {
        StringBuilder sb = new StringBuilder();
        sb.append(namesLast.get(R.nextInt(namesLast.size())))
                .append(" ")
                .append(businessEntities.get(R.nextInt(businessEntities.size())));
        return sb.toString();
    }

    public GeneratedAddress makeAddress() {
        return new GeneratedAddress.Builder()
                .setStreet(streets.get(R.nextInt(streets.size())))
                .setNumber(R.nextInt(300))
                .setPostalCode(String.format("%05d", R.nextInt(100000)))
                .setTown(towns.get(R.nextInt(towns.size())))
                .build();
    }

    public static void main(String[] args) {
        NameGenerator n = new NameGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(n.makeAddress());
            System.out.println(n.makeCompanyName());
        }
    }

    // Copied from IOUtils
    // TODO: Replace with Files.copy
    private String read(InputStream input, String encoding) throws IOException {
        final int EOF = -1;
        final int DEFAULT_BUFFER_SIZE = 1024 * 4;

        try (final StringWriter sw = new StringWriter();
                final InputStreamReader in = new InputStreamReader(input, "UTF-8")) {
            int n;
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            while (EOF != (n = in.read(buffer))) {
                sw.write(buffer, 0, n);
            }
            return sw.toString();
        }
    }

}
