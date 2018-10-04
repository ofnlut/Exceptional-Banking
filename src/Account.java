import java.io.File;
import java.util.Scanner;
import java.util.zip.DataFormatException;

public class Account {

  private static final int MAX_GROUPS = 10000;
  private static int nextUniqueId = 1000;

  private String name;
  private final int UNIQUE_ID;
  private TransactionGroup[] transactionGroups;
  private int transactionGroupsCount;

  public Account(String name) {
    this.name = name;
    this.UNIQUE_ID = Account.nextUniqueId;
    Account.nextUniqueId++;
    this.transactionGroups = new TransactionGroup[MAX_GROUPS];
    this.transactionGroupsCount = 0;
  }

  public Account(File file) {
    // NOTE: THIS METHOD SHOULD NOT BE CALLED MORE THAN ONCE, BECAUSE THE
    // RESULTING BEHAVIOR IS NOT DEFINED WITHIN THE JAVA SPECIFICATION ...
    Scanner in = new Scanner(System.in);
    // ... THIS WILL BE UPDATED TO READ FROM A FILE INSTEAD OF SYSTEM.IN.

    this.name = in.nextLine();
    this.UNIQUE_ID = Integer.parseInt(in.nextLine());
    Account.nextUniqueId = this.UNIQUE_ID + 1;
    this.transactionGroups = new TransactionGroup[MAX_GROUPS];
    this.transactionGroupsCount = 0;
    String nextLine = "";
    while (in.hasNextLine())
      try {
        this.addTransactionGroup(in.nextLine());
      }catch(DataFormatException except) {
        System.out.println(except.getMessage());
      }
      
    in.close();
  }

  public int getId() {
    return this.UNIQUE_ID;
  }

  public void addTransactionGroup(String command) throws DataFormatException {
    
    for (int i = 0; i < command.length(); i++) {
      if ((command.charAt(i) != 32) || (command.charAt(i) != 45)
          || (command.charAt(i) >= 48 && command.charAt(i) <= 57)) {
        throw new DataFormatException("addTransactionGroup requires string commands that contain only space separated integer values");

      }
    }
    if(this.transactionGroups[MAX_GROUPS-1] == this.transactionGroups[MAX_GROUPS]) {
      throw new OutOfMemoryError("Capacitay has exceeded" + MAX_GROUPS);
    }
    String[] parts = command.split(" ");
    int[] newTransactions = new int[parts.length];
    for (int i = 0; i < parts.length; i++)
      newTransactions[i] = Integer.parseInt(parts[i]);
    TransactionGroup t = new TransactionGroup(newTransactions);
    this.transactionGroups[this.transactionGroupsCount] = t;
    this.transactionGroupsCount++;
  }

  public int getTransactionCount() {
    int transactionCount = 0;
    for (int i = 0; i < this.transactionGroupsCount; i++)
      transactionCount += this.transactionGroups[i].getTransactionCount();
    return transactionCount;
  }

  public int getTransactionAmount(int index) throws IndexOutOfBoundsException{
    if(index > this.transactionGroups.length) {
      throw new IndexOutOfBoundsException("Transaction: " + index + " is out of bounds only " + this.transactionGroups.length + " exist on this account.");
    }
    int transactionCount = 0;
    for (int i = 0; i < this.transactionGroupsCount; i++) {
      int prevTransactionCount = transactionCount;
      transactionCount += this.transactionGroups[i].getTransactionCount();
      if (transactionCount > index) {
        index -= prevTransactionCount;
        return this.transactionGroups[i].getTransactionAmount(index);
      }
    }
    return -1;
  }

  public int getCurrentBalance() {
    int balance = 0;
    int size = this.getTransactionCount();
    for (int i = 0; i < size; i++)
      balance += this.getTransactionAmount(i);
    return balance;
  }

  public int getNumberOfOverdrafts() {
    int balance = 0;
    int overdraftCount = 0;
    int size = this.getTransactionCount();
    for (int i = 0; i < size; i++) {
      int amount = this.getTransactionAmount(i);
      balance += amount;
      if (balance < 0 && amount < 0)
        overdraftCount++;
    }
    return overdraftCount;
  }

}
