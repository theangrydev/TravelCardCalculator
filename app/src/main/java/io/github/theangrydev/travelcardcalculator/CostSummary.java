package io.github.theangrydev.travelcardcalculator;

public class CostSummary {
	public final double firstCostPerJourney;
	public final double secondCostPerJourney;
	public final int totalJourneys;

	public CostSummary(int totalJourneys, double firstCostTotal, double secondCostTotal) {
		this.firstCostPerJourney = firstCostTotal / totalJourneys;
		this.secondCostPerJourney = secondCostTotal / totalJourneys;
		this.totalJourneys = totalJourneys;
	}

	public boolean firstIsCheaperThanSecond() {
		return firstCostPerJourney < secondCostPerJourney;
	}

	public double firstTotalCost() {
		return totalJourneys * firstCostPerJourney;
	}

	public double secondTotalCost() {
		return totalJourneys * secondCostPerJourney;
	}
}