package msg;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Sender {
    /* local ip */
    public static String localIP = "127.0.0.1";
    /* wrong message */
    public static String err_msg = "";
    /* default send port */
    public static int SendPort = 5555;
    /* default chat port */
    public static int chatPort = 6666;

    public Sender() {
    }

    /**
     * @param msgType    message Type
     * @return send success or not
     */
    public static boolean sendUDPMsg(int msgType, String uname, String friendIP, int friendPort, String message) {
        try {
            /* From Common line get to send context, convert to string by UTF-8 */
            byte[] msg = (msgType + "*" + uname + "*" + message).getBytes(StandardCharsets.UTF_8);
            /* get the host internet Address */
            InetAddress address = InetAddress.getByName(friendIP);

            /* Initializes a datagram packet (packet) with data and address*/
            DatagramPacket packet = new DatagramPacket(msg, msg.length, address,
                    friendPort);

            /* Create a default socket and send packets through this socket */
            DatagramSocket dSocket = new DatagramSocket();
            dSocket.send(packet);

            /* Close the socket after sending */
            dSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
            err_msg = "system error";
            return false;
        }
        return true;
    }
}
