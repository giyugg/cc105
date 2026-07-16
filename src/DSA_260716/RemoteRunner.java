package DSA_260716;

import java.util.*;
class Remote {

    private static String status = "OFF";
    private static int volume = 0;
    private static int channel = 1;
    Remote() {
        this.status = "ON";
    }

    public void powerOff() {
        System.out.println("Sesison/Remote terminated successfully.");
    }

    public void volumeUp() {
        volume++;
        statusUpdate();
    }

    public void volumeDown() {
        volume--;
        statusUpdate();
    }

    public void channelUp() {
        channel++;
        statusUpdate();
    }

    public void channelDown() {
        channel--;
        statusUpdate();
    }

    public void statusUpdate() {
        System.out.println("Current channel: " + channel);
        System.out.println("Current volume: " + volume);
    }
}

public class RemoteRunner {
    static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {
        // call options
        // Remote ctrl =new Remote();
        options();
    }
    public static void options() {
        Remote ctrl = new Remote();
        //swtich options 1 2 3 4
        do {
            System.out.print("Enter choice (1-6): ");
            int option = sc.nextInt();
            switch(option) {
                case 1:
                    ctrl.powerOff();
                    break;
                case 2:
                    ctrl.volumeUp();
                    break;
                case 3:
                    ctrl.volumeDown();
                    break;
                case 4:
                    ctrl.channelUp();
                    break;
                case 5:
                    ctrl.channelDown();
                    break;
            }
        }
    }
}