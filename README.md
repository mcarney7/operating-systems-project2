# Operating Systems Project 2  
**Thread-Based Process Simulation and Synchronization**  
**Author:** MiKayla Carney  
**Course:** CSC 4320-002 – Operating Systems  
**Instructor:** Dr. Roya Hosseini  
**Semester:** Spring 2025

---

## 📌 Project Description

This project simulates real-time process execution using threads in Java and implements the **Dining Philosophers** synchronization problem using `ReentrantLock`. It consists of two main components:

1. **CPU Burst Simulation**  
   - Each process is defined in `processes.txt` with a PID and burst time.
   - Each line becomes a thread that "runs" using `Thread.sleep()`.

2. **Dining Philosophers Synchronization**  
   - Five philosophers alternate between thinking and eating.
   - Forks are modeled using `ReentrantLock`.
   - Deadlock is avoided by alternating fork pickup order based on philosopher ID.
---

## 🧪 How to Run

1. **Compile:**
   ```bash
   javac DiningPhilosophers.java
