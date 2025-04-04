package utm.tn.dari.modules.bank_credit;

import org.springframework.stereotype.Service;
import utm.tn.dari.modules.bank_credit.dto.CreditResponse;

@Service
public class CreditServiceImpl implements CreditService {
    private static final double ANNUAL_INTEREST_RATE = 0.05; // 5%

    public CreditResponse calculateCredit(double apartmentPrice, double personalContribution, int loanDurationMonths) {
        // Calculate loan amount
        double loanAmount = apartmentPrice - personalContribution;

        // Convert annual rate to monthly rate
        double monthlyInterestRate = ANNUAL_INTEREST_RATE / 12;

        // Calculate monthly payment using the formula:
        // M = P [ i(1 + i)^n ] / [ (1 + i)^n – 1]
        double monthlyPayment = loanAmount *
                (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanDurationMonths)) /
                (Math.pow(1 + monthlyInterestRate, loanDurationMonths) - 1);

        return new CreditResponse(
                apartmentPrice,
                personalContribution,
                loanAmount,
                ANNUAL_INTEREST_RATE,
                loanDurationMonths,
                monthlyPayment
        );
    }
}
