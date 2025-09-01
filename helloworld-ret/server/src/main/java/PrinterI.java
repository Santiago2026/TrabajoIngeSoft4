import Demo.Response;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class PrinterI implements Demo.Printer {

    public Response printString(String s, com.zeroc.Ice.Current current) {
        String [] clientInfo = s.split(",",3);
        

        try {
            // 2a.
            if (clientInfo[2].matches("\\d+")) {
                long startTime = System.currentTimeMillis();
                
                int n = Integer.parseInt(clientInfo[2]);

                String fib = fibonacci(n);
                String primes = primeFactors(n);

                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;

                System.out.println( clientInfo[0] + clientInfo[1] + " Fibonacci(" + n + "): " + fib);
                return new Response(responseTime, "Factores primos de " + n + ": " + primes);
            }

            // 2b.
            if (clientInfo[2].startsWith("listifs")) {
                long startTime = System.currentTimeMillis();

                String interfaces = getNetworkInterfaces();

                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                System.out.println(clientInfo[0] + clientInfo[1] + " Interfaces de red:\n" + interfaces);
                return new Response(responseTime,"Interfaces de red:\\n" + interfaces);
            }

            // 2c. 
            if (clientInfo[2].startsWith("listports")) {
                long startTime = System.currentTimeMillis();
                String[] parts = clientInfo[2].split("\\s+"); // Divide por espacios 
        
                if (parts.length == 2 && isValidIPv4(parts[1])) {
                    
                    String scanResult = scanOpenPorts(parts[1]);
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;

                    System.out.println(clientInfo[0] + clientInfo[1] + " Puertos abiertos: " + ":\n" + scanResult);
                    return new Response(responseTime, scanResult);
                } else {
                    long endTime = System.currentTimeMillis();
                    long responseTime = endTime - startTime;
                    return new Response(responseTime, "Formato incorrecto. Usa: listports <IPv4>");
                }
            }

            // 2d. !<comando>
            if (clientInfo[2].startsWith("!")) {
                long startTime = System.currentTimeMillis();
                String cmd = clientInfo[2].substring(1); // Elimina el '!'
                System.out.println(cmd);
                String result = runCommand(cmd);
                long endTime = System.currentTimeMillis();
                long responseTime = endTime - startTime;
                System.out.println(clientInfo[0] + clientInfo[1]+ " Resultado de comando '" + cmd + "':\n" + result);
                return new Response(responseTime, result);
            }

            if (clientInfo[2].equalsIgnoreCase("exit")) {
                System.out.println(clientInfo[0] + clientInfo[1] + " ha salido.");
                return new Response(0, "Has salido del programa.");
            }

            return new Response(0, "Server response: " + s);

        } catch (Exception e) {
            e.printStackTrace();
            return new Response(1, "Error del servidor: " + e.getMessage());
        }
    }

    // Funciones auxiliares:

    private String fibonacci(int n) {
        List<Integer> fib = new ArrayList<>();
        int a = 0;
        int  b = 1;
        while (n-- > 0) {
            fib.add(a);
            int temp = a + b;
            a = b;
            b = temp;
        }
        return fib.toString();
    }

    private String primeFactors(int number) {
        List<Integer> factors = new ArrayList<>();
        for (int i = 2; i <= number; i++) {
            while (number % i == 0) {
                factors.add(i);
                number /= i;
            }
        }
        return factors.toString();
    }

    private String getNetworkInterfaces() throws SocketException {
        StringBuilder result = new StringBuilder();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            result.append("Interface: ").append(ni.getName()).append("\n");
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                result.append("  Address: ").append(addr.getHostAddress()).append("\n");
            }
        }
        return result.toString();
    }

    private boolean isValidIPv4(String ip) {
        String regex = "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";
        System.out.println(ip.matches(regex));
        return ip.matches(regex);
    }

    private String scanOpenPorts(String ip) {
        StringBuilder result = new StringBuilder();
        try {
            for (int port = 1; port <= 5000; port++) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, port), 100);
                    result.append("Puerto abierto: ").append(port).append("\n");
                } catch (IOException ignored) {
                    
                }
            }
        } catch (Exception e) {
            return "Error escaneando puertos: " + e.getMessage();
        }
        return result.length() == 0 ? "No se encontraron puertos abiertos." : result.toString();
    }

    private String runCommand(String cmd) throws IOException {
        StringBuilder output = new StringBuilder();
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        return output.toString();
    }
}