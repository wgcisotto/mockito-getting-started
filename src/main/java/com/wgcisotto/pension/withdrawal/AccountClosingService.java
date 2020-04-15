package com.wgcisotto.pension.withdrawal;

import com.wgcisotto.pension.Account;
import com.wgcisotto.pension.setup.BackgroundCheckResults;
import com.wgcisotto.pension.setup.BackgroundCheckService;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;

import static com.wgcisotto.pension.setup.AccountOpeningService.UNACCEPTABLE_RISK_PROFILE;
import static com.wgcisotto.pension.withdrawal.AccountClosingStatus.*;

public class AccountClosingService {

    public static final int RETIREMENT_AGE = 65;
    private BackgroundCheckService backgroundCheckService;
    private Clock clock;

    public AccountClosingService(BackgroundCheckService backgroundCheckService, Clock clock) {
        this.backgroundCheckService = backgroundCheckService;
        this.clock = clock;
    }

    public AccountClosingResponse closeAccount(Account account) throws IOException {
        Period accountHolderAge = Period.between(account.getDob(), LocalDate.now(clock));
        if(accountHolderAge.getYears() < RETIREMENT_AGE){
            return buildAccountClosingResponseByStatus(CLOSING_DENIED);
        }else{
            final BackgroundCheckResults backgroundCheckResults = backgroundCheckService.confirm(
                    account.getFirtName(),
                    account.getLastName(),
                    account.getTaxId(),
                    account.getDob());
            if(Objects.nonNull(backgroundCheckResults)){
                return buildAccountClosingResponseByStatus(CLOSING_PENDING);
            }else {
                final String riskProfile = backgroundCheckResults.getRiskProfile();
                if(riskProfile.equals(UNACCEPTABLE_RISK_PROFILE)) {
                    return buildAccountClosingResponseByStatus(CLOSING_PENDING);
                } else {
                    return buildAccountClosingResponseByStatus(CLOSING_OK);
                }
            }
        }
    }

    private AccountClosingResponse buildAccountClosingResponseByStatus(AccountClosingStatus closingPending) {
        return AccountClosingResponse.builder()
                .status(closingPending)
                .procesingDate(LocalDateTime.now(clock))
                .build();
    }
}
