package org.example.controllers;

import org.example.Invest;

import org.example.dao.UserDao;
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
import ru.tinkoff.piapi.core.InvestApi;

import java.util.Collections;
import java.util.List;

@Controller
public class InvestController {

    private final static String token = "t.689sori3m0JEHD86Rejwl7Ewzp_53AD_TmuMFE8t7qWfoIJx2FNK9dWnCKCkzsT0iL-Plwt_tCljZVGcr4bqWQ";
    private Invest invest;
    private UserDao userDao;

    @Autowired
    public InvestController(Invest invest) {
        this.invest = invest;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/")
    public String search(@ModelAttribute("search") Search search) {
        return "index";
    }

    @GetMapping("/share")
    public String show(@RequestParam(value = "search") String shareName, Model model) {
        InvestApi api = invest.getSandBoxApi(token);

        Share share = api.getInstrumentsService().getAllSharesSync().stream().
                                        filter(s -> s.getTicker().equals(shareName)).findFirst().orElse(null);

        List<LastPrice> list = api.getMarketDataService().getLastPricesSync(Collections.singleton(share.getFigi()));


        Quotation price = list.get(list.size()-1).getPrice();


        model.addAttribute("share", share);
        model.addAttribute("price", PriceInstruments.priceToString(price, share.getCurrency()));
        return "show";
    }

}
