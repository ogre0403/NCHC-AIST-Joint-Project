package mr.mapper;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by ogre0403 on 2015/8/31.
 */
public class IdentityMapper extends Mapper<Text, BytesWritable , Text, BytesWritable> {
    @Override
    protected void map(Text key, BytesWritable value, Context context)
            throws IOException, InterruptedException {

            context.write(key,value);
    }
}
