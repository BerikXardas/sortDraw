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
        SortMethod sortMethod = SortMethod.INSERTION;
        int canvasWidth = 800;
        int canvasHeight = 600;
        int cardinality = 7;
        int lowerBound = 0;
        int upperBound = 50;
        int milliseconds = 2000;

        int titleSpace = canvasHeight / 10;
        int offset = canvasWidth / 100;
        int chartWidth = canvasWidth - 5*offset;
        int chartHeight = canvasHeight - 2*titleSpace;
        int barWidth = chartWidth / cardinality;

        CodeDraw chart = new CodeDraw(canvasWidth, canvasHeight);
        Random random = new Random();
        int[] data = new int[cardinality];
        for (int i = 0; i < cardinality; i++) {
            data[i] = random.nextInt(lowerBound, upperBound);
        }
        //int[] data = random.ints(cardinality, lowerBound, upperBound).toArray();

        switch (sortMethod){
            // insertion sort
            case INSERTION ->{
                chart.setTitle("Insertion Sort");
                for (int i = 1; i < data.length; i++) {
                    drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i, milliseconds);
                    for (int j = i; j > 0 && data[j] < data[j - 1]; j--) {
                        highlightBar(chart, chartHeight, offset, titleSpace, barWidth, j, milliseconds);
                        highlightBar(chart, chartHeight, offset, titleSpace, barWidth, j-1, milliseconds);
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
                    highlightBar(chart, chartHeight, offset, titleSpace, barWidth, i, milliseconds);
                    highlightBar(chart, chartHeight, offset, titleSpace, barWidth, min, milliseconds);
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
                            highlightBar(chart, chartHeight, offset, titleSpace, barWidth, j, milliseconds);
                            highlightBar(chart, chartHeight, offset, titleSpace, barWidth, j+1, milliseconds);
                            exchange (data, j,j + 1);
                            drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i+1, milliseconds);
                        }
                    }
                    endOfIterationText(sortMethod, chart, chartHeight, offset, titleSpace, i+1, milliseconds);
                }
                drawChart(sortMethod, chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, cardinality, milliseconds);
            }

            case MERGE -> {}

            default -> {}
        }

    }


    private static void exchange(int[] data, int i, int j) {
        int swap = data[i];
        data[i] = data[j];
        data[j] = swap;
    }

    private static void drawChart(SortMethod sortMethod, CodeDraw chart, int chartWidth, int chartHeight, int offset, int titleSpace, int[] data, int lowerBound, int upperBound, int iteration, int milliseconds) throws InterruptedException {
        int barMaxHeight = chartHeight - offset;
        int barWidth = chartWidth / data.length;

        chart.clear();
        switch (sortMethod){
            case INSERTION -> chart.drawText(4*offset, offset, "Insertion Sort");
            case SELECTION -> chart.drawText(4*offset, offset, "Selection Sort");
            case BUBBLE -> chart.drawText(4*offset, offset, "Bubble Sort");
            default -> {}
        }

        chart.drawRectangle(4*offset,titleSpace, chartWidth, chartHeight);

        for (int i = 0; i < data.length; i++) {
            int barHeight = (int) ((double) data[i] / upperBound * barMaxHeight + offset);
            chart.fillRectangle(4*offset + i*barWidth, chartHeight + titleSpace - barHeight ,barWidth-1, barHeight);
        }
        if (iteration < data.length){
            if (sortMethod == SortMethod.INSERTION){
                chart.setColor(Color.green);
                chart.drawLine(4*offset + (iteration+1)*barWidth, titleSpace - offset, 4*offset + (iteration+1)*barWidth, chartHeight + titleSpace + offset);
                chart.setColor(Color.black);
            }
            chart.drawText(4*offset, chartHeight + titleSpace + offset, "outer loop iteration #" + iteration);
        }
        else {
            chart.drawText(4*offset, chartHeight + titleSpace + offset, "sorting completed!");
        }

        chart.show(milliseconds);
    }

    private static void highlightBar(CodeDraw chart, int chartHeight, int offset, int titleSpace, int barWidth, int barIndex, int milliseconds) throws InterruptedException {
        chart.setColor(Color.red);
        chart.drawRectangle(4*offset + barIndex*barWidth, titleSpace, barWidth, chartHeight);
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


}