package mr.HD;

import mr.IntPair;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by 1403035 on 2015/8/14.
 */
public class HDCombiner extends Reducer<Text, IntPair,Text,IntPair> {

    private IntPair result = new IntPair();

    @Override
    protected void reduce(Text key, Iterable<IntPair> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        int total = 0;
        for (IntPair val : values) {
            sum += val.getFirst();
            total += val.getSecond();
        }
        result.set(sum, total);
        context.write(key, result);
    }
}
