package io.github.theangrydev.travelcardcalculator;

import org.joda.time.*;

// TODO: #4 Calculator seems buggy
public class CostSummaryCalculator {
	private final LocalDate dateFrom;
	private final LocalDate dateTo;

	private final int numberOfJourneys;
	private final TimeUnit journeyPeriodTimeUnit;

	private final TravelPaymentType firstTravelPaymentType;
	private final double firstTravelPaymentTypeCost;

	private final TravelPaymentType secondTravelPaymentType;
	private final double secondTravelPaymentTypeCost;

	public CostSummaryCalculator(LocalDate dateFrom, LocalDate dateTo, int numberOfJourneys, TimeUnit journeyPeriodTimeUnit,
			TravelPaymentType firstTravelPaymentType,  double firstTravelPaymentTypeCost,
			TravelPaymentType secondTravelPaymentType, double secondTravelPaymentTypeCost) {
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.numberOfJourneys = numberOfJourneys;
		this.journeyPeriodTimeUnit = journeyPeriodTimeUnit;
		this.firstTravelPaymentType = firstTravelPaymentType;
		this.firstTravelPaymentTypeCost = firstTravelPaymentTypeCost;
		this.secondTravelPaymentType = secondTravelPaymentType;
		this.secondTravelPaymentTypeCost = secondTravelPaymentTypeCost;
	}

	public CostSummary calculate() {
		int frequency = getFrequencyBetweenDates(journeyPeriodTimeUnit, dateFrom, dateTo);
		double remainder = getRemainder(journeyPeriodTimeUnit, dateFrom, dateTo, frequency);
		int journeysInPeriod = frequency * numberOfJourneys + (int) Math.ceil(remainder * numberOfJourneys);

		int firstNumberOfPurchases = getNumberOfPurchases(dateFrom, dateTo, journeysInPeriod, firstTravelPaymentType);
		double firstTotalCost = firstTravelPaymentTypeCost * firstNumberOfPurchases;

		int secondNumberOfPurchases = getNumberOfPurchases(dateFrom, dateTo, journeysInPeriod, secondTravelPaymentType);
		double secondTotalCost = secondTravelPaymentTypeCost * secondNumberOfPurchases;

		return new CostSummary(journeysInPeriod, firstTotalCost, secondTotalCost);
	}

	private int getNumberOfPurchases(LocalDate dateFrom, LocalDate dateTo, int journeysInPeriod, TravelPaymentType firstTravelPaymentType) {
		if (firstTravelPaymentType == TravelPaymentType.SINGLE_FARE) {
			return journeysInPeriod;
		} else {
			return getFrequencyCeilingBetweenTwoDates(dateFrom, dateTo, firstTravelPaymentType.timeUnit);
		}
	}

	private int getFrequencyCeilingBetweenTwoDates(LocalDate dateFrom, LocalDate dateTo, TimeUnit timeUnit) {
		int frequency = getFrequencyBetweenDates(timeUnit, dateFrom, dateTo);
		double remainder = getRemainder(timeUnit, dateFrom, dateTo, frequency);
		if (remainder > 0) {
			return frequency + 1;
		} else {
			return frequency;
		}
	}

	private double getRemainder(TimeUnit timeUnit, LocalDate dateFrom, LocalDate dateTo, int frequency) {
		switch (timeUnit) {
		case DAY:
			return 0;
		case MONTH:
			return getRemainderMonth(dateFrom, dateTo, frequency);
		case WEEK:
			return getRemainderWeek(dateFrom, dateTo, frequency);
		case YEAR:
			return getRemainderYear(dateFrom, dateTo, frequency);
		default:
			throw new IllegalStateException("Should never reach here");
		}
	}

	private double getRemainderYear(LocalDate dateFrom, LocalDate dateTo, int frequency) {
		LocalDate remainderStart = dateFrom.plusYears(frequency);
		int daysInThatYear = Days.daysBetween(remainderStart, remainderStart.plusYears(1)).getDays();
		return getRemainder(dateTo, remainderStart, daysInThatYear);
	}

	private double getRemainderWeek(LocalDate dateFrom, LocalDate dateTo, int frequency) {
		LocalDate remainderStart = dateFrom.plusWeeks(frequency);
		int daysInThatWeek = Days.daysBetween(remainderStart, remainderStart.plusWeeks(1)).getDays();
		return getRemainder(dateTo, remainderStart, daysInThatWeek);
	}

	private double getRemainderMonth(LocalDate dateFrom, LocalDate dateTo, int frequency) {
		LocalDate remainderStart = dateFrom.plusMonths(frequency);
		int daysInThatMonth = Days.daysBetween(remainderStart, remainderStart.plusMonths(1)).getDays();
		return getRemainder(dateTo, remainderStart, daysInThatMonth);
	}

	private double getRemainder(LocalDate dateTo, LocalDate remainderStart, int daysInThatPeriod) {
		int daysRemaining = Days.daysBetween(remainderStart, dateTo).getDays();
		return (double) daysRemaining / daysInThatPeriod;
	}

	private int getFrequencyBetweenDates(TimeUnit timeUnit, LocalDate dateFrom, LocalDate dateTo) {
		switch (timeUnit) {
		case DAY:
			return Days.daysBetween(dateFrom, dateTo).getDays();
		case MONTH:
			return Months.monthsBetween(dateFrom, dateTo).getMonths();
		case WEEK:
			return Weeks.weeksBetween(dateFrom, dateTo).getWeeks();
		case YEAR:
			return Years.yearsBetween(dateFrom, dateTo).getYears();
		default:
			throw new IllegalStateException("Should never reach here");
		}
	}
}
