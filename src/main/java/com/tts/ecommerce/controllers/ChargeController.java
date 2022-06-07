package com.tts.ecommerce.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.tts.ecommerce.entity.ChargeRequest;
import com.tts.ecommerce.entity.ChargeRequest.Currency;
import com.tts.ecommerce.models.User;
import com.tts.ecommerce.service.StripeService;
import com.tts.ecommerce.service.UserService;

@Controller
public class ChargeController {

    @Autowired
    private StripeService paymentsService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/charge")
    public String charge(ChargeRequest chargeRequest, Model model)
      throws StripeException {
        chargeRequest.setDescription("Example charge");
        chargeRequest.setCurrency(Currency.USD);
        Charge charge = paymentsService.charge(chargeRequest);
        model.addAttribute("id", charge.getId());
        model.addAttribute("status", charge.getStatus());
        model.addAttribute("chargeId", charge.getId());
        model.addAttribute("balance_transaction", charge.getBalanceTransaction());
        //put this at the end of the post request just before the return.
        User user = userService.getLoggedInUser();
        
        user.getCart().clear();
        
        return "result";
    }

    @ExceptionHandler(StripeException.class)
    public String handleError(Model model, StripeException ex) {
        model.addAttribute("error", ex.getMessage());
        return "result";
    }
}
