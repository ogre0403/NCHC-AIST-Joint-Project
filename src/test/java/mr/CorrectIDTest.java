package mr;

import junit.framework.Assert;
import mr.combiner.CorrectIDCombiner;
import mr.mapper.CorrectIDMapper;
import mr.reducer.CorrectIDReducer;
import mr.type.ArrayIntPair;
import mr.type.BytesArrayWritable;
import mr.type.IntArrayWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import util.TestTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by 1403035 on 2015/8/24.
 */
public class CorrectIDTest {

    private static Logger logger = Logger.getLogger(CorrectIDTest.class);

    private Mapper<Text, BytesArrayWritable, Text, ArrayIntPair> mapper;
    private MapDriver<Text, BytesArrayWritable, Text, ArrayIntPair> mapperDriver;

    private Reducer<Text, ArrayIntPair,Text,ArrayIntPair> combiner;
    private ReduceDriver<Text, ArrayIntPair,Text,ArrayIntPair> combindriver;

    private Reducer<Text, ArrayIntPair,Text, BytesWritable> reducer;
    private ReduceDriver<Text, ArrayIntPair,Text, BytesWritable> reducerdriver;

    private MapReduceDriver<Text, BytesArrayWritable, Text, ArrayIntPair, Text, BytesWritable> mapReduceDriver;

    String[] data = new String[]{
            "1,0,0,128,0  ,0  ,0,0,0,0,0,0,0,0,0,0"
            ,"1,1,0,128,128,0  ,0,0,0,0,0,0,0,0,0,0"
            ,"1,1,1,128,128,128,0,0,0,0,0,0,0,0,0,0",
    };

    @Before
    public void init() {

        mapper = new CorrectIDMapper();
        mapperDriver = new MapDriver<Text,BytesArrayWritable,Text,ArrayIntPair>(mapper);

        combiner = new CorrectIDCombiner();
        combindriver = new ReduceDriver<Text, ArrayIntPair,Text,ArrayIntPair>(combiner);

        reducer = new CorrectIDReducer();
        reducerdriver = new ReduceDriver<Text, ArrayIntPair, Text, BytesWritable>(reducer);

        mapReduceDriver = new MapReduceDriver<Text, BytesArrayWritable, Text, ArrayIntPair, Text, BytesWritable>(mapper,reducer);
        mapReduceDriver.setCombiner(combiner);
    }


    @Test
    public void testIntArrayWritable(){
        IntWritable[] ints = new IntWritable[5];
        ints[0] = new IntWritable(0);
        ints[1] = new IntWritable(1);
        ints[2] = new IntWritable(2);
        ints[3] = new IntWritable(3);
        ints[4] = new IntWritable(4);

        IntWritable[] ints1 = new IntWritable[5];
        ints1[0] = new IntWritable(0);
        ints1[1] = new IntWritable(1);
        ints1[2] = new IntWritable(2);
        ints1[3] = new IntWritable(3);
        ints1[4] = new IntWritable(4);

        IntWritable[] ints2 = new IntWritable[4];
        ints1[0] = new IntWritable(0);
        ints1[1] = new IntWritable(1);
        ints1[2] = new IntWritable(2);
        ints1[3] = new IntWritable(3);


        IntArrayWritable ia = new IntArrayWritable(ints);
        IntArrayWritable ia1 = new IntArrayWritable(ints1);
        IntArrayWritable ia2 = new IntArrayWritable(ints2);
        Assert.assertTrue(ia.get().length == 5);
        logger.info(ia1.toString());
        Assert.assertTrue(ia.equals(ia1));
        Assert.assertFalse(ia.equals(ia2));
    }

    @Test
    public void testArrayIntPair(){
        IntWritable[] ints1 = new IntWritable[5];
        ints1[0] = new IntWritable(0);
        ints1[1] = new IntWritable(1);
        ints1[2] = new IntWritable(2);
        ints1[3] = new IntWritable(3);
        ints1[4] = new IntWritable(4);
        IntArrayWritable ia1 = new IntArrayWritable(ints1);


        IntWritable[] ints2 = new IntWritable[5];
        ints2[0] = new IntWritable(0);
        ints2[1] = new IntWritable(1);
        ints2[2] = new IntWritable(2);
        ints2[3] = new IntWritable(3);
        ints2[4] = new IntWritable(4);
        IntArrayWritable ia2 = new IntArrayWritable(ints2);

        IntWritable[] ints3 = new IntWritable[5];
        ints3[0] = new IntWritable(0);
        ints3[1] = new IntWritable(1);
        ints3[2] = new IntWritable(2);
        ints3[3] = new IntWritable(3);
        ints3[4] = new IntWritable(4);
        IntArrayWritable ia3 = new IntArrayWritable(ints3);

        ArrayIntPair ap1 = new ArrayIntPair(ia1,new IntWritable(1));
        ArrayIntPair ap2 = new ArrayIntPair(ia2,new IntWritable(1));
        ArrayIntPair ap3 = new ArrayIntPair(ia3,new IntWritable(2));

        Assert.assertTrue(ap1.equals(ap2));
        Assert.assertFalse(ap1.equals(ap3));
        logger.info(ap1.toString());
        logger.info(ap2.toString());
        logger.info(ap3.toString());

    }

    @Test
    public void testMapper() throws IOException {


        BytesArrayWritable input = TestTool.toBytesArrayWritable(data);

        IntWritable[] result1 = new IntWritable[128];
        IntWritable[] result2 = new IntWritable[128];
        IntWritable[] result3 = new IntWritable[128];


        //"1,0,0,128,0  ,0  ,0,0,0,0,0,0,0,0,0,0"
        for(int i = 0;i<result1.length;i++)
            result1[i] = new IntWritable(0);

        result1[7].set(1);
        result1[24].set(1);

        for(int i = 0;i<result2.length;i++)
            result2[i] = new IntWritable(0);

        result2[7].set(1);
        result2[15].set(1);
        result2[24].set(1);
        result2[32].set(1);

        for(int i = 0;i<result3.length;i++)
            result3[i] = new IntWritable(0);

        result3[7].set(1);
        result3[15].set(1);
        result3[23].set(1);
        result3[24].set(1);
        result3[32].set(1);
        result3[40].set(1);


        mapperDriver
                .withInput(new Text("1"), input)
                .withOutput(new Text("1"), new ArrayIntPair(new IntArrayWritable(result1), new IntWritable(1)))
                .withOutput(new Text("1"), new ArrayIntPair(new IntArrayWritable(result2), new IntWritable(1)))
                .withOutput(new Text("1"), new ArrayIntPair(new IntArrayWritable(result3), new IntWritable(1)))
                        .runTest();
    }

    @Test
    public void testCombiner() throws IOException {


        IntWritable[] vote = new IntWritable[128];
        for(int i = 0;i<vote.length;i++)
            vote[i] = new IntWritable(0);

        vote[7].set(3);
        vote[15].set(2);
        vote[23].set(1);
        vote[24].set(3);
        vote[32].set(2);
        vote[40].set(1);

        LinkedList<ArrayIntPair> input = new LinkedList<ArrayIntPair>();
        IntWritable[] result1 = TestTool.toBinaryIntArray(
                TestTool.toByteArray(data[0])
        );
        IntWritable[] result2 = TestTool.toBinaryIntArray(
                TestTool.toByteArray(data[1])
        );
        IntWritable[] result3 = TestTool.toBinaryIntArray(
                TestTool.toByteArray(data[2])
        );

        input.add(new ArrayIntPair(new IntArrayWritable(result1), new IntWritable(1)));
        input.add(new ArrayIntPair(new IntArrayWritable(result2), new IntWritable(1)));
        input.add(new ArrayIntPair(new IntArrayWritable(result3), new IntWritable(1)));


        combindriver.withInput(new Text("1"), input)
                .withOutput(new Text("1"), new ArrayIntPair(new IntArrayWritable(vote), new IntWritable(3)))
                .runTest();
    }

    @Test
    public void testReducer() throws IOException {

        IntWritable[] vote = new IntWritable[128];
        for(int i = 0;i<vote.length;i++)
            vote[i] = new IntWritable(0);

        vote[7].set(3);
        vote[15].set(2);
        vote[23].set(1);
        vote[24].set(3);
        vote[32].set(2);
        vote[40].set(1);

        ArrayIntPair ap = new ArrayIntPair(new IntArrayWritable(vote),new IntWritable(3));
        ArrayList<ArrayIntPair> values = new ArrayList<ArrayIntPair>();
        values.add(ap);


        byte[] result = new byte[16];

        result[0] = (byte)1;
        result[1] = (byte)1;
        result[2] = (byte)0;
        result[3] = (byte)128;
        result[4] = (byte)128;
        result[5] = (byte)0;


        reducerdriver.withInput(new Text("1"),values)
                .withOutput(new Text("1"),new BytesWritable(result))
                .runTest();
    }

    @Test
    public void testMR() throws IOException {


        byte[] result = new byte[16];

        result[0] = (byte) 1;
        result[1] = (byte)1;
        result[2] = (byte) 0;
        result[3] = (byte) 128;
        result[4] = (byte) 128;
        result[5] = (byte) 0;

        mapReduceDriver
                .withInput(new Text("1"), TestTool.toBytesArrayWritable(data))
                .withOutput(new Text("1"), new BytesWritable(result))
                .runTest();
    }
}
