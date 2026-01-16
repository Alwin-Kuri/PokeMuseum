/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;
/**
 * 
 * @author Kuri
 */
import java.util.ArrayList;

/**
 * Dynamic Queue using ArrayList
 */
public class CardQueue {
    
    private final ArrayList<PokeCard> elements;
    private int front = 0;
    private int size = 0;
    private final int capacity;
    

    public CardQueue(int capacity) {
        this.capacity = capacity;
        this.elements = new ArrayList<>(capacity);
        // Pre-allocate space with nulls
        for (int i = 0; i < capacity; i++) {
            elements.add(null);
        }
    }
    
    /**
     * To check for empty
     * @return True when front == -1
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * To check if the arraylist is full or nah
     * BUT not needed since no limit is added here
     * @return True or False when full or not respectively
     */
    public boolean isFull() {
        if (size == capacity){
            return true;
        }
        return false;
    }
    
    public void enQueue(PokeCard card) {
        if (isFull()) {
            throw new IllegalArgumentException("Queue is full");
        }
        
        if (isFull()) {
            deQueue();  // drop the oldest one
        }

        // Calculate rear index = (front + size) % capacity
        // But since we don't wrap around here (linear), just use front + size
        int rear = front + size;
        elements.set(rear, card);
        size++;
    }

    public PokeCard deQueue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        
        PokeCard card = elements.get(front);
        // Instead of removing, we just move front pointer forward
        front++;
        size--;
        
        return card;
    }

    public PokeCard peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return elements.get(front);
    }

    public int size() {
        return size;
    }

    //displaying front to rear
    public void display() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }
        
        System.out.print("Queue (front to rear): ");
        for (int i = 0; i < size; i++) {
            PokeCard card = elements.get(front + i);
            System.out.print((card != null ? card.getName() : "[null]") + " → ");
        }
        System.out.println("(rear)");
    }
}