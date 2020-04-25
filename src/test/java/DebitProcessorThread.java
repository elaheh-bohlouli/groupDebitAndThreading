import java.util.ArrayList;
import java.util.List;

public class DebitProcessorThread extends Thread {

    private List<List<DebitPerRecord>> debitPerRecordList = new ArrayList<>();

    public DebitProcessorThread(List<List<DebitPerRecord>> debitPerRecordList) {
        this.debitPerRecordList = debitPerRecordList;
    }

    @Override
    public void run() {
        for (List<DebitPerRecord> debitPerRecord : debitPerRecordList) {
            // do transaction
        }
    }
}
