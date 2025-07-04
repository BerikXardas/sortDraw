import codedraw.CodeDraw;

import java.awt.*;
import java.util.Random;

public class SortDraw {

    private enum SortMethod {
        BUBBLE,
        INSERTION,
        SELECTION,
        MERGE
    }
    public static void main(String[] args) throws InterruptedException {

        // This block can be seen as "settings" - feel free to change the values to an arbitrary (but reasonable) value
        SortMethod sortMethod = SortMethod.MERGE;
        int canvasWidth = 800;
        int canvasHeight = 600;
        int cardinality = 27;
        int lowerBound = 0;
        int upperBound = 50;
        int milliseconds = 2000;

        int titleSpace = canvasHeight / 10;
        int offset = canvasWidth / 100;
        int chartWidth = canvasWidth - 5*offset;
        int chartHeight = canvasHeight - 2*titleSpace;
        double barWidth = (double) chartWidth / cardinality;

        CodeDraw chart = new CodeDraw(canvasWidth, canvasHeight);
        int cutoff = 5;
        Random random = new Random();
        int[] data = new int[cardinality];
        for (int i = 0; i < cardinality; i++) {
            data[i] = random.nextInt(lowerBound, upperBound);
        }
        //int[] data = random.ints(cardinality, lowerBound, upperBound).toArray();

        switch (sortMethod){
            // insertion sort
            case INSERTION -> {
                chart.setTitle("Insertion Sort");
                for (int i = 1; i < data.length; i++) {
                    drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i, milliseconds);
                    for (int j = i; j > 0 && data[j] < data[j - 1]; j--) {
                        highlightBarGroup(chart, chartHeight, offset, titleSpace, barWidth, j, 1, milliseconds);
                        highlightBarGroup(chart, chartHeight, offset, titleSpace, barWidth, j-1, 1, milliseconds);
                        exchange(data, j, j - 1);
                        drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i, milliseconds);
                    }
                    endOfIterationText(sortMethod, chart, chartHeight, offset, titleSpace, i, milliseconds);
                }
                drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, cardinality, milliseconds);
            }

            // selection sort
            case SELECTION -> {
                chart.setTitle("Selection Sort");
                for (int i = 0 ; i < data.length - 1 ; i++){
                    drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i+1, milliseconds);

                    int min = i;
                    for (int j = i + 1 ; j < data.length ; j++){
                        if (data[j] < data[min]){
                            min = j;
                        }
                    }
                    highlightBarGroup(chart, chartHeight, offset, titleSpace, barWidth, i, 1, milliseconds);
                    highlightBarGroup(chart, chartHeight, offset, titleSpace, barWidth, min, 1, milliseconds);
                    exchange(data, i, min);
                    drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i+1, milliseconds);
                    endOfIterationText(sortMethod, chart, chartHeight, offset, titleSpace, i+1, milliseconds);
                }
                drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, cardinality, milliseconds);
            }

            // bubble sort
            case BUBBLE -> {
                chart.setTitle("Bubble Sort");
                for (int i = 0; i < data.length - 1 ; i++){
                    drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i+1, milliseconds);
                    for (int j = 0 ; j < data.length - i - 1 ; j++){
                        if (data [j] > data [j + 1]){
                            highlightBarGroup(chart, chartHeight, offset, titleSpace, barWidth, j, 1, milliseconds);
                            highlightBarGroup(chart, chartHeight, offset, titleSpace, barWidth, j+1, 1, milliseconds);
                            exchange (data, j,j + 1);
                            drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i+1, milliseconds);
                        }
                    }
                    endOfIterationText(sortMethod, chart, chartHeight, offset, titleSpace, i+1, milliseconds);
                }
                drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, cardinality, milliseconds);
            }

            case MERGE -> {
                chart.setTitle("Merge Sort");
                // drawData is an object of type DrawData which is defined at the bottom of this file
                // the purpose is to encapsulate information (data) needed for updating the chart without inflating the input parameters of the recursive calls of mergeSort
                DrawData drawData = new DrawData(canvasWidth, canvasHeight, milliseconds, titleSpace, offset, chartWidth, chartHeight, barWidth, lowerBound, upperBound, chart);
                int[] help = new int[data.length];
                mergeSort(data, help, 0, data.length - 1, cutoff, 0, drawData);
            }

            default -> {}
        }

    }
    private static void mergeSort(int[] data, int[] help, int lo, int hi, int cutoff, int i, DrawData drawData) throws InterruptedException {
        if (hi <= lo) return;
        drawChart(SortMethod.MERGE, drawData.chart, drawData.chartWidth, drawData.chartHeight, drawData.offset, drawData.titleSpace, data, drawData.lowerBound, drawData.upperBound, i, drawData.milliseconds);
        highlightBarGroup(drawData.chart, drawData.chartHeight, drawData.offset, drawData.titleSpace, drawData.barWidth, lo, hi-lo+1, drawData.milliseconds);
        int mid = lo + (hi - lo) / 2;
        if (hi - lo < cutoff){
            drawData.chart.drawText(4*drawData.offset, drawData.chartHeight + drawData.titleSpace + drawData.titleSpace/2,
                    "Cutoff cardinality of " + cutoff + " reached - using insertion sort...");
            drawData.chart.show(drawData.milliseconds);
            boundedInsertionSort(data, lo, hi);
        }
        else {
            drawData.chart.drawText(4*drawData.offset, drawData.chartHeight + drawData.titleSpace + drawData.titleSpace/2,
                    "Cutoff cardinality of " + cutoff + " not reached - splitting into recursion level " + (i+1));
            drawData.chart.show(drawData.milliseconds);
            mergeSort(data, help, lo, mid, cutoff, i + 1, drawData);
            mergeSort(data, help, mid + 1, hi, cutoff, i + 1, drawData);
            highlightBarGroup(drawData.chart, drawData.chartHeight, drawData.offset, drawData.titleSpace, drawData.barWidth, lo, hi-lo+1, drawData.milliseconds);
            merge(data, help, lo, mid, hi);
            drawData.chart.drawText(4*drawData.offset, drawData.chartHeight + drawData.titleSpace + drawData.titleSpace/2,
                    "Merging recursion level " + (i+1) + " into level " + i);
            drawData.chart.show(drawData.milliseconds);
        }
        drawChart(SortMethod.MERGE, drawData.chart, drawData.chartWidth, drawData.chartHeight,drawData.offset, drawData.titleSpace, data, drawData.lowerBound, drawData.upperBound, i, drawData.milliseconds);
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

    private static void drawChart(SortMethod sortMethod, CodeDraw chart, int chartWidth, int chartHeight, int offset, int titleSpace, int[] data, int lowerBound, int upperBound, int iteration, int milliseconds) throws InterruptedException {
        int barMaxHeight = chartHeight - offset;
        double barWidth = (double) chartWidth / data.length;

        chart.clear();
        switch (sortMethod){
            case INSERTION -> chart.drawText(4*offset, offset, "Insertion Sort");
            case SELECTION -> chart.drawText(4*offset, offset, "Selection Sort");
            case BUBBLE -> chart.drawText(4*offset, offset, "Bubble Sort");
            case MERGE -> chart.drawText(4*offset, offset, "Merge Sort");
            default -> {}
        }

        chart.drawRectangle(4*offset,titleSpace, chartWidth, chartHeight);

        for (int i = 0; i < data.length; i++) {
            int barHeight = (int) ((double) data[i] / upperBound * barMaxHeight + offset);
            chart.fillRectangle(4*offset + i*barWidth, chartHeight + titleSpace - barHeight ,barWidth-1, barHeight);
        }
        if (sortMethod == SortMethod.MERGE){
            chart.drawText(4*offset, chartHeight + titleSpace + offset, "recursion level " + iteration);
        }
        else {
            if (iteration < data.length){
                if (sortMethod == SortMethod.INSERTION){
                    chart.setColor(Color.blue);
                    chart.drawLine(4*offset + (iteration+1)*barWidth, titleSpace - offset, 4*offset + (iteration+1)*barWidth, chartHeight + titleSpace + offset);
                    chart.setColor(Color.black);
                }
                chart.drawText(4*offset, chartHeight + titleSpace + offset, "outer loop iteration #" + iteration);
            }
            else {
                chart.drawText(4*offset, chartHeight + titleSpace + offset, "sorting completed!");
            }
        }
        chart.show(milliseconds);
    }
    private static void highlightBarGroup(CodeDraw chart, int chartHeight, int offset, int titleSpace, double barWidth, int barIndex, int barCount, int milliseconds) throws InterruptedException {
        chart.setColor(Color.red);
        chart.drawRectangle(4*offset + barIndex*barWidth, titleSpace, barCount*barWidth, chartHeight);
        chart.setColor(Color.black);
        chart.show(milliseconds);
    }

    private static void endOfIterationText(SortMethod sortMethod, CodeDraw chart, int chartHeight, int offset, int titleSpace, int iteration, int milliseconds) throws InterruptedException {
        switch (sortMethod){
            case INSERTION -> {
                chart.drawText(4*offset, chartHeight + titleSpace + titleSpace/2, "data sorted up to marker - starting next iteration...");
            }
            case SELECTION -> {
                if (iteration == 1){
                    chart.drawText(4*offset, chartHeight + titleSpace + titleSpace/2, "the first entry is on its correct index - starting next iteration...");
                }
                else{
                    chart.drawText(4*offset, chartHeight + titleSpace + titleSpace/2, "the first " + iteration + " entries are on their correct indexes - starting next iteration...");
                }
            }
            case BUBBLE -> {
                if (iteration == 1){
                    chart.drawText(4*offset, chartHeight + titleSpace + titleSpace/2, "the last entry is on its correct index - starting next iteration...");
                }
                else{
                    chart.drawText(4*offset, chartHeight + titleSpace + titleSpace/2, "the last " + iteration + " entries are on their correct indexes - starting next iteration...");
                }
            }
            default -> {}
        }

        chart.show(milliseconds * 2);
    }

    // the purpose of the class DrawData is to encapsulate information (data) needed for updating the chart without inflating the input parameters of the recursive calls of mergeSort
    static class DrawData {
        public DrawData(int canvasWidth, int canvasHeight, int milliseconds, int titleSpace, int offset, int chartWidth, int chartHeight, double barWidth, int lowerBound, int upperBound, CodeDraw chart) {
            this.canvasWidth = canvasWidth;
            this.canvasHeight = canvasHeight;
            this.milliseconds = milliseconds;
            this.titleSpace = titleSpace;
            this.offset = offset;
            this.chartWidth = chartWidth;
            this.chartHeight = chartHeight;
            this.barWidth = barWidth;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.chart = chart;
        }
        int canvasWidth;
        int canvasHeight;
        int milliseconds;

        int titleSpace;
        int offset;
        int chartWidth;
        int chartHeight;
        double barWidth;
        int lowerBound;
        int upperBound;

        CodeDraw chart;
    }

}