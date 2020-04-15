package com.wgcisotto.pension.withdrawal;

import com.wgcisotto.pension.Account;
import com.wgcisotto.pension.setup.BackgroundCheckService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AccountClosingServiceTest {


    //used Clock to test an fixed time, I particually don't like this approach I prefer power mock that can
    //mock static methods.

    @Mock
    private BackgroundCheckService backgroundCheckService;

    private Instant fixedTime = LocalDate.of(2019, 7, 4)
            .atStartOfDay(ZoneId.systemDefault()).toInstant();

    private Clock clock = Clock.fixed(fixedTime, ZoneId.systemDefault());

    private AccountClosingService underTest = new AccountClosingService(backgroundCheckService, clock);

    @Test
     void shouldDeclinAccountClosingTodayIfHolderReachedRetirementTomorrow() throws IOException {
        Account account = Account.builder()
                .dob(LocalDate.of(1955, 7, 4))
                .build();

        final AccountClosingResponse accountClosingResponse = underTest.closeAccount(account);

        assertEquals(AccountClosingStatus.CLOSING_DENIED, accountClosingResponse.getStatus());
        assertEquals(LocalDateTime.ofInstant(fixedTime, ZoneOffset.systemDefault()),
                accountClosingResponse.getProcesingDate());
    }


}