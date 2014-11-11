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
package eu.ggnet.dwoss.assembly.sample;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author oliver.guenther
 */
public class UnClosableContext implements Context {

    private Context context;

    public UnClosableContext(Context context) {
        this.context = context;
    }

    @Override
    public Object lookup(Name name) throws NamingException {
        return context.lookup(name);
    }

    @Override
    public Object lookup(String name) throws NamingException {
        return context.lookup(name);
    }

    @Override
    public void bind(Name name, Object obj) throws NamingException {
        context.bind(name, obj);
    }

    @Override
    public void bind(String name, Object obj) throws NamingException {
        context.bind(name, obj);
    }

    @Override
    public void rebind(Name name, Object obj) throws NamingException {
        context.rebind(name, obj);
    }

    @Override
    public void rebind(String name, Object obj) throws NamingException {
        context.rebind(name, obj);
    }

    @Override
    public void unbind(Name name) throws NamingException {
        context.unbind(name);
    }

    @Override
    public void unbind(String name) throws NamingException {
        context.unbind(name);
    }

    @Override
    public void rename(Name oldName, Name newName) throws NamingException {
        context.rename(oldName, newName);
    }

    @Override
    public void rename(String oldName, String newName) throws NamingException {
        context.rename(oldName, newName);
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        return context.list(name);
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return context.list(name);
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return context.listBindings(name);
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return context.listBindings(name);
    }

    @Override
    public void destroySubcontext(Name name) throws NamingException {
        context.destroySubcontext(name);
    }

    @Override
    public void destroySubcontext(String name) throws NamingException {
        context.destroySubcontext(name);
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        return context.createSubcontext(name);
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        return context.createSubcontext(name);
    }

    @Override
    public Object lookupLink(Name name) throws NamingException {
        return context.lookupLink(name);
    }

    @Override
    public Object lookupLink(String name) throws NamingException {
        return context.lookupLink(name);
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        return context.getNameParser(name);
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        return context.getNameParser(name);
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        return context.composeName(name, prefix);
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        return context.composeName(name, prefix);
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return context.addToEnvironment(propName, propVal);
    }

    @Override
    public Object removeFromEnvironment(String propName) throws NamingException {
        return context.removeFromEnvironment(propName);
    }

    @Override
    public Hashtable<?, ?> getEnvironment() throws NamingException {
        return context.getEnvironment();
    }

    @Override
    public void close() throws NamingException {
        // Ignore
    }

    @Override
    public String getNameInNamespace() throws NamingException {
        return context.getNameInNamespace();
    }
}
