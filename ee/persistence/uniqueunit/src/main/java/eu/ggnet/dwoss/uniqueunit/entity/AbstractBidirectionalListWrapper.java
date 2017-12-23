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
package eu.ggnet.dwoss.uniqueunit.entity;

import java.util.*;

/**
 * Abstract class for bidirection handling of entities.
 *
 * @author oliver.guenther
 */
// TODO: Kandiat für Util.
public abstract class AbstractBidirectionalListWrapper<T> implements List<T> {

    private final List<T> elems;

    public AbstractBidirectionalListWrapper(List<T> elems) {
        this.elems = elems;
    }

    /**
     * Wrapps all operation for the bidirectional work.
     *
     * @param e   the element that is either added or removed
     * @param add if ture a add is called, otherwise remove.
     */
    protected abstract void update(T e, boolean add);

    @Override
    public boolean add(T e) {
        if ( e == null ) return false;
        update(e, true);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if ( o == null ) return false;
        try {
            update((T)o, false);
        } catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    @Override
    public int size() {
        return elems.size();
    }

    @Override
    public boolean isEmpty() {
        return elems.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return elems.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableCollection(elems).iterator(); // disable remove on iterator.
    }

    @Override
    public Object[] toArray() {
        return elems.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return elems.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return elems.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        new ArrayList<>(c).forEach((product) -> add(product)); // Need extra collection, as the add migth change the underlying collection.
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Not supported, as the order of the collection is not relevant");
    }

    @Override
    @SuppressWarnings(value = "element-type-mismatch")
    public boolean removeAll(Collection<?> c) {
        new ArrayList<>(c).forEach((product) -> remove(product));
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @SuppressWarnings(value = "element-type-mismatch")
    public void clear() {
        new ArrayList<>(elems).forEach(p -> remove(p)); // Need extra collection, as we change the underlying collection.
    }

    @Override
    public T get(int index) {
        return elems.get(index);
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("Not supported, as the order of the collection is not relevant");
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Not supported, as the order of the collection is not relevant");
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException("Not supported, as the order of the collection is not relevant");
    }

    @Override
    public int indexOf(Object o) {
        return elems.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return elems.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return Collections.unmodifiableList(elems).listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return Collections.unmodifiableList(elems).listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return Collections.unmodifiableList(elems).subList(fromIndex, toIndex);
    }

}
