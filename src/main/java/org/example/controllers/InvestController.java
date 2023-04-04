package org.example.controllers;

import org.example.Invest;

import org.example.models.Search;
import org.example.utils.PriceInstruments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.contract.v1.Share;

import java.util.Collections;
import java.util.List;

@Controller
public class InvestController {

    private final static String token = "t.5hQppYOnC64WLP9EMYVuL72f0nZYDY6ejwUfK6-IXcaolRpIw6RGBcoQ1lxSkcLvU9scg__bUEZwAwiDdZL3uA";
    private Invest invest;

    @Autowired
    public InvestController(Invest invest) {
        this.invest = invest;
    }

    @GetMapping("/")
    public String search(@ModelAttribute("search") Search search) {
        return "index";
    }

    @GetMapping("/share")
    public String show(@RequestParam(value = "search") String shareName, Model model) {
        Share share = invest.getSandBoxApi(token).getInstrumentsService().getAllSharesSync().stream().
                filter(s -> s.getTicker().equals(shareName)).findFirst().orElse(null);

        List<LastPrice> list = invest.getSandBoxApi(token).getMarketDataService().
                getLastPricesSync(Collections.singleton(share.getFigi()));

        Quotation price = list.get(list.size()-1).getPrice();


        model.addAttribute("share", share);
        model.addAttribute("price", PriceInstruments.priceToString(price, share.getCurrency()));
        return "show";
    }

}
