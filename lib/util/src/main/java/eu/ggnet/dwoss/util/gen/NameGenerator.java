/* 
 * Copyright (C) 2014 pascal.perau
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
package eu.ggnet.dwoss.util.gen;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;

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
                String all = IOUtils.toString(in, "UTF-8");
                List<String> data = sources.get(resource);
                for (StringTokenizer st = new StringTokenizer(all, "\n"); st.hasMoreTokens();) {
                    String s = st.nextToken();
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
        return new Name(
                first.get(R.nextInt(first.size())),
                namesLast.get(R.nextInt(namesLast.size())),
                gender);
    }

    public String makeCompanyName() {
        StringBuilder sb = new StringBuilder();
        sb.append(namesLast.get(R.nextInt(namesLast.size()))).append(" ").append(businessEntities.get(R.nextInt(businessEntities.size())));
        return sb.toString();
    }

    public GeneratedAddress makeAddress() {
        return new GeneratedAddress(
                streets.get(R.nextInt(streets.size())),
                R.nextInt(300),
                String.format("%05d", R.nextInt(100000)),
                towns.get(R.nextInt(towns.size())));
    }

    public static void main(String[] args) {
        NameGenerator n = new NameGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(n.makeAddress());
            System.out.println(n.makeCompanyName());
        }
    }
}
