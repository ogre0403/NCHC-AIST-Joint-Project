package mr.combiner;

import mr.type.ArrayIntPair;
import mr.type.IntArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by 1403035 on 2015/8/24.
 */
public class CorrectIDCombiner extends Reducer<Text, BytesWritable,Text,ArrayIntPair> {
    @Override
    protected void reduce(Text key, Iterable<BytesWritable> values, Context context) throws IOException, InterruptedException {

        int count  = 0;
        IntWritable[] vote = new IntWritable[128];
        for(int i = 0;i<vote.length;i++)
            vote[i] = new IntWritable(0);

        for(BytesWritable val : values){
            byte[] b_16 = val.getBytes();
            accumulateBitNum(vote, b_16);
            count++;
        }

        context.write(key, new ArrayIntPair(new IntArrayWritable(vote),new IntWritable(count)));

    }

    private void accumulateBitNum(IntWritable[] vote, byte[] bs){

        for(int j = 0;j<bs.length;j++){
            for(int i = 0; i < 8; i++) {
                if(((0x80 >>> i) & bs[j]) > 0 ){
                    int v = vote[j*8+i].get();
                    vote[j*8+i].set(++v);
                }
            }
        }

    }
}
