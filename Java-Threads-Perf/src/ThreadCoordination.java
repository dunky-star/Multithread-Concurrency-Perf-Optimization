import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ThreadCoordination {

    public static void main(String[] args) {
        List<Long> inputNumbers = Arrays.asList(100000L, 3435L, 35435L, 2324L, 4656L, 23L, 5566L);
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<BigInteger>> futures = new ArrayList<>();

        for (long num : inputNumbers) {
            futures.add(executor.submit(() -> factorial(num)));
        }

        for (int i = 0; i < inputNumbers.size(); i++) {
            try {
                BigInteger result = futures.get(i).get(2, TimeUnit.SECONDS);
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + result);
            } catch (TimeoutException e) {
                System.out.println("Calculation for " + inputNumbers.get(i) + " timed out.");
            } catch (ExecutionException e) {
                System.out.println("Error in task for " + inputNumbers.get(i) + ": " + e.getCause());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted while waiting for result.");
            }
        }

        executor.shutdown();
    }

    private static BigInteger factorial(long n) {
        BigInteger result = BigInteger.ONE;
        for (long i = n; i > 0; i--) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }
}
