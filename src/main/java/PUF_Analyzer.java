import org.apache.hadoop.hbase.util.Bytes;
import util.HammingDistance;

/**
 * Created by 1403035 on 2015/8/13.
 */
public class PUF_Analyzer {
    public static void main(String[] args) {
        String a1 = "244";
        String a2 = "10";

        Integer I1   = Integer.parseInt(a1);
        Integer I2   = Integer.parseInt(a2);
        byte b1 = I1.byteValue();
        byte b2 = I2.byteValue();

//        System.out.println(Bytes.toString(new byte[]{b1}));

        String s1 = String.format("%8s", Integer.toBinaryString(b1 & 0xFF)).replace(' ', '0');
        String s2 = String.format("%8s", Integer.toBinaryString(b2 & 0xFF)).replace(' ', '0');
        System.out.println(s1); // 10000001
        System.out.println(s2); // 10000001

        int dd = HammingDistance.getHammingDistance(b1,b2);

        System.out.println(dd);


    }
}
