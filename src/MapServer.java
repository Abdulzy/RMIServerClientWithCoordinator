import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Callable;

/**
 * MapServer. ServerImpl implements these methods, and RMIClient accesses them via RMI.
 */
public interface MapServer extends Remote, Callable<Integer> {

    /**
     * Returns a String of the current system time in "yyyy-MM-dd HH:mm:ss.SSS" format
     *
     * This code from:
     * https://stackoverflow.com/questions/1459656/how-to-get-the-current-time-in-yyyy-mm-dd-hhmisec-millisecond-format-in-java
     *
     * @return String of current system time in "yyyy-MM-dd HH:mm:ss.SSS" format
     * @throws RemoteException RMI methods throw remote exception
     */
    String getFormattedCurrentSystemTime() throws RemoteException;

    /**
     * Returns a boolean representing whether a given string consists of all letters and digits
     * @param string - a string that we want to verify as alphanumeric
     * @return boolean representing whether a string is alphanumeric
     * @throws RemoteException RMI methods throw remote exception
     */
    boolean isAlphaNumeric(String string) throws RemoteException;

    /**
     * Check if a string can be converted into an integer
     * @param string - string that we want to convert to integer
     * @return boolean representing whether a string is an integer or not
     * @throws RemoteException RMI methods throw remote exception
     */
    boolean isInteger(String string) throws RemoteException;

    /**
     * Handles PUT requests from the client, rejecting bad inputs and passing good inputs to coordinator. Returns a String response to the client
     * @param clientINetAddress client's IP Address
     * @param clientPORT_NUMBER client's Port Number
     * @param splitClientMessage client's input
     * @return String representing the server's response to the client
     * @throws RemoteException RMI methods throw remote exception
     * @throws InterruptedException If response times out
     */
    String handlePUT(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException, InterruptedException;

    /**
     * Executes PUT requests on the server, putting a key-value pair into the hashmap
     * @param clientINetAddress client's IP Address
     * @param clientPORT_NUMBER client's Port Number
     * @param splitClientMessage client's input
     * @throws RemoteException RMI methods throw remote exception
     */
    void executePUT(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException;

    /**
     * Handles GET requests from the client. Returns a String response to the client
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @return String representing our response to the client
     * @throws RemoteException RMI methods throw remote exception
     */
    String handleGET(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException;

    /**
     * Handles DELETE requests from the client, rejecting bad inputs and passing good inputs to coordinator. Returns a String response to the client
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @return String representing our response to the client
     * @throws RemoteException RMI methods throw remote exception
     * @throws InterruptedException If response times out
     */
    String handleDELETE(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException, InterruptedException;

    /**
     * Executes DELETE requests on the server, removing a key-value pair from the hashmap
     * @param clientINetAddress client's internet address
     * @param clientPORT_number client's port number
     * @param splitClientMessage client's input
     * @throws RemoteException RMI methods throw remote exception
     */
    void executeDELETE(String clientINetAddress, String clientPORT_number, String[] splitClientMessage) throws RemoteException;

    /**
     * Long story short, this is my work-around for Callable's not taking parameters. Used for getting consensus
     * @param key used to get the value
     * @throws RemoteException RMI methods throw remote exception
     */
    void setValue(String key) throws RemoteException;
}
