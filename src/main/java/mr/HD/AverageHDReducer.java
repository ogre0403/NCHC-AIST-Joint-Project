package mr.HD;

import mr.IntPair;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class AverageHDReducer extends Reducer<Text, IntPair, Text, DoubleWritable>{
    private DoubleWritable result = new DoubleWritable();

    @Override
    protected void reduce(Text key, Iterable<IntPair> values, Context context) throws IOException, InterruptedException {
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
