import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Time;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * RunCoordinator. This creates the registry, 5 servers, and binds the servers to the
 * registry for the client to access. RunCoordinator contains a list of all servers.
 */
public class RunCoordinator implements Remote {

    List<MapServer> listOfServers;

    public RunCoordinator() throws RemoteException {
        this.listOfServers = new ArrayList<>();
        UnicastRemoteObject.exportObject(this, 0);
    }

    /**
     * Getter method for the list of servers
     * @return List<MapServer> List of MapServers
     */
    public List<MapServer> getListOfServers() {
        return this.listOfServers;
    }

    /**
     * Gathers consensus from the servers on whether to commit or abort
     * @param splitClientMessage client's input
     * @return boolean indicating whether to commit or not
     * @throws RemoteException RMI methods throw remote exception
     */
    public boolean getConsensus(String[] splitClientMessage) throws RemoteException {

        List<Integer> accumList = new ArrayList<>();
        List<Future> allFutures = new ArrayList<>();
        ExecutorService service = Executors.newFixedThreadPool(5);

        // gathering votes multithreaded
        for (MapServer server : listOfServers) {
            server.setValue(splitClientMessage[1]);
            Future<Integer> future = service.submit(server);
            allFutures.add(future);
        }

        // gathering consensus
        for (int i=0; i<allFutures.size(); i++) {
            Future<Integer> future = allFutures.get(i);
            try {
                Integer result = future.get(3, TimeUnit.SECONDS);
                accumList.add(result);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException!");
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("ExecutionException!");
                e.printStackTrace();
            } catch (TimeoutException e) {
                System.out.println("TimeoutException!"); // defensive coding
                e.printStackTrace();
            }
        }

        // comparing votes
        Integer firstVote = accumList.get(0);
        for (int i=1; i<accumList.size(); i++) {
            if (firstVote != accumList.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * If all servers vote to commit the PUT request, we execute the PUT request on all servers
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @throws RemoteException RMI methods throw remote exception
     */
    public void executeConsensusPUT(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException {
        // if all votes are the same, we can run executePUT on all servers
        for (MapServer server : listOfServers) {
            server.executePUT(clientINetAddress, clientPORT_NUMBER, splitClientMessage);
        }
    }

    /**
     * If all servers vote to commit the DELETE request, we execute the DELETE request on all servers
     * @param clientINetAddress client's internet address
     * @param clientPORT_NUMBER client's port number
     * @param splitClientMessage client's input
     * @throws RemoteException RMI methods throw remote exception
     */
    public void executeConsensusDELETE(String clientINetAddress, String clientPORT_NUMBER, String[] splitClientMessage) throws RemoteException {
        // if all votes are the same, we can run executeDELETE on all servers
        for (MapServer server : listOfServers) {
            server.executeDELETE(clientINetAddress, clientPORT_NUMBER, splitClientMessage);
        }
    }

    /**
     * Main method creates the registry and 5 servers, and binds the servers to the
     * registry for the client to access (randomly)
     * @param args args[0] is Port Number
     * @throws RemoteException Exception may occur during the execution of a remote method call.
     * @throws AlreadyBoundException Thrown if an attempt is made to lookup or unbind in the registry a
     *                           name that has no associated binding.
     * @throws NotBoundException Thrown when a lookup or unbind call does not find the object in the registry.
     */
    public static void main(String[] args) throws RemoteException, AlreadyBoundException, NotBoundException {
        if (args.length != 1) {
            System.out.println("Please pass PORT_NUMBER as arguments through args");
        } else {
            int PORT_NUMBER = Integer.valueOf(args[0]);

            RunCoordinator coordinator = new RunCoordinator();

            MapServer server1 = new ServerImpl(1);
            MapServer server2 = new ServerImpl(2);
            MapServer server3 = new ServerImpl(3);
            MapServer server4 = new ServerImpl(4);
            MapServer server5 = new ServerImpl(5);
            coordinator.getListOfServers().add(server1);
            coordinator.getListOfServers().add(server2);
            coordinator.getListOfServers().add(server3);
            coordinator.getListOfServers().add(server4);
            coordinator.getListOfServers().add(server5);

            Registry registry = LocateRegistry.createRegistry(PORT_NUMBER);
            registry.bind("Coordinator", coordinator);
            registry.bind("Server1", server1);
            registry.bind("Server2", server2);
            registry.bind("Server3", server3);
            registry.bind("Server4", server4);
            registry.bind("Server5", server5);
            ((ServerImpl) server1).setCoordinator(coordinator);
            ((ServerImpl) server2).setCoordinator(coordinator);
            ((ServerImpl) server3).setCoordinator(coordinator);
            ((ServerImpl) server4).setCoordinator(coordinator);
            ((ServerImpl) server5).setCoordinator(coordinator);
            // This is a great debug line
//            for (MapServer server : coordinator.listOfServers) {
//                System.out.println("server coordinator: " + ((ServerImpl) server).coordinator);
//                System.out.println("server myMap: " + ((ServerImpl) server).myMap);
//            }

            System.out.println("RMI Coordinator started!");
        }
    }
}
