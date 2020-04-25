public class DebitRecordClass {
 String[] debitRecord;
    String type;
    String depositNumber;
    Integer amount;

    public DebitRecordClass(String[] debitRecord) {
        this.debitRecord = debitRecord;
        this.type = debitRecord[0];
        this.depositNumber = debitRecord[1];
        this.amount = Integer.valueOf(debitRecord[2]);
    }
}
