package org.example.utils;

import ru.tinkoff.piapi.contract.v1.Quotation;

import java.util.HashMap;
import java.util.Map;

public class PriceInstruments {

    private static Map<String, String> currencyMap = new HashMap<>();

    static {
        currencyMap.put("usd", "$");
    }

    public static String priceToString(Quotation quotation, String currency) {
        String price = quotation.getUnits() + "," +
                Integer.toString(quotation.getNano()).replaceAll("0","") +
                currencyMap.get(currency);
        return price;
    }
}
