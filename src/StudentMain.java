import operations.*;
import student.*;
import student.mz150346_General;
import tests.TestHandler;
import tests.TestRunner;

public class StudentMain {

    public static void main(String[] args) {

        ArticleOperations articleOperations = new mz150346_Article(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new mz150346_Buyer();
        CityOperations cityOperations = new mz150346_City();
        GeneralOperations generalOperations = new mz150346_General();
        OrderOperations orderOperations = new mz150346_Order();
        ShopOperations shopOperations = new mz150346_Shop();
        TransactionOperations transactionOperations = new mz150346_Transaction();
//
//        Calendar c = Calendar.getInstance();
//        c.clear();
//        c.set(2010, Calendar.JANUARY, 01);
//
//
//        Calendar c2 = Calendar.getInstance();
//        c2.clear();
//        c2.set(2010, Calendar.JANUARY, 01);
//
//        if(c.equals(c2)) System.out.println("jednako");
//        else System.out.println("nije jednako");

        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();
    }
}
