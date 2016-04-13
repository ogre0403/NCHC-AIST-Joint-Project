package puf.reducer;

import puf.type.IntPair;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class AverageHDReducer extends Reducer<IntWritable, IntPair, IntWritable, DoubleWritable>{
    private DoubleWritable result = new DoubleWritable();

    /**
     * reduce() calculate total sum of Hamming distance and
     * total sum of number of byte pairs.
     * reduce() emmit the average hamming distance by calculating
     * total HD over total number of byte pairs.
     */
    @Override
    protected void reduce(IntWritable key, Iterable<IntPair> values, Context context) throws IOException, InterruptedException {
        double sum = 0;
        int total = 0;
        for (IntPair val : values) {
            sum += val.getFirst();
            total += val.getSecond();
        }
        double average = sum / total;
        result.set(average);
        context.write(key, result);
    }
}
