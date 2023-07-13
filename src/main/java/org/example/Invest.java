package org.example;

import org.example.models.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import java.util.List;
import java.util.UUID;


@Component
public class Invest {
    static final Logger log = LoggerFactory.getLogger(Invest.class);


    public static InvestApi getSandBoxApi(String token) {
        InvestApi sandboxApi = InvestApi.createSandbox(token);
        log.info("SandboxApi created");
        return sandboxApi;
    }

    public boolean isValidSandboxApi(String token) {
        InvestApi api = InvestApi.createSandbox(token);

        try {
            Share share = this.findShareByTicker(api, "AAPL");

            log.info(share.getFigi());
        } catch (Exception ex) {
            log.info("token invalid");
            return false;
        }
        log.info("token valid");

        return true;
    }

    public List<Account> getAccount(String token) {
        return getSandBoxApi(token).getSandboxService().getAccountsSync();
    }

    public PositionsResponse getPositionsResponse(String token, String accountId) {
        return getSandBoxApi(token).getSandboxService().getPositionsSync(accountId);
    }

    public PortfolioResponse getPortfolio(String token, int accountId) {
        return getSandBoxApi(token).getSandboxService().getPortfolioSync(getAccount(token).get(accountId).getId());
    }

    public void buyShare(String token, String ticker, long quantity) {
        Account acc = getAccount(token).get(0);

        buyShare(token, ticker, quantity, acc);
    }

    public void buyShare(String token, String ticker, long quantity, int idAcc) {
        Account acc = getAccount(token).get(idAcc);

        buyShare(token, ticker, quantity, acc);
    }

    public void buyShare(String token, String ticker, long quantity, Account acc) {
        InvestApi api = getSandBoxApi(token);

        String figi = findShareByTicker(api, ticker).getFigi();


        Quotation lastPrice = api.getMarketDataService().getLastPricesSync(List.of(figi)).get(0).getPrice();
        Quotation price = Quotation.newBuilder().setUnits(lastPrice.getUnits())
                .setNano(lastPrice.getNano()).build();


        PostOrderResponse orderId = api.getSandboxService().postOrderSync(figi, quantity, price,
                OrderDirection.ORDER_DIRECTION_BUY, acc.getId(),
                OrderType.ORDER_TYPE_LIMIT, UUID.randomUUID().toString());


        OrderState order = api.getSandboxService().getOrderStateSync(acc.getId(), orderId.getOrderId());
        if (order != null ) {
            log.info("Request with id {} is in list", order);
        } else {
            log.info("Request with id {} isn't in list", order);
        }

    }

    public Share findShareByTicker(String token, String ticker) {
        return getSandBoxApi(token).getInstrumentsService().getAllSharesSync().stream().
                filter(s -> s.getTicker().equals(ticker)).findFirst().orElse(null);
    }

    public Share findShareByTicker(InvestApi api, String ticker) {
        return api.getInstrumentsService().getAllSharesSync().stream().
                filter(s -> s.getTicker().equals(ticker)).findFirst().orElse(null);
    }

    public void payIn(String token, Money money) {
        InvestApi api = getSandBoxApi(token);


        api.getSandboxService().payIn(this.getAccount(token).get(money.getAccountId()).getId(), MoneyValue.newBuilder().setUnits(money.getValue()).setCurrency(money.getCurrency()).build());

    }

    public List<OrderState> getOrders(String token, Account acc) {
        InvestApi api = getSandBoxApi(token);

        List<OrderState> order = api.getSandboxService().getOrdersSync(acc.getId());

        return order;
    }

    public static String findTickerByFigi(String figi, String token) {
        InvestApi api = getSandBoxApi(token);

        String ticker = api.getInstrumentsService().getInstrumentByFigiSync(figi).getTicker();

        return ticker;
    }

    public void cancelOrder(String token, String accountId, String orderId) {
        InvestApi api = getSandBoxApi(token);

        api.getSandboxService().cancelOrderSync(accountId, orderId);
        log.info("Order canceled");
    }

}
