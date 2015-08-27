package mr.mapper;

import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import util.HammingDistance;

import java.io.IOException;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class HDMapper extends Mapper<Text, BytesArrayWritable,IntWritable, IntPair> {

    private final static IntPair sumAndCount = new IntPair();
    @Override
    protected void map(Text key, BytesArrayWritable value, Context context) throws IOException, InterruptedException {
        BytesWritable[] vv = (BytesWritable[])value.toArray();

        for(int i = 0; i < vv.length;i++){
            for(int j = i+1; j< vv.length;j++){
                int d = HammingDistance.getHammingDistance(vv[i].get(),vv[j].get());
                if(d > -1){
                    sumAndCount.set(d,1);

                    context.write(new IntWritable(Integer.parseInt(key.toString())), sumAndCount);
                }
            }
        }

    }
}
