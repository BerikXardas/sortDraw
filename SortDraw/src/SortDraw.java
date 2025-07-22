import codedraw.CodeDraw;
import codedraw.TextFormat;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class SortDraw {

    // main settings
    private static final SortMethod sortMethod = SortMethod.MERGE; // choose from INSERTION, SELECTION, BUBBLE, MERGE, QUICK (sort) or LINEAR, BINARY (search)
    private static final int cardinality = 29; // number of data points to be sorted - recommended range is [10, 100]
    private static final int lowerBound = 1; // lower bound of values (inclusive) - must be greater than 0
    private static final int upperBound = 35; // upper bound of values (exclusive) - must be greater than lowerBound
    private static final long shortWait = 400L; // the freeze time in milliseconds upon minor changes in the graphical representation - recommended range is [300, 1000]
    private static final long longWait = 3000L; // the freeze time in milliseconds upon major changes in the graphical representation - recommended range is [2000, 4000]
    private static final int splitCutoff = 5; // in merge sort and quick sort, if the recursion level has at most this many data points, then insertion sort is used rather than a recursive call
    private static final boolean showMerge = true; // if set true, then the merge steps in merge sort will be shown in detail on a second canvas
    private static final boolean showPartition = true; // if set true, then the partition exchanges in quick sort will be shown in detail
    private static int searchValue = 0; // the value to be found in linear or binary search - will be set to a random number within range if less than 1
    private static final int canvasWidth = 800;

    // better leave these alone
    private static final int canvasHeight = canvasWidth * 3 / 4;
    private static final double titleSpace = canvasHeight / 15.;
    private static final double offset = canvasWidth / 100.;
    private static final double chartWidth = canvasWidth - 6. * offset;
    private static final double chartHeight = canvasHeight - 3. * titleSpace;
    private static final double barWidth = chartWidth / cardinality;
    private static final double primaryTextY = chartHeight + titleSpace + 4. * offset;
    private static final double secondaryTextY = chartHeight + 2. * titleSpace + 2. * offset;
    private static final CodeDraw chart = new CodeDraw(canvasWidth, canvasHeight);
    private static final TextFormat textFormat = new TextFormat();

    private enum SortMethod {
        INSERTION,
        SELECTION,
        BUBBLE,
        MERGE,
        QUICK,
        LINEAR,
        BINARY
    }

    public static void main(String[] args) {

        textFormat.setFontSize(canvasWidth / 35);
        chart.setTextFormat(textFormat);
        chart.setWindowPositionX(0);
        chart.setWindowPositionY(0);
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
                    secondaryText("Data locally sorted up to vertical line marker - starting next iteration...");
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
                            secondaryText("Found new minimum fur current iteration.");
                            min = j;
                        }
                    }
                    drawChart(data, i + 1, i, cardinality, min);
                    secondaryText("Place minimum at the first unsorted position.");
                    highlightBarGroup(i, 1);
                    exchange(data, i, min);
                    drawChart(data, i + 1, i, cardinality, i);
                    if (i == 0){
                        secondaryText("The first data point is globally sorted - starting next iteration...");
                    } else{
                        secondaryText("The first " + (i+1) + " data points are globally sorted - starting next iteration...");
                    }
                }
                drawChart(data, cardinality, 0, cardinality, -1);
                chart.show();
            }

            // bubble sort
            case BUBBLE -> {
                for (int i = 0; i < data.length - 1; i++) {
                    drawChart(data, i + 1, 0, cardinality - i, 0);
                    for (int j = 0; j < data.length - i - 1; j++) {
                        if (data[j] > data[j + 1]) {
                            highlightBarGroup(j + 1, 1);
                            exchange(data, j, j + 1);
                        }
                        drawChart(data, i + 1, j + 1, cardinality - i, j + 1);
                    }
                    if (i == 0){
                        secondaryText("The last data point is globally sorted - starting next iteration...");
                    } else{
                        secondaryText("The last " + (i+1) + " data points are globally sorted - starting next iteration...");
                    }
                }
                drawChart(data, cardinality, 0, cardinality, -1);
            }

            // merge sort
            case MERGE -> {
                int[] help = new int[data.length];
                mergeSort(data, help, 0, data.length - 1, 0);
                secondaryText("Finished sorting!");
            }

            // quick sort
            case QUICK -> {
                quickSort(data, 0, data.length - 1, 0);
                drawChart(data, 0, 0, cardinality + 1, -1);
                secondaryText("Finished sorting!");
            }

            // linear search
            case LINEAR -> {
                if (searchValue < 1) {
                    searchValue = random.nextInt(lowerBound, upperBound);
                }
                boolean found = false;
                for (int i = 0; i < data.length; i++) {
                    drawChart(data, i + 1, i, cardinality + 1, i);
                    if (data[i] == searchValue) {
                        found = true;
                        secondaryText("Found key " + searchValue + " on index " + i);
                        break;
                    }
                }
                if (!found) {
                    drawChart(data, data.length, -1, -1, -1);
                    secondaryText("Key " + searchValue + " not found");
                }
            }

            // binary search
            case BINARY -> {
                if (searchValue < 1) {
                    searchValue = random.nextInt(lowerBound, upperBound);
                }
                boolean found = false;

                // in reality, it's your own responsibility to provide pre-sorted data
                // it would be absurd to sort before every call of a binary search
                boundedInsertionSort(data, 0, data.length - 1);

                int step = 1;
                int lo = 0, hi = data.length - 1;
                while (lo <= hi) {
                    int mid = lo + (hi - lo) / 2;
                    int value = data[mid];
                    drawChart(data, step, lo, hi + 1, mid);
                    if (value < searchValue) {
                        secondaryText("Key on mid (" + value + ") is less than " + searchValue + " - continue search on right side only...");
                        lo = mid + 1;
                    } else if (value > searchValue) {
                        secondaryText("Key on mid (" + value + ") is greater than " + searchValue + " - continue search on left side only...");
                        hi = mid - 1;
                    } else {
                        found = true;
                        secondaryText("Found key " + searchValue + " on index " + mid + " - linear search would need " + (mid + 1) + " step(s)");
                        break;
                    }
                    step += 1;
                }
                if (!found) {
                    drawChart(data, step, -1, hi - 1, -1);
                    //secondaryText("Key " + searchValue + " not found - linear search would need " + data.length + " step(s)");
                    secondaryText("Key " + searchValue + " not found - linear search would need " + (lo + 1) + " step(s)");
                }

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
            secondaryText("One data point - no further action needed...");
            return;
        } else if (hi - lo < splitCutoff) {
            secondaryText("Less than " + (splitCutoff + 1) + " data points - using insertion sort...");
            boundedInsertionSort(data, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            secondaryText("More than " + splitCutoff + " data point(s) - splitting into recursion level " + (recursion + 1));
            mergeSort(data, help, lo, mid, recursion + 1);
            mergeSort(data, help, mid + 1, hi, recursion + 1);
            drawChart(data, recursion, lo, hi + 1, -1);
            highlightBarGroup(lo, hi - lo + 1);
            drawVerticalLine(mid);
            secondaryText("Merging recursion level " + (recursion + 1) + " into level " + recursion);
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
        mergeChart.setWindowPositionX(chart.getWindowPositionX() + canvasWidth);
        mergeChart.setWindowPositionY(chart.getWindowPositionY() + 100);
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
            double x = 2. * offset + chartWidth - (hi - lo + 1) % 2 * chartWidth / ((hi - lo + 1) + (hi - lo + 1) % 2);
            double y = chartHeight / 2. + titleSpace - (data[k] * (chartHeight / 2. - titleSpace) / (upperBound - 1));
            mergeChart.drawLine(2. * offset, y, x, y);
            mergeChart.setLineWidth(1);
            mergeChart.setColor(Color.black);
            mergeChart.show(longWait);
            drawMerge(mergeChart, Arrays.copyOfRange(data, lo, hi + 1), Arrays.copyOfRange(help, lo, hi + 1), i - lo, j - mid - 1, recursion);
        }
        mergeChart.drawText(2. * offset, primaryTextY, "Merge completed!");
        mergeChart.show(longWait);
        mergeChart.close();
    }

    private static void quickSort(int[] data, int lo, int hi, int recursion) {
        if (hi - lo < splitCutoff) {
            drawChart(data, recursion, lo, hi + 1, -1);
            if (hi < lo) {
                secondaryText("Base case: no data points to be sorted - returning to recursion level " + (recursion - 1));
            } else if (hi == lo) {
                secondaryText("Base case: one data point to be sorted - returning to recursion level " + (recursion - 1));
            } else {
                secondaryText("Less than " + (splitCutoff + 1) + " data points - using insertion sort...");
                boundedInsertionSort(data, lo, hi + 1);
                drawChart(data, recursion, lo, hi + 1, -1);
                secondaryText("Returning to recursion level " + (recursion - 1));
            }
            return;
        }
        int j = partition(data, lo, hi, recursion);
        drawChart(data, recursion, lo, hi + 1, j);
        secondaryText("Pivot element is on its correct place - sorting the left side...");
        quickSort(data, lo, j - 1, recursion + 1);
        drawChart(data, recursion, lo, hi + 1, j);
        secondaryText("Left part is sorted - sorting the right side...");
        quickSort(data, j + 1, hi, recursion + 1);
        drawChart(data, recursion, lo, hi + 1, j);
        secondaryText("Left and right part sorted - returning to recursion level " + (recursion - 1));
    }

    // the rightmost element is always chosen as the pivot element v
    private static int partition(int[] data, int lo, int hi, int recursion) {
        int k = lo;
        int v = data[hi];
        drawChart(data, recursion, lo, hi + 1, hi);
        secondaryText("Pivot element found - moving elements less than pivot to the left...");
        for (int i = k; i < hi; i++) {
            if (showPartition) {
                drawChart(data, recursion, lo, hi + 1, i);
                drawVerticalLine(k - 1);
                chart.show(shortWait);
            }
            if (data[i] < v) {
                if (showPartition) {
                    drawVerticalLine(k - 1);
                    secondaryText("Found an element less than pivot - exchanging...");
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
        secondaryText("Moving Pivot element to its correct place (vertical line marker).");
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
            case LINEAR -> chart.drawText(4 * offset, offset, "Linear Search");
            case BINARY -> chart.drawText(4 * offset, offset, "Binary Search (works only on sorted data)");
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
            chart.fillRectangle(4. * offset + i * barWidth, chartHeight + titleSpace - barHeight, barWidth - 1, barHeight);
        }
        if (highlight >= 0 && highlight < data.length) {
            chart.setColor(Color.red);
            chart.setLineWidth(3);
            chart.drawRectangle(4. * offset + highlight * barWidth, titleSpace, barWidth, chartHeight);
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
                case LINEAR, BINARY -> {
                    text = "Search for key " + searchValue + ": step " + step;
                    chart.setColor(Color.red);
                    chart.setLineWidth(3);
                    double lineHeight = chartHeight + titleSpace - Math.min(searchValue * chartHeight / (upperBound - 1), chartHeight + offset);
                    chart.drawLine(4. * offset, lineHeight, 4. * offset + chartWidth, lineHeight);
                    chart.setLineWidth(1);
                    chart.setColor(Color.black);
                }
                default -> {
                }
            }
        }
        chart.drawText(4. * offset, primaryTextY, text);

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
        mergeChart.drawText(2. * offset, offset, "Merging recursion level " + (recursion + 1) + " into level " + recursion);

        double halfWidth = chartWidth / 2.;
        double halfHeight = chartHeight / 2.;
        double barWidth = chartWidth / (help.length + help.length % 2);
        mergeChart.drawRectangle(2. * offset, titleSpace, halfWidth, halfHeight);
        mergeChart.drawRectangle(2. * offset + halfWidth, titleSpace, halfWidth - (help.length % 2 * barWidth), halfHeight);
        mergeChart.drawRectangle(2. * offset, titleSpace + offset + halfHeight, chartWidth - (help.length % 2 * barWidth), halfHeight);
        mergeChart.drawText(3. * offset, titleSpace + offset, "help (left half)");
        mergeChart.drawText(3. * offset + halfWidth, titleSpace + offset, "help (right half)");
        mergeChart.drawText(3. * offset, titleSpace + 2. * offset + halfHeight, "data");

        mergeChart.setColor(Color.gray);
        for (int i = 0; i < help.length; i++) {
            if ((i == leftIndex && i < help.length / 2.) || i == (int) Math.ceil(help.length / 2.) + rightIndex) {
                mergeChart.setColor(Color.black);
            }
            double barHeight = help[i] * (halfHeight - titleSpace) / (upperBound - 1);
            mergeChart.fillRectangle(2. * offset + i * barWidth, halfHeight + titleSpace - barHeight, barWidth - 1, barHeight);
            mergeChart.setColor(Color.gray);
        }
        mergeChart.setColor(Color.black);
        for (int i = 0; i < data.length; i++) {
            if (i == leftIndex + rightIndex) {
                mergeChart.setColor(Color.gray);
            }
            double barHeight = data[i] * (halfHeight - titleSpace) / (upperBound - 1);
            mergeChart.fillRectangle(2. * offset + i * barWidth, chartHeight + titleSpace - barHeight + offset, barWidth - 1, barHeight);
        }
        mergeChart.setColor(Color.cyan);
        mergeChart.setLineWidth(3);
        mergeChart.drawLine(2. * offset + halfWidth, titleSpace, 2. * offset + halfWidth, titleSpace + halfHeight);
        mergeChart.setLineWidth(1);
        mergeChart.setColor(Color.black);
    }

    private static void highlightBarGroup(int barIndex, int barCount) {
        chart.setColor(Color.red);
        chart.setLineWidth(3);
        chart.drawRectangle(4. * offset + barIndex * barWidth, titleSpace, barCount * barWidth, chartHeight);
        chart.setLineWidth(1);
        chart.setColor(Color.black);
        chart.show(shortWait);
    }

    private static void drawVerticalLine(int index) {
        chart.setColor(Color.cyan);
        chart.setLineWidth(3);
        chart.drawLine(4. * offset + (index + 1) * barWidth, titleSpace - offset,
                4. * offset + (index + 1) * barWidth, chartHeight + titleSpace + offset);
        chart.setLineWidth(1);
        chart.setColor(Color.black);
    }

    private static void secondaryText(String text) {
        chart.drawText(4. * offset, secondaryTextY, text);
        chart.show(longWait);
    }

}