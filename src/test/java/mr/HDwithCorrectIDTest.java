package mr;

import mr.combiner.HDCombiner;
import mr.mapper.HDwithCorrectIDMapper;
import mr.reducer.AverageHDReducer;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
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

    private Reducer<IntWritable, IntPair,IntWritable,IntPair> combiner;
    private ReduceDriver<IntWritable, IntPair,IntWritable,IntPair> combinedriver;

    private Reducer<IntWritable, IntPair,IntWritable,DoubleWritable> reducer;
    private ReduceDriver<IntWritable,IntPair,IntWritable,DoubleWritable> reducedriver;

    private MapReduceDriver<Text, BytesArrayWritable, IntWritable, IntPair, IntWritable, DoubleWritable> mapReduceDriver;


    String[] data = new String[]{
            "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
            "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
            "235,2,197,99,189,218,110,67,135,6,121,185,113,104,255,216"
    };

    @Before
    public void init() {
        mapper = new HDwithCorrectIDMapper();
        mapdriver = new MapDriver<Text,BytesArrayWritable,IntWritable, IntPair>(mapper);

        combiner = new HDCombiner();
        combinedriver = new ReduceDriver<IntWritable, IntPair, IntWritable, IntPair>(combiner);

        reducer = new AverageHDReducer();
        reducedriver = new ReduceDriver<IntWritable, IntPair, IntWritable, DoubleWritable>(reducer);

        mapReduceDriver = new MapReduceDriver<Text, BytesArrayWritable, IntWritable, IntPair, IntWritable, DoubleWritable>(mapper,reducer);
        mapReduceDriver.setCombiner(combiner);

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

    @Test
    public void testMR() throws IOException {

        double average = ((double)2) / 3;
        mapReduceDriver
                .withCacheFile("correctids.seq")
                .withInput(new Text("1"), TestTool.toBytesArrayWritable(data))
                .withOutput(new IntWritable(1), new DoubleWritable(average))
                .runTest();

    }
}
