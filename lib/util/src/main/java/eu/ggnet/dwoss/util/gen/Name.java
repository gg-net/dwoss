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

/**
 * A Name
 *
 * @author oliver.guenther
 */
public class Name {

    public static enum Gender {
        MALE,FEMALE
    }

    private final String first;

    private final String last;

    private final Gender gender;

    public Name(String first, String last, Gender gender) {
        this.first = first;
        this.last = last;
        this.gender = gender;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public Gender getGender() {
        return gender;
    }

    @Override
    public String toString() {
        return "Name{" + "first=" + first + ", last=" + last + ", gender=" + gender + '}';
    }

}
