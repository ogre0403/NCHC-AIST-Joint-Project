package puf.mapper;

import puf.type.ArrayIntPair;
import puf.type.BytesArrayWritable;
import puf.type.IntArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;

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

    /**
     * transform a array of byte to a array of ints.
     * The length of int array is multiples of 8.
     * Every 8 element is binary representation of byte value.
     * byte[] = {(byte)1, (byte)128}
     * int[]  = {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0}
     **/
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
