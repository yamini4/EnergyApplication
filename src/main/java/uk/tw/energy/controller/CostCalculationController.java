package uk.tw.energy.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;

@RestController
@RequestMapping("/calculate")
public class CostCalculationController {
	private final PricePlanService pricePlanService;
	private final MeterReadingService meterReadingService;
	private final AccountService accountService;
	private PricePlanComparatorController controller;


	public CostCalculationController(PricePlanService pricePlanService, MeterReadingService meterReadingService, AccountService accountService) {
		this.pricePlanService = pricePlanService;
		this.meterReadingService = meterReadingService;
		this.accountService = accountService;
	}

	@GetMapping("/calculate/cost/{smartMeterId}")
	public ResponseEntity<Object> calculateReadings(@PathVariable String smartMeterId) {
		Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
		Optional<Map<String, BigDecimal>> consumptionsForPricePlans = pricePlanService
				.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);
		Map<String, String> smartMeterToPricePlanAccounts = accountService.getPricePlanIdForSmartMeterId(smartMeterId) ;
		if (!readings.isPresent()) {
			return ResponseEntity.ok("smartMeterId reading not available");
		}

		if (!consumptionsForPricePlans.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		// var electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        // var otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        // meterReadingService.storeReadings(SMART_METER_ID, List.of(readings, otherReading));

        ResponseEntity<Map<String, Object>> pricePlans = calculatedCostForEachPricePlan(SMART_METER_ID);

        // Map<String, Object> expected = Map.of(
        //         PricePlanComparatorController.PRICE_PLAN_ID_KEY,
        //         WORST_PLAN_ID,
        //         PricePlanComparatorController.PRICE_PLAN_COMPARISONS_KEY,
        //         Map.of(
        //                 WORST_PLAN_ID, BigDecimal.valueOf(100.0),
        //                 BEST_PLAN_ID, BigDecimal.valueOf(10.0),
        //                 SECOND_BEST_PLAN_ID, BigDecimal.valueOf(20.0)));

		return Optional.of(pricePlans.stream().filter(pPlan -> )
                .collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(readings.get(), t))));
	}

	public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(String smartMeterId) {
		String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);
		Optional<Map<String, BigDecimal>> consumptionsForPricePlans = pricePlanService
				.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

		if (!consumptionsForPricePlans.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		Map<String, Object> pricePlanComparisons = new HashMap<>();
		pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
		pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, consumptionsForPricePlans.get());

		return consumptionsForPricePlans.isPresent() ? ResponseEntity.ok(pricePlanComparisons)
				: ResponseEntity.notFound().build();
	}
}
