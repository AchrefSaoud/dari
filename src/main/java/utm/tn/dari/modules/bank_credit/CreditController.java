package utm.tn.dari.modules.bank_credit;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utm.tn.dari.modules.bank_credit.dto.CreditResponse;

@RestController
@RequestMapping("/api/credit")
public class CreditController {
    final private CreditService creditService;
    @Autowired
    CreditController( CreditService creditService){
        this.creditService = creditService;
    }

    @Operation(
            summary = "Calculate monthly mortgage payment",
            description = "Calculates the monthly payment based on apartment price, personal contribution and loan duration"
                    )
    @GetMapping("/simulate")
    public CreditResponse calculateMonthlyPayment(
            @RequestParam double apartmentPrice,
            @RequestParam double personalContribution,
            @RequestParam int loanDurationMonths) {
        return creditService.calculateCredit(apartmentPrice, personalContribution, loanDurationMonths);
    }

}
