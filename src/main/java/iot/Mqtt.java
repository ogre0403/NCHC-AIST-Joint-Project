package iot;

import com.google.common.base.Optional;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.StorageLevels;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 1403035 on 2016/4/6.
 */
public class Mqtt {

    private static final Duration BATCH_INTERVAL = Durations.seconds(10);
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("SparkStreamingMqttTest").setMaster("local[*]");
        // spark streaming context with a 10 second batch size
        JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, BATCH_INTERVAL);
        ssc.checkpoint("/tmp/mqtt");

        //Define MQTT url and topic
        String brokerUrl = "tcp://140.110.141.58:8443";
        String topic = "test";

        //collect MQTT data using streaming context and MQTTUtils library
        JavaReceiverInputDStream<String> messages =
                MQTTUtils.createStream(ssc, brokerUrl, topic, StorageLevels.MEMORY_AND_DISK_SER);

        JavaDStream<String> words = messages
                .map(new Function<String, String>() {
                    @Override
                    public String call(String s) throws Exception {
                        return s;
                    }
                })
                .flatMap(new FlatMapFunction<String, String>() {
                    @Override
                    public Iterable<String> call(String s) throws Exception {
                        return Arrays.asList(s.split(" "));
                    }
                });


        JavaPairDStream<String, Integer> word_one = words.mapToPair(
                new PairFunction<String, String, Integer>() {
                    public Tuple2<String, Integer> call(String in) {
                        return new Tuple2<String, Integer>(in, 1);
                    }
                }
        );

        JavaPairDStream<String, Integer> wordCount = word_one
                .reduceByKey(new Function2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer v1, Integer v2) throws Exception {
                        return v1 + v2;
                    }
                });

        JavaPairDStream<String, Integer> wordCountTotal =
                wordCount.updateStateByKey(new Function2<List<Integer>, Optional<Integer>, Optional<Integer>>() {
                    @Override
                    public Optional<Integer> call(List<Integer> events, Optional<Integer> oldState) throws Exception {
                        Integer newState = oldState.or(0);
                        for (Integer i : events)
                            newState += i;
                        return Optional.of(newState);
                    }
                });

        wordCountTotal.print();

        // Start the streaming server.
        ssc.start();              // Start the computation
        ssc.awaitTermination();   // Wait for the computation to terminate
        ssc.stop();
    }
}
