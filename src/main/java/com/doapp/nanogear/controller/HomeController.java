package com.doapp.nanogear.controller;


import com.doapp.nanogear.model.DTO.UserDTO;
import com.doapp.nanogear.security.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class HomeController {
//    private final UserService userService;

    public HomeController() {
//        this.userService = userService;
        home();
    }

    @GetMapping("/home")
    public String home() {
        return "/form/index";
    }

    @GetMapping("/laptop-re")
    public String laptopRe() {

        return "form/laptop-re";
    }

    @GetMapping("/login")
    public String login(Model model) {
//        userLoginDTO userDTO = new userLoginDTO();
//        model.addAttribute("userDTO",userDTO);
        return "/form/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xóa thông tin người dùng khỏi phiên làm việc
        session.removeAttribute("loggedInUser");
        session.removeAttribute("userRole");
        session.removeAttribute("cart");
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
//        User user = new User();
//        ContactUser contactUser = new ContactUser();
//        model.addAttribute("user", user);
//        model.addAttribute("contactUser", contactUser);
        UserDTO registrationDTO = new UserDTO();
        model.addAttribute("registrationDTO", registrationDTO);
        return "form/register";
    }

    @GetMapping("/myCart")
    public String myCart() {
        return "/form/cart";
    }

    @GetMapping("/inForUser/{username}")
    public String inforUser(@PathVariable String userName) {
        return "/form/inforuser";
    }
}
