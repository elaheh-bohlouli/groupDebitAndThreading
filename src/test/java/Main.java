
import com.google.common.collect.Lists;

import java.nio.file.Paths;
import java.util.*;

import static com.google.common.collect.Lists.partition;

public class Main {

    public static void main(String[] args) {

        Operation operation;
        operation = new Operation("F:\\New folder", "F:\\New folder\\BalanceFile.txt",
                "F:\\New folder\\TransactionFile.txt", "F:\\New folder\\DebitFile.txt",
                100, 5, 10);

        operation.createBalanceFile("1.10.100.10   200", "1.20.100.", "   0");
        operation.createDebitFile("debtor   1.10.100.1   200", "1.20.100.", "5");
        operation.debitAndCreditAmountCalculate();
        int numberOfThread = operation.numberOfThreadCalculate();

        List<DebitPerRecord> listDebitPerRecord = new ArrayList<>();
        List<String> list = UtilFileOperation.readFromFile(Paths.get("F:\\New folder\\DebitFile.txt"));


            int count = 10;
            while (true){
                List<DebitProcessorThread> list1 = new ArrayList<>();
                for livethread
            }

        for (String s : list) {
            DebitPerRecord debitPerRecord = new DebitPerRecord(UtilFileOperation.splitLine(s));
            listDebitPerRecord.add(debitPerRecord);
            List<List<DebitPerRecord>> smallerLists = partition(listDebitPerRecord, 5);

            for (int i = 0; i < numberOfThread; i++) {
                DebitProcessorThread debitProcessorThread = new DebitProcessorThread(smallerLists);
                debitProcessorThread.start();

                try {
                    debitProcessorThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}