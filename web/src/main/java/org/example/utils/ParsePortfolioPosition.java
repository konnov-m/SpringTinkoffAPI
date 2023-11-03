package org.example.utils;

import ru.tinkoff.piapi.contract.v1.PortfolioPosition;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.ArrayList;
import java.util.List;

public final class ParsePortfolioPosition {
    private String figi;
    private String ticker;
    private String price;
    private long lots;

    private String instrumentType;


    public static List<ParsePortfolioPosition> parse(List<PortfolioPosition> portfolioPositions, InvestApi api, String token) {
        List<ParsePortfolioPosition> parsed = new ArrayList<>();

        for (PortfolioPosition portfolioPosition:portfolioPositions) {
            parsed.add(new ParsePortfolioPosition(portfolioPosition, api, token));
        }

        return parsed;
    }

    public ParsePortfolioPosition(PortfolioPosition portfolioPosition, InvestApi api, String token) {
        figi = portfolioPosition.getFigi();
        price = PriceInstruments.moneyValueToString(portfolioPosition.getCurrentPrice(), token);

        lots = portfolioPosition.getQuantity().getUnits();

        if(portfolioPosition.getInstrumentType().equals("share")) {
            ticker = api.getInstrumentsService().getShareByFigiSync(figi).getTicker();
            instrumentType = "share";
        } else if(portfolioPosition.getInstrumentType().equals("currency")) {
            ticker = api.getInstrumentsService().getCurrencyByFigiSync(figi).getName();
            instrumentType = "currency";
        }


    }

    private String nanoToString(int nano) {
        if (nano != 0) {
            while(nano % 10 == 0) {
                nano = nano / 10;
            }
        }

        return String.valueOf(nano);
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

    @Override
    public String toString() {
        return "ParsePortfolioPosition{" +
                "figi='" + figi + '\'' +
                ", ticker='" + ticker + '\'' +
                ", price='" + price + '\'' +
                ", lots=" + lots +
                '}';
    }
}
