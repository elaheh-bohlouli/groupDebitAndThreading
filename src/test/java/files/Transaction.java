package files;

import java.util.ArrayList;
import java.util.List;


public class Transaction {
    private String debtorDepositNumber;
    private String creditorDepositNumber;
    private Integer amount;

    public Transaction(String debtorDepositNumber, String creditorDepositNumber, Integer amount) {
        this.debtorDepositNumber = debtorDepositNumber;
        this.creditorDepositNumber = creditorDepositNumber;
        this.amount = amount;
    }

    public String getDebtorDepositNumber() {
        return debtorDepositNumber;
    }

    public void setDebtorDepositNumber(String debtorDepositNumber) {
        this.debtorDepositNumber = debtorDepositNumber;
    }

    public String getCreditorDepositNumber() {
        return creditorDepositNumber;
    }

    public void setCreditorDepositNumber(String creditorDepositNumber) {
        this.creditorDepositNumber = creditorDepositNumber;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void doTransaction(String path) {
        List list = new ArrayList();
        list.add(debtorDepositNumber);
        list.add(creditorDepositNumber);
        list.add(String.valueOf(amount));
        String join = UtilFileOperation.join(list);
        UtilFileOperation.createFile(path);
        UtilFileOperation.AppendToFile(path, join);
    }
}
