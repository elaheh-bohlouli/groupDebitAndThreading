import java.util.ArrayList;
import java.util.List;

public class DebitProcessorThread extends Thread {

    private List<DebitPerRecord> debitPerRecordList = new ArrayList<>();
    private DebitPerRecord debtorItem;

    public DebitProcessorThread(DebitPerRecord debtorItem, List<DebitPerRecord> debitPerRecordList) {
        this.debitPerRecordList = debitPerRecordList;
        this.debtorItem = debtorItem;
    }

    @Override
    public void run() {
        for (DebitPerRecord creditorItem : debitPerRecordList) {
            Main.operation.doTransactionOperationOnFiles(debtorItem, creditorItem);
        }
    }
}
