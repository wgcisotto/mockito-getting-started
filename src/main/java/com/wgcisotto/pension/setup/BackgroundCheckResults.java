package com.wgcisotto.pension.setup;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BackgroundCheckResults {

    String riskProfile;
    Integer upperAccountLimit;

}
