package mr.reducer;

import mr.type.ArrayIntPair;
import mr.type.IntArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CorrectIDReducer extends Reducer<Text, ArrayIntPair,Text, BytesWritable> {
    private static int[] pow_2 = new int[]{128,64,32,16,8,4,2,1};

    @Override
    protected void reduce(Text key, Iterable<ArrayIntPair> values, Context context) throws IOException, InterruptedException {

        int[] total_vote = new int[128];
        int total = 0;
        for (ArrayIntPair val : values) {
            IntArrayWritable v = val.getFirst();
            IntWritable[]v2 = (IntWritable[]) v.toArray();

            for(int i = 0;i<v2.length;i++){
                total_vote[i] = total_vote[i]+v2[i].get();
            }

            total += val.getSecond().get();
        }
        context.write(key, getCorrectID(total_vote,total));
    }

    /**
     * Get the correct ids, and get back the byte number.
     * The value of each position in binary representation
     * is determined by majority voting.
     **/
    private BytesWritable getCorrectID(int[] vote,int total){

        byte[] result = new byte[16];
        for(int i = 0;i<vote.length;i++){
            if(vote[i] * 2 > total)
                vote[i] = 1;
            else
                vote[i] = 0;
        }

        for(int i = 0;i< vote.length / 8 ;i++) {
            int v =0;
            for (int j = 0; j < 8; j++) {
                v = v + vote[8*i+j] * pow_2[j];
            }
            result[i] = (byte)v;
        }
        return new BytesWritable(result);
    }
}
