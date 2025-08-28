package challenge3;

public class Transfer implements Runnable {
    private final BankAccountTransfer sourceAccount;
    private final BankAccountTransfer destinationAccount;
    private final double amount;

    public Transfer(BankAccountTransfer sourceAccount, BankAccountTransfer destinationAccount, double amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
    }

    public void run() {
        while (!sourceAccount.transfer(destinationAccount, amount))
            continue;
        System.out.printf("%s completed\n", Thread.currentThread().getName());
    }
}
