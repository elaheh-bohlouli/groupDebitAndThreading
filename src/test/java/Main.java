import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.partition;

public class Main {

    public static Operation operation = new Operation("F:\\JavaForElaheh\\JavaFile\\", "BalanceFile.txt",
            "TransactionFile.txt", "DebitFile.txt",
            100, 5, 10);

    public static void main(String[] args) throws Exception {

        operation.createBalanceFile("1.10.100.1\t500", "1.20.100.", "0");
        operation.createDebitFile("debtor\t1.10.100.1\t500", "1.20.100.", "5");

        operation.checkDebitAndCreditAmount();


        List<DebitPerRecord> listDebitPerRecord = new ArrayList<>();
        List<String> list = UtilFileOperation.readFromFile(Paths.get(operation.getDebitFilePath()));

        DebitPerRecord debtorItem = null;
        for (String record : list) {
            DebitPerRecord debitPerRecord = new DebitPerRecord(UtilFileOperation.splitLine(record));
            if (debitPerRecord.type.equals("debtor")) {
                debtorItem = debitPerRecord;
            } else if (debitPerRecord.type.equals("creditor")) {
                listDebitPerRecord.add(debitPerRecord);
            }
        }

        List<List<DebitPerRecord>> smallerLists = partition(listDebitPerRecord, operation.getNumberOfRecordPerThread());

        List<DebitProcessorThread> threadList = new ArrayList<>();
        for (int i = 0; i < smallerLists.size(); i++) {
            while (true) {
                int liveThreads = 0;
                for (DebitProcessorThread debitProcessorThread : threadList) {
                    if (debitProcessorThread.isAlive()) {
                        liveThreads++;
                    }
                }
                if (operation.getNumberOfAliveThread() > liveThreads) {
                    DebitProcessorThread debitProcessorThread = new DebitProcessorThread(debtorItem, smallerLists.get(i));
                    threadList.add(debitProcessorThread);
                    debitProcessorThread.start();
                    break;
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        for (DebitProcessorThread debitProcessorThread : threadList) {
            try {
                debitProcessorThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}