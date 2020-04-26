import java.util.ArrayList;
import java.util.List;

public class DebitProcessorThread extends Thread {

    private List<List<DebitPerRecord>> debitPerRecordList = new ArrayList<>();

    public DebitProcessorThread(List<List<DebitPerRecord>> debitPerRecordList) {
        this.debitPerRecordList = debitPerRecordList;
    }

    @Override
    public void run() {
        Operation operation = new Operation("F:\\New folder", "F:\\New folder\\BalanceFile.txt",
                "F:\\New folder\\TransactionFile.txt", "F:\\New folder\\DebitFile.txt",
                100, 5, 10);
        for (List<DebitPerRecord> debitPerRecord : debitPerRecordList) {
            operation.doTransactionOperationOnFiles(debitPerRecord);        }
    }
}
