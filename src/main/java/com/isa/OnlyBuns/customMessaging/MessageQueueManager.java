package com.isa.OnlyBuns.customMessaging;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueueManager {

    private static final ConcurrentHashMap<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();

    public static void createQueue(String queueName) {
        queues.putIfAbsent(queueName, new LinkedBlockingQueue<>());
    }

    public static void sendMessage(String queueName, String message) {
        BlockingQueue<String> queue = queues.get(queueName);
        if (queue != null) {
            queue.offer(message);
        }
    }

    public static String receiveMessage(String queueName) throws InterruptedException {
        BlockingQueue<String> queue = queues.get(queueName);
        return queue != null ? queue.take() : null;
    }
}
