package org.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.HashMap;
import java.util.Map;

public class PriceInstruments {

    static final Logger log = LoggerFactory.getLogger(PriceInstruments.class);

    public static Map<String, String> currencyMap = new HashMap<>();

    static {
        currencyMap.put("usd", "$");
        currencyMap.put("rub", "Р");
    }

    public static String priceToString(Quotation quotation, String currency) {
        String price = currencyMap.get(currency) + quotation.getUnits() + "," +
                Integer.toString(quotation.getNano()).replaceAll("0","");

        if(quotation.getNano() == 0 ) {
            price = price.substring(0, price.length()-1);
        }

        return price;
    }

    public  static String moneyValueToString(MoneyValue money, String currency) {
        String price;
        if(currencyMap.get(currency) == null) {
            price = money.getUnits() + "," +
                    Integer.toString(money.getNano()).replaceAll("0","");
        } else{
            price = currencyMap.get(currency) + money.getUnits() + "," +
                    Integer.toString(money.getNano()).replaceAll("0","");
        }


        if(money.getNano() == 0) {
            price = price.substring(0, price.length()-1);
        }

        return price;
    }
}
