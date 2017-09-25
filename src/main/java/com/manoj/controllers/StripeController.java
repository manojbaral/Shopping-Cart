package com.manoj.controllers;

import com.manoj.models.CartItem;
import com.manoj.models.Product;
import com.manoj.models.User;
import com.manoj.repository.CartItemRepository;
import com.manoj.repository.ProductRepository;
import com.manoj.repository.ShoppingCartRepository;
import com.manoj.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Plan;
import com.stripe.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Manoj Baral on 9/24/2017.
 */
@Controller
@RequestMapping("/stripe")
public class StripeController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String stripe(Model model) {
        List<Product> productList = (List<Product>) productRepository.findAll();

        List<CartItem> cartItemList = (List<CartItem>) cartItemRepository.findAll();

        BigDecimal total = new BigDecimal(0);

        for (CartItem item : cartItemList) {
            total = total.add(item.getSubTotal());
        }

        model.addAttribute("productList", productList);
        model.addAttribute("cartItemList", cartItemList);

        model.addAttribute("total", total.abs());

        return "stripe-cart";
    }

    @RequestMapping("/addToCart")
    public String addToCart(@RequestParam Long id) {
        Product product = productRepository.findOne(id);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQty(1);
        cartItem.setSubTotal(new BigDecimal(product.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        cartItemRepository.save(cartItem);

        return "redirect:/stripe/";
    }

    @RequestMapping("/remove")
    public String remove(@RequestParam Long id) {

        cartItemRepository.delete(id);

        return "redirect:/stripe/";
    }

    @RequestMapping(value = "/updateCart", method = RequestMethod.POST)
    public String updateCart(HttpServletRequest request) {
        Long id = Long.parseLong(request.getParameter("id"));
        int qty = Integer.parseInt(request.getParameter("qty"));

        CartItem cartItem =cartItemRepository.findOne(id);
        cartItem.setQty(qty);
        cartItem.setSubTotal(new BigDecimal(cartItem.getProduct().getPrice()*qty).setScale(2, BigDecimal.ROUND_HALF_UP));

        cartItemRepository.save(cartItem);

        return "redirect:/stripe/";
    }

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(HttpServletRequest request, Model model, @RequestParam double total) {

        // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = "pk_test_2ADQLo0S2G6c9VJF0P7AWZJU";

        // Token is created using Stripe.js or Checkout!
        // Get the payment token submitted by the form:
        String token = request.getParameter("stripeToken");

        // Create a Customer:
        Map<String, Object> customerParams = new HashMap<String, Object>();
        customerParams.put("email", "paying.user@example.com");
        customerParams.put("source", token);
        Customer customer = null;
        try {
            customer = Customer.create(customerParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
            model.addAttribute("invalidCard", true);
            System.out.println("returning...");
            return "forward:/stripe/";
        } catch (APIException e) {
            e.printStackTrace();
        }

        // Charge the Customer instead of the card:
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", 1000);
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", customer.getId());

        Map<String, String> initialMetadata = new HashMap<String, String>();
        initialMetadata.put("order_id", "6735");
        chargeParams.put("metadata", initialMetadata);
        try {
            Charge charge = Charge.create(chargeParams);
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (CardException e) {
            e.printStackTrace();
            model.addAttribute("invalidCard", true);
            System.out.println("returning...");
            return "forward:/stripe/";
        } catch (APIException e) {
            e.printStackTrace();
        }

        // YOUR CODE: Save the customer ID and other info in a database for later.
        User user = new User();
        user.setStripeId(customer.getId());
        user.setUsername("Customer1");
        userRepository.save(user);

        List<CartItem> cartItemList = (List<CartItem>) cartItemRepository.findAll();

        for (CartItem item : cartItemList) {
            cartItemRepository.delete(item);
        }

        model.addAttribute("paymentSuccess", true);

        return "forward:/stripe/";
    }

    @RequestMapping(value = "/repay", method = RequestMethod.POST)
    public String repay(HttpServletRequest request) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = "pk_test_2ADQLo0S2G6c9VJF0P7AWZJU";

        // Token is created using Stripe.js or Checkout!
        // Get the payment token submitted by the form:
        String token = request.getParameter("stripeToken");


        List<User> userList = (List<User>) userRepository.findAll();
        String customerId = userList.get(0).getStripeId();

        // YOUR CODE (LATER): When it's time to charge the customer again, retrieve the customer ID.
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", 1500); // $15.00 this time
        chargeParams.put("currency", "usd");
        chargeParams.put("customer", customerId);
        Charge charge = Charge.create(chargeParams);

        return "forward:/stripe/";

    }

    @RequestMapping(value = "/subscribe", method = RequestMethod.POST)
    public String subscribe(HttpServletRequest request) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {
        // Set your secret key: remember to change this to your live secret key in production
        // See your keys here: https://dashboard.stripe.com/account/apikeys
        Stripe.apiKey = "pk_test_2ADQLo0S2G6c9VJF0P7AWZJU";

//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("name", "Basic Plan");
//        params.put("id", "basic-monthly");
//        params.put("interval", "month");
//        params.put("currency", "usd");
//        params.put("amount", 900);
//
//        Plan plan = Plan.create(params);

        List<User> userList = (List<User>) userRepository.findAll();
        String customerId = userList.get(0).getStripeId();

        Map<String, Object> subscriptionParams = new HashMap<String, Object>();
        subscriptionParams.put("customer", customerId);
        subscriptionParams.put("plan", "basic-monthly");

        //Subscription subscription = Subscription.create(subscriptionParams);

        return "forward:/stripe/";
    }


}
