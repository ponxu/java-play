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
import java.util.Random;
import java.util.StringTokenizer;

/**
 * @author ponxu
 * @date 2016-08-14
 */
public class WordCount {

    static class RandomSentenceSpout extends BaseRichSpout {
        private SpoutOutputCollector collector;

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.collector = spoutOutputCollector;
        }

        @Override
        public void nextTuple() {
            String[] sentences = {
                    "hello world",
                    "hello storm",
                    "how old are you",
            };

            Random r = new Random();
            while (true) {
                int i = r.nextInt(sentences.length);
                collector.emit(new Values(sentences[i]));
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

    static class SplitBolt extends BaseRichBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String sentence = tuple.getStringByField("sentence");

            StringTokenizer st = new StringTokenizer(sentence);
            while (st.hasMoreTokens()) {
                String word = st.nextToken();
                collector.emit(new Values(word));
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word"));
        }
    }

    static class CountBolt extends BaseRichBolt {
        private OutputCollector collector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            String word = tuple.getStringByField("word");
            System.out.println(word);
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        }
    }

    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("RandomSentenceSpout", new RandomSentenceSpout());
        builder.setBolt("SplitBolt", new SplitBolt()).shuffleGrouping("RandomSentenceSpout");
        builder.setBolt("CountBolt", new CountBolt()).fieldsGrouping("SplitBolt", new Fields("word"));

        Config conf = new Config();
        conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("WordCount", conf, builder.createTopology());
    }
}
