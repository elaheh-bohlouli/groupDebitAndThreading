
import java.nio.file.Paths;
import java.util.*;

import static com.google.common.collect.Lists.partition;

public class Main {

    private static String PRE_PATH = "F:\\New folder";
    private static String balanceFilePath ;
    private static String transactionFilePath ;
    private static String debitFilePath;
    private static int numberOfCreditorRecord = 100;
    private static int numberOfRecordPerThread = 5;
    private static int numberOfAliveThread = 10;
    static Object lock = new Object();


    public static void main(String[] args) {

        Operation operation = new Operation("F:\\New folder", "F:\\New folder\\BalanceFile.txt",
                "F:\\New folder\\TransactionFile.txt", "F:\\New folder\\DebitFile.txt",
                100, 5, 10);

        operation.createBalanceFile("1.10.100.10   200", "1.20.100.", "   0");
        operation.createDebitFile("debtor   1.10.100.1   200", "1.20.100.", "5");


    }
}