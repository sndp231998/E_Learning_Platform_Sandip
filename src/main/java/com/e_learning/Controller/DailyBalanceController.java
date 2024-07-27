package com.e_learning.Controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_learning.payloads.CategoryDto;
import com.e_learning.payloads.DailyBalanceDto;
import com.e_learning.services.CategoryService;
import com.e_learning.services.DailyBalanceService;

@RestController
@RequestMapping("/api/v1/dailybalances")
public class DailyBalanceController {

	@Autowired
	private DailyBalanceService dailyService;

	// create

	@PostMapping("/")
	public ResponseEntity<DailyBalanceDto> createBalance(@Valid @RequestBody DailyBalanceDto dailyBalanceDto) {
		DailyBalanceDto createBalance = this.dailyService.createBalance(dailyBalanceDto);
		return new ResponseEntity<DailyBalanceDto>(createBalance, HttpStatus.CREATED);
	}

	// get all
		@GetMapping("/")
		public ResponseEntity<List<DailyBalanceDto>> getDailyBalances() {
			List<DailyBalanceDto> allblc = this.dailyService.getDailyBalances();
			return ResponseEntity.ok(allblc);
		}
}
