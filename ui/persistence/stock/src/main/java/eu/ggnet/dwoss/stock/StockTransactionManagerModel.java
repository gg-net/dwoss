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
