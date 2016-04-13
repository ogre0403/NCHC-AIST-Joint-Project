package puf.type;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by 1403035 on 2015/8/24.
 */
public class ArrayIntPair implements Writable {


    private IntArrayWritable first;
    private IntWritable second;

    public ArrayIntPair(){
        first = new IntArrayWritable();
        second = new IntWritable();
    }

    public ArrayIntPair(IntArrayWritable first, IntWritable second){
        this.first = first;
        this.second = second;
    }

    public IntArrayWritable getFirst(){
        return first;
    }

    public  IntWritable getSecond(){
        return second;
    }


    @Override
    public void write(DataOutput out) throws IOException {
        first.write(out);
        second.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        first.readFields(in);
        second.readFields(in);
    }

    @Override
    public String toString() {
        return "<" + first +
                "|" + second + ">";
    }

    public boolean equals(Object obj) {
        if (obj instanceof ArrayIntPair){
            ArrayIntPair aa = (ArrayIntPair)obj;
            return this.getFirst().equals(aa.getFirst()) &&
                    this.getSecond().equals(aa.getSecond());
        }
        return false;
    }

    public int hashCode() {
        return ((IntWritable)first.get()[0]).get() * 157 + second.get();
    }
}
