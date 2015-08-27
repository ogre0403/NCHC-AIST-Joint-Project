package mr;

import mr.reducer.AverageHDReducer;
import mr.combiner.HDCombiner;
import mr.mapper.HDMapper;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.io.BytesWritable;
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
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class HDTest {

    private Mapper<Text,BytesArrayWritable,IntWritable, IntPair> mapper;
    private MapDriver<Text,BytesArrayWritable,IntWritable, IntPair> mapdriver;

    private Reducer<IntWritable, IntPair,IntWritable,IntPair> combiner;
    private ReduceDriver<IntWritable, IntPair,IntWritable,IntPair> combindriver;

    private Reducer<IntWritable, IntPair,IntWritable,DoubleWritable> reducer;
    private ReduceDriver<IntWritable,IntPair,IntWritable,DoubleWritable>reducedriver;

    private MapReduceDriver<Text, BytesArrayWritable, IntWritable, IntPair, IntWritable, DoubleWritable> mapReduceDriver;

    String[] data = new String[]{
            "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
            "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216",
            "235,2,197,99,189,218,110,67,135,6,121,185,113,104,255,216"
    };


    @Before
    public void init() {
        mapper = new HDMapper();
        mapdriver = new MapDriver<Text,BytesArrayWritable,IntWritable, IntPair>(mapper);

        combiner = new HDCombiner();
        combindriver = new ReduceDriver<IntWritable, IntPair, IntWritable, IntPair>(combiner);

        reducer = new AverageHDReducer();
        reducedriver = new ReduceDriver<IntWritable, IntPair, IntWritable, DoubleWritable>(reducer);

        mapReduceDriver = new MapReduceDriver<Text, BytesArrayWritable, IntWritable, IntPair, IntWritable, DoubleWritable>(mapper,reducer);
        mapReduceDriver.setCombiner(combiner);
    }

    @Test
    public void testMapper() throws IOException {

        mapdriver.withInput(new Text("1"), TestTool.toBytesArrayWritable(data))
                .withOutput(new IntWritable(1), new IntPair(0, 1))
                .withOutput(new IntWritable(1), new IntPair(2, 1))
                .withOutput(new IntWritable(1), new IntPair(2, 1))
                .runTest();
    }


    @Test
    public void testCombiner() throws IOException {
        ArrayList<IntPair> values = new ArrayList<IntPair>();
        values.add(new IntPair(0,1));
        values.add(new IntPair(2,1));
        values.add(new IntPair(2,1));

        combindriver.withInput(new IntWritable(1),values)
                .withOutput(new IntWritable(1), new IntPair(4, 3))
                .runTest();

    }


    @Test
    public void testReducer() throws IOException{
        ArrayList<IntPair> values = new ArrayList<IntPair>();
        values.add(new IntPair(0,1));
        values.add(new IntPair(2,1));
        values.add(new IntPair(2,1));

        double average = ((double)4) / 3;
        reducedriver.withInput(new IntWritable(1),values)
                .withOutput(new IntWritable(1), new DoubleWritable(average))
                .runTest();
    }


    @Test
    public void testMR() throws IOException {
        double average = ((double)4) / 3;
        mapReduceDriver
                .withInput(new Text("1"), TestTool.toBytesArrayWritable(data))
                .withInput(new Text("2"), TestTool.toBytesArrayWritable(data))
                .withOutput(new IntWritable(1), new DoubleWritable(average))
                .withOutput(new IntWritable(2), new DoubleWritable(average))
                .runTest();
    }



}
