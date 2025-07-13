import codedraw.CodeDraw;
import codedraw.TextFormat;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SortDraw {

    // main settings
    static final SortMethod sortMethod = SortMethod.QUICK; // choose from INSERTION, SELECTION, BUBBLE, MERGE and QUICK
    static final int cardinality = 17; // amount of data points to be sorted - recommended range is [10, 100]
    static final int lowerBound = 1; // lower bound of values (inclusive) - must be greater than 0
    static final int upperBound = 50; // upper bound of values (exclusive) - must be greater than lowerBound
    static long shortWait = 400L; // the freeze time in milliseconds upon minor changes in the graphical representation - recommended range is [300, 1000]
    static long longWait = 3000L; // the freeze time in milliseconds upon major changes in the graphical representation - recommended range is [2000, 4000]
    static final int splitCutoff = 5; // in merge sort and quick sort, if the recursion level has at most this many data points, then insertion sort is used rather than entering another recursion
    static final boolean showMerge = true; // if set true, then the merge steps in merge sort will be shown in detail in a second canvas
    static final boolean showPartition = true; // if set true, then the partition exchanges in quick sort will be shown in detail
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
        MERGE,
        QUICK
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

        switch (sortMethod) {

            // insertion sort
            case INSERTION -> {
                for (int i = 1; i < data.length; i++) {
                    drawChart(data, i, 0, i + 1, i);
                    for (int j = i; j > 0 && data[j] < data[j - 1]; j--) {
                        highlightBarGroup(j - 1, 1);
                        exchange(data, j, j - 1);
                        drawChart(data, i, 0, j, j - 1);
                    }
                    endOfIterationText(i);
                }
                drawChart(data, cardinality, 0, cardinality, -1);
            }

            // selection sort
            case SELECTION -> {
                for (int i = 0; i < data.length - 1; i++) {
                    int min = i;
                    drawChart(data, i + 1, i, cardinality, i);
                    for (int j = i + 1; j < data.length; j++) {
                        drawChart(data, i + 1, i, cardinality, min);
                        highlightBarGroup(j, 1);
                        if (data[j] < data[min]) {
                            chart.drawText(4 * offset, secondaryTextY, "Found new minimum fur current iteration.");
                            chart.show(longWait);
                            min = j;
                        }
                    }
                    drawChart(data, i + 1, i, cardinality, min);
                    chart.drawText(4 * offset, secondaryTextY, "Place minimum at the first unsorted position.");
                    highlightBarGroup(i, 1);
                    chart.show(longWait);
                    exchange(data, i, min);
                    drawChart(data, i + 1, i, cardinality, i);
                    endOfIterationText(i + 1);
                }
                drawChart(data, cardinality, 0, cardinality, -1);
                chart.show();
            }

            // bubble sort
            case BUBBLE -> {
                for (int i = 0; i < data.length - 1; i++) {
                    drawChart(data, i + 1, 0, cardinality, 0);
                    for (int j = 0; j < data.length - i - 1; j++) {
                        if (data[j] > data[j + 1]) {
                            highlightBarGroup(j + 1, 1);
                            exchange(data, j, j + 1);
                        }
                        drawChart(data, i + 1, j + 1, cardinality, j + 1);
                    }
                    endOfIterationText(i + 1);
                }
                drawChart(data, cardinality, 0, cardinality, -1);
            }

            // merge sort
            case MERGE -> {
                int[] help = new int[data.length];
                mergeSort(data, help, 0, data.length - 1, 0);
                chart.drawText(4 * offset, secondaryTextY, "Finished sorting!");
                chart.show();
            }

            // quick sort
            case QUICK -> {
                quickSort(data, 0, data.length - 1, 0);
                drawChart(data, 0, 0, cardinality + 1, -1);
                chart.drawText(4 * offset, secondaryTextY, "Finished sorting!");
                chart.show();
            }

            default -> {
            }
        }
    }

    private static void exchange(int[] data, int i, int j) {
        int swap = data[i];
        data[i] = data[j];
        data[j] = swap;
    }

    private static void mergeSort(int[] data, int[] help, int lo, int hi, int recursion) {
        if (hi < lo) {
            return;
        }
        drawChart(data, recursion, lo, hi + 1, -1);
        highlightBarGroup(lo, hi - lo + 1);
        if (hi == lo) {
            chart.drawText(4 * offset, secondaryTextY, "One data point - no further action needed...");
            chart.show(longWait);
            return;
        } else if (hi - lo < splitCutoff) {
            chart.drawText(4 * offset, secondaryTextY,
                    "Less than " + (splitCutoff + 1) + " data points - using insertion sort...");
            chart.show(longWait);
            boundedInsertionSort(data, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            chart.drawText(4 * offset, secondaryTextY,
                    "More than " + splitCutoff + " data point(s) - splitting into recursion level " + (recursion + 1));
            chart.show(longWait);
            mergeSort(data, help, lo, mid, recursion + 1);
            mergeSort(data, help, mid + 1, hi, recursion + 1);
            drawChart(data, recursion, lo, hi + 1, -1);
            highlightBarGroup(lo, hi - lo + 1);
            drawVerticalLine(mid);
            chart.drawText(4 * offset, secondaryTextY,
                    "Merging recursion level " + (recursion + 1) + " into level " + recursion);
            chart.show(longWait);
            if (showMerge) {
                visualMerge(data, help, lo, mid, hi, recursion);
            } else {
                merge(data, help, lo, mid, hi);
            }
        }
        drawChart(data, recursion, lo, hi + 1, -1);
    }

    private static void merge(int[] data, int[] help, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            help[k] = data[k];
        }
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                data[k] = help[j++];
            } else if (j > hi) {
                data[k] = help[i++];
            } else if (help[j] < help[i]) {
                data[k] = help[j++];
            } else {
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

        for (int k = lo; k <= hi; k++) {
            help[k] = data[k];
        }

        drawMerge(mergeChart, Arrays.copyOfRange(data, lo, hi + 1), Arrays.copyOfRange(help, lo, hi + 1), 0, 0, recursion);

        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            // if the merge chart is closed manually, data is reset to a consistent state, and normal merge is called instead
            if (mergeChart.isClosed()) {
                for (int l = lo; l <= hi; l++) {
                    data[l] = help[l];
                }
                merge(data, help, lo, mid, hi);
                return;
            }
            String text = "";
            if (i > mid) {
                data[k] = help[j++];
                text = "Take element from the right side...";
            } else if (j > hi) {
                data[k] = help[i++];
                text = "Take element from the left side...";
            } else if (help[j] < help[i]) {
                data[k] = help[j++];
                text = "Take element from the right side...";
            } else {
                data[k] = help[i++];
                text = "Take element from the left side...";
            }
            mergeChart.drawText(2 * offset, primaryTextY, text);
            mergeChart.setColor(Color.red);
            mergeChart.setLineWidth(3);
            double x = 2 * offset + chartWidth - (hi - lo + 1) % 2 * chartWidth / ((hi - lo + 1) + (hi - lo + 1) % 2);
            double y = chartHeight / 2 + titleSpace - (data[k] * (chartHeight / 2 - titleSpace) / (upperBound - 1));
            mergeChart.drawLine(2 * offset, y, x, y);
            mergeChart.setLineWidth(1);
            mergeChart.setColor(Color.black);
            mergeChart.show(longWait);
            drawMerge(mergeChart, Arrays.copyOfRange(data, lo, hi + 1), Arrays.copyOfRange(help, lo, hi + 1), i - lo, j - mid - 1, recursion);
        }
        mergeChart.drawText(2 * offset, primaryTextY, "Merge completed!");
        mergeChart.show(longWait);
        mergeChart.close();
    }

    private static void quickSort(int[] data, int lo, int hi, int recursion) {
        if (hi - lo < splitCutoff) {
            drawChart(data, recursion, lo, hi + 1, -1);
            if (hi < lo) {
                chart.drawText(4 * offset, secondaryTextY, "Base case: no data points to be sorted - returning to recursion level " + (recursion - 1));
            } else if (hi == lo) {
                chart.drawText(4 * offset, secondaryTextY, "Base case: one data point to be sorted - returning to recursion level " + (recursion - 1));
            } else {
                chart.drawText(4 * offset, secondaryTextY, "Less than " + (splitCutoff + 1) + " data points - using insertion sort...");
                chart.show(longWait);
                boundedInsertionSort(data, lo, hi + 1);
                drawChart(data, recursion, lo, hi + 1, -1);
                chart.drawText(4 * offset, secondaryTextY, "Returning to recursion level " + (recursion - 1));
            }
            chart.show(longWait);
            return;
        }
        int j = partition(data, lo, hi, recursion);
        drawChart(data, recursion, lo, hi + 1, j);
        chart.drawText(4 * offset, secondaryTextY, "Pivot element is on its correct place - sorting the left side...");
        chart.show(longWait);
        quickSort(data, lo, j - 1, recursion + 1);
        drawChart(data, recursion, lo, hi + 1, j);
        chart.drawText(4 * offset, secondaryTextY, "Left part is sorted - sorting the right side...");
        chart.show(longWait);
        quickSort(data, j + 1, hi, recursion + 1);
        drawChart(data, recursion, lo, hi + 1, j);
        chart.drawText(4 * offset, secondaryTextY, "Left and right parts sorted - returning to recursion level " + (recursion - 1));
        chart.show(longWait);
    }

    // the rightmost element is always chosen as the pivot element v
    private static int partition(int[] data, int lo, int hi, int recursion) {
        int k = lo;
        int v = data[hi];
        drawChart(data, recursion, lo, hi + 1, hi);
        chart.drawText(4 * offset, secondaryTextY, "Pivot element found - moving elements less than pivot to the left...");
        chart.show(longWait);
        for (int i = k; i < hi; i++) {
            if (showPartition) {
                drawChart(data, recursion, lo, hi + 1, i);
                drawVerticalLine(k - 1);
                chart.show(shortWait);
            }
            if (data[i] < v) {
                if (showPartition) {
                    chart.drawText(4 * offset, secondaryTextY, "Found an element less than pivot - exchanging...");
                    drawVerticalLine(k - 1);
                    chart.show(longWait);
                    highlightBarGroup(k, 1);
                }
                exchange(data, i, k++);
                if (showPartition) {
                    drawChart(data, recursion, lo, hi + 1, i);
                    drawVerticalLine(k - 2);
                    chart.show(shortWait);
                    drawChart(data, recursion, lo, hi + 1, i);
                    drawVerticalLine(k - 1);
                    chart.show(shortWait);
                }
            }
        }
        drawChart(data, recursion, lo, hi + 1, hi);
        drawVerticalLine(k - 1);
        chart.drawText(4 * offset, secondaryTextY, "Moving Pivot element to its correct place (vertical line marker).");
        chart.show(longWait);
        highlightBarGroup(k, 1);
        exchange(data, k, hi);
        return k;
    }

    private static void boundedInsertionSort(int[] data, int lo, int hi) {
        for (int i = lo; i <= hi && i < data.length; i++) {
            for (int j = i; j > lo && data[j] < data[j - 1]; j--) {
                exchange(data, j, j - 1);
            }
        }
    }

    // the main chart
    // elements in data which are not relevant for the current step/iteration/recursion are drawn greyed out
    // bars for indexes in between lo (inclusive) and hi (exclusive) will be drawn with black color - other bars in gray
    // if highlight is a legal index of data, then the bar of that index will be highlighted with a red frame
    private static void drawChart(int[] data, int step, int lo, int hi, int highlight) {
        if (chart.isClosed()) {
            System.exit(143);
        }
        if (hi <= lo) {
            hi = -1;
            lo = -1;
        }
        chart.clear();
        chart.setColor(Color.black);
        switch (sortMethod) {
            case INSERTION -> chart.drawText(4 * offset, offset, "Insertion Sort");
            case SELECTION -> chart.drawText(4 * offset, offset, "Selection Sort");
            case BUBBLE -> chart.drawText(4 * offset, offset, "Bubble Sort");
            case MERGE -> chart.drawText(4 * offset, offset, "Merge Sort");
            case QUICK -> chart.drawText(4 * offset, offset, "Quick Sort");
            default -> {
            }
        }

        chart.drawRectangle(4 * offset, titleSpace, chartWidth, chartHeight);

        chart.setColor(Color.gray);
        for (int i = 0; i < data.length; i++) {
            if (i == lo) {
                chart.setColor(Color.black);
            } else if (i == hi) {
                chart.setColor(Color.gray);
            }
            double barHeight = data[i] * chartHeight / (upperBound - 1);
            chart.fillRectangle(4 * offset + i * barWidth, chartHeight + titleSpace - barHeight, barWidth - 1, barHeight);
        }
        if (highlight >= 0 && highlight < data.length) {
            chart.setColor(Color.red);
            chart.setLineWidth(3);
            chart.drawRectangle(4 * offset + highlight * barWidth, titleSpace, barWidth, chartHeight);
            chart.setColor(Color.black);
            chart.setLineWidth(1);
        }
        chart.setColor(Color.black);

        String text = "";
        if (step >= data.length && sortMethod.ordinal() < 3) {
            text = "Finished sorting!";
        } else {
            switch (sortMethod) {
                case INSERTION -> {
                    drawVerticalLine(step);
                    text = "Outer loop iteration #" + step;
                }
                case SELECTION, BUBBLE -> text = "Outer loop iteration #" + step;
                case MERGE, QUICK -> text = "Recursion level " + step;
                default -> {
                }
            }
        }
        chart.drawText(4 * offset, primaryTextY, text);

        if (sortMethod != SortMethod.QUICK && sortMethod != SortMethod.SELECTION) {
            chart.show(shortWait);
        }
    }

    // the second canvas for merge sort's merge step
    // leftIndex is the index of the first element of the left half in help, which is not yet placed in data
    // rightIndex is the index of the first element of the right half in help, which is not yet placed in data
    private static void drawMerge(CodeDraw mergeChart, int[] data, int[] help, int leftIndex, int rightIndex, int recursion) {
        mergeChart.clear();
        mergeChart.setColor(Color.black);
        mergeChart.drawText(2 * offset, offset, "Merging recursion level " + (recursion + 1) + " into level " + recursion);

        double halfWidth = chartWidth / 2.;
        double halfHeight = chartHeight / 2.;
        double barWidth = chartWidth / (help.length + help.length % 2);
        mergeChart.drawRectangle(2 * offset, titleSpace, halfWidth, halfHeight);
        mergeChart.drawRectangle(2 * offset + halfWidth, titleSpace, halfWidth - (help.length % 2 * barWidth), halfHeight);
        mergeChart.drawRectangle(2 * offset, titleSpace + offset + halfHeight, chartWidth - (help.length % 2 * barWidth), halfHeight);
        mergeChart.drawText(3 * offset, titleSpace + offset, "help (left half)");
        mergeChart.drawText(3 * offset + halfWidth, titleSpace + offset, "help (right half)");
        mergeChart.drawText(3 * offset, titleSpace + 2 * offset + halfHeight, "data");

        mergeChart.setColor(Color.gray);
        for (int i = 0; i < help.length; i++) {
            if ((i == leftIndex && i < help.length / 2.) || i == (int) Math.ceil(help.length / 2.) + rightIndex) {
                mergeChart.setColor(Color.black);
            }
            double barHeight = help[i] * (halfHeight - titleSpace) / (upperBound - 1);
            mergeChart.fillRectangle(2 * offset + i * barWidth, halfHeight + titleSpace - barHeight, barWidth - 1, barHeight);
            mergeChart.setColor(Color.gray);
        }
        mergeChart.setColor(Color.black);
        for (int i = 0; i < data.length; i++) {
            if (i == leftIndex + rightIndex) {
                mergeChart.setColor(Color.gray);
            }
            double barHeight = data[i] * (halfHeight - titleSpace) / (upperBound - 1);
            mergeChart.fillRectangle(2 * offset + i * barWidth, chartHeight + titleSpace - barHeight + offset, barWidth - 1, barHeight);
        }
        mergeChart.setColor(Color.cyan);
        mergeChart.setLineWidth(3);
        mergeChart.drawLine(2 * offset + halfWidth, titleSpace, 2 * offset + halfWidth, titleSpace + halfHeight);
        mergeChart.setLineWidth(1);
        mergeChart.setColor(Color.black);
    }

    private static void highlightBarGroup(int barIndex, int barCount) {
        chart.setColor(Color.red);
        chart.setLineWidth(3);
        chart.drawRectangle(4 * offset + barIndex * barWidth, titleSpace, barCount * barWidth, chartHeight);
        chart.setColor(Color.black);
        chart.setLineWidth(1);
        chart.show(shortWait);
    }

    private static void drawVerticalLine(int index) {
        chart.setColor(Color.cyan);
        chart.setLineWidth(3);
        chart.drawLine(4 * offset + (index + 1) * barWidth, titleSpace - offset,
                4 * offset + (index + 1) * barWidth, chartHeight + titleSpace + offset);
        chart.setLineWidth(1);
        chart.setColor(Color.black);
    }

    private static void endOfIterationText(int iteration) {
        String text = "";
        switch (sortMethod) {
            case INSERTION -> {
                text = "Data locally sorted up to vertical line marker - starting next iteration...";
            }
            case SELECTION -> {
                if (iteration == 1) {
                    text = "The first entry is globally sorted - starting next iteration...";
                } else {
                    text = "The first " + iteration + " entries are globally sorted - starting next iteration...";
                }
            }
            case BUBBLE -> {
                if (iteration == 1) {
                    text = "The last entry is globally sorted - starting next iteration...";
                } else {
                    text = "The last " + iteration + " entries are globally sorted - starting next iteration...";
                }
            }
            default -> {
            }
        }
        chart.drawText(4 * offset, secondaryTextY, text);
        chart.show(longWait);
    }

}