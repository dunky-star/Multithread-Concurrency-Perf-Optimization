package performance.throughput;

/*
 * Copyright (c) 2025-2025. Geoffrey Duncan Opiyo
 * https://dunkystars.com
 * All rights reserved
 * This application will act as a very basic search engine [Search and Cound Words]
 * It will use Java's built-in HTTP server to handle incoming requests and respond with search results
 * The server will be optimized for throughput by using a thread pool to handle multiple requests concurrently
 * http://localhost:8000/search?query=Golang
 */

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class HttpServerPerf {
    private static final String INPUT_FILE = "./resources/throughput/war_and_peace.txt";
    private static final int PORT = 8000;
    private static final int CORE = Runtime.getRuntime().availableProcessors();
    private static final int max  = CORE * 3;  // small burst headroom

    public static void main(String[] args) throws IOException {
        // Read whole file as String
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)), StandardCharsets.UTF_8);

        // Preview first 5 lines (unchanged)
        String[] lines = text.split("\\R");
        for (int i = 0; i < Math.min(5, lines.length); i++) System.out.println(lines[i]);
        System.out.println("Total lines: " + lines.length);

        // Build word -> count map ONCE (lowercase tokens)
        Map<String, Integer> freq = new HashMap<>(131_072);
        for (String tok : text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+")) {
            if (!tok.isEmpty()) freq.merge(tok, 1, Integer::sum);
        }

        startServer(freq);
    }

    private static void startServer(Map<String, Integer> freq) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/search", new WordCountHandler(freq));

        // bounded queue (tune)
        final int QUEUE_CAPACITY = 10000;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE,
                max,
                30, TimeUnit.SECONDS,  // keep-alive for idle threads
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),     // bounded queue
                new ThreadPoolExecutor.CallerRunsPolicy() // throttles caller under pressure
        );
        server.setExecutor(executor);

        // Light monitor (daemon) – keep or comment during formal runs
        Thread monitor = new Thread(() -> {
            try {
                while (true) {
                    System.out.printf("Pool:%d Active:%d Completed:%d Queue:%d%n",
                            executor.getPoolSize(),
                            executor.getActiveCount(),
                            executor.getCompletedTaskCount(),
                            executor.getQueue().size());
                    Thread.sleep(2000);
                }
            } catch (InterruptedException ignored) { }
        }, "pool-monitor");
        monitor.setDaemon(true);
        monitor.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            executor.shutdown();
        }));

        server.start();
        System.out.println("Server started on port " + PORT);
    }

    private static class WordCountHandler implements HttpHandler {
        private final Map<String, Integer> freq;

        WordCountHandler(Map<String, Integer> freq) { this.freq = freq; }

        @Override
        public void handle(HttpExchange ex) throws IOException {
            // Avoid per-request println during load
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                send(ex, 405, "Method Not Allowed");
                return;
            }
            String raw = ex.getRequestURI().getRawQuery();
            if (raw == null || raw.isEmpty()) {
                send(ex, 400, "Missing query string. Use /search?word=Golang");
                return;
            }
            String word = firstParam(raw, "word");
            if (word == null || word.isEmpty()) {
                send(ex, 400, "Missing 'word' parameter");
                return;
            }
            int count = freq.getOrDefault(word.toLowerCase(Locale.ROOT), 0);
            send(ex, 200, Integer.toString(count));
        }

        private String firstParam(String raw, String key) {
            for (String p : raw.split("&")) {
                int i = p.indexOf('=');
                String k = i >= 0 ? p.substring(0, i) : p;
                if (k.equals(key)) {
                    String v = i >= 0 ? p.substring(i + 1) : "";
                    return URLDecoder.decode(v, StandardCharsets.UTF_8);
                }
            }
            return null;
        }

        private void send(HttpExchange ex, int status, String body) throws IOException {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
            ex.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
        }
    }
}