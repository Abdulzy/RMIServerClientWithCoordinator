import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ServerImpl. Implements methods of MapServer and contains all the functionality
 * that the client and coordinator will access via RMI.
 */
public class ServerImpl implements MapServer {

    Integer gotResult;
    RunCoordinator coordinator;
    Map<String, Integer> myMap;
    int serverNumber;

    public ServerImpl(int serverNumber) throws RemoteException, NotBoundException {
        this.myMap = new HashMap<>();
        this.serverNumber = serverNumber;
        UnicastRemoteObject.exportObject(this, 0);
    }

    /**
     * Setter method for coordinator
     * @param coordinator coordinator
     */
    public void setCoordinator(RunCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * Returns a String of the current system time in "yyyy-MM-dd HH:mm:ss.SSS" format
     *
     * This code from:
     * https://stackoverflow.com/questions/1459656/how-to-get-the-current-time-in-yyyy-mm-dd-hhmisec-millisecond-format-in-java
     *
     * @return String of current system time in "yyyy-MM-dd HH:mm:ss.SSS" format
     */
    @Override
    public String getFormattedCurrentSystemTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        // Get the current date
        Date currentDate = new Date();
        String currentDateTimeOutput = timeFormat.format(currentDate);
        return currentDateTimeOutput;
    }

    /**
     * Returns a boolean representing whether a given string consists of all letters and digits
     * @param string - a string that we want to verify as alphanumeric
     * @return boolean representing whether a string is alphanumeric
     */
    @Override
    public boolean isAlphaNumeric(String string) {
        for (char c : string.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if a string can be converted into an integer
     * @param string - string that we want to convert to integer
     * @return boolean representing whether a string is an integer or not
     */
    @Override
    public boolean isInteger(String string) {
        long value = 0;
        for (char c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
            if (value > 2147483647) { // integers only go up to this value
                return false;
            }
            value = value*10 + Integer.parseInt(String.valueOf(c));
        }
        if (value > 2147483647) { // integers only go up to this value
            return false;
        }
        return true;
    }

    /**
     * Executes PUT requests on the server, putting a key-value pair into the hashmap
     * @param clientINetAddress client's IP Address
     * @param clientPORT_NUMBER client's Port Number
     * @param splitClientMessage client's input
     * @throws RemoteException RMI methods throw remote exception
     */
    @Override
    public synchronized void executePUT(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException {
        String timeStampClientINetPortNumber = "Timestamp=" + getFormattedCurrentSystemTime() + " (From " + clientINetAddress + " " + clientPORT_NUMBER + " server:" + serverNumber;
        String key = splitClientMessage[1];
        String stringValue = splitClientMessage[2];
        int value = Integer.valueOf(stringValue);
        myMap.put(key, value);
        System.out.println(timeStampClientINetPortNumber + ") Server successfully execute PUT " + key + " " + value + " into the map");
    }

    /**
     * Handles PUT requests from the client, rejecting bad inputs and passing good inputs to coordinator. Returns a String response to the client
     * @param clientINetAddress client's IP Address
     * @param clientPORT_NUMBER client's Port Number
     * @param splitClientMessage client's input
     * @return String representing the server's response to the client
     * @throws RemoteException RMI methods throw remote exception
     * @throws InterruptedException If response times out
     */
    @Override
    public synchronized String handlePUT(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException, InterruptedException {
        String timeStampClientINetPortNumber = "Timestamp=" + getFormattedCurrentSystemTime() + " (From " + clientINetAddress + ":" + clientPORT_NUMBER + " server:" + serverNumber;
        String responseToClient = "";
        if (splitClientMessage.length != 3) {
            System.out.println(timeStampClientINetPortNumber + ") Server received bad PUT request; PUT request does not have appropriate number of arguments");
            responseToClient = "Unsuccessful operation: PUT request does not have appropriate number of arguments";
        } else {
            String key = splitClientMessage[1];
            String stringValue = splitClientMessage[2];
            boolean isAlphaNum = isAlphaNumeric(splitClientMessage[1]);
            boolean isInt = isInteger(splitClientMessage[2]);
            if (isAlphaNum && isInt) { // valid input, lets try to get consensus
                boolean result = this.coordinator.getConsensus(splitClientMessage);
                if (result) {
                    System.out.println(timeStampClientINetPortNumber + ") Coordinator ALL COMMIT reached. Commencing PUT operation: " + key + " " + stringValue + " into all maps");
                    this.coordinator.executeConsensusPUT(clientINetAddress, clientPORT_NUMBER, splitClientMessage);
                    responseToClient = "Successful PUT operation: " + key + " " + stringValue; // no need to mention coordinator because client doesn't need to know about coordinator
                } else {
                    System.out.println(timeStampClientINetPortNumber + ") Coordinator ABORT reached. Couldn't PUT into all servers");
                    responseToClient = "Unsuccessful PUT operation: " + key + " " + stringValue;
                }
            } else if (!isAlphaNum && !isInt) {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad PUT request; bad PUT key & bad PUT value");
                responseToClient = "Unsuccessful operation: PUT's key must contain only alphanumeric characters and PUT's value must be a non-negative integer";
            } else if (!isAlphaNum) {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad PUT request; bad PUT key");
                responseToClient = "Unsuccessful operation: PUT's key must contain only alphanumeric characters";
            } else {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad PUT request; bad PUT value");
                responseToClient = "Unsuccessful operation: PUT's value must be a non-negative integer";
            }
        }
        return responseToClient;
    }

    /**
     * Handles GET requests from the client. Returns a String response to the client
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @return String representing our response to the client
     * @throws RemoteException RMI methods throw remote exception
     */
    @Override
    public synchronized String handleGET(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException {
        String timeStampClientINetPortNumber = "Timestamp=" + getFormattedCurrentSystemTime() + " (From " + clientINetAddress + " " + clientPORT_NUMBER + " server:" + serverNumber;
        String responseToClient = "";
        if (splitClientMessage.length != 2) {
            System.out.println(timeStampClientINetPortNumber + ") Server received bad GET request; GET request does not have appropriate number of arguments");
            responseToClient = "Unsuccessful operation: GET request does not have appropriate number of arguments";
        } else {
            String key = splitClientMessage[1];
            boolean isAlphaNum = isAlphaNumeric(splitClientMessage[1]);
            Integer value = myMap.get(key);
            if (value != null) { // successful GET operation
                System.out.println(timeStampClientINetPortNumber + ") Server successfully retrieved GET's (" + key + ")'s value (" + value + ")");
                responseToClient = "Successful GET operation. key=" + key + " value=" + String.valueOf(value);
            } else if (!isAlphaNum) {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad GET request; bad GET key");
                responseToClient = "Unsuccessful operation: GET's key must contain only alphanumeric characters";
            } else {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad GET request; GET's key does not exist");
                responseToClient = "Unsuccessful operation: GET's key does not exist";
            }
        }
        return responseToClient;
    }

    /**
     * Executes DELETE requests on the server, removing a key-value pair from the hashmap
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @throws RemoteException RMI methods throw remote exception
     */
    @Override
    public synchronized void executeDELETE(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException {
        String timeStampClientINetPortNumber = "Timestamp=" + getFormattedCurrentSystemTime() + " (From " + clientINetAddress + " " + clientPORT_NUMBER + " server:" + serverNumber;
        String key = splitClientMessage[1];
        myMap.remove(key);
        System.out.println(timeStampClientINetPortNumber + ") Server successfully execute DELETE " + key + " from the map");
    }

    /**
     * Handles DELETE requests from the client, rejecting bad inputs and passing good inputs to coordinator. Returns a String response to the client
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @return String representing our response to the client
     * @throws RemoteException RMI methods throw remote exception
     * @throws InterruptedException If response times out
     */
    @Override
    public synchronized String handleDELETE(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException, InterruptedException {
        String timeStampClientINetPortNumber = "Timestamp=" + getFormattedCurrentSystemTime() + " (From " + clientINetAddress + " " + clientPORT_NUMBER + " server:" + serverNumber;;
        String responseToClient = "";
        if (splitClientMessage.length != 2) {
            System.out.println(timeStampClientINetPortNumber + ") Server received bad DELETE request; DELETE request does not have appropriate number of arguments");
            responseToClient = "Unsuccessful operation: DELETE request does not have appropriate number of arguments";
        } else {
            String key = splitClientMessage[1];
            boolean isAlphaNum = isAlphaNumeric(splitClientMessage[1]);
            Integer value = myMap.get(key);
            if (value != null) { // valid input, lets try to get consensus
                boolean result = this.coordinator.getConsensus(splitClientMessage);
                if (result) {
                    System.out.println(timeStampClientINetPortNumber + ") Coordinator ALL COMMIT reached. Commencing DELETE operation: " + key + " from all maps");
                    this.coordinator.executeConsensusDELETE(clientINetAddress, clientPORT_NUMBER, splitClientMessage);
                    responseToClient = "Successful DELETE operation: " + key; // no need to mention coordinator because client doesn't need to know about coordinator
                } else {
                    System.out.println(timeStampClientINetPortNumber + ") Coordinator ABORT reached. Couldn't DELETE from all servers");
                    responseToClient = "Unsuccessful DELETE operation: " + key;
                }
            } else if (!isAlphaNum) {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad DELETE request; bad DELETE key");
                responseToClient = "Unsuccessful operation: DELETE's key must contain only alphanumeric characters";
            } else {
                System.out.println(timeStampClientINetPortNumber + ") Server received bad DELETE request; DELETE's key does not exist");
                responseToClient = "Unsuccessful operation: DELETE's key does not exist";
            }
        }
        return responseToClient;
    }

    /**
     * Long story short, this is my work-around for Callable's not taking parameters. Used for getting consensus
     * @param key used to get the value
     * @throws RemoteException RMI methods throw remote exception
     */
    @Override
    public void setValue(String key) {
        this.gotResult = myMap.get(key);
    }

    /**
     * Required override for Callable<T>
     * @return value of our key-value pair
     */
    @Override
    public Integer call() {
        return myMap.get(this.gotResult);
    }

}
