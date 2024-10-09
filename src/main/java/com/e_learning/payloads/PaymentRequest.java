package com.e_learning.payloads;

import java.util.List;

import lombok.Data;

@Data
public class PaymentRequest {

	private PaymentDto paymentDto;
    private List<Integer> categoryIds;
}
