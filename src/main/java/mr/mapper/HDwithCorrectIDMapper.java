package mr.mapper;

import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.log4j.Logger;
import util.HammingDistance;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ogre0403 on 2015/8/26.
 */
public class HDwithCorrectIDMapper extends Mapper<Text, BytesArrayWritable,IntWritable, IntPair>  {

    private static Logger logger = Logger.getLogger(HDwithCorrectIDMapper.class);
    private Map<Text,BytesWritable> correctIDs=new HashMap<Text,BytesWritable>();
    private final static IntPair sumAndCount = new IntPair();


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        Configuration conf = context.getConfiguration();
        Path[] cacheFiles = context.getLocalCacheFiles();
        FileSystem fs = FileSystem.get(conf);
        SequenceFile.Reader reader = new SequenceFile.Reader(fs, cacheFiles[0], conf);

        Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(), conf);
        Writable value = (Writable) ReflectionUtils.newInstance(reader.getValueClass(), conf);
        Class<Writable> writableClassK=(Class<Writable>) reader.getKeyClass();
        Class<Writable> writableClassV=(Class<Writable>) reader.getValueClass();

        try {
            while (reader.next(key, value)) {
                Text k = (Text)deepCopy(key, writableClassK); // Writable 的深度复制
                BytesWritable v = (BytesWritable)deepCopy(value, writableClassV);
                correctIDs.put(k, v);
            }
        }catch (IllegalAccessException e){
            logger.error(e.getMessage());
        }catch (InstantiationException e){
            logger.error(e.getMessage());
        }
        finally {
            reader.close();
        }

    }

    @Override
    protected void map(Text key, BytesArrayWritable value, Context context) throws IOException, InterruptedException {
        BytesWritable[] vv = (BytesWritable[])value.toArray();

        for(int i = 0; i < vv.length;i++){
            int d = HammingDistance.getHammingDistance(
                    vv[i].get(), correctIDs.get(key).get());
            if(d > -1){
                sumAndCount.set(d,1);
                context.write(new IntWritable(Integer.parseInt(key.toString())), sumAndCount);
            }
        }
    }


    private  Writable deepCopy(Writable source,Class<Writable> writableClass)
            throws IOException, IllegalAccessException, InstantiationException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        DataOutputStream dataOut = new DataOutputStream(byteOutStream);
        Writable copiedValue = null;
        source.write(dataOut);
        dataOut.flush();
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteOutStream.toByteArray());
        DataInput dataInput = new DataInputStream(byteInStream);
        copiedValue = writableClass.newInstance();
        copiedValue.readFields(dataInput);

        return copiedValue;
    }
}
