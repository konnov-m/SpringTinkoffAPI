package org.example.controllers;

import org.example.Invest;
import org.example.dao.RolesDao;
import org.example.dao.UserDao;
import org.example.models.Role;
import org.example.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    private UserDao userDao;

    private PasswordEncoder passwordEncoder;

    private RolesDao rolesDao;

    private Invest invest;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setRolesDao(RolesDao rolesDao) {
        this.rolesDao = rolesDao;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setInvestApi(Invest invest) {
        this.invest = invest;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model, Principal principal) {
        model.addAttribute("isAuth", principal != null);
        model.addAttribute("user",new User());
        model.addAttribute("error", error != null);
        model.addAttribute("logout", logout != null);

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/signup")
    public String signupGet(@RequestParam(value = "usernameExist", required = false) String usernameExist,
                            @RequestParam(value = "invalidToken", required = false) String invalidToken,
            @ModelAttribute("user") User user, Model model) {
        model.addAttribute("usernameExist", usernameExist != null);
        model.addAttribute("invalidToken", invalidToken != null);

        return "signup";
    }

    @PostMapping("/signup")
    public String signupPost(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "signup";
        }

        if (userDao.getUser(user.getUsername()) != null) {
            return "redirect:/signup?usernameExist";
        }

        if (!invest.isValidSandboxApi(user.getToken())) {
            return "redirect:/signup?invalidToken";
        }



        Role role = rolesDao.getRole("ROLE_USER");

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRoles(role);

        userDao.update(user);

        return "redirect:/";
    }

    @GetMapping("/update")
    public String updateGet(@RequestParam(value = "invalidToken", required = false) String invalidToken,
                            Principal principal, Model model) {
        model.addAttribute("isAuth", principal != null);
        model.addAttribute("user", userDao.getUser(principal.getName()));
        model.addAttribute("invalidToken", invalidToken != null);

        return "update";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update";
        }

        userDao.update(user);

        return "redirect:/update";
    }
}
