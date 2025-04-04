package utm.tn.dari.modules.bank_credit;


import utm.tn.dari.modules.bank_credit.dto.CreditResponse;

public interface  CreditService {

    CreditResponse calculateCredit(double apartmentPrice, double personalContribution, int loanDurationMonths);
}
