import java.nio.file.Paths;
import java.util.*;

import static com.google.common.collect.Lists.partition;

public class Operation {
    private String PRE_PATH;
    private String balanceFilePath;
    private String transactionFilePath;
    private String debitFilePath;
    private int numberOfCreditorRecord;
    private int numberOfRecordPerThread;
    private int numberOfAliveThread;
    Map<String, BalancePerRecord> balanceMap = prepareBalanceMap();
    static Object lock = new Object();

    public Operation(String PRE_PATH, String balanceFilePath, String transactionFilePath, String debitFilePath,
                     int numberOfCreditorRecord, int numberOfRecordPerThread, int numberOfAliveThread) {
        this.PRE_PATH = PRE_PATH;
        this.balanceFilePath = balanceFilePath;
        this.transactionFilePath = transactionFilePath;
        this.debitFilePath = debitFilePath;
        this.numberOfCreditorRecord = numberOfCreditorRecord;
        this.numberOfRecordPerThread = numberOfRecordPerThread;
        this.numberOfAliveThread = numberOfAliveThread;
    }

    protected void createDebitFile(String debtorRecord, String baseCreditorNumber, String amount) {
        List<String> debitFile = new ArrayList<>();
        debitFile.add(debtorRecord);
        for (int i = 1; i < numberOfCreditorRecord; i++) {
            debitFile.add("creditor   " + baseCreditorNumber + i + "   " + amount);
        }

        UtilFileOperation.createFile(debitFilePath);
        UtilFileOperation.writeToFile(debitFile, Paths.get(debitFilePath));
    }

    protected void createBalanceFile(String debtorRecord, String baseCreditorNumber, String amount) {
        List<String> balanceFile = new ArrayList<>();
        balanceFile.add(debtorRecord);
        for (int i = 1; i < numberOfCreditorRecord + 1; i++) {
            balanceFile.add(baseCreditorNumber + i + amount);
        }
        UtilFileOperation.createFile(balanceFilePath);
        UtilFileOperation.writeToFile(balanceFile, Paths.get(balanceFilePath));
    }

    protected Map<String, BalancePerRecord> prepareBalanceMap() {
        List<String> balanceRecord = null;
        if (balanceFilePath != null) {
            balanceRecord = UtilFileOperation.readFromFile(Paths.get(balanceFilePath));
        }
        System.out.println("There Is Not File Exist");
        List<BalancePerRecord> balancePerRecordList = new ArrayList<>();
        Map<String, BalancePerRecord> balanceMap = new HashMap<>();
        for (String perBalanceRecord : balanceRecord) {
            BalancePerRecord balancePerRecord = new BalancePerRecord(UtilFileOperation.splitLine(perBalanceRecord));
            balancePerRecordList.add(balancePerRecord);
            balanceMap.put(balancePerRecord.depositNumber, balancePerRecord);
        }
        return balanceMap;
    }

    protected int numberOfThreadCalculate() {
        if (numberOfRecordPerThread % numberOfCreditorRecord == 0) {
            return numberOfCreditorRecord / numberOfRecordPerThread;
        } else
            return ((numberOfCreditorRecord / numberOfRecordPerThread) + 1);
    }

    protected void debitAndCreditAmountCalculate() {
        List<DebitPerRecord> listDebitPerRecord = new ArrayList<>();
        List<String> list = UtilFileOperation.readFromFile(Paths.get(debitFilePath));
        int creditSumAmount = 0;
        int debtorAmount = 0;
        for (String s : list) {
            DebitPerRecord debitPerRecord = new DebitPerRecord(UtilFileOperation.splitLine(s));
            listDebitPerRecord.add(debitPerRecord);
            if (debitPerRecord.type.equals("debtor")) {
                debtorAmount = debitPerRecord.amount;
            } else {
                creditSumAmount += debitPerRecord.amount;
            }
        }
        if (creditSumAmount != debtorAmount) {
            System.out.println("debit and credit amounts is not equal!");
        } else doTransactionOperationOnFiles(listDebitPerRecord);
    }

    protected void doTransactionOperationOnFiles(List<DebitPerRecord> listDebitPerRecord) {
        synchronized (lock) {
            List<String> list = UtilFileOperation.readFromFile(Paths.get(debitFilePath));
            String debtorDepositNumber = null;
            for (String s : list) {
                DebitPerRecord debitPerRecord = new DebitPerRecord(UtilFileOperation.splitLine(s));
                listDebitPerRecord.add(debitPerRecord);
                if (debitPerRecord.type.equals("debtor")) {
                    debtorDepositNumber = debitPerRecord.depositNumber;
                } else if (debitPerRecord.type.equals("creditor")) {
                    String creditorDepositNumber = debitPerRecord.depositNumber;
                    TransactionFileCreation transaction = new TransactionFileCreation(debtorDepositNumber,
                            creditorDepositNumber, debitPerRecord.amount);
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