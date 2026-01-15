/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Model;

/**
 *
 * @author admin
 */
import java.util.ArrayList;

/**
 * Dynamic Stack using ArrayList, with more manual control
 */
public class CardStack {
    
    private ArrayList<PokeCard> elements;
    private int top; // index of the top element
    private int capacity; //size

    public CardStack() {
        elements = new ArrayList<>();
        top = -1;
    }
    
    //Not needed as no fixed size but keeping this here
    public boolean isFull(){
        return top == capacity -1;
    }

    public void push(PokeCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot push null card");
        }
        if (isFull()) {
            throw new IllegalArgumentException("Overflow! Cannot push card!");
        }
        
        top++;
        while (elements.size() <= top) {
            elements.add(null);  // reserve space for push
        }
        
        elements.set(top, card); //+=top = x
    }

    public PokeCard pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        
        PokeCard card = elements.get(top);
        //dont shrink the list - just move top pointer
        elements.set(top, null);
        top--;
        return card;
    }

    public PokeCard peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return elements.get(top);
    }

    public boolean isEmpty() {
        return top < 0;
    }

    public int size() {
        return top + 1;
    }

    public void display() {
        if (isEmpty()) {
            System.out.println("Stack is empty");
            return;
        }
        
        System.out.println("Stack (top to bottom):");
        for (int i = top; i >= 0; i--) {
            PokeCard card = elements.get(i);
            System.out.println("  " + (card != null ? card.getName() : "[null]"));
        }
        System.out.println("Bottom ^");
    }
}
