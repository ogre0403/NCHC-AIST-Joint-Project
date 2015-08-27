package mr;

import mr.mapper.HDwithCorrectIDMapper;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;
import util.TestTool;

import java.io.IOException;

/**
 * Created by ogre0403 on 2015/8/26.
 */
public class HDwithCorrectIDTest {

    private Mapper<Text,BytesArrayWritable,IntWritable, IntPair> mapper;
    private MapDriver<Text,BytesArrayWritable,IntWritable, IntPair> mapdriver;

    String[] data = new String[]{
            "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
            "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
            "235,2,197,99,189,218,110,67,135,6,121,185,113,104,255,216"
    };

    @Before
    public void init() {
        mapper = new HDwithCorrectIDMapper();
        mapdriver = new MapDriver<Text,BytesArrayWritable,IntWritable, IntPair>(mapper);
    }

    @Test
    public void testMapper() throws IOException {

        mapdriver.withCacheFile("correctids.seq")
                .withInput(new Text("1"), TestTool.toBytesArrayWritable(data))
                .withOutput(new IntWritable(1), new IntPair(0, 1))
                .withOutput(new IntWritable(1), new IntPair(0, 1))
                .withOutput(new IntWritable(1), new IntPair(2, 1))
                .runTest();

    }
}
