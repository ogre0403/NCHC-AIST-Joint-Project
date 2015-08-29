package mr.mapper;

import mr.type.ArrayIntPair;
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
public class CorrectIDMapper extends Mapper<Text, Text, Text, ArrayIntPair> {
    private static Logger logger = Logger.getLogger(CorrectIDMapper.class);

    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        byte[] ba = new byte[16];

        StringTokenizer itr = new StringTokenizer(value.toString(),",");
        int index = 0;
        Integer I;
        while (itr.hasMoreTokens()) {
            I = Integer.parseInt(itr.nextToken().trim());
            ba[index] = I.byteValue();
            index+=1;
        }

        IntWritable[] result = toBinaryIntArray(ba);
        context.write(key, new ArrayIntPair(new IntArrayWritable(result),new IntWritable(1)));

    }

    private IntWritable[] toBinaryIntArray(byte[] bs){
        IntWritable[] result = new IntWritable[bs.length*8];
        for(int i = 0;i<result.length;i++)
            result[i] = new IntWritable(0);


        for(int j = 0;j<bs.length;j++){
            for(int i = 0; i < 8; i++) {
                if(((0x80 >>> i) & bs[j]) > 0 ){
                    result[j*8+i].set(1);
                }
            }
        }
        return  result;
    }
}
