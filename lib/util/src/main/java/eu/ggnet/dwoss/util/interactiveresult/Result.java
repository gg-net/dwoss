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
package eu.ggnet.dwoss.util.interactiveresult;

import java.io.Serializable;
import java.util.*;

import eu.ggnet.dwoss.util.UserInfoException;


/**
 * A Result for afterwards questioning of the result.
 *
 * @author oliver.guenther
 */
public class Result<T> implements Serializable {

    private T payload;

    private final List<YesNoQuestion> questions = new ArrayList<>();

    public Result(T payload) {
        this.payload = payload;
    }

    public void add(YesNoQuestion q) {
        if (q == null) return;
        questions.add(q);
    }

    public T request(InteractionListener listener) throws UserInfoException {
        for (YesNoQuestion question : questions) {
            question.ask(listener);
        }
        return payload;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
    
}
