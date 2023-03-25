import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class RunClient {

    /**
     * Per project requirements, we are to pre-populate our map with data. This method calls 7 PUTS,
     * 5 GETS, and 5 DELETES.
     *
     * @param IP_ADDRESS IP_Address of server
     * @param PORT_NUMBER Port number of server
     * @param client which will call methods on the server which populate the server
     * @return void
     */
    private static void fillUpServer(String IP_ADDRESS, String PORT_NUMBER, RMIClient client) {
        try {

            String stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";

            // populate with 2 key-value pairs (aka 2 PUTS)
            String clientMessage = "PUT Tom 70000";
            String[] splitClientMessage = clientMessage.split(" ");
            String serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "PUT Jerry 80000";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            // 5 PUTs
            clientMessage = "PUT Billy 150000";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "PUT Mandy 60000";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);


            clientMessage = "PUT Jill 100000";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "PUT Jack 120000";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "PUT Elon 5000000";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            // 5 GETs
            clientMessage = "GET Billy";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleGET(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "GET Mandy";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleGET(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "GET Jill";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleGET(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "GET Jack";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleGET(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "GET Elon";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleGET(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            // 5 DELETEs
            clientMessage = "DELETE Billy";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleDELETE(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "DELETE Mandy";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleDELETE(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "DELETE Jill";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleDELETE(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "DELETE Jack";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleDELETE(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);

            clientMessage = "DELETE Elon";
            splitClientMessage = clientMessage.split(" ");
            serverResponse = client.handleDELETE(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
            stringCurrentDate = "(Timestamp=" + client.getFormattedCurrentSystemTime() + ")";
            System.out.println(stringCurrentDate + ": " + serverResponse);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    /**
     * Prints some statements to the console, informing the user of how to communicate with the server.
     *
     * @return void
     */
    private static void introduceClient() {
        System.out.println("Type commands to the server in the following format:");
        System.out.println("PUT (KEY) (VALUE) - puts an employee name (KEY) and an employee salary (VALUE) into the server.");
        System.out.println("GET (KEY) - GET an employee salary (KEY) from the server.");
        System.out.println("DELETE (KEY) - DELETE an employee entry (KEY) from the server");
        System.out.println();
        System.out.println("Here's an example how to use each:");
        System.out.println("'PUT Billy 100000'");
        System.out.println("'GET Billy'");
        System.out.println("'DELETE Billy'");
        System.out.println();
        System.out.println("Starting map is populated with following entries- Tom:70000, Jerry:80000");
    }

    /**
     * Main method instantiates the RMIClient and sends user input for the RMIClient to handle.
     * @param args args[0] is the IP Address, and args[1] is the Port Number
     * @throws Exception unexpected exception
     */
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Please pass IP_ADDRESS and PORT_NUMBER as arguments through args");
        } else {

            String IP_ADDRESS = args[0];
            String PORT_NUMBER = args[1];

            RMIClient client = new RMIClient(IP_ADDRESS, Integer.valueOf(PORT_NUMBER));
            client.startClient();

            fillUpServer(IP_ADDRESS, PORT_NUMBER, client);
            introduceClient();

            Scanner in = new Scanner(System.in);

            while (true) {
                System.out.print("Enter a command: ");

                String line = in.nextLine();

                String responseToClient = "";

                try {
                    String[] splitClientMessage = line.split(" ");
                    if (splitClientMessage[0].equals("PUT")) {
                        responseToClient = client.handlePUT(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
                    } else if (splitClientMessage[0].equals("GET")) {
                        responseToClient = client.handleGET(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
                    } else if (splitClientMessage[0].equals("DELETE")) {
                        responseToClient = client.handleDELETE(IP_ADDRESS, PORT_NUMBER, splitClientMessage);
                    } else {
                        System.out.println("Timestamp=" + client.getFormattedCurrentSystemTime() + " (From " + IP_ADDRESS + " " + PORT_NUMBER + ") Server received bad request: client did not pick from 'PUT', 'GET', 'DELETE'");
                        responseToClient = "Unsuccessful operation: Please pick from 'PUT', 'GET', 'DELETE'";
                    }
                    System.out.println("Result: " + responseToClient);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}
