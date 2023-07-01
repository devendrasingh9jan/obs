package com.xyz.obs.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transfer {
    private Long userId;
    private Long payeeId;
    private Double amount;
}
