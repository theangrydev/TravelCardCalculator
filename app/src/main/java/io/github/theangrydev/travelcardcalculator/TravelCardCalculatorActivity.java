package io.github.theangrydev.travelcardcalculator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import org.joda.time.LocalDate;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class TravelCardCalculatorActivity extends AppCompatActivity {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
    private static final String CURRENCY_SYMBOL = CURRENCY_FORMAT.getCurrency().getSymbol();

    private static final List<Integer> JOURNEY_PERIOD_RESOURCE_IDS = Arrays.asList(
            R.string.day, R.string.week, R.string.month, R.string.year
    );

    private static final List<Integer> PAYMENT_TYPE_RESOURCE_IDS = Arrays.asList(
            R.string.single_fares, R.string.a_daily_travelcard, R.string.a_weekly_travelcard,
            R.string.a_monthly_travelcard, R.string.an_annual_travelcard
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_saving_calculator);

        setUpSpinner(R.id.journey_period, JOURNEY_PERIOD_RESOURCE_IDS);
        setUpSpinner(R.id.first_to_compare_period, PAYMENT_TYPE_RESOURCE_IDS);
        setUpSpinner(R.id.second_to_compare_period, PAYMENT_TYPE_RESOURCE_IDS);

        final Button button = (Button) findViewById(R.id.calculate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result;
                try {
                    result = calculateCostSummary();
                } catch (RuntimeException e) {
                    result = getString(R.string.invalid_data_entered);
                }
                TextView resultTextView = (TextView) findViewById(R.id.result);
                resultTextView.setText(result);
            }
        });

        addCurrencyFocusListener(R.id.first_to_compare_cost);
        addCurrencyFocusListener(R.id.second_to_compare_cost);
    }

    private void addCurrencyFocusListener(int editTextId) {
        final EditText currencyText = (EditText) findViewById(editTextId);
        currencyText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String text = currencyText.getText().toString();
                if (hasFocus) {
                    convertCurrencyFormatToDouble(text);
                } else {
                    convertDoubleFormatToCurrency(text);
                }
            }

            private void convertDoubleFormatToCurrency(String text) {
                if (text.isEmpty()) {
                    currencyText.setText(CURRENCY_SYMBOL);
                } else {
                    double value = Double.parseDouble(text);
                    currencyText.setText(CURRENCY_FORMAT.format(value));
                }
            }

            private void convertCurrencyFormatToDouble(String text) {
                try {
                    if (text.equals(CURRENCY_SYMBOL)) {
                        currencyText.setText("");
                    } else {
                        double value = CURRENCY_FORMAT.parse(text).doubleValue();
                        currencyText.setText(Double.toString(value));
                    }
                } catch (ParseException e) {
                    Log.e("ConvertCurrencyToDouble", e.toString());
                }
            }
        });
        currencyText.setText(CURRENCY_SYMBOL);
    }

    private String calculateCostSummary() {
        LocalDate dateFrom = getEditTextValueAsDate(R.id.date_from);
        LocalDate dateTo = getEditTextValueAsDate(R.id.date_to);

        int numberOfJourneys = getEditTextValueAsInt(R.id.number_of_journies);
        TimeUnit journeyPeriodTimeUnit = getTimeUnitForSpinner(R.id.journey_period);

        TravelPaymentType firstTravelPaymentType = getTravelPaymentTypeForSpinner(R.id.first_to_compare_period);
        double firstTravelPaymentTypeCost = getCurrencyEditTextValueAsDouble(R.id.first_to_compare_cost);

        TravelPaymentType secondTravelPaymentType = getTravelPaymentTypeForSpinner(R.id.second_to_compare_period);
        double secondTravelPaymentTypeCost = getCurrencyEditTextValueAsDouble(R.id.second_to_compare_cost);

        CostSummaryCalculator costSummaryCalculator = new CostSummaryCalculator(
                dateFrom, dateTo, numberOfJourneys, journeyPeriodTimeUnit,
                firstTravelPaymentType, firstTravelPaymentTypeCost,
                secondTravelPaymentType, secondTravelPaymentTypeCost);

        CostSummary costSummary = costSummaryCalculator.calculate();
        String firstCompared = costSummary.firstIsCheaperThanSecond() ? getString(R.string.cheaper) : getString(R.string.more_expensive);
        String secondCompared = costSummary.firstIsCheaperThanSecond() ? getString(R.string.more_expensive) : getString(R.string.cheaper);

        return String.format(getString(R.string.result),
                getSpinnerText(R.id.first_to_compare_period), firstCompared, formatCurrency(costSummary.firstCostPerJourney), formatCurrency(costSummary.firstTotalCost()),
                getSpinnerText(R.id.first_to_compare_period), secondCompared, formatCurrency(costSummary.secondCostPerJourney), formatCurrency(costSummary.secondTotalCost()));
    }

    private String formatCurrency(double value) {
        return CURRENCY_FORMAT.format(value);
    }

    private LocalDate getEditTextValueAsDate(int editTextId) {
        EditText editText = (EditText) findViewById(editTextId);
        try {
            return new LocalDate(DATE_FORMAT.parse(editText.getText().toString()));
        } catch (ParseException e) {
            return null;
        }
    }

    private double getCurrencyEditTextValueAsDouble(int editTextId) {
        EditText editText = (EditText) findViewById(editTextId);
        return Double.parseDouble(editText.getText().toString().replaceFirst(Pattern.quote(CURRENCY_SYMBOL), ""));
    }

    private int getEditTextValueAsInt(int editTextId) {
        EditText editText = (EditText) findViewById(editTextId);
        return Integer.parseInt(editText.getText().toString());
    }

    private TimeUnit getTimeUnitForSpinner(int spinnerId) {
        Spinner journeyPeriodSpinner = (Spinner) findViewById(spinnerId);
        int resourceId = JOURNEY_PERIOD_RESOURCE_IDS.get(journeyPeriodSpinner.getSelectedItemPosition());
        return getTimeUnit(resourceId);
    }

    private TravelPaymentType getTravelPaymentTypeForSpinner(int spinnerId) {
        Spinner journeyPeriodSpinner = (Spinner) findViewById(spinnerId);
        int resourceId = PAYMENT_TYPE_RESOURCE_IDS.get(journeyPeriodSpinner.getSelectedItemPosition());
        return getTravelPaymentType(resourceId);
    }

    private String getSpinnerText(int spinnerId) {
        Spinner spinner = (Spinner) findViewById(spinnerId);
        return spinner.getSelectedItem().toString();
    }

    private TimeUnit getTimeUnit(int resourceId) {
        switch (resourceId) {
            case R.string.day:
                return TimeUnit.DAY;
            case R.string.week:
                return TimeUnit.WEEK;
            case R.string.month:
                return TimeUnit.MONTH;
            case R.string.year:
                return TimeUnit.YEAR;
            default:
                throw new IllegalArgumentException("Unknown resource id");
        }
    }

    private TravelPaymentType getTravelPaymentType(int resourceId) {
        switch (resourceId) {
            case R.string.single_fares:
                return TravelPaymentType.SINGLE_FARE;
            case R.string.a_daily_travelcard:
                return TravelPaymentType.DAILY_TRAVELCARD;
            case R.string.a_weekly_travelcard:
                return TravelPaymentType.WEEKLY_TRAVELCARD;
            case R.string.a_monthly_travelcard:
                return TravelPaymentType.MONTHLY_TRAVELCARD;
            case R.string.an_annual_travelcard:
                return TravelPaymentType.ANNUAL_TRAVELCARD;
            default:
                throw new IllegalArgumentException("Unknown resource id");
        }
    }
    private ArrayAdapter<CharSequence> createArrayAdapter(List<Integer> resourceIds) {
        List<CharSequence> options = new ArrayList<>(resourceIds.size());
        for (int resourceId : resourceIds) {
            options.add(getResources().getString(resourceId));
        }
        return new ArrayAdapter<>(this, R.layout.spinner_layout, options);
    }

    private void setUpSpinner(int viewId, List<Integer> resourceIds) {
        Spinner spinner = (Spinner) findViewById(viewId);
        ArrayAdapter<CharSequence> adapter = createArrayAdapter(resourceIds);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fare_saving_calculator, menu);
        return true;
    }

}
