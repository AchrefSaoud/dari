package utm.tn.dari.modules.bank_credit.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditResponse {
    // Getters
    private final double apartmentPrice;
    private final double personalContribution;
    private double loanAmount;
    private double annualInterestRate;
    private int loanDurationMonths;
    private double monthlyPayment;

    // Constructor
    public CreditResponse(double apartmentPrice, double personalContribution,
                          double loanAmount, double annualInterestRate,
                          int loanDurationMonths, double monthlyPayment) {
        this.apartmentPrice = apartmentPrice;
        this.personalContribution = personalContribution;
        this.loanAmount = loanAmount;
        this.annualInterestRate = annualInterestRate;
        this.loanDurationMonths = loanDurationMonths;
        this.monthlyPayment = monthlyPayment;
    }

}
