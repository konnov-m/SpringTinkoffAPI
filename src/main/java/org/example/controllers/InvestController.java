package org.example.controllers;

import org.example.Invest;

import org.example.dao.UserDao;
import org.example.models.Identifier;
import org.example.models.Money;
import org.example.models.Search;
import org.example.models.User;
import org.example.services.UserService;
import org.example.utils.ParseOrderState;
import org.example.utils.ParsePortfolioPosition;
import org.example.utils.PriceInstruments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;

import javax.validation.Valid;
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

    @PostMapping("/accounts")
    public String accountsPost(Model model, Principal principal) {

        model.addAttribute("isAuth", principal != null);
        User user = userService.findByUsername(principal.getName());

        //Create account
        invest.getSandBoxApi(user.getToken()).getSandboxService().openAccountSync();

        return "redirect:/accounts";
    }

    @GetMapping("/accounts")
    public String accounts(@RequestParam(value = "id", defaultValue = "0") int id, Model model, Principal principal) {
        model.addAttribute("isAuth", principal != null);
        User user = userService.findByUsername(principal.getName());
        List<Account> accounts = invest.getAccount(user.getToken());
        InvestApi api = invest.getSandBoxApi(user.getToken());

        PositionsResponse positionsResponse = invest.getPositionsResponse(user.getToken(),
                accounts.get(id).getId());


        List<ParsePortfolioPosition> positionsList = ParsePortfolioPosition.parse(invest.getPortfolio(user.getToken(),
                id).getPositionsList(), api, user.getToken());

        List<ParseOrderState> orders = ParseOrderState.parseOrderStateList(invest.getOrders(user.getToken(), accounts.get(id)), user.getToken());

        positionsList.removeIf(x -> !x.getInstrumentType().equals("share"));

        model.addAttribute("moneyList", positionsResponse.getMoneyList());
        model.addAttribute("positionsList", positionsList);
        model.addAttribute("orders", orders);
        model.addAttribute("len", accounts.size()-1);
        model.addAttribute("idAcc", new Identifier(id));
        model.addAttribute("ordersNotNull", orders.size() != 0);
        model.addAttribute("positionsNotNull", positionsList.size() != 0);


        return "account";
    }

    @PostMapping("/buy")
    public String buyShare(@ModelAttribute("ticker") Search ticker, Principal principal, Model model) {
        model.addAttribute("isAuth", principal != null);
        User user = userService.findByUsername(principal.getName());

        invest.buyShare(user.getToken(), ticker.getSearch(), 1);


        return "redirect:/";
    }

    @GetMapping("/payin")
    public String payIn(Model model, Principal principal) {
        model.addAttribute("isAuth", principal != null);

        User user = userService.findByUsername(principal.getName());
        List<Account> accounts = invest.getAccount(user.getToken());

        model.addAttribute("Money", new Money());
        model.addAttribute("Currencies", PriceInstruments.currencyMap.keySet());
        model.addAttribute("len", accounts.size()-1);

        return "pay";
    }

    @PostMapping("/payin")
    public String payInPost(@ModelAttribute("Money") @Valid Money money, Model model, Principal principal, BindingResult bindingResult) {
        User user = userService.findByUsername(principal.getName());

        invest.payIn(user.getToken(), money);

        return "redirect:/accounts?id=" + money.getAccountId();
    }

}
