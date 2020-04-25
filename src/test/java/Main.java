import files.Transaction;
import files.UtilFileOperation;

import java.nio.file.Paths;
import java.util.*;

public class Main {

    private static String PRE_PATH = "F:\\New folder";
    private static String balanceFilePath = PRE_PATH + "BalanceFile.txt";
    private static String transactionFilePath = PRE_PATH + "TransactionFile.txt";
    private static String debitFilePath = PRE_PATH + "DebitFile.txt";
    private static int numberOfCreditorRecord = 100;

    public static void main(String[] args) {
        createBalanceFile("1.10.100.10   200", "1.20.100.", "   0");
        Map<String, BalanceRecordClass> balanceMap = prepareBalanceMap();
        createDebitFile("debtor   1.10.100.1   200", "1.20.100.", "5");


        List<String> debitRecord = UtilFileOperation.readFromFile(Paths.get(debitFilePath));
        List<DebitRecordClass> debitRecordClassList = new ArrayList<>();
        DebitRecordClass debtor = null;
        int creditoSumAmount = 0;
        for (String perDebitRecord : debitRecord) {
            DebitRecordClass debitRecordClass = new DebitRecordClass(UtilFileOperation.splitLine(perDebitRecord));
            debitRecordClassList.add(debitRecordClass);
            if (debitRecordClass.type.equalsIgnoreCase("debtor")) {
                debtor = debitRecordClass;
            } else {
                creditoSumAmount = debitRecordClass.amount;
            }
        }

//new code
        int numberOfRecord = numberOfCreditorRecord;
        int numberOfRecordPerThread = 5;
        int numberOfAliveThread = 10;
        if ( numberOfRecordPerThread % numberOfCreditorRecord != 0) {
           int numberOfThread = (numberOfCreditorRecord/numberOfRecordPerThread) + 1;
        }
        int numberOfThread =  (numberOfCreditorRecord/numberOfRecordPerThread);
//new code
        if (debtor.amount < creditoSumAmount) {
            System.out.println("debit and credit amounts is not equal!");
        }else {
            new Thread(new Runnable(){
                @Override
                public void run(){
            for (DebitRecordClass debitRecordClass : debitRecordClassList) {
                String debtorDepositNumber = debtor.depositNumber;
                String creditorDepositNumber = debitRecordClass.depositNumber;
                if (debitRecordClass.type.equalsIgnoreCase("creditor")) {
                    Transaction transaction = new Transaction(debtor.depositNumber, creditorDepositNumber, debitRecordClass.amount);
                    transaction.doTransaction(transactionFilePath);
                    balanceMap.get(debtorDepositNumber).balance -= debitRecordClass.amount;
                    balanceMap.get(creditorDepositNumber).balance += debitRecordClass.amount;
                    UtilFileOperation.replaceInFile(balanceFilePath, debtorDepositNumber, UtilFileOperation.join(Arrays.asList(debtorDepositNumber, String.valueOf(balanceMap.get(debtorDepositNumber).balance))));
                    UtilFileOperation.replaceInFile(balanceFilePath, creditorDepositNumber, UtilFileOperation.join(Arrays.asList(creditorDepositNumber, String.valueOf(balanceMap.get(creditorDepositNumber).balance))));
                } else System.out.println("All deposits are done");
            } }
            }).start();
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

    private static Map<String, BalanceRecordClass> prepareBalanceMap() {
        List<String> balanceRecord = UtilFileOperation.readFromFile(Paths.get(balanceFilePath));
        List<BalanceRecordClass> balanceRecordClassList = new ArrayList<>();
        Map<String, BalanceRecordClass> balanceMap = new HashMap<>();
        for (String perBalanceRecord : balanceRecord) {
            BalanceRecordClass balanceRecordClass = new BalanceRecordClass(UtilFileOperation.splitLine(perBalanceRecord));
            balanceRecordClassList.add(balanceRecordClass);
            balanceMap.put(balanceRecordClass.depositNumber, balanceRecordClass);
        }
        return balanceMap;
    }
}

