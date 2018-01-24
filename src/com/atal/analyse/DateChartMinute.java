package com.atal.analyse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.demo.charts.ExampleChart;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * Millisecond Scale
 * <p>
 * Demonstrates the following:
 * <ul>
 * <li>Millisecond Scale
 * <li>Series with no Markers
 */
public class DateChartMinute implements ExampleChart<XYChart> {

	private static final String SORT_MINUTES = "05";
	private static final String SORT_HOUR = "14";

	private static final String MATCHING_PATTERN = "Request: Site:";
	// private static final String MATCHING_PATTERN_EXCEPTION = "Exception when
	// running process";

	private static final String MATCHING_PATTERN_OPERATION = "requestType:retrieve;";

	public static void main(String[] args) {
		getDates();
		ExampleChart<XYChart> exampleChart = new DateChartMinute();
		XYChart chart = exampleChart.getChart();
		new SwingWrapper<XYChart>(chart).displayChart();
	}

	@Override
	public XYChart getChart() {

		// Create Chart
		XYChart chart = new XYChartBuilder().width(800).height(600).title("Scale").build();

		// Customize Chart
		chart.getStyler().setLegendVisible(false);

		// generate data
		List<Double> yData = new ArrayList<Double>();
		List<Double> xData = new ArrayList<Double>();
		Double value = 1.0;
		Map<Integer, Integer> listDate = getDates();

		for (Integer dateValue : listDate.keySet()) {

			xData.add((double) (dateValue));
			yData.add((double) listDate.get(dateValue).intValue());
			value++;
		}

		XYSeries series = chart.addSeries("Series", xData, yData);
		series.setMarker(SeriesMarkers.NONE);

		return chart;
	}

	private static Map<Integer, Integer> getDates() {

		String fileName = "D:/logs/all-debug_PP.log.19012018";
		List<String> list = new ArrayList<>();
		try (Stream<String> stream = Files.lines(Paths.get(fileName), Charset.forName("Cp1252"))) {

			list = stream.filter(line -> line.contains(MATCHING_PATTERN))
					.filter(line -> line.contains(MATCHING_PATTERN_OPERATION)).map(line -> line.substring(0, 23))
					.collect(Collectors.toList());

		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> listPerHour = new ArrayList<>();
		try (Stream<String> stream = list.stream()) {

			listPerHour = stream.map(line -> line.substring(11)).collect(Collectors.toList());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return groupByHour(listPerHour);
	}

	/**
	 * group by minutes
	 * 
	 * @param listPerHour
	 * @return
	 */
	private static Map<Integer, Integer> groupByHour(List<String> listPerHour) {

		Map<Integer, Integer> numbers = new HashMap<>();
		for (String hourPer : listPerHour) {
			if (hourPer.substring(0, 2).equals(SORT_HOUR) && hourPer.substring(3, 5).equals(SORT_MINUTES)) {
				if (!numbers.containsKey(Integer.parseInt(hourPer.substring(6, 8)))) {
					numbers.put(Integer.parseInt(hourPer.substring(6, 8)), 1);
				} else {
					numbers.put(Integer.parseInt(hourPer.substring(6, 8)),
							numbers.get(Integer.parseInt(hourPer.substring(6, 8))) + 1);
				}
			}
		}

		for (int i = 0; i < 60; i++) {
			if (!numbers.keySet().contains(i)) {
				numbers.put(i, 0);
			}
		}

		return numbers;
	}

}