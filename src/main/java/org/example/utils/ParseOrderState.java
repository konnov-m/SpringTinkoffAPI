package org.example.utils;


import org.example.Invest;
import ru.tinkoff.piapi.contract.v1.OrderState;

import java.util.ArrayList;
import java.util.List;

public class ParseOrderState {

    private String figi;
    private String ticker;
    private String price;
    private long lots;

    private String instrumentType;

    private String orderId;

    public static List<ParseOrderState> parseOrderStateList(List<OrderState> orders, String token) {
        List<ParseOrderState> parsed = new ArrayList<>();

        for (OrderState order :orders) {
            parsed.add(new ParseOrderState(order, token));
        }

        return parsed;
    }

    public ParseOrderState(OrderState orderState, String token) {
        this.figi = orderState.getFigi();
        this.price = PriceInstruments.moneyValueToString(orderState.getTotalOrderAmount(), orderState.getCurrency());
        this.lots = orderState.getLotsRequested();
        this.instrumentType = orderState.getInstrumentUid();
        this.orderId = orderState.getOrderId();

        this.ticker = Invest.findTickerByFigi(figi, token);
    }

    public ParseOrderState() {
    }

    public String getFigi() {
        return figi;
    }

    public void setFigi(String figi) {
        this.figi = figi;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getLots() {
        return lots;
    }

    public void setLots(long lots) {
        this.lots = lots;
    }

    public String getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(String instrumentType) {
        this.instrumentType = instrumentType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "ParseOrderState{" +
                "figi='" + figi + '\'' +
                ", ticker='" + ticker + '\'' +
                ", price='" + price + '\'' +
                ", lots=" + lots +
                ", instrumentType='" + instrumentType + '\'' +
                '}';
    }
}
