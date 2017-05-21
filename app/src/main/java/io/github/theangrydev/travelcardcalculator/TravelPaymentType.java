package io.github.theangrydev.travelcardcalculator;

public enum TravelPaymentType {
	SINGLE_FARE(null),
	DAILY_TRAVELCARD(TimeUnit.DAY),
	WEEKLY_TRAVELCARD(TimeUnit.WEEK),
	MONTHLY_TRAVELCARD(TimeUnit.MONTH),
	ANNUAL_TRAVELCARD(TimeUnit.YEAR);

	public final TimeUnit timeUnit;

	TravelPaymentType(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}
}
