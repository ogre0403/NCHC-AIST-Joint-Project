package mr;

import mr.reducer.AverageHDReducer;
import mr.combiner.HDCombiner;
import mr.mapper.HDMapper;
import mr.type.BytesArrayWritable;
import mr.type.IntPair;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class HDTest {

    private Mapper<Text,BytesArrayWritable,Text, IntPair> mapper;
    private MapDriver<Text,BytesArrayWritable,Text, IntPair> mapdriver;

    private Reducer<Text, IntPair,Text,IntPair> combiner;
    private ReduceDriver<Text, IntPair,Text,IntPair> combindriver;

    private Reducer<Text, IntPair,Text,DoubleWritable> reducer;
    private ReduceDriver<Text,IntPair,Text,DoubleWritable>reducedriver;

    private MapReduceDriver<Text, BytesArrayWritable, Text, IntPair, Text, DoubleWritable> mapReduceDriver;



    @Before
    public void init() {
        mapper = new HDMapper();
        mapdriver = new MapDriver<Text,BytesArrayWritable,Text, IntPair>(mapper);

        combiner = new HDCombiner();
        combindriver = new ReduceDriver<Text, IntPair, Text, IntPair>(combiner);

        reducer = new AverageHDReducer();
        reducedriver = new ReduceDriver<Text, IntPair, Text, DoubleWritable>(reducer);

        mapReduceDriver = new MapReduceDriver<Text, BytesArrayWritable, Text, IntPair, Text, DoubleWritable>(mapper,reducer);
        mapReduceDriver.setCombiner(combiner);
    }

    @Test
    public void testMapper() throws IOException {


        mapdriver.withInput(new Text("1"), getTestData())
                .withOutput(new Text("1"), new IntPair(0,1))
                .withOutput(new Text("1"), new IntPair(2,1))
                .withOutput(new Text("1"), new IntPair(2,1))
                .runTest();
    }


    private BytesArrayWritable getTestData(){
        String line1 = "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        String line2 = "235,1,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        String line3 = "235,2,197,99,189,218,110,67,135,6,121,185,113,104,255,216";

        byte[] ba1 = new byte[16];
        byte[] ba2 = new byte[16];
        byte[] ba3 = new byte[16];

        StringTokenizer itr1 = new StringTokenizer(line1,",");
        StringTokenizer itr2 = new StringTokenizer(line2,",");
        StringTokenizer itr3 = new StringTokenizer(line3,",");
        Integer I;
        int index = 0;
        while (itr1.hasMoreTokens()) {
            I = Integer.parseInt(itr1.nextToken());
            ba1[index] = I.byteValue();
            index+=1;
        }

        index = 0;
        while (itr2.hasMoreTokens()) {
            I = Integer.parseInt(itr2.nextToken());
            ba2[index] = I.byteValue();
            index+=1;
        }

        index = 0;
        while (itr3.hasMoreTokens()) {
            I = Integer.parseInt(itr3.nextToken());
            ba3[index] = I.byteValue();
            index+=1;
        }

        BytesWritable bw1 = new BytesWritable(ba1);
        BytesWritable bw2 = new BytesWritable(ba2);
        BytesWritable bw3 = new BytesWritable(ba3);

        return new BytesArrayWritable(new BytesWritable[]{bw1,bw2,bw3});
    }

    @Test
    public void testCombiner() throws IOException {
        ArrayList<IntPair> values = new ArrayList<IntPair>();
        values.add(new IntPair(0,1));
        values.add(new IntPair(2,1));
        values.add(new IntPair(2,1));

        combindriver.withInput(new Text("1"),values)
                .withOutput(new Text("1"), new IntPair(4, 3))
                .runTest();

    }


    @Test
    public void testReducer() throws IOException{
        ArrayList<IntPair> values = new ArrayList<IntPair>();
        values.add(new IntPair(0,1));
        values.add(new IntPair(2,1));
        values.add(new IntPair(2,1));

        double average = ((double)4) / 3;
        reducedriver.withInput(new Text("1"),values)
                .withOutput(new Text("1"), new DoubleWritable(average))
                .runTest();
    }


    @Test
    public void testMR() throws IOException {
        double average = ((double)4) / 3;
        mapReduceDriver
                .withInput(new Text("1"), getTestData())
                .withInput(new Text("2"), getTestData())
                .withOutput(new Text("1"),new DoubleWritable(average))
                .withOutput(new Text("2"),new DoubleWritable(average))
                .runTest();



    }



}
