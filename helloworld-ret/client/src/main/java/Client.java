import Demo.Response;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client
{
    public static void main(String[] args)
    {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try(com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args,"config.client",extraArgs))
        {
            //com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimplePrinter:default -p 10000");
            Response response = null;
            Demo.PrinterPrx service = Demo.PrinterPrx
                    .checkedCast(communicator.propertyToProxy("Printer.Proxy"));
            
            if(service == null)
            {
                throw new Error("Invalid proxy");
            }

            String username = System.getProperty("user.name");
            InetAddress ip= InetAddress.getLocalHost();

            Scanner scanner = new Scanner(System.in);
            

            while (true){
                System.out.println("Ingrese un mensaje para enviar al servidor: ");
                String message = scanner.nextLine();
                if (message.equalsIgnoreCase("exit")) {
                    response = service.printString("User: " + username + ", IP: " + ip.getHostAddress() + "," + message);
                    System.out.println(response.value + ", " + response.responseTime);
                    break;
                }
                response = service.printString("User: " + username + ", IP: " + ip.getHostAddress() + "," + message);
                System.out.println(response.value + ", " + response.responseTime);
            }


            System.out.println("Respuesta del server: " + response.value + ", " + response.responseTime);
        } catch (UnknownHostException e) 
        {
            e.printStackTrace();
        } 
    }
}