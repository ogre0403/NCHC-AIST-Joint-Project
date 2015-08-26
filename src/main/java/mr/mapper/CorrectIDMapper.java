package mr.mapper;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/24.
 */
public class CorrectIDMapper extends Mapper<Text, Text, Text, BytesWritable> {
    private static Logger logger = Logger.getLogger(CorrectIDMapper.class);

    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {
        byte[] ba = new byte[16];

        StringTokenizer itr = new StringTokenizer(value.toString(),",");
        int index = 0;
        Integer I;
        while (itr.hasMoreTokens()) {
            I = Integer.parseInt(itr.nextToken());
            ba[index] = I.byteValue();
            index+=1;
        }

        context.write(key, new BytesWritable(ba));
    }
}
