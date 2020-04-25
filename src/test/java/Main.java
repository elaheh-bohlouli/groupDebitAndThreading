import files.Transaction;
import files.UtilFileOperation;

import java.nio.file.Paths;
import java.util.*;

import static com.google.common.collect.Lists.partition;

public class Main {

    private static String PRE_PATH = "F:\\New folder";
    private static String balanceFilePath = PRE_PATH + "BalanceFile.txt";
    private static String transactionFilePath = PRE_PATH + "TransactionFile.txt";
    private static String debitFilePath = PRE_PATH + "DebitFile.txt";
    private static int numberOfCreditorRecord = 100;
    private static int numberOfRecordPerThread = 5;
    private static int numberOfAliveThread = 10;
    static Object lock = new Object();
    private static int numberOfThread = (numberOfCreditorRecord / numberOfRecordPerThread);


    public static void main(String[] args) {
        createBalanceFile("1.10.100.10   200", "1.20.100.", "   0");
        Map<String, BalancePerRecord> balanceMap = prepareBalanceMap();
        createDebitFile("debtor   1.10.100.1   200", "1.20.100.", "5");


        if (numberOfRecordPerThread % numberOfCreditorRecord != 0) {
            numberOfThread = (numberOfCreditorRecord / numberOfRecordPerThread) + 1;
        }

        List<String> debitRecord = UtilFileOperation.readFromFile(Paths.get(debitFilePath));
        List<DebitPerRecord> debitPerRecordList = new ArrayList<>();
        DebitPerRecord debtor = null;
        int creditSumAmount = 0;
        for (String perDebitRecord : debitRecord) {
            DebitPerRecord debitPerRecord = new DebitPerRecord(UtilFileOperation.splitLine(perDebitRecord));
            debitPerRecordList.add(debitPerRecord);
            if (debitPerRecord.type.equalsIgnoreCase("debtor")) {
                debtor = debitPerRecord;
            } else {
                creditSumAmount = debitPerRecord.amount;
            }
        }


        if (debtor.amount < creditSumAmount) {
            System.out.println("debit and credit amounts is not equal!");

        } else {
            List<DebitPerRecord> bigList = new ArrayList<>();
            List<List<DebitPerRecord>> smallerLists = partition(bigList, numberOfRecordPerThread);
            for (DebitPerRecord debitPerRecord : debitPerRecordList) {
                bigList.add(debitPerRecord);

                for (int j = 0; j < numberOfThread; j++) {
                    DebitProcessorThread debitProcessorThread = new DebitProcessorThread(smallerLists);
                    debitProcessorThread.start();
                }

                synchronized (lock) {
                    String debtorDepositNumber = debtor.depositNumber;
                    String creditorDepositNumber = debitPerRecord.depositNumber;
                    if (debitPerRecord.type.equalsIgnoreCase("creditor")) {
                        Transaction transaction = new Transaction(debtorDepositNumber, creditorDepositNumber, debitPerRecord.amount);
                        transaction.doTransaction(transactionFilePath);
                        balanceMap.get(debtorDepositNumber).balance -= debitPerRecord.amount;
                        balanceMap.get(creditorDepositNumber).balance += debitPerRecord.amount;
                        UtilFileOperation.replaceInFile(balanceFilePath, debtorDepositNumber, UtilFileOperation
                                .join(Arrays.asList(debtorDepositNumber, String.valueOf(balanceMap.get(debtorDepositNumber).balance))));
                        UtilFileOperation.replaceInFile(balanceFilePath, creditorDepositNumber, UtilFileOperation
                                .join(Arrays.asList(creditorDepositNumber, String.valueOf(balanceMap.get(creditorDepositNumber).balance))));

                    } else System.out.println("All deposits are done");
                }
            }
        }
    }


    private static void createDebitFile(String debtorRecord, String baseCreditorNumber, String amount) {
        List<String> debitFile = new ArrayList<>();
        debitFile.add(debtorRecord);
        for (int i = 1; i < numberOfCreditorRecord; i++) {
            debitFile.add("creditor   " + baseCreditorNumber + i + "   " + amount);
        }

        UtilFileOperation.createFile(debitFilePath);
        UtilFileOperation.writeToFile(debitFile, Paths.get(debitFilePath));
    }

    private static void createBalanceFile(String debtorRecord, String baseCreditorNumber, String amount) {
        List<String> balanceFile = new ArrayList<>();
        balanceFile.add(debtorRecord);
        for (int i = 1; i < numberOfCreditorRecord + 1; i++) {
            balanceFile.add(baseCreditorNumber + i + amount);
        }
        UtilFileOperation.createFile(balanceFilePath);
        UtilFileOperation.writeToFile(balanceFile, Paths.get(balanceFilePath));
    }

    private static Map<String, BalancePerRecord> prepareBalanceMap() {
        List<String> balanceRecord = UtilFileOperation.readFromFile(Paths.get(balanceFilePath));
        List<BalancePerRecord> balancePerRecordList = new ArrayList<>();
        Map<String, BalancePerRecord> balanceMap = new HashMap<>();
        for (String perBalanceRecord : balanceRecord) {
            BalancePerRecord balancePerRecord = new BalancePerRecord(UtilFileOperation.splitLine(perBalanceRecord));
            balancePerRecordList.add(balancePerRecord);
            balanceMap.put(balancePerRecord.depositNumber, balancePerRecord);
        }
        return balanceMap;
    }
}

