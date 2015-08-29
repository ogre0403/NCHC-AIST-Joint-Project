package mr.mapper;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class FlattenMapper extends Mapper<Text, Text, Text, BytesWritable> {
    private static Logger logger = Logger.getLogger(FlattenMapper.class);
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

        BytesWritable bw = new BytesWritable(ba);
        context.write(new Text(key),bw);
    }
}
