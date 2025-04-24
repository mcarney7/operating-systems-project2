// Project 2: Thread-Based Process Simulation and Synchronization 
// Author: MiKayla Carney
// GitHub: https://github.com/mcarney7/operating-systems-project2
// Course: Operating Systems 4320
// Spring 2025

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

// ProcessThread simulates CPU bursts based on processes.txt input
class ProcessThread extends Thread {
    private final int pid;        // Process ID
    private final int burstTime;  // CPU burst time in seconds

    public ProcessThread(int pid, int burstTime) {
        this.pid = pid;
        this.burstTime = burstTime;
    }

    public void run() {
        // Simulates starting the process and sleeping for burst time to mimic CPU execution
        System.out.println("[Process " + pid + "] Started with burst time: " + burstTime + "s");
        try {
            Thread.sleep(burstTime * 1000L); // Convert seconds to milliseconds
        } catch (InterruptedException e) {
            System.out.println("[Process " + pid + "] Interrupted.");
        }
        System.out.println("[Process " + pid + "] Finished.");
    }
}

// Represents a fork using a ReentrantLock for mutual exclusion
class Fork {
    private final ReentrantLock lock = new ReentrantLock();

    // Acquire the lock (i.e., pick up the fork)
    public void pickUp() {
        lock.lock();
    }

    // Release the lock (i.e., put down the fork)
    public void putDown() {
        lock.unlock();
    }
}

// Philosopher thread for the Dining Philosophers synchronization problem
class Philosopher extends Thread {
    private final int id;               // Philosopher ID
    private final Fork leftFork;        // Fork to the left of the philosopher
    private final Fork rightFork;       // Fork to the right of the philosopher

    public Philosopher(int id, Fork leftFork, Fork rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    // Simulate thinking by sleeping for a random duration
    private void think() throws InterruptedException {
        System.out.println("[Philosopher " + id + "] Thinking...");
        Thread.sleep((int)(Math.random() * 1000)); // Random think time
    }

    // Simulate eating by sleeping for a random duration
    private void eat() throws InterruptedException {
        System.out.println("[Philosopher " + id + "] Eating...");
        Thread.sleep((int)(Math.random() * 1000)); // Random eat time
    }

    public void run() {
        try {
            // Each philosopher performs 3 cycles of thinking and eating
            for (int i = 0; i < 3; i++) {
                think();
                System.out.println("[Philosopher " + id + "] Waiting for forks...");

                // To reduce deadlock risk: even philosophers pick up left then right,
                // odd philosophers pick up right then left
                if (id % 2 == 0) {
                    leftFork.pickUp();
                    System.out.println("[Philosopher " + id + "] Picked up left fork.");
                    rightFork.pickUp();
                    System.out.println("[Philosopher " + id + "] Picked up right fork.");
                } else {
                    rightFork.pickUp();
                    System.out.println("[Philosopher " + id + "] Picked up right fork.");
                    leftFork.pickUp();
                    System.out.println("[Philosopher " + id + "] Picked up left fork.");
                }

                eat();

                // Put down forks after eating
                leftFork.putDown();
                rightFork.putDown();
                System.out.println("[Philosopher " + id + "] Released forks.");
                System.out.println("[Philosopher " + id + "] Finished cycle " + (i + 1));
            }
        } catch (InterruptedException e) {
            System.out.println("[Philosopher " + id + "] Interrupted.");
        }
    }
}

// Main driver class
public class DiningPhilosophers {
    public static void main(String[] args) {
        runProcessSimulation();        // Step 1: Thread simulation from input file
        runDiningPhilosophers();      // Step 2: Synchronization problem
    }

    // Step 1: Simulate CPU process execution using threads
    private static void runProcessSimulation() {
        System.out.println("=== PROCESS SIMULATION START ===");

        List<ProcessThread> processes = new ArrayList<>();

        // Read processes from the input file and create threads
        try (BufferedReader br = new BufferedReader(new FileReader("processes.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.strip().split("\\s+");
                int pid = Integer.parseInt(parts[0]);           // Extract process ID
                int burstTime = Integer.parseInt(parts[1]);     // Extract burst time
                processes.add(new ProcessThread(pid, burstTime));
            }
        } catch (IOException e) {
            System.out.println("Error reading processes.txt: " + e.getMessage());
            return;
        }

        // Start all process threads
        for (ProcessThread p : processes) {
            p.start();
        }

        // Wait for all process threads to finish
        for (ProcessThread p : processes) {
            try {
                p.join();
            } catch (InterruptedException e) {
                System.out.println("Join interrupted for process " + p);
            }
        }

        System.out.println("=== PROCESS SIMULATION END ===\n");
    }

    // Step 2: Simulate the Dining Philosophers problem with synchronization
    private static void runDiningPhilosophers() {
        System.out.println("=== DINING PHILOSOPHERS START ===");

        int numPhilosophers = 5; // Number of philosophers and forks
        Philosopher[] philosophers = new Philosopher[numPhilosophers];
        Fork[] forks = new Fork[numPhilosophers];

        // Create fork objects
        for (int i = 0; i < numPhilosophers; i++) {
            forks[i] = new Fork();
        }

        // Assign forks to each philosopher and start their threads
        for (int i = 0; i < numPhilosophers; i++) {
            Fork leftFork = forks[i];
            Fork rightFork = forks[(i + 1) % numPhilosophers]; // Circular table
            philosophers[i] = new Philosopher(i, leftFork, rightFork);
            philosophers[i].start();
        }

        // Wait for all philosophers to finish their cycles
        for (Philosopher p : philosophers) {
            try {
                p.join();
            } catch (InterruptedException e) {
                System.out.println("Join interrupted for philosopher " + p);
            }
        }

        System.out.println("=== DINING PHILOSOPHERS END ===");
    }
}
