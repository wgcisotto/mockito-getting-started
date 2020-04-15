package com.wgcisotto.pension;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class Account {

    private BigDecimal availableCash;
    private Set<String> investments;
    private LocalDate dob;
    private String firtName;
    private String lastName;
    private String taxId;


}
