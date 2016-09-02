package com.ponxu.test.storm;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * @author ponxu
 * @date 2016-08-14
 */
public class HelloStorm {

    static class MySpout extends BaseRichSpout {
        private SpoutOutputCollector collector;
        private int i;

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.i = 0;
            this.collector = spoutOutputCollector;
        }

        @Override
        public void nextTuple() {
            while (true) {
                collector.emit(new Values("hello world " + i++));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("sentence"));
        }
    }

    static class MyBolt extends BaseRichBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String sentence = tuple.getStringByField("sentence");
            System.out.println(sentence);
            collector.ack(tuple);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        }
    }

    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("MySpout", new MySpout());
        builder.setBolt("MyBolt", new MyBolt()).shuffleGrouping("MySpout");

        Config conf = new Config();
        conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("HelloStorm", conf, builder.createTopology());
    }
}
