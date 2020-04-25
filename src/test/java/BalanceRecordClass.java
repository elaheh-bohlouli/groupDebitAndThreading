public class BalanceRecordClass {
    String[] balanceRecord;
    String depositNumber;
    Integer balance;

    public BalanceRecordClass(String[] balanceRecord) {
        this.balanceRecord = balanceRecord;
        this.depositNumber = balanceRecord[0];
        this.balance = Integer.valueOf(balanceRecord[1]);
    }
}
