package com.wgcisotto.pension.setup;

import com.wgcisotto.pension.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;

import static com.wgcisotto.pension.setup.AccountOpeningService.UNACCEPTABLE_RISK_PROFILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountOpeningServiceTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final LocalDate DOB  = LocalDate.of(1990, 1, 1);
    private static final String TAX_ID = "123xyz9";
    public static final String SOMETHING_NOT_UNACCEPTABLE = "something_not_unacceptable";
    public static final String SOME_ID = "someId";

//    @InjectMocks

    private AccountOpeningService underTest;

    @Mock
    private BackgroundCheckService backgroundCheckService;// = mock(BackgroundCheckService.class);
    @Mock
    private ReferenceIdsManager referenceIdsManager;// = mock(ReferenceIdsManager.class);
    @Mock
    private AccountRepository accountRepository;// = mock(AccountRepository.class);

    private AccountOpeningEventPublisher accountOpeningEventPublisher = mock(AccountOpeningEventPublisher.class);

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.initMocks(this);
        underTest = new AccountOpeningService(backgroundCheckService, referenceIdsManager, accountRepository, accountOpeningEventPublisher);
    }

    @Test
    void shouldOpenAccount() throws IOException {
        BackgroundCheckResults backgroundCheckResults = new BackgroundCheckResults(SOMETHING_NOT_UNACCEPTABLE, 100);

        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(backgroundCheckResults);

        when(referenceIdsManager.obtainId(eq(FIRST_NAME),
                anyString(),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB))).thenReturn(SOME_ID);

        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(FIRST_NAME,
                LAST_NAME, TAX_ID, DOB);

        assertEquals(AccountOpeningStatus.OPENED, accountOpeningStatus);

        verify(accountRepository).save(SOME_ID,
                FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB,
                backgroundCheckResults);

        verify(accountOpeningEventPublisher).notify(SOME_ID);
    }

    @Test
    void shouldDeclineAccountIfUnnaceptableRiskProfileBackgroundCheckResponseReceived() throws IOException {
        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(new BackgroundCheckResults(UNACCEPTABLE_RISK_PROFILE, 0));

        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(FIRST_NAME, LAST_NAME, TAX_ID, DOB);

        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
    }

    @Test
    public void shouldDeclineAccountIfNullBackgroundCheckResponseReceived() throws IOException {
        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(null);

        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(FIRST_NAME,
                LAST_NAME, TAX_ID, DOB);

        assertEquals(AccountOpeningStatus.DECLINED, accountOpeningStatus);
    }

    @Test
    void shouldThrowIfBackgroundChecksServiceThrows() throws IOException {
        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenThrow(new IOException());
        assertThrows(IOException.class, () -> underTest.openAccount(FIRST_NAME,
                LAST_NAME, TAX_ID, DOB));
    }

    @Test
    void shouldThrowIfReferenceIdsManagerThrows() throws IOException {
        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(new BackgroundCheckResults(SOMETHING_NOT_UNACCEPTABLE, 100));

        when(referenceIdsManager.obtainId(eq(FIRST_NAME),
                anyString(),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB))).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> underTest.openAccount(FIRST_NAME,
                LAST_NAME, TAX_ID, DOB));
    }

    @Test
    void shouldThrowIfAccountRepositoryThrows() throws IOException {
        final BackgroundCheckResults backgroundCheckResults = new BackgroundCheckResults(SOMETHING_NOT_UNACCEPTABLE, 100);

        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(backgroundCheckResults);

        when(referenceIdsManager.obtainId(eq(FIRST_NAME),
                anyString(),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB))).thenReturn(SOME_ID);

        when(accountRepository.save(SOME_ID, FIRST_NAME, LAST_NAME, TAX_ID, DOB, backgroundCheckResults))
                .thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> underTest.openAccount(FIRST_NAME,
                LAST_NAME, TAX_ID, DOB));
    }

    @Test
    void shouldThrowIfEventPublisherThrows() throws IOException {
        final BackgroundCheckResults backgroundCheckResults = new BackgroundCheckResults(SOMETHING_NOT_UNACCEPTABLE, 100);

        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(backgroundCheckResults);

        when(referenceIdsManager.obtainId(eq(FIRST_NAME),
                anyString(),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB))).thenReturn(SOME_ID);

        when(accountRepository.save(SOME_ID, FIRST_NAME, LAST_NAME, TAX_ID, DOB, backgroundCheckResults))
                .thenReturn(true);

//        when(accountOpeningEventPublisher.notify(SOME_ID))
//                .thenThrow(new RuntimeException());
        doThrow(new RuntimeException()).when(accountOpeningEventPublisher).notify(SOME_ID);

        assertThrows(RuntimeException.class, () -> underTest.openAccount(FIRST_NAME,
                LAST_NAME, TAX_ID, DOB));
    }

    @Test
    void shouldOpenAccountOpened() throws IOException {
        final BackgroundCheckResults backgroundCheckResults = new BackgroundCheckResults(SOMETHING_NOT_UNACCEPTABLE, 100);

        when(backgroundCheckService.confirm(FIRST_NAME, LAST_NAME, TAX_ID, DOB))
                .thenReturn(backgroundCheckResults);

        when(referenceIdsManager.obtainId(eq(FIRST_NAME),
                anyString(),
                eq(LAST_NAME),
                eq(TAX_ID),
                eq(DOB))).thenReturn(SOME_ID);

        when(accountRepository.save(SOME_ID, FIRST_NAME, LAST_NAME, TAX_ID, DOB, backgroundCheckResults))
                .thenReturn(true);

        doNothing().when(accountOpeningEventPublisher).notify(SOME_ID);

        final AccountOpeningStatus accountOpeningStatus = underTest.openAccount(FIRST_NAME,
                LAST_NAME,
                TAX_ID,
                DOB);

        assertEquals(AccountOpeningStatus.OPENED, accountOpeningStatus);
    }


}