import codedraw.CodeDraw;
import codedraw.TextFormat;
import codedraw.TextOrigin;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SortDraw {

    // main settings
    static final SortMethod sortMethod = SortMethod.MERGE; // choose from INSERTION, SELECTION, BUBBLE and MERGE
    static final int cardinality = 31; // amount of data points to be sorted - recommended range is [10, 100]
    static final int lowerBound = 1; // lower bound of values (inclusive) - must be at least 0
    static final int upperBound = 50; // upper bound of values (exclusive)
    static long shortWait = 500L; // the freeze time in milliseconds upon changes in the graphical representation
    static long longWait = 3000L; // the freeze time in milliseconds upon text changes in the graphical representation
    static final boolean showMerge = true; // if set true, then the merge step in merge sort will be shown in detail in a second canvas
    static final int splitCutoff = 5; // in merge sort, if the recursion level has at most this many data points, then insertion sort is used rather than entering another recursion
    static final int canvasWidth = 800;

    // better leave these alone
    static final int canvasHeight = canvasWidth * 3 / 4;
    static final double titleSpace = canvasHeight / 15.;
    static final double offset = canvasWidth / 100.;
    static final double chartWidth = canvasWidth - 6 * offset;
    static final double chartHeight = canvasHeight - 3 * titleSpace;
    static final double barWidth = chartWidth / cardinality;
    static final double primaryTextY = chartHeight + titleSpace + 4 * offset;
    static final double secondaryTextY = chartHeight + 2 * titleSpace + 2 * offset;
    static final CodeDraw chart = new CodeDraw(canvasWidth, canvasHeight);
    static final TextFormat textFormat = new TextFormat();


    private enum SortMethod {
        INSERTION,
        SELECTION,
        BUBBLE,
        MERGE
    }

    public static void main(String[] args) {

        textFormat.setFontSize(canvasWidth / 35);
        chart.setTextFormat(textFormat);
        chart.setCanvasPositionX(25);
        chart.setCanvasPositionY(50);
        chart.setTitle("");

        Random random = new Random();
        int[] data = new int[cardinality];
        for (int i = 0; i < cardinality; i++) {
            data[i] = random.nextInt(lowerBound, upperBound);
        }
        //int[] data = random.ints(cardinality, lowerBound, upperBound).toArray();

        switch (sortMethod) {

            // insertion sort
            case INSERTION -> {
                for (int i = 1; i < data.length; i++) {
                    drawChart(data, i, 0, i + 1);
                    for (int j = i; j > 0 && data[j] < data[j - 1]; j--) {
                        highlightBarGroup(j, 1);
                        highlightBarGroup(j - 1, 1);
                        exchange(data, j, j - 1);
                        drawChart(data, i, 0, j);
                    }
                    endOfIterationText(i);
                }
                drawChart(data, cardinality, 0, cardinality);
            }

            // selection sort
            case SELECTION -> {
                for (int i = 0; i < data.length - 1; i++) {
                    drawChart(data, i + 1, i, cardinality);

                    int min = i;
                    for (int j = i + 1; j < data.length; j++) {
                        if (data[j] < data[min]) {
                            min = j;
                        }
                    }
                    highlightBarGroup(i, 1);
                    highlightBarGroup(min, 1);
                    exchange(data, i, min);
                    drawChart(data, i + 1, i + 1, cardinality);
                    endOfIterationText(i + 1);
                }
                drawChart(data, cardinality, 0, cardinality);
            }

            // bubble sort
            case BUBBLE -> {
                for (int i = 0; i < data.length - 1; i++) {
                    drawChart(data, i + 1, 0, cardinality);
                    for (int j = 0; j < data.length - i - 1; j++) {
                        if (data[j] > data[j + 1]) {
                            highlightBarGroup(j, 1);
                            highlightBarGroup(j + 1, 1);
                            exchange(data, j, j + 1);
                            drawChart(data, i + 1, j + 1, cardinality);
                        }
                    }
                    endOfIterationText(i + 1);
                }
                drawChart(data, cardinality, 0, cardinality);
            }

            // merge sort
            case MERGE -> {
                int[] help = new int[data.length];
                mergeSort(data, help, 0, data.length - 1, 0);
                chart.drawText(4 * offset, secondaryTextY, "finished sorting!");
                chart.show();
            }

            default -> {
            }
        }
    }

    private static void mergeSort(int[] data, int[] help, int lo, int hi, int i) {
        if (hi <= lo) return;
        drawChart(data, i, lo, hi + 1);
        highlightBarGroup(lo, hi - lo + 1);
        if (hi - lo < splitCutoff) {
            chart.drawText(4 * offset, secondaryTextY,
                    "Less than " + (splitCutoff + 1) + " data points - using insertion sort...");
            chart.show(longWait);
            boundedInsertionSort(data, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            chart.drawText(4 * offset, secondaryTextY,
                    "More than " + splitCutoff + " data points - splitting into recursion level " + (i + 1));
            chart.show(longWait);
            mergeSort(data, help, lo, mid, i + 1);
            mergeSort(data, help, mid + 1, hi, i + 1);
            drawChart(data, i, lo, hi + 1);
            highlightBarGroup(lo, hi - lo + 1);
            chart.setColor(Color.cyan);
            chart.setLineWidth(3);
            chart.drawLine(4 * offset + (mid + 1) * barWidth, titleSpace - offset,
                    4 * offset + (mid + 1) * barWidth, chartHeight + titleSpace + offset);
            chart.setLineWidth(1);
            chart.setColor(Color.black);
            chart.drawText(4 * offset, secondaryTextY,
                    "Merging recursion level " + (i + 1) + " into level " + i);
            chart.show(longWait);
            if (showMerge) {
                visualMerge(data, help, lo, mid, hi, i);
            } else {
                merge(data, help, lo, mid, hi);
            }
        }
        drawChart(data, i, lo, hi + 1);
    }

    private static void merge(int[] data, int[] help, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            help[k] = data[k];
        }
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                data[k] = help[j++];
            }
            else if (j > hi) {
                data[k] = help[i++];
            }
            else if (help[j] < help[i]) {
                data[k] = help[j++];
            }
            else {
                data[k] = help[i++];
            }
        }
    }

    private static void visualMerge(int[] data, int[] help, int lo, int mid, int hi, int recursion) {
        CodeDraw mergeChart = new CodeDraw(canvasWidth, canvasHeight);
        mergeChart.setCanvasPositionX(25 + canvasWidth);
        mergeChart.setCanvasPositionY(150);
        mergeChart.setTextFormat(textFormat);
        mergeChart.setTitle("");

        int[] left = Arrays.copyOfRange(data, lo, mid + 1);
        int[] right = Arrays.copyOfRange(data, mid + 1, hi + 1);
        int[] result = new int[hi - lo + 1];
        Arrays.fill(result, -1);
        drawMerge(mergeChart, left, right, result, recursion);

        for (int k = lo; k <= hi; k++) {
            help[k] = data[k];
        }
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (mergeChart.isClosed()){
                merge(data, help, lo, mid, hi);
                return;
            }
            if (i > mid) {
                right[j - mid - 1] = -1;
                data[k] = help[j++];
                mergeChart.drawText(2 * offset, primaryTextY, "Take from right...");
            }
            else if (j > hi) {
                left[i - lo] = -1;
                data[k] = help[i++];
                mergeChart.drawText(2 * offset, primaryTextY, "Take from left...");
            }
            else if (help[j] < help[i]) {
                right[j - mid - 1] = -1;
                data[k] = help[j++];
                mergeChart.drawText(2 * offset, primaryTextY, "Take from right...");
            }
            else {
                left[i - lo] = -1;
                data[k] = help[i++];
                mergeChart.drawText(2 * offset, primaryTextY, "Take from left...");
            }
            mergeChart.setColor(Color.red);
            mergeChart.setLineWidth(3);
            mergeChart.drawLine(2 * offset, chartHeight / 2 + titleSpace - (data[k] * chartHeight / 2 / (upperBound - 1)),
                    2 * offset + chartWidth, chartHeight / 2 + titleSpace - (data[k] * chartHeight / 2 / (upperBound - 1)));
            mergeChart.setLineWidth(1);
            result[k - lo] = data[k];
            mergeChart.show(longWait);
            drawMerge(mergeChart, left, right, result, recursion);
        }
        mergeChart.drawText(2 * offset, primaryTextY, "Merge completed!");
        mergeChart.show(longWait);
        mergeChart.close();
    }

    private static void drawMerge(CodeDraw mergeChart, int[] left, int[] right, int[] result, int recursion){
        mergeChart.clear();
        mergeChart.setColor(Color.black);
        mergeChart.drawText(2 * offset, offset, "Merging recursion level " + (recursion + 1) + " into level " + recursion);

        double halfWidth = chartWidth / 2 - offset;
        mergeChart.drawRectangle(2 * offset, titleSpace, halfWidth, chartHeight / 2.);
        mergeChart.drawRectangle(3 * offset + chartWidth / 2., titleSpace, halfWidth, chartHeight / 2.);
        mergeChart.drawRectangle(2 * offset, titleSpace + offset + chartHeight / 2., chartWidth, chartHeight / 2.);


        double barMaxHeight = chartHeight / 2;
        double barWidth = halfWidth / left.length;
        for (int i = 0; i < left.length; i++) {
            if (left[i] == -1) continue;
            double barHeight = left[i] * barMaxHeight / (upperBound - 1);
            mergeChart.fillRectangle(2 * offset + i * barWidth, chartHeight / 2. + titleSpace - barHeight, barWidth - 1, barHeight);
            mergeChart.setColor(Color.gray);
        }
        mergeChart.setColor(Color.black);
        for (int i = 0; i < right.length; i++) {
            if (right[i] == -1) continue;
            double barHeight = right[i] * barMaxHeight / (upperBound - 1);
            mergeChart.fillRectangle(3 * offset + i * barWidth + chartWidth / 2., chartHeight / 2. + titleSpace - barHeight, barWidth - 1, barHeight);
            mergeChart.setColor(Color.gray);
        }
        mergeChart.setColor(Color.black);
        barWidth = chartWidth / result.length;
        for (int i = 0; i < result.length; i++) {
            if (result[i] == -1) break;
            double barHeight = result[i] * barMaxHeight / (upperBound - 1);
            mergeChart.fillRectangle(2 * offset + i * barWidth, chartHeight + titleSpace - barHeight + offset, barWidth - 1, barHeight);
        }

    }

    private static void boundedInsertionSort(int[] data, int lo, int hi) {
        for (int i = lo; i <= hi && i < data.length; i++) {
            for (int j = i; j > lo && data[j] < data[j - 1]; j--) {
                exchange(data, j, j - 1);
            }
        }
    }

    private static void exchange(int[] data, int i, int j) {
        int swap = data[i];
        data[i] = data[j];
        data[j] = swap;
    }

    private static void drawChart(int[] data, int iteration, int lo, int hi) {
        if (chart.isClosed()){
            System.exit(143);
        }
        chart.clear();
        chart.setColor(Color.black);
        switch (sortMethod) {
            case INSERTION -> chart.drawText(4 * offset, offset, "Insertion Sort");
            case SELECTION -> chart.drawText(4 * offset, offset, "Selection Sort");
            case BUBBLE -> chart.drawText(4 * offset, offset, "Bubble Sort");
            case MERGE -> chart.drawText(4 * offset, offset, "Merge Sort");
            default -> {
            }
        }

        chart.drawRectangle(4 * offset, titleSpace, chartWidth, chartHeight);

        double barMaxHeight = chartHeight - offset;
        chart.setColor(Color.gray);
        for (int i = 0; i < data.length; i++) {
            if (i == lo) {
                chart.setColor(Color.black);
            } else if (i == hi) {
                chart.setColor(Color.gray);
            }
            double barHeight = data[i] * barMaxHeight / (upperBound - 1);
            chart.fillRectangle(4 * offset + i * barWidth, chartHeight + titleSpace - barHeight, barWidth - 1, barHeight);
        }
        chart.setColor(Color.black);

        if (iteration >= data.length) {
            chart.drawText(4 * offset, primaryTextY, "finished sorting!");
        } else {
            switch (sortMethod) {
                case INSERTION -> {
                    chart.setColor(Color.cyan);
                    chart.setLineWidth(3);
                    chart.drawLine(4 * offset + (iteration + 1) * barWidth, titleSpace - offset,
                            4 * offset + (iteration + 1) * barWidth, chartHeight + titleSpace + offset);
                    chart.setColor(Color.black);
                    chart.setLineWidth(1);
                    chart.drawText(4 * offset, primaryTextY, "outer loop iteration #" + iteration);
                }
                case SELECTION, BUBBLE ->
                        chart.drawText(4 * offset, primaryTextY, "outer loop iteration #" + iteration);
                case MERGE ->
                        chart.drawText(4 * offset, primaryTextY, "recursion level " + iteration);
                default -> {
                }
            }
        }

        chart.show(shortWait);
    }

    private static void highlightBarGroup(int barIndex, int barCount) {
        chart.setColor(Color.red);
        chart.setLineWidth(3);
        chart.drawRectangle(4 * offset + barIndex * barWidth, titleSpace, barCount * barWidth, chartHeight);
        chart.setColor(Color.black);
        chart.setLineWidth(1);
        chart.show(shortWait);
    }

    private static void endOfIterationText(int iteration) {
        switch (sortMethod) {
            case INSERTION -> {
                chart.drawText(4 * offset, secondaryTextY,
                        "Data locally sorted up to marker - starting next iteration...");
            }
            case SELECTION -> {
                if (iteration == 1) {
                    chart.drawText(4 * offset, secondaryTextY,
                            "The first entry is globally sorted - starting next iteration...");
                } else {
                    chart.drawText(4 * offset, secondaryTextY,
                            "The first " + iteration + " entries are globally sorted - starting next iteration...");
                }
            }
            case BUBBLE -> {
                if (iteration == 1) {
                    chart.drawText(4 * offset, secondaryTextY,
                            "The last entry is globally sorted - starting next iteration...");
                } else {
                    chart.drawText(4 * offset, secondaryTextY,
                            "The last " + iteration + " entries are globally sorted - starting next iteration...");
                }
            }
            default -> {
            }
        }

        chart.show(longWait);
    }

}