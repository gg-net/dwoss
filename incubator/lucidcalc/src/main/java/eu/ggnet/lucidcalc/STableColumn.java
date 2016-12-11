/* 
 * Copyright (C) 2014 GG-Net GmbH - Oliver Günther.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; If not, see <http://www.gnu.org/licenses/>.
 */
package eu.ggnet.lucidcalc;

/**
 * A great way to build a headline.
 * Template.getHeadLine().add(...).add(...).....
 */
public class STableColumn {

    private String head;
    private Integer size;
    private SAction action;
    private CFormat format;

    public STableColumn() {
        this(null, null, null);
    }

    public STableColumn(String head) {
        this(head,null,null);
    }

    public STableColumn(String head,CFormat format) {
        this(head,null,format);
    }

    public STableColumn(String head,Integer size) {
        this(head,size,null);
    }

    public STableColumn(String head, Integer size, CFormat format) {
        this.head = head;
        this.size = size;
        this.format = format;
    }

    public STableColumn setHead(String head) {
        this.head = head;
        return this;
    }

    public STableColumn setSize(Integer size) {
        this.size = size;
        return this;
    }

    public STableColumn setAction(SAction action) {
        this.action = action;
        return this;
    }

    public STableColumn setFormat(CFormat format) {
        this.format = format;
        return this;
    }

    public CFormat getFormat() {
        return format;
    }

    public SAction getAction() {
        return action;
    }

    public String getHead() {
        return head;
    }

    public Integer getSize() {
        return size;
    }

}
