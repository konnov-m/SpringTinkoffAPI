package org.example.controllers;

import org.example.Invest;

import org.example.dao.UserDao;
import org.example.models.Search;
import org.example.models.User;
import org.example.services.UserService;
import org.example.utils.ParsePortfolioPosition;
import org.example.utils.PriceInstruments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tinkoff.piapi.contract.v1.*;
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
        User user = userService.findByUsername(principal.getName());

        InvestApi api = invest.getSandBoxApi(user.getToken());
        Share share = invest.findShareByTicker(api, shareName);

        List<LastPrice> list = api.getMarketDataService().getLastPricesSync(Collections.singleton(share.getFigi()));
        Quotation price = list.get(0).getPrice();


        model.addAttribute("isAuth", principal != null); // always true because of Security config
        model.addAttribute("share", share);
        model.addAttribute("price", PriceInstruments.priceToString(price, share.getCurrency()));
        model.addAttribute("user", user);
        model.addAttribute("ticker", new Search(share.getTicker()));

        return "show";
    }

    @GetMapping("/accounts")
    public String accounts(Model model, Principal principal) {
        model.addAttribute("isAuth", principal != null);
        User user = userService.findByUsername(principal.getName());

        PositionsResponse positionsResponse = invest.getPositionsResponse(user.getToken(),
                invest.getAccount(user.getToken()).get(0).getId());

        List<ParsePortfolioPosition> positionsList = ParsePortfolioPosition.parse(invest.getPortfolio(user.getToken(),
                invest.getAccount(user.getToken()).get(0).getId()).getPositionsList(), invest.getSandBoxApi(user.getToken()));

        positionsList.removeIf(x -> !x.getInstrumentType().equals("share"));

        model.addAttribute("moneyList", positionsResponse.getMoneyList());
        model.addAttribute("positionsList", positionsList);

        return "account";
    }

    @PostMapping("/buy")
    public String buyShare(@ModelAttribute("ticker") Search ticker, Principal principal, Model model) {
        model.addAttribute("isAuth", principal != null);
        User user = userService.findByUsername(principal.getName());

        invest.buyShare(user.getToken(), ticker.getSearch(), 1);


        return "redirect:/";
    }

}
