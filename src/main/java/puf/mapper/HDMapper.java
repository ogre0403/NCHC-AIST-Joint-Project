package puf.mapper;

import puf.type.BytesArrayWritable;
import puf.type.IntPair;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import puf.util.HammingDistance;

import java.io.IOException;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class HDMapper extends Mapper<Text, BytesArrayWritable,IntWritable, IntPair> {

    private final static IntPair sumAndCount = new IntPair();

    /**
     *  map() first List all combination of two BytesWritable object.
     *  Then, for each combination, Calculate Hamming Distance between
     *  these two BytesWritable.
     *  map() emmit one <HD,1> K/V pair for calculating mean Hamming distance.
     */
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
