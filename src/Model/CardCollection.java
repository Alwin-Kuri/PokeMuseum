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
import java.util.LinkedList;
import java.util.List;
public class CardCollection {
    private LinkedList<PokeCard> mainCollection;//Main storage
    private ArrayList<PokeCard> recentAdds;//for recently added cards (FIFO)

    /**
     * Constructor to initialize the collection.
     */
    public CardCollection() {
        mainCollection = new LinkedList<>();//For main CRUD operations
        recentAdds = new ArrayList<>();//(limited to 5)
    }
    /**
     * Adds a new Pokémon card to the collection.
     * @param card The Pokémon card to add.
     * @throws IllegalArgumentException if card is null or duplicate ID exists.
     */
    public void addCard(PokeCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Card cannot be null");
        }
        if (hasDuplicateId(card.getId())) {
            throw new IllegalArgumentException("Duplicate ID not allowed");
        }
        mainCollection.add(card);  //adds the card to arraylist
        recentAdds.addFirst(card); //adds to the front of LinkedList
        if (recentAdds.size() > 5) {
            recentAdds.removeLast();
        }
    }

    /**
     * Updates an existing Pokémon card by ID.
     * @param updatedCard The updated Pokémon card.
     * @throws IllegalArgumentException if card is null or ID not found.
     */
    public void updateCard(PokeCard updatedCard) {
        if (updatedCard == null) {
            throw new IllegalArgumentException("Updated card cannot be null");
        }
        boolean found = false;
        for (int i = 0; i < mainCollection.size(); i++) {
            if (mainCollection.get(i).getId().equals(updatedCard.getId())) {
                mainCollection.set(i, updatedCard);  //updates in arraylist
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Card ID not found for update");
        }
    }

    /**
     * Deletes a Pokémon card by ID.
     * @param id The ID of the card to delete.
     * @throws IllegalArgumentException if ID not found.
     */
    public void deleteCard(String id) {
        boolean found = false;
        for (int i = 0; i < mainCollection.size(); i++) {
            if (mainCollection.get(i).getId().equals(id)) {
                mainCollection.remove(i);  //removes it from arraylist
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("Card ID not found for deletion");
        }
        //then remove from recentAdds if its present
        recentAdds.removeIf(card -> card.getId().equals(id));
    }

    /**
     * Gets all Pokémon cards in the main collection.
     * @return List of all cards (unmodifiable to prevent external changes).
     */
    public List<PokeCard> getAllCards() {
        return List.copyOf(mainCollection);  //return copy for safety
    }

    /**
     * Gets the recently added cards (from LinkedList queue).
     * @return List of recent cards (up to 5).
     */
    public List<PokeCard> getRecentAdds() {
        return List.copyOf(recentAdds);
    }

    /**
     * Checks if a duplicate ID exists in the collection.
     * @param id The ID to check.
     * @return true if duplicate exists, false otherwise.
     */
    private boolean hasDuplicateId(String id) {
        for (PokeCard card : mainCollection) {
            if (card.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }
}
