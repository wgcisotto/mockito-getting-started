package com.wgcisotto.pension.investiment;

import com.wgcisotto.pension.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;

public interface InvestmentManagementService {

    void addFunds(Account account, BigDecimal investimentAmount, Currency investmentCcy) throws IllegalArgumentException;

    boolean buyInvestmentFund(Account account, String fundId, BigDecimal investmentAmount) throws IllegalArgumentException, IOException;

    boolean sellInvestmentFund(Account account, String fundId, BigDecimal investmentAmount) throws IllegalArgumentException, IOException;
}
