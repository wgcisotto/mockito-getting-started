package com.wgcisotto.pension.setup;

public interface AccountOpeningEventPublisher {

    void notify(String accountId);
}
