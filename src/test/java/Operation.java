import java.nio.file.Paths;
import java.util.*;

public class Operation {
    private String PRE_PATH;
    private String balanceFilePath;
    private String transactionFilePath;
    private String debitFilePath;
    private int numberOfCreditorRecord;
    private int numberOfRecordPerThread;
    private int numberOfAliveThread;
    private Map<String, BalancePerRecord> balanceMap;
    static Object lock = new Object();

    public Operation(String PRE_PATH, String balanceFilePath, String transactionFilePath, String debitFilePath,
                     int numberOfCreditorRecord, int numberOfRecordPerThread, int numberOfAliveThread) {
        this.PRE_PATH = PRE_PATH;
        this.balanceFilePath = PRE_PATH + balanceFilePath;
        this.transactionFilePath = PRE_PATH + transactionFilePath;
        this.debitFilePath = PRE_PATH + debitFilePath;
        this.numberOfCreditorRecord = numberOfCreditorRecord;
        this.numberOfRecordPerThread = numberOfRecordPerThread;
        this.numberOfAliveThread = numberOfAliveThread;
    }

    protected void createDebitFile(String debtorRecord, String baseCreditorNumber, String amount) {
        List<String> debitFile = new ArrayList<>();
        debitFile.add(debtorRecord);
        for (int i = 0; i < numberOfCreditorRecord; i++) {
            debitFile.add("creditor\t" + baseCreditorNumber + (i + 1) + "\t" + amount);
        }

        UtilFileOperation.createFile(debitFilePath);
        UtilFileOperation.writeToFile(debitFile, Paths.get(debitFilePath));
    }

    protected void createBalanceFile(String debtorRecord, String baseCreditorNumber, String amount) {
        List<String> balanceFileRecords = new ArrayList<>();
        balanceFileRecords.add(debtorRecord);
        for (int i = 1; i < numberOfCreditorRecord + 1; i++) {
            balanceFileRecords.add(baseCreditorNumber + i + "\t" + amount);
        }
        UtilFileOperation.createFile(balanceFilePath);
        UtilFileOperation.writeToFile(balanceFileRecords, Paths.get(balanceFilePath));
        balanceMap = prepareBalanceMap(balanceFileRecords);
    }

    protected Map<String, BalancePerRecord> prepareBalanceMap(List<String> balanceFileRecords) {
        Map<String, BalancePerRecord> balanceMap = new HashMap<>();
        for (String perBalanceRecord : balanceFileRecords) {
            BalancePerRecord balancePerRecord = new BalancePerRecord(UtilFileOperation.splitLine(perBalanceRecord));
            balanceMap.put(balancePerRecord.depositNumber, balancePerRecord);
        }
        return balanceMap;
    }

    protected void checkDebitAndCreditAmount() throws Exception {
        List<String> recordList = UtilFileOperation.readFromFile(Paths.get(debitFilePath));
        int creditSumAmount = 0;
        int debtorAmount = 0;
        for (String record : recordList) {
            DebitPerRecord debitPerRecord = new DebitPerRecord(UtilFileOperation.splitLine(record));
            if (debitPerRecord.type.equals("debtor")) {
                debtorAmount = debitPerRecord.amount;
            } else {
                creditSumAmount += debitPerRecord.amount;
            }
        }
        if (creditSumAmount != debtorAmount) {
            throw new Exception("debit and credit amounts is not equal!");
        }

    }

    protected void doTransactionOperationOnFiles(DebitPerRecord debtorItem, DebitPerRecord creditorItem) {
        synchronized (lock) {
            String creditorDepositNumber = creditorItem.depositNumber;
            TransactionFileCreation transaction = new TransactionFileCreation(debtorItem.depositNumber, creditorDepositNumber, creditorItem.amount);
            transaction.doTransaction(transactionFilePath);

            balanceMap.get(debtorItem.depositNumber).balance -= creditorItem.amount;
            balanceMap.get(creditorDepositNumber).balance += creditorItem.amount;
            UtilFileOperation.replaceInFile(balanceFilePath, debtorItem.depositNumber, UtilFileOperation.join(Arrays.asList(debtorItem.depositNumber, String.valueOf(balanceMap.get(debtorItem.depositNumber).balance))));
            UtilFileOperation.replaceInFile(balanceFilePath, creditorDepositNumber, UtilFileOperation.join(Arrays.asList(creditorDepositNumber, String.valueOf(balanceMap.get(creditorDepositNumber).balance))));
        }
    }

    public String getPRE_PATH() {
        return PRE_PATH;
    }

    public void setPRE_PATH(String PRE_PATH) {
        this.PRE_PATH = PRE_PATH;
    }

    public String getBalanceFilePath() {
        return balanceFilePath;
    }

    public void setBalanceFilePath(String balanceFilePath) {
        this.balanceFilePath = balanceFilePath;
    }

    public String getTransactionFilePath() {
        return transactionFilePath;
    }

    public void setTransactionFilePath(String transactionFilePath) {
        this.transactionFilePath = transactionFilePath;
    }

    public String getDebitFilePath() {
        return debitFilePath;
    }

    public void setDebitFilePath(String debitFilePath) {
        this.debitFilePath = debitFilePath;
    }

    public int getNumberOfCreditorRecord() {
        return numberOfCreditorRecord;
    }

    public void setNumberOfCreditorRecord(int numberOfCreditorRecord) {
        this.numberOfCreditorRecord = numberOfCreditorRecord;
    }

    public int getNumberOfRecordPerThread() {
        return numberOfRecordPerThread;
    }

    public void setNumberOfRecordPerThread(int numberOfRecordPerThread) {
        this.numberOfRecordPerThread = numberOfRecordPerThread;
    }

    public int getNumberOfAliveThread() {
        return numberOfAliveThread;
    }

    public void setNumberOfAliveThread(int numberOfAliveThread) {
        this.numberOfAliveThread = numberOfAliveThread;
    }

    public Map<String, BalancePerRecord> getBalanceMap() {
        return balanceMap;
    }

    public void setBalanceMap(Map<String, BalancePerRecord> balanceMap) {
        this.balanceMap = balanceMap;
    }

    public static Object getLock() {
        return lock;
    }

    public static void setLock(Object lock) {
        Operation.lock = lock;
    }
}