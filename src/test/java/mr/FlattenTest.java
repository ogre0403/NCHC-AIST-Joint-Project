package mr;

import mr.flatten.FlattenMapper;
import mr.flatten.FlattenReducer;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class FlattenTest {

    private Mapper<Text, Text, Text, BytesWritable> mapper;
    private MapDriver<Text, Text, Text, BytesWritable> mapdriver;
    private Reducer<Text,BytesWritable,Text,BytesArrayWritable> reducer;
    private ReduceDriver<Text,BytesWritable,Text,BytesArrayWritable> reducerdriver;

    @Before
    public void init() {
        mapper = new FlattenMapper();
        mapdriver = new MapDriver<Text, Text, Text, BytesWritable>(mapper);

        reducer = new FlattenReducer();
        reducerdriver = new ReduceDriver<Text,BytesWritable,Text,BytesArrayWritable>(reducer);
    }

    @Test
    public void testType(){
        String line1 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        String line2 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";

        byte[] ba1 = new byte[16];
        byte[] ba2 = new byte[16];

        StringTokenizer itr1 = new StringTokenizer(line1,",");
        StringTokenizer itr2 = new StringTokenizer(line2,",");
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
        BytesWritable bw1 = new BytesWritable(ba1);
        BytesWritable bw2 = new BytesWritable(ba2);

        BytesArrayWritable bwa = new BytesArrayWritable(new BytesWritable[]{bw1,bw2});
        System.out.println(bwa);
        Assert.assertTrue(bw1.equals(bw2));
    }

    @Test
    public void testMapper() throws IOException {

        String line1 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        byte[] ba = new byte[16];

        StringTokenizer itr = new StringTokenizer(line1,",");
        int index = 0;
        while (itr.hasMoreTokens()) {
            Integer I = Integer.parseInt(itr.nextToken());
            ba[index] = I.byteValue();
            index+=1;
        }

        BytesWritable bw = new BytesWritable(ba);
        mapdriver.withInput(new Text("1"), new Text(line1))
                .withOutput(new Text("1"), bw)
                .runTest();
    }

    @Test
    public void testReducer() throws IOException {
        String line1 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";
        String line2 = "235,23,197,99,189,218,110,67,135,6,121,185,113,104,255,216";

        byte[] ba1 = new byte[16];
        byte[] ba2 = new byte[16];

        StringTokenizer itr1 = new StringTokenizer(line1,",");
        StringTokenizer itr2 = new StringTokenizer(line2,",");
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
        BytesWritable bw1 = new BytesWritable(ba1);
        BytesWritable bw2 = new BytesWritable(ba2);
        ArrayList<BytesWritable> values = new ArrayList<BytesWritable>();
        values.add(bw1);
        values.add(bw2);


        BytesArrayWritable bwa = new BytesArrayWritable(new BytesWritable[]{bw1,bw2});
        reducerdriver
                .withInput(new Text("1"), values)
                .withOutput(new Text("1"), bwa)
                .runTest();
    }
}
