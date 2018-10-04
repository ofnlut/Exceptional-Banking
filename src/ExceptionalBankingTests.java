import java.util.zip.DataFormatException;
public class ExceptionalBankingTests {

  public static boolean testAccountBalance() {

    // Test account
    Account testAccount = new Account("testAccount");

    
    try {
      //testAccount.addTransactionGroup("8 8 8 8 8");
      testAccount.addTransactionGroup("0 0 0 0 1 1 1 1"); // Binary
      testAccount.addTransactionGroup("1 10 10 20 -10"); // Integer
                                                         // 31
    }catch(DataFormatException except){
      System.out.println(except.getMessage());
    }



    if (testAccount.getCurrentBalance() != 31) {
      System.out.println("Test Failed: balance ammount returned " + testAccount.getCurrentBalance()
          + " instead of 31");
      return false;
    }

    return true;


  }

  public static boolean testOverdraftCount() {

    Account testOverdraftAccount = new Account("testOverdraftAccount");

    try {
      testOverdraftAccount.addTransactionGroup("0 1 1 1 1 0 0 0 0 0");
    }catch(DataFormatException except) {
      System.out.println(except.getMessage());
    }


    if (testOverdraftAccount.getNumberOfOverdrafts() != 1) {
      System.out.println("Overdraft test failed test returned: "
          + testOverdraftAccount.getNumberOfOverdrafts() + "instead of 1");
      return false;
    }

    return true;

  }

  public static void main(String[] args) {
    testAccountBalance();
    //testOverdraftCount();
  }
}

