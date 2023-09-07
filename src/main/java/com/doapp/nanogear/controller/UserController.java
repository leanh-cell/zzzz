package com.doapp.nanogear.controller;

//import com.doapp.nanogear.model.DTO.userLoginDTO;
import com.doapp.nanogear.model.data.Cart;
import com.doapp.nanogear.model.data.ContactUser;
import com.doapp.nanogear.model.DTO.UserRegistrationDTO;
import com.doapp.nanogear.model.data.User;
import com.doapp.nanogear.security.CartService;
import com.doapp.nanogear.security.ContactUserService;
import com.doapp.nanogear.security.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final CartService cartService;
    private final ContactUserService contactUserService;

    public UserController(UserService userService,CartService cartService,ContactUserService contactUserService) {
        this.userService = userService;
        this.cartService = cartService;
        this.contactUserService = contactUserService;
    }

    public enum UserRole {
        USER("USER"), ADMIN("ADMIN");
        private final String value;

        UserRole(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("usernameOrEmail") String usernameOrEmail,@RequestParam("password") String password, Model model, HttpSession session) {
        // Xác thực người dùng và lấy thông tin từ cơ sở dữ liệu
        User authenticatedUser = userService.authenticateUser(usernameOrEmail, password);

        if (authenticatedUser != null) {
                // Lưu thông tin người dùng vào phiên làm việc
                session.setAttribute("loggedInUser", authenticatedUser);
                session.setAttribute("userRole",authenticatedUser.role);
                String userRoleValue = authenticatedUser.getRole(); // "admin", "user"
                UserRole userRole = UserRole.valueOf(userRoleValue.toUpperCase());
                System.out.println(session + "/ " + authenticatedUser.username + "/ " + authenticatedUser.id + " /" + userRole + " /");
                List<Cart> cart = cartService.getCartsByUserId(authenticatedUser.id);
//                for (Cart cartItem : cart){
//                    System.out.println("user id: " + cartItem.getUser());
//                    System.out.println("Product ID: " + cartItem.getProduct());
//                    System.out.println("Quantity: " + cartItem.getQuantity());
//                }
            System.out.println("gio hang : "+ cart );

                session.setAttribute("cart", cart);
                if (userRole == UserRole.ADMIN) {
                    return "redirect:/admin/home";
                } else if (userRole == UserRole.USER) {
                    return "redirect:/home";
                }
//            }
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng.");
            return "/form/index";
        }
        return null;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Xóa thông tin người dùng khỏi phiên làm việc
        session.removeAttribute("loggedInUser");
        return "redirect:/home";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registrationDTO") UserRegistrationDTO registrationDTO) {
        User user = registrationDTO.getUser();
        ContactUser contactUser = registrationDTO.getContactUser();
        // Kiểm tra xem người dùng đã tồn tại chưa
        if (userService.findByUsernameOrEmail(user.getUsername() != null ? user.getUsername():user.getEmail()) != null) {
            // Xử lý lỗi: người dùng đã tồn tại
            return "redirect:/users/register?error";
        }

        // Mã hóa mật khẩu trước khi lưu vào đối tượng User
        String encodedPassword = encodePassword(user.getPassword());
        user.setPassword(encodedPassword);
        contactUser.setUser(user);
        // Lưu đối tượng User vào cơ sở dữ liệu
        userService.save(user);
        contactUserService.saveUserInfo(contactUser);

        return "redirect:/users/login";
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
