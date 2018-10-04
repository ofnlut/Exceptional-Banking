import java.util.zip.DataFormatException;

public class TransactionGroup {

  private enum EncodingType {
    BINARY_AMOUNT, INTEGER_AMOUNT, QUICK_WITHDRAW
  };

  private EncodingType type;
  private int[] values;

  public TransactionGroup(int[] groupEncoding) throws DataFormatException {

    // Try to determine the transaction is valid
    
    // IF null throw error
    if(groupEncoding == null) {
      throw new DataFormatException("transaction group encoding cannot be null or empty");
    }
    // If not a transaction type throw an error
    if (!(groupEncoding[0] == EncodingType.BINARY_AMOUNT.ordinal())
        || !(groupEncoding[0] == EncodingType.INTEGER_AMOUNT
            .ordinal())
        || !(groupEncoding[0] == EncodingType.QUICK_WITHDRAW
            .ordinal())) {
      throw new DataFormatException("the first element within a transaction group must be 0, 1, or 2");

    }

    this.type = EncodingType.values()[groupEncoding[0]];
    this.values = new int[groupEncoding.length - 1];
    
    
    for (int i = 0; i < values.length; i++) {
      
      //If the binary transaction does not have a 0 or 1 throw an error
      if (this.type.equals(EncodingType.BINARY_AMOUNT)) {
        if (!(this.values[i] == 0) && !(this.values[i] == 1)) {
          throw new DataFormatException(
              "binary amount transaction groups may only contain 0s and 1s");
        }
      }
      if (this.type.equals(EncodingType.INTEGER_AMOUNT)) {
        if (this.values[i] == 0) {
          throw new DataFormatException("integer amount transaction groups may not contain 0s");
        }
      }
      if (this.type.equals(EncodingType.QUICK_WITHDRAW) && values.length > 5) {
        throw new DataFormatException("quick withdraw transaction groups must contain 5 elements");
      }
      if (this.type.equals(EncodingType.QUICK_WITHDRAW) && this.values[i] < 0) {
        throw new DataFormatException(
            "quick withdraw transaction groups may not contain negative numbers");
      }
      this.values[i] = groupEncoding[i + 1];
    }
  }

  public int getTransactionCount() {
    int transactionCount = 0;
    switch (this.type) {
      case BINARY_AMOUNT:
        int lastAmount = -1;
        for (int i = 0; i < this.values.length; i++) {
          if (this.values[i] != lastAmount) {
            transactionCount++;
            lastAmount = this.values[i];
          }
        }
        break;
      case INTEGER_AMOUNT:
        transactionCount = values.length;
        break;
      case QUICK_WITHDRAW:
        for (int i = 0; i < this.values.length; i++)
          transactionCount += this.values[i];
    }
    return transactionCount;
  }

  public int getTransactionAmount(int transactionIndex) throws IndexOutOfBoundsException {
    if(transactionIndex > this.values.length-1) {
      throw new IndexOutOfBoundsException("Transaction: " + transactionIndex + " is out of bounds only " + this.values.length + " exist on this account.");
    }
    int transactionCount = 0;
    switch (this.type) {
      case BINARY_AMOUNT:
        int lastAmount = -1;
        int amountCount = 0;
        for (int i = 0; i <= this.values.length; i++) {
          if (i == this.values.length || this.values[i] != lastAmount) {
            if (transactionCount - 1 == transactionIndex) {
              if (lastAmount == 0)
                return -1 * amountCount;
              else
                return +1 * amountCount;
            }
            transactionCount++;
            lastAmount = this.values[i];
            amountCount = 1;
          } else
            amountCount++;
          lastAmount = this.values[i];
        }
        break;
      case INTEGER_AMOUNT:
        return this.values[transactionIndex];
      case QUICK_WITHDRAW:
        final int[] QW_AMOUNTS = new int[] {-20, -40, -80, -100};
        for (int i = 0; i < this.values.length; i++)
          for (int j = 0; j < this.values[i]; j++)
            if (transactionCount == transactionIndex)
              return QW_AMOUNTS[i];
            else
              transactionCount++;
    }
    return -1;
  }
}
