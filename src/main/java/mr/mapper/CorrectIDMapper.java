package mr.mapper;

import mr.type.ArrayIntPair;
import mr.type.BytesArrayWritable;
import mr.type.IntArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/24.
 */
public class CorrectIDMapper extends Mapper<Text, BytesArrayWritable, Text, ArrayIntPair> {
    private static Logger logger = Logger.getLogger(CorrectIDMapper.class);

    @Override
    protected void map(Text key, BytesArrayWritable value, Context context) throws IOException, InterruptedException {

        BytesWritable[] vv = (BytesWritable[])value.toArray();

        for(BytesWritable bw : vv) {
            IntWritable[] result = toBinaryIntArray(bw.getBytes(),bw.getLength());
            context.write(key, new ArrayIntPair(new IntArrayWritable(result), new IntWritable(1)));
        }
    }

    private IntWritable[] toBinaryIntArray(byte[] bs,int size){
        IntWritable[] result = new IntWritable[size*8];

        for(int i = 0;i<result.length;i++)
            result[i] = new IntWritable(0);

        for(int j = 0;j<size;j++){
            for(int i = 0; i < 8; i++) {
                if(((0x80 >>> i) & bs[j]) > 0 ){
                    result[j*8+i].set(1);
                }
            }
        }
        return  result;
    }
}
