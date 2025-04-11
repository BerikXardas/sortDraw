import codedraw.CodeDraw;

import java.awt.*;
import java.util.Random;

public class SortDraw {


    public static void main(String[] args) throws InterruptedException {

        int canvasWidth = 800;
        int canvasHeight = 600;
        int titleSpace = canvasHeight / 10;
        int offset = canvasWidth / 100;
        int chartWidth = canvasWidth - 5*offset;
        int chartHeight = canvasHeight - 2*titleSpace;
        CodeDraw chart = new CodeDraw(canvasWidth, canvasHeight);
        Random random = new Random();

        int cardinality = 7;
        int lowerBound = 0;
        int upperBound = 50;

        int[] data = new int[cardinality];
        for (int i = 0; i < cardinality; i++) {
            data[i] = random.nextInt(lowerBound, upperBound);
        }
        //int[] data = random.ints(cardinality, lowerBound, upperBound).toArray();

        int seconds = 2;

        // insertion sort
        chart.setTitle("Insertion Sort");
        for (int i = 1; i < data.length; i++) {
            drawInsertionSort(chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i);
            Thread.sleep(seconds * 1000);
            for (int j = i; j > 0 && data[j] < data[j - 1]; j--) {
                chart.setColor(Color.red);
                int barWidth = chartWidth / data.length;
                chart.drawRectangle(4*offset + j*barWidth, titleSpace, barWidth, chartHeight);
                chart.show();
                Thread.sleep(seconds * 1000);
                chart.drawRectangle(4*offset + (j-1)*barWidth, titleSpace, barWidth, chartHeight);
                chart.setColor(Color.black);
                chart.show();
                Thread.sleep(seconds * 1000);

                exchange(data, j, j - 1);

                drawInsertionSort(chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, i);
                Thread.sleep(seconds * 1000);
            }
            chart.drawText(4*offset, chartHeight + titleSpace + titleSpace/2, "data sorted up to marker - starting next iteration...");
            chart.show();
            Thread.sleep(seconds * 2000);
        }
        drawInsertionSort(chart, chartWidth, chartHeight, offset, titleSpace, data, lowerBound, upperBound, cardinality);



    }


    private static void exchange(int[] data, int i, int j) {
        int swap = data[i];
        data[i] = data[j];
        data[j] = swap;
    }

    private static void drawInsertionSort(CodeDraw chart, int chartWidth, int chartHeight, int offset, int titleSpace, int[] data, int lowerBound, int upperBound, int iteration){
        int barMaxHeight = chartHeight - offset;
        int barWidth = chartWidth / data.length;

        chart.clear();
        chart.drawText(4*offset, offset, "Insertion Sort");
        chart.drawRectangle(4*offset,titleSpace, chartWidth, chartHeight);

        for (int i = 0; i < data.length; i++) {
            int barHeight = (int) ((double) data[i] / upperBound * barMaxHeight + offset);
            chart.fillRectangle(4*offset + i*barWidth, chartHeight + titleSpace - barHeight ,barWidth-1, barHeight);
        }
        if (iteration < data.length){
            chart.setColor(Color.green);
            chart.drawLine(4*offset + (iteration+1)*barWidth, titleSpace - offset, 4*offset + (iteration+1)*barWidth, chartHeight + titleSpace + offset);
            chart.setColor(Color.black);
            chart.drawText(4*offset, chartHeight + titleSpace + offset, "outer loop iteration #" + iteration);
        }
        else {
            chart.drawText(4*offset, chartHeight + titleSpace + offset, "sorting completed!");
        }

        chart.show();
    }


}