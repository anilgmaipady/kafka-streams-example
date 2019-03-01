package com.util.datagen;

import com.model.PublicTradedCompany;
import com.model.StockTransaction;
import com.github.javafaker.Faker;
import com.github.javafaker.Finance;
import com.github.javafaker.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;


public class DataGenerator {

    private static Faker dateFaker = new Faker();
    private static Supplier<Date> timestampGenerator = () -> dateFaker.date().past(15, TimeUnit.MINUTES, new Date());

    private DataGenerator() {
    }

    //KEEP
    public static List<StockTransaction> generateStockTransactions(List<Customer> customers, List<PublicTradedCompany> companies, int number) {
        List<StockTransaction> transactions = new ArrayList<>(number);
        Faker faker = new Faker();
        for (int i = 0; i < number; i++) {
            int numberShares = faker.number().numberBetween(100, 50000);
            Customer customer = customers.get(faker.number().numberBetween(0, customers.size()));
            PublicTradedCompany company = companies.get(faker.number().numberBetween(0, companies.size()));
            Date transactionDate = timestampGenerator.get();
            StockTransaction transaction = StockTransaction.newBuilder().withCustomerId(customer.customerId).withTransactionTimestamp(transactionDate)
                    .withIndustry(company.getIndustry()).withSector(company.getSector()).withSharePrice(company.updateStockPrice()).withShares(numberShares)
                    .withSymbol(company.getSymbol()).withPurchase(true).build();
            transactions.add(transaction);
        }
        return transactions;
    }

    //KEEP
    public static List<StockTransaction> generateStockTransactionsForIQ(int number) {
        return generateStockTransactions(generateCustomersForInteractiveQueries(), generatePublicTradedCompaniesForInteractiveQueries(), number);
    }

    /**
     * This is a special method for returning the List of PublicTradedCompany to use for
     * Interactive Query examples.  This method uses a fixed list of company names and ticker symbols
     * to make querying by key easier for demo purposes.
     *
     * @return List of PublicTradedCompany for interactive queries
     */
    //KEEP
    public static List<PublicTradedCompany> generatePublicTradedCompaniesForInteractiveQueries() {
        List<String> symbols = Arrays.asList("AEBB", "VABC", "ALBC", "EABC", "BWBC", "BNBC", "MASH", "BARX", "WNBC", "WKRP");
        List<String> companyName = Arrays.asList("Acme Builders", "Vector Abbot Corp", "Albatros Enterprise", "Enterprise Atlantic",
                "Bell Weather Boilers", "Broadcast Networking", "Mobile Surgical", "Barometer Express", "Washington National Business Corp", "Cincinnati Radio Corp.");
        List<PublicTradedCompany> companies = new ArrayList<>();
        Faker faker = new Faker();

        for (int i = 0; i < symbols.size(); i++) {
            double volatility = Double.parseDouble(faker.options().option("0.01", "0.02", "0.03", "0.04", "0.05", "0.06", "0.07", "0.08", "0.09"));
            double lastSold = faker.number().randomDouble(2, 15, 150);
            String sector = faker.options().option("Energy", "Finance", "Technology", "Transportation", "Health Care");
            String industry = faker.options().option("Oil & Gas Production", "Coal Mining", "Commercial Banks", "Finance/Investors Services", "Computer Communications Equipment", "Software Consulting", "Aerospace", "Railroads", "Major Pharmaceuticals");
            companies.add(new PublicTradedCompany(volatility, lastSold, symbols.get(i), companyName.get(i), sector, industry));
        }
        return companies;
    }

    /**
     * Special method for generating customers with static customer ID's so
     * we can easily run interactive queries with a predictable list of names
     *
     * @return customer list
     */

    //KEEP
    public static List<Customer> generateCustomersForInteractiveQueries() {
        List<Customer> customers = new ArrayList<>(10);
        List<String> customerIds = Arrays.asList("12345678", "222333444", "33311111", "55556666", "4488990011", "77777799", "111188886", "98765432", "665552228", "660309116");
        Faker faker = new Faker();
        List<String> creditCards = generateCreditCardNumbers(10);
        for (int i = 0; i < 10; i++) {
            Name name = faker.name();
            String creditCard = creditCards.get(i);
            String customerId = customerIds.get(i);
            customers.add(new Customer(name.firstName(), name.lastName(), customerId, creditCard));
        }
        return customers;
    }

    private static List<String> generateCreditCardNumbers(int numberCards) {
        int counter = 0;
        Pattern visaMasterCardAmex = Pattern.compile("(\\d{4}-){3}\\d{4}");
        List<String> creditCardNumbers = new ArrayList<>(numberCards);
        Finance finance = new Faker().finance();
        while (counter < numberCards) {
            String cardNumber = finance.creditCard();
            if (visaMasterCardAmex.matcher(cardNumber).matches()) {
                creditCardNumbers.add(cardNumber);
                counter++;
            }
        }
        return creditCardNumbers;
    }

    public static class Customer {
        private String firstName;
        private String lastName;
        private String customerId;
        private String creditCardNumber;

        private Customer(String firstName, String lastName, String customerId, String creditCardNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.customerId = customerId;
            this.creditCardNumber = creditCardNumber;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getCreditCardNumber() {
            return creditCardNumber;
        }
    }

    private static class Store {
        private String employeeId;
        private String zipCode;
        private String storeId;
        private String department;

        private Store(String employeeId, String zipCode, String storeId, String department) {
            this.employeeId = employeeId;
            this.zipCode = zipCode;
            this.storeId = storeId;
            this.department = department;
        }
    }
}
