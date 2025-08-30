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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HttpServerPerf {
    private static final String INPUT_FILE = "./resources/throughput/war_and_peace.txt";
    private static final int THREAD_POOL_SIZE = 1;
    public static void main(String[] args) throws IOException {
        String text = new String(Files.readAllBytes(Paths.get(INPUT_FILE)));

        // Split on the line break
        String[] lines = text.split("\\R");  // \\R matches any line break sequence
        // Print first 5 or fewer if file
        for (int i = 0; i < Math.min(5, lines.length); i++) {
            System.out.println(lines[i]);
        }
        System.out.println("Total lines: " + lines.length);
        startServer(text);
    }

    public static void startServer(String text) throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/search", new WordCountHandler(text));
        Executor executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        server.setExecutor(executor);
        server.start();
        System.out.println("Server started on port " + port);
    }

    private static class WordCountHandler implements HttpHandler {

        /**
         * Handle the given request and generate an appropriate response.
         * See {@link HttpExchange} for a description of the steps
         * involved in handling an exchange.
         *
         * @param exchange the exchange containing the request from the
         *                 client and used to send the response
         * @throws NullPointerException if exchange is {@code null}
         * @throws IOException          if an I/O error occurs
         */
        private String text;
        public WordCountHandler(String text) {
            this.text = text;
        }
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String query = httpExchange.getRequestURI().getQuery();
            String[] keyValue = query.split("=");
            String action = keyValue[0];
            String word = keyValue[1];
            if(!action.equals("word")) {
               httpExchange.sendResponseHeaders(400, 0);
                return;
            }

            long count = countWord(word);

            byte [] response = Long.toString(count).getBytes();
            httpExchange.sendResponseHeaders(200, response.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(response);
            outputStream.close();
        }

        private long countWord(String word) {
            long count = 0;
            int index = 0;
            while(index >= 0) {
                index = text.indexOf(word, index);
                if(index >= 0) {
                    count++;
                    index++;
                }
            }
            return count;
        }
    }

}
