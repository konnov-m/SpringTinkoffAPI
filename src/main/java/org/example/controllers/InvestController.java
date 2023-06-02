package org.example.controllers;

import org.example.Invest;

import org.example.dao.UserDao;
import org.example.models.Search;
import org.example.models.User;
import org.example.services.UserService;
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

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@Controller
public class InvestController {
    private Invest invest;
    private UserDao userDao;

    private UserService userService;

    @Autowired
    public InvestController(Invest invest) {
        this.invest = invest;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @GetMapping("/")
    public String search(@ModelAttribute("search") Search search, Principal principal, Model model) {
        model.addAttribute("isAuth", principal != null);

        return "index";
    }

    @GetMapping("/share")
    public String show(@RequestParam(value = "search") String shareName, Model model, Principal principal) {
        model.addAttribute("isAuth", principal != null);

        User user = userService.findByUsername(principal.getName());

        InvestApi api = invest.getSandBoxApi(user.getToken());

        Share share = api.getInstrumentsService().getAllSharesSync().stream().
                                        filter(s -> s.getTicker().equals(shareName)).findFirst().orElse(null);

        List<LastPrice> list = api.getMarketDataService().getLastPricesSync(Collections.singleton(share.getFigi()));


        Quotation price = list.get(list.size()-1).getPrice();


        model.addAttribute("share", share);
        model.addAttribute("price", PriceInstruments.priceToString(price, share.getCurrency()));
        model.addAttribute("user", user);
        return "show";
    }

}
