package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.core.InvestApi;


@Component
public class Invest {
    static final Logger log = LoggerFactory.getLogger(Invest.class);


    public InvestApi getSandBoxApi(String token) {
        InvestApi sandboxApi = InvestApi.createSandbox(token);
        log.info("SandboxApi created");
        return sandboxApi;
    }

}
