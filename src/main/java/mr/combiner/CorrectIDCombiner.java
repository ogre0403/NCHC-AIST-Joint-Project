package mr.combiner;

import mr.type.ArrayIntPair;
import mr.type.IntArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CorrectIDCombiner extends Reducer<Text, ArrayIntPair,Text,ArrayIntPair> {


    /**
     * Value which emitted by reduce is composed by
     * the number of 1's in each position of binary representation, and
     * the partial number of binary representation.
     */
    @Override
    protected void reduce(Text key, Iterable<ArrayIntPair> values, Context context) throws IOException, InterruptedException {

        int total  = 0;
        IntWritable[] vote = new IntWritable[128];

        for(int i = 0;i<vote.length;i++)
            vote[i] = new IntWritable(0);

        for(ArrayIntPair val : values){
            IntArrayWritable v = val.getFirst();
            IntWritable[]v2 = (IntWritable[]) v.toArray();

            for(int i = 0;i<v2.length;i++){
                int tmp = vote[i].get() + v2[i].get();
                vote[i].set(tmp);
            }

            total = total + val.getSecond().get();
        }

        context.write(key, new ArrayIntPair(new IntArrayWritable(vote),new IntWritable(total)));

    }

}
