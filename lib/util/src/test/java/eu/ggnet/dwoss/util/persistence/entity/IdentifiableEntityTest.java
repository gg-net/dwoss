package eu.ggnet.dwoss.util.persistence.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author oliver.guenther
 */
public class IdentifiableEntityTest {

    private class IdentifiableEntityImpl extends IdentifiableEntity {

        private long id;

        public IdentifiableEntityImpl() {
        }

        public IdentifiableEntityImpl(long id) {
            this.id = id;
        }

        @Override
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "BaseImpl{" + "id=" + id + '}';
        }

    }

    @Test
    public void testEquals() {
        IdentifiableEntityImpl b1 = new IdentifiableEntityImpl();
        IdentifiableEntityImpl b2 = new IdentifiableEntityImpl();
        assertEquals(b1, b1);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode() == b2.hashCode());
        assertEquals(b1.getId(), b2.getId());
        b2.setId(10);
        assertFalse(b1.equals(b2));
        assertFalse(b1.hashCode() == b2.hashCode());
        b1.setId(10);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

}
