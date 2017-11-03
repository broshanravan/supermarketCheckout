/**
 * Created by Behrooz on 31/10/2017.
 */
import java.text.DecimalFormat;
import java.util.*;

public class PriceCalculator  implements IPriceCalculator{

    Inventory inventory = new Inventory();
    DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    Map<String, GroceryItem> groceryItemsMap = inventory.readItemsFromFile();



    public void addItemsToBasket(String itemCode) {

        List<GroceryItem> basket = new ArrayList<GroceryItem>();
        Scanner scanner = new Scanner(System.in);
        GroceryItem groceryItem = groceryItemsMap.get(itemCode);
        if(MeasurementMethod.WEIGHT.equals(groceryItem.getMeasurementUnit())) {
            System.out.println("please enter weight; ");
            double weight = Double.parseDouble(scanner.nextLine());
            groceryItem.setWeight(weight);
        }

        while (!"end".equalsIgnoreCase(itemCode)) {
            String code = scanner.nextLine();
            if (groceryItem != null) {
                basket.add(groceryItem);
            }
            System.out.println("Description : " + groceryItem.getItemName());
            System.out.println("Price : \u00a3" + decimalFormatter.format(groceryItem.getPrice()));

            System.out.println("please enter next Item code; ");
            groceryItem = groceryItemsMap.get(itemCode);
            if(MeasurementMethod.WEIGHT.equals(groceryItem.getMeasurementUnit())) {
                System.out.println("please enter weight; ");
                double weight = Double.parseDouble(scanner.nextLine());
                groceryItem.setWeight(weight);
            }
        }

        calculatePayment(basket);
    }


    /**
     * Calculating the total price.
     * This function runs continuously until the
     * user completes the shopping
     * @param basket
     * @return finalPayment
     */

    public String calculatePayment(List<GroceryItem> basket) {
        double subTotal = 0;
        Scanner scanner = new Scanner(System.in);

        for(GroceryItem groceryItem :basket){
            if(groceryItem != null) {
                if (groceryItem.getMeasurementUnit().equals(MeasurementMethod.WEIGHT)) {
                    System.out.println("Please Enter Weight in kg: ");
                    boolean waightIsValid = false;
                    while (!waightIsValid) {
                        try {
                            double weight = groceryItem.getWeight();
                            subTotal += groceryItem.getPrice() * weight;
                            waightIsValid = true;
                        } catch (Exception ex) {
                            System.out.println("Please Enter Weight in kg: ");
                        }
                    }
                } else {
                    subTotal += groceryItem.getPrice();
                }
            }
        }
        subTotal = Double.valueOf(decimalFormatter.format(subTotal));

        System.out.println("Sub-total is: \u00a3" + subTotal);
        double discount = this.getPromotionalDiscount(basket);
        if (discount == 0) {
            System.out.println("(No offers available)");
        } else {
            System.out.println("Sub-total is: \u00a3" + subTotal);
            System.out.println("total discount amount is: \u00a3" + decimalFormatter.format(discount));
        }
        String finalPayment = decimalFormatter.format(subTotal - discount);
        return  finalPayment;

    }


    /**
     * will validate the barcode entered by
     * the user
     * @param barCodeIn
     * @return validation result
     */
    public boolean isBarCodeValid(String barCodeIn){

        List<String> barCodeList = inventory.getAllBarCodes();
        if(barCodeList.contains(barCodeIn)) {
            return true;
        }

        return false;
    }

    /**
     * This unction calculates the combined
     * discount for promoted goods
     * @param shoppingBasket
     * @return promotionalDiscount
     */
    public double getPromotionalDiscount(List<GroceryItem> shoppingBasket){
        double promotionalDiscount = 0;
        List<PromotionalOffer> promotionalOfferList = inventory.getPromotionalOffersList();

        for( PromotionalOffer promotionalOffer :promotionalOfferList) {
            int discountedGoodPurchasedCount = 0;
            double itemPrice =0;

            for (GroceryItem groceryItem : shoppingBasket ) {
                String itemCode = groceryItem.getItemCode();


                if (itemCode.equalsIgnoreCase(promotionalOffer.getPromotedItemBarcode())){
                    discountedGoodPurchasedCount ++;
                    itemPrice = groceryItem.getPrice();
                }
            }
            if(discountedGoodPurchasedCount !=0) {
                if (promotionalOffer.getDiscountType() == DeductionType.NUMBER) {
                    promotionalDiscount = +promotionalOffer.getPromotedItemCount() / discountedGoodPurchasedCount * itemPrice;
                } else {

                    double discountedQuantity = +(discountedGoodPurchasedCount / promotionalOffer.getPromotedItemCount());
                    double originalPrice = promotionalOffer.getPromotedItemCount() * discountedQuantity * itemPrice;
                    double discountedPrice = discountedQuantity * promotionalOffer.getDiscountedPrice();
                    promotionalDiscount = (double)(originalPrice *100 - discountedPrice * 100)/100;
                }
            }

        }


        return promotionalDiscount;
    }


    public Map<String, GroceryItem> getGroceryItemsMap() {
        return groceryItemsMap;
    }

    public void setGroceryItemsMap(Map<String, GroceryItem> groceryItemsMap) {
        this.groceryItemsMap = groceryItemsMap;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
