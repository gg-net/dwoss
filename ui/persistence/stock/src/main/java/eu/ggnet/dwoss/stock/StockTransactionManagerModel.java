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
package eu.ggnet.dwoss.stock;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import eu.ggnet.dwoss.stock.entity.StockTransaction;
import eu.ggnet.dwoss.stock.entity.StockTransactionStatusType;
import eu.ggnet.dwoss.stock.entity.StockTransactionType;

import lombok.Getter;
import lombok.Setter;

public class StockTransactionManagerModel extends AbstractListModel {

    @Getter
    @Setter
    private StockTransactionStatusType statusType = StockTransactionStatusType.PREPARED;

    @Getter
    @Setter
    private StockTransactionType transactionType = StockTransactionType.TRANSFER;
    
    private List<StockTransaction> transactions = new ArrayList<>();

    @Override
    public int getSize() {
        return transactions.size();
    }

    @Override
    public Object getElementAt(int index) {
        return transactions.get(index);
    }

    public void setTransactions(List<StockTransaction> transactions) {
        this.transactions = transactions;
        fireContentsChanged(this, 0, transactions.size());
    }
    
    public void clear() {
        int oldSize = this.transactions.size();
        this.transactions = new ArrayList<>();
        if (oldSize == transactions.size()) return;
        fireIntervalRemoved(this, 0, oldSize);
    }
    
    public void addAll(List<StockTransaction> transactions) {
        int oldEnd = this.transactions.size();
        this.transactions.addAll(transactions);
        fireIntervalAdded(this, oldEnd, this.transactions.size());
    }
    
    public void remove(StockTransaction transaction) {
        transactions.remove(transaction);
        fireContentsChanged(this, 0, transactions.size());
    }
    
}
