package org.example;

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


    public InvestApi getSandBoxApi(String token) {
        InvestApi sandboxApi = InvestApi.createSandbox(token);
        log.info("SandboxApi created");
        return sandboxApi;
    }

    public List<Account> getAccount(String token) {
        return getSandBoxApi(token).getSandboxService().getAccountsSync();
    }

    public PositionsResponse getPositionsResponse(String token, String accountId) {
        return getSandBoxApi(token).getSandboxService().getPositionsSync(accountId);
    }

    public PortfolioResponse getPortfolio(String token, String accountId) {
        return getSandBoxApi(token).getSandboxService().getPortfolioSync(getAccount(token).get(0).getId());
    }

    public void buyShare(String token, String ticker, long quantity) {
        Account acc = getAccount(token).get(0);

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
    }

    public Share findShareByTicker(String token, String ticker) {
        return getSandBoxApi(token).getInstrumentsService().getAllSharesSync().stream().
                filter(s -> s.getTicker().equals(ticker)).findFirst().orElse(null);
    }

    public Share findShareByTicker(InvestApi api, String ticker) {
        return api.getInstrumentsService().getAllSharesSync().stream().
                filter(s -> s.getTicker().equals(ticker)).findFirst().orElse(null);
    }

}
