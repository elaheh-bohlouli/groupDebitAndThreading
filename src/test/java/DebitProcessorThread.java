import java.util.List;

public class DebitProcessorThread extends Thread {

    public DebitProcessorThread(BalanceRecordClass balanceRecordClass) {
        super(balanceRecordClass);
    }
}

