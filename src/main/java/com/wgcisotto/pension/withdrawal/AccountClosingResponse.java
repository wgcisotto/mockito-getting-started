package com.wgcisotto.pension.withdrawal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AccountClosingResponse {

    private AccountClosingStatus status;
    private LocalDateTime procesingDate;

}
