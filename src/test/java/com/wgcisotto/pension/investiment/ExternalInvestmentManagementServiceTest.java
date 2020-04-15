package com.wgcisotto.pension.investiment;

import com.wgcisotto.pension.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ExternalInvestmentManagementServiceTest {


    public static final String FUND_ID = "FUND_ID";

    @Spy
    private ExternalInvestmentManagementService underTest;

//    @BeforeEach
//    void setup(){
//        MockitoAnnotations.initMocks(this);
//    }

    @Test
    void shouldBeAbleToBuyPensionFundInvestmentIfEnoughCashInAccount() throws IOException {
//        when(underTest.executeInvestmentTransaction(anyString(), any(BigDecimal.class), anyString()))
//                .thenReturn(true);
//        does not work when using parcial mock @Spy

        doReturn(true).when(underTest).executeInvestmentTransaction(anyString(), any(BigDecimal.class), anyString());

        Account account = Account.builder().availableCash(new BigDecimal(10000000))
                .investments(new HashSet<>()).build();

        final BigDecimal desiredInvestmentAmount = new BigDecimal(1000000);

        underTest.buyInvestmentFund(account, FUND_ID, desiredInvestmentAmount);

        assertEquals(new BigDecimal(9000000), account.getAvailableCash());
        assertTrue(account.getInvestments().contains(FUND_ID));



    }




}