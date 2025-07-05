import codedraw.CodeDraw;

import java.awt.*;
import java.util.Random;

public class SortDraw {

    // main settings
    static final SortMethod sortMethod = SortMethod.MERGE; // choose from INSERTION, SELECTION, BUBBLE and MERGE
    static final int cardinality = 31; // amount of data points to be sorted - recommended range is [10, 100]
    static final int lowerBound = 0; // lower bound of values (inclusive)
    static final int upperBound = 50; // upper bound of values (exclusive)
    static final long milliseconds = 2000L; // the freeze time upon changes in the graphical representation
    static final int splitCutoff = 5; // in merge sort, if the recursion level has at most this many data points, then insertion sort is used rather than entering another recursion

    // visual settings
    static final int canvasWidth = 800;
    static final int canvasHeight = 600;

    // better leave these alone
    static final int titleSpace = canvasHeight / 10;
    static final int offset = canvasWidth / 100;
    static final int chartWidth = canvasWidth - 5 * offset;
    static final int chartHeight = canvasHeight - 2 * titleSpace;
    static final double barWidth = (double) chartWidth / cardinality;
    static final CodeDraw chart = new CodeDraw(canvasWidth, canvasHeight);


    private enum SortMethod {
        INSERTION,
        SELECTION,
        BUBBLE,
        MERGE
    }

    public static void main(String[] args) {

        Random random = new Random();
        int[] data = new int[cardinality];
        for (int i = 0; i < cardinality; i++) {
            data[i] = random.nextInt(lowerBound, upperBound);
        }
        //int[] data = random.ints(cardinality, lowerBound, upperBound).toArray();

        switch (sortMethod) {

            // insertion sort
            case INSERTION -> {
                chart.setTitle("Insertion Sort");
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
                chart.setTitle("Selection Sort");
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
                chart.setTitle("Bubble Sort");
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
                chart.setTitle("Merge Sort");
                int[] help = new int[data.length];
                mergeSort(data, help, 0, data.length - 1, splitCutoff, 0);
                chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2., "sorting completed!");
                chart.show();
            }

            default -> {
            }
        }
    }

    private static void mergeSort(int[] data, int[] help, int lo, int hi, int cutoff, int i) {
        if (hi <= lo) return;
        drawChart(data, i, lo, hi + 1);
        highlightBarGroup(lo, hi - lo + 1);
        if (hi - lo < cutoff) {
            chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                    "Less than " + (cutoff + 1) + " data points - using insertion sort...");
            chart.show(milliseconds);
            boundedInsertionSort(data, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                    "More than " + cutoff + " data points - splitting into recursion level " + (i + 1));
            chart.show(milliseconds);
            mergeSort(data, help, lo, mid, cutoff, i + 1);
            mergeSort(data, help, mid + 1, hi, cutoff, i + 1);
            highlightBarGroup(lo, hi - lo + 1);
            merge(data, help, lo, mid, hi);
            chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                    "Merging recursion level " + (i + 1) + " into level " + i);
            chart.show(milliseconds);
        }
        drawChart(data, i, lo, hi + 1);
    }

    private static void merge(int[] data, int[] help, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            help[k] = data[k];
        }
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) data[k] = help[j++];
            else if (j > hi) data[k] = help[i++];
            else if (help[j] < help[i]) data[k] = help[j++];
            else data[k] = help[i++];
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
        chart.clear();
        switch (sortMethod) {
            case INSERTION -> chart.drawText(4 * offset, offset, "Insertion Sort");
            case SELECTION -> chart.drawText(4 * offset, offset, "Selection Sort");
            case BUBBLE -> chart.drawText(4 * offset, offset, "Bubble Sort");
            case MERGE -> chart.drawText(4 * offset, offset, "Merge Sort");
            default -> {
            }
        }

        chart.drawRectangle(4 * offset, titleSpace, chartWidth, chartHeight);

        int barMaxHeight = chartHeight - offset;
        chart.setColor(Color.gray);
        for (int i = 0; i < data.length; i++) {
            if (i == lo) {
                chart.setColor(Color.black);
            } else if (i == hi) {
                chart.setColor(Color.gray);
            }
            int barHeight = (int) ((double) data[i] / upperBound * barMaxHeight + offset);
            chart.fillRectangle(4 * offset + i * barWidth, chartHeight + titleSpace - barHeight, barWidth - 1, barHeight);
        }
        chart.setColor(Color.black);

        if (iteration >= data.length) {
            chart.drawText(4 * offset, chartHeight + titleSpace + offset, "sorting completed!");
        } else {
            switch (sortMethod) {
                case INSERTION -> {
                    chart.setColor(Color.cyan);
                    chart.setLineWidth(3);
                    chart.drawLine(4 * offset + (iteration + 1) * barWidth, titleSpace - offset,
                            4 * offset + (iteration + 1) * barWidth, chartHeight + titleSpace + offset);
                    chart.setColor(Color.black);
                    chart.setLineWidth(1);
                    chart.drawText(4 * offset, chartHeight + titleSpace + offset, "outer loop iteration #" + iteration);
                }
                case SELECTION, BUBBLE ->
                        chart.drawText(4 * offset, chartHeight + titleSpace + offset, "outer loop iteration #" + iteration);
                case MERGE ->
                        chart.drawText(4 * offset, chartHeight + titleSpace + offset, "recursion level " + iteration);
                default -> {
                }
            }
        }

        chart.show(milliseconds);
    }

    private static void highlightBarGroup(int barIndex, int barCount) {
        chart.setColor(Color.red);
        chart.setLineWidth(3);
        chart.drawRectangle(4 * offset + barIndex * barWidth, titleSpace, barCount * barWidth, chartHeight);
        chart.setColor(Color.black);
        chart.setLineWidth(1);
        chart.show(milliseconds);
    }

    private static void endOfIterationText(int iteration) {
        switch (sortMethod) {
            case INSERTION -> {
                chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                        "Data locally sorted up to marker - starting next iteration...");
            }
            case SELECTION -> {
                if (iteration == 1) {
                    chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                            "The first entry is globally sorted - starting next iteration...");
                } else {
                    chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                            "The first " + iteration + " entries are globally sorted - starting next iteration...");
                }
            }
            case BUBBLE -> {
                if (iteration == 1) {
                    chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2.,
                            "The last entry is globally sorted - starting next iteration...");
                } else {
                    chart.drawText(4 * offset, chartHeight + titleSpace + titleSpace / 2,
                            "The last " + iteration + " entries are globally sorted - starting next iteration...");
                }
            }
            default -> {
            }
        }

        chart.show(milliseconds * 2);
    }

}