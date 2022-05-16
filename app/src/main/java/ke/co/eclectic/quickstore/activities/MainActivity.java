package ke.co.eclectic.quickstore.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import ke.co.eclectic.quickstore.MyMarkerView;
import ke.co.eclectic.quickstore.R;
import ke.co.eclectic.quickstore.XYMarkerView;
import ke.co.eclectic.quickstore.adapters.SingleViewAdapter;
import ke.co.eclectic.quickstore.helper.CodingMsg;
import ke.co.eclectic.quickstore.helper.GlobalMethod;
import ke.co.eclectic.quickstore.helper.GlobalVariable;
import timber.log.Timber;


/**
 * Created by David Manduku on 08/10/2018.
 */

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener,SingleViewAdapter.StringComm {
    /**
     * The Tool bar.
     */
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    /**
     * The Store graph.
     */
//    @BindView(R.id.salesGraph)
//     GraphView salesGraph;
    @BindView(R.id.storeGraph)
    BarChart storeGraph;
    /**
     * The Sales graph.
     */
    @BindView(R.id.salesGraph)
    BarChart salesGraph;


    /**
     * The Img b storeforward.
     */
    @BindView(R.id.imgBStoreforward)
    ImageButton imgBStoreforward;
    /**
     * The Img b salesforward.
     */
    @BindView(R.id.imgBSalesforward)
    ImageButton imgBSalesforward;
    /**
     * The Img b categoryforward.
     */
    @BindView(R.id.imgBCategoryforward)
    ImageButton imgBCategoryforward;

    /**
     * The Txt date.
     */
    @BindView(R.id.txtDate)
    TextView txtDate;
    /**
     * The Txt category title.
     */
    @BindView(R.id.txtCategoryTitle)
    TextView txtCategoryTitle;
    /**
     * The Txt store title.
     */
    @BindView(R.id.txtStoreTitle)
    TextView txtStoreTitle;

    /**
     * The Txt sales title.
     */
    @BindView(R.id.txtSalesTitle)
    TextView txtSalesTitle;
    /**
     * The Txt sales total.
     */
    @BindView(R.id.txtSalesTotal)
    TextView txtSalesTotal;

    /**
     * The Txt sales v total.
     */
    @BindView(R.id.txtSalesVTotal)
    TextView txtSalesVTotal;
    /**
     * The Txt sales total v title.
     */
    @BindView(R.id.txtSalesTotalVTitle)
    TextView txtSalesTotalVTitle;

    /**
     * The Txt average sales.
     */
    @BindView(R.id.txtAverageSales)
    TextView txtAverageSales;
    /**
     * The Txt average sales title.
     */
    @BindView(R.id.txtAverageSalesTitle)
    TextView txtAverageSalesTitle;
    /**
     * The Category rv.
     */
    @BindView(R.id.categoryRV)
    RecyclerView categoryRV;
    /**
     * The Img b prev.
     */
    @BindView(R.id.imgBPrev)
    ImageButton imgBPrev;

    private int year;
    private int month;
    private int day;
    private boolean toDate= false;
    /**
     * The Output date.
     */
    SimpleDateFormat outputDate = new SimpleDateFormat("MMMM dd , yyyy");//July 20,2018
    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {
        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            String date = String.valueOf(day) +
                    "-" + (month + 1) + "-" + year;
            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");

            try {
                 selectedDate = input.parse(date);                 // parse input
                txtDate.setText(outputDate.format(selectedDate));    // format output
            } catch (ParseException e) {
                e.printStackTrace();
            }


            filterByDate();


        }
    };
    /**
     * selected date as seen by user
     */
    private Date selectedDate = new Date();
    /**
     * The M date picker.
     */
    DatePickerDialog  mDatePicker;


    /**
     * filter data by date selected
     */
    private void filterByDate() {
Timber.v("filterByDate ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // init();

        ButterKnife.bind(this);
        initDate();
        initToolbar();
        //initGraph();
        initSalesGraph();
        initStoreGraph();
        //initRV();
        Completable.timer(5, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(this::initRV);

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
    //todo remove this code
//
//    }

    /**
     * initializes date data
     */
    private void initDate() {
        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
        selectedDate = c.getTime();
        txtDate.setText(outputDate.format(selectedDate));
        mDatePicker = new DatePickerDialog(this,R.style.DateTheme, pickerListener, year, month,day);
        mDatePicker.getDatePicker().setMaxDate(selectedDate.getTime());
    }

    /**
     *handles previous imageview clicks event
     */
    @OnClick(R.id.imgBPrev)
    public void prevDate() {
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        c.add(Calendar.DATE, -1);  // number of days to add
        selectedDate = c.getTime();
        txtDate.setText(outputDate.format(selectedDate));
        filterByDate();
    }

    /**
     * handle next date imagevie clicks event
     */
    @OnClick(R.id.imgBNext)
    public void nextDate() {
        Calendar c = Calendar.getInstance();
        Calendar d = Calendar.getInstance();
        c.setTime(selectedDate);

        c.add(Calendar.DATE, 1);  // number of days to add
        if(c.getTimeInMillis()>d.getTimeInMillis()){
            CodingMsg.tl(this,"No records yet");
            return;
        }
        selectedDate = c.getTime();
        txtDate.setText(outputDate.format(selectedDate));
        filterByDate();
    }

    /**
     * handles date textview click event
     */
    @OnClick(R.id.txtDate)
    public void showCal() {
        mDatePicker.show();

    }

    /**
     * initializes category list
     */
    private void initRV() {
        List<String> categoryList = new ArrayList<>();
        //dummy data
        categoryList.add("Bread");
        categoryList.add("Pastries");
        categoryList.add("Bread");
        categoryList.add("Pastries");
        categoryList.add("Bread");
        categoryList.add("Pastries");


        SingleViewAdapter singleViewAdapter = new SingleViewAdapter(this, categoryList);

        categoryRV.setLayoutManager(new LinearLayoutManager(this));
        categoryRV.setAdapter(singleViewAdapter);
    }

    /**
     * initializes sales graph
     */
    private void initSalesGraph() {
        salesGraph.setOnChartValueSelectedListener(this);

        salesGraph.setDrawBarShadow(false);
        salesGraph.setDrawValueAboveBar(true);

        salesGraph.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        salesGraph.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        salesGraph.setPinchZoom(false);

        salesGraph.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Float d = value;
                int value1 = d.intValue();
                if(value1 > 11){
                    if(value1 != 12){
                        value1 =   value1 - 12;
                        if(value1 > 11){
                            if(value1 != 12){
                                value1 =   value1 - 12;
                            }
                            return value1+" AM";
                        }
                    }


                    return value1+" PM";
                }

                return value1+" AM";

            }
        };

        XAxis xAxis = salesGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(3f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return value+"";
            }
        };

        YAxis leftAxis = salesGraph.getAxisLeft();
        leftAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        leftAxis.setGranularity(1f);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setEnabled(true);



        YAxis rightAxis = salesGraph.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setGranularity(1f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(true);

        Legend l = salesGraph.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(salesGraph); // For bounds control
        salesGraph.setMarker(mv);

        salesGraph.getAxisRight().setEnabled(false);


        // setting data
        initSalesGraphData(7, 50);
    }

    /**
     * initializes sales graph data
     * @param count
     * @param range
     */
    @SuppressWarnings("SameParameterValue")
    private void initSalesGraphData( int count, float range) {
        float start = 6f;
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = (int) start; i < 30; i+=3) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);


            if (Math.random() * 100 < 25) {
                yVals1.add(new BarEntry(i, val, ContextCompat.getDrawable(this,R.drawable.ic_back)));
            } else {
                yVals1.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (salesGraph.getData() != null &&
                salesGraph.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) salesGraph.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            salesGraph.getData().notifyDataChanged();
            salesGraph.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "");

            set1.setDrawIcons(false);

            int startColor1 = ContextCompat.getColor(this, R.color.bar11Color);
            int endColor1 = ContextCompat.getColor(this, R.color.bar22Color);
            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, endColor1));
            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(0f);
            data.setValueTypeface(GlobalVariable.getMontserratRegular(this));
            data.setBarWidth(0.9f);

            salesGraph.setData(data);
        }
    }

    /**
     * initializes store graph
     */
    private void initStoreGraph() {
        storeGraph.setOnChartValueSelectedListener(this);
        storeGraph.getDescription().setEnabled(false);


        // scaling can now only be done on x- and y-axis separately
        storeGraph.setPinchZoom(false);

        storeGraph.setDrawBarShadow(false);

        storeGraph.setDrawGridBackground(false);
        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(storeGraph); // For bounds control
        storeGraph.setMarker(mv); // Set the marker to the chart

        Legend l = storeGraph.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTypeface(GlobalVariable.getMontserratRegular(this));
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = storeGraph.getXAxis();
        xAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });
        //xAxis.setAxisMinValue(0f);
        xAxis.setAxisMinimum(0f);


        YAxis leftAxis = storeGraph.getAxisLeft();
        leftAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        storeGraph.getAxisRight().setEnabled(false);
        initStoreGraphData();
    }

    /**
     * initializes store graph  data
     */
    private void initStoreGraphData() {
        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x4 DataSet
        float barWidth = 0.43f; // x4 DataSet
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

        int groupCount = 4;
        int startYear = 1;
        int endYear = startYear + groupCount;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals4 = new ArrayList<BarEntry>();

        float randomMultiplier = 4 * 100000f;

        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
            yVals2.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
            yVals3.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
            yVals4.add(new BarEntry(i, (float) (Math.random() * randomMultiplier)));
        }

        BarDataSet set1, set2,set3, set4;

        if (storeGraph.getData() != null && storeGraph.getData().getDataSetCount() > 0) {

            set1 = (BarDataSet) storeGraph.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) storeGraph.getData().getDataSetByIndex(1);
           // set3 = (BarDataSet) salesGraph.getData().getDataSetByIndex(2);
           // set4 = (BarDataSet) salesGraph.getData().getDataSetByIndex(3);

            set1.setValues(yVals1);
            set2.setValues(yVals2);
           // set3.setValues(yVals3);
           // set4.setValues(yVals4);

            storeGraph.getData().notifyDataChanged();
            storeGraph.notifyDataSetChanged();

        } else {
            // create 4 DataSets
            set1 = new BarDataSet(yVals1, "Opening Balance");

            set1.setColor(getResources().getColor(R.color.bar1Color));
            set2 = new BarDataSet(yVals2, "Closing Balance");
            set2.setColor(getResources().getColor(R.color.bar2Color));
//            set3 = new BarDataSet(yVals3, "Company C");
//            set3.setColor(Color.rgb(242, 247, 158));
//            set4 = new BarDataSet(yVals4, "Company D");
//            set4.setColor(Color.rgb(255, 102, 0));


            BarData data = new BarData(set1, set2);
            data.setValueFormatter(new LargeValueFormatter());
            data.setValueTypeface(GlobalVariable.getMontserratRegular(this));

            storeGraph.setData(data);
        }

        // specify the width each bar should have
        storeGraph.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        storeGraph.getXAxis().setAxisMinimum(startYear);

        storeGraph.getXAxis().setAxisMaximum(startYear + storeGraph.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);

        storeGraph.groupBars(startYear, groupSpace, barSpace);
        storeGraph.invalidate();

    }


    /**
     * initializes toolbar
     */
    private void initToolbar() {

        setSupportActionBar(toolBar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        setTitle(GlobalVariable.getCurrentUser().getBusinessname());
        // making all textview use monteserrat font
        for(int i = 0; i < toolBar.getChildCount(); i++)
        {
            View view = toolBar.getChildAt(i);
            if(view instanceof TextView) {
                TextView textView = (TextView) view;
                textView.setTypeface(GlobalVariable.getMontserratMedium(this));
            }
        }
    }

    /**
     * fetches store name
     * @param position
     * @return
     */
    private String getGraphStoreName(int position){
        String sName = "";
        if(position == 0){
            sName = "kk Store";
        }
        if(position == 2){
            sName = "ksg Store";
        }
        if(position == 4){
            sName = "kdf Store";
        }



        return position+"";
    }
    private void initGraph(){
        storeGraph.setDrawBarShadow(false);
        storeGraph.setDrawValueAboveBar(false);
        //storeGraph.setTextColor(getResources().getColor(android.R.color.white));
       // storeGraph.setNoDataTextColor(getResources().getColor(android.R.color.white));

        storeGraph.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        storeGraph.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        storeGraph.setPinchZoom(false);

        storeGraph.setDrawGridBackground(false);
        // storeGraph.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Float  d = value;

                return "S"+getGraphStoreName ( d.intValue());
            }
        };

        //new MonthAxisValueFormatter(storeGraph);

        XAxis xAxis = storeGraph.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
       xAxis.setLabelCount(10,true);
        xAxis.setTextColor(getResources().getColor(android.R.color.black));
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setCenterAxisLabels(true);


        IAxisValueFormatter custom = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ""+value;
            }
        };

        YAxis leftAxis = storeGraph.getAxisLeft();
        leftAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
       // leftAxis.setLabelCount(8);
        leftAxis.setGranularity(1f);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(getResources().getColor(android.R.color.black));
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setEnabled(true);

        YAxis rightAxis = storeGraph.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setTypeface(GlobalVariable.getMontserratRegular(this));
        rightAxis.setLabelCount(10);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextColor(getResources().getColor(android.R.color.black));
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setEnabled(false);

        //soecufying the legend
        Legend l = storeGraph.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(10f);
        l.setTextSize(10f);
        //l.setTextColor(getResources().getColor(android.R.color.white));
        //l.setXEntrySpace(7f);
        //l.setYEntrySpace(50f);
       // l.setYOffset(20f);
        l.setWordWrapEnabled(true);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        l.setEnabled(true);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(storeGraph); // For bounds control
        storeGraph.setMarker(mv); // Set the marker to the chart


        initStoreGraphData();
    }

    private void initGraphData() {

        storeGraph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

        BarDataSet set1,set2;
        ArrayList<BarEntry> set1Values = new ArrayList<>();
        ArrayList<BarEntry> set2Values = new ArrayList<>();

        set1Values.add(new BarEntry(1,5));
        set1Values.add(new BarEntry(2,3));
        set1Values.add(new BarEntry(3,1));
        set1Values.add(new BarEntry(4,5));
        set1Values.add(new BarEntry(5,10));
        set1Values.add(new BarEntry(6,3));
        set1Values.add(new BarEntry(7,0));
        set1Values.add(new BarEntry(8,0));
//        set1Values.add(new BarEntry(9,5));

        set2Values.add(new BarEntry(1,3));
        set2Values.add(new BarEntry(2,4));
        set2Values.add(new BarEntry(3,2));
        set2Values.add(new BarEntry(4,3));
        set2Values.add(new BarEntry(5,3));
        set2Values.add(new BarEntry(6,2));
        set2Values.add(new BarEntry(7,0));
        set2Values.add(new BarEntry(8,0));
//        set2Values.add(new BarEntry(9,7));




        if(storeGraph.getData() != null && storeGraph.getData().getDataSetCount() >0){
            set1 = (BarDataSet) storeGraph.getData().getDataSetByIndex(0);
            set1.setValues(set1Values);
            set2 = (BarDataSet) storeGraph.getData().getDataSetByIndex(1);
            set2.setValues(set2Values);

            storeGraph.getData().notifyDataChanged();
            storeGraph.notifyDataSetChanged();
        }else{
            set1 = new BarDataSet(set1Values ,"Opening Balance");
            set2 = new BarDataSet(set2Values ,"Closing Balance");

            set1.setDrawIcons(false);//sets icon on bar
            set2.setDrawIcons(false);//sets icon on bar

            //setting gradient colors
            set1.setColor(getResources().getColor(R.color.bar1Color));
            set2.setColor(getResources().getColor(R.color.bar2Color));


            //creating dataset list values
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(0f);
            data.setBarWidth(0.5f);
            data.setValueTypeface(GlobalVariable.getMontserratRegular(this));

            storeGraph.setData(data);

            storeGraph.groupBars(0.5f,0.2f,0.01f);
           // storeGraph.setHorizontalScrollBarEnabled(true);
//            storeGraph.setVisibleXRangeMaximum(20); // allow 20 values to be displayed at once on the x-axis, not more
//            storeGraph.moveViewToX(10);
            storeGraph.setNestedScrollingEnabled(true);
            storeGraph.invalidate();

        }

        //defining the y and x axis



    }


    /**
     * initializes tollbar menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem addItem = menu.findItem(R.id.action_add);

        return true;
    }

    /**
     * handle menu item clicks
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home: {
               finish();
                return true;
            }

            case R.id.action_add: {
                return true;
            }

            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /**
     * implements barchart method
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    /**
     * implements barchart method
     */
    @Override
    public void onNothingSelected() {

    }

    /**
     * implements stringadapter method
     * @param position the position
     * @param val      the val
     * @param action   the action
     */
    @Override
    public void strMessage(int position, String val, String action) {

    }


}
