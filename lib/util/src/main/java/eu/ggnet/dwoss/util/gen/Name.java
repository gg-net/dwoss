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
