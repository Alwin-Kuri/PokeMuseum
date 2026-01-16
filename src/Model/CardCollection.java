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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
public class CardCollection {
    private LinkedList<PokeCard> mainCollection;//Main storage
    private CardQueue recentAdds;//for recently added cards (limit 5)
    private CardStack undoStack;//for undo deletes (limit 10)
    private HashMap<String, PokeCard> idMap; // For hashing search

    /**
     * Constructor to initialize the collection.
     */
    public CardCollection() {
        mainCollection = new LinkedList<>();//For main CRUD operations
        recentAdds = new CardQueue(15);
        undoStack = new CardStack(50);
        idMap = new HashMap<>();
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
        mainCollection.add(card); //adds the card to arraylist
        recentAdds.enQueue(card); //adds to the front of LinkedList
        idMap.put(card.getId(), card);
        while (recentAdds.size() > 5) {
            recentAdds.deQueue();
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
                mainCollection.set(i, updatedCard);  //updates in linkedlist
                idMap.put(updatedCard.getId(), updatedCard);
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
        PokeCard toDelete = null;
        for (int i = 0; i < mainCollection.size(); i++) {
            if (mainCollection.get(i).getId().equals(id)) {
                toDelete = mainCollection.remove(i);  //removes from linkedlist
                idMap.remove(id);
                break;
            }
        }
        if (toDelete == null) {
            throw new IllegalArgumentException("Card ID not found for deletion");
        }
        // Push to undo stack
        if (undoStack != null) {
            undoStack.push(toDelete);
            System.out.println("DEBUG: Pushed deleted card to undo stack: " + toDelete.getName());
        }
        // Remove from recent if present (manual check since queue no remove)
        CardQueue temp = new CardQueue(15);
        while (!recentAdds.isEmpty()) {
            PokeCard c = recentAdds.deQueue();
            if (!c.getId().equals(id)) {
                temp.enQueue(c);
            }
        }
        recentAdds = temp;
    }
    
    /**
     * Undo the last delete
     * @return Restored card or null
     */
    public PokeCard undoDelete(){
        if (undoStack.isEmpty()){
            throw new IllegalArgumentException("Nothing to undo");
        }
        PokeCard restored = undoStack.pop();
        addCard(restored); //add back to main collection
        return restored;
    }
    
    public boolean canUndoDelete() {
        boolean can = !undoStack.isEmpty();
        System.out.println("DEBUG: canUndoDelete() = " + can + " (stack size: " + undoStack.size() + ")");
        return can;
    }
    
    /**
    * Peeks at the top of undo stack without popping
     * @return last deleted card or null
     */
    public PokeCard peekLastDeleted() {
        if (undoStack.isEmpty()) {
            return null;
        }
        return undoStack.peek();
    }

    /**
     * Gets all Pokémon cards in the main collection.
     * @return List of all cards (unmodifiable to prevent external changes).
     */
    public List<PokeCard> getAllCards() {
        return List.copyOf(mainCollection);
    }

    /**
     * Gets the recently added cards from queue.
     * @return List of recent cards
     */
    public List<PokeCard> getRecentAdds() {
        List<PokeCard> list = new ArrayList<>();
        CardQueue temp = new CardQueue(15);
        while (!recentAdds.isEmpty()) {
            PokeCard c = recentAdds.deQueue();
            list.add(c);
            temp.enQueue(c);
        }
        recentAdds = temp;
        return list;
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
    
    public void displayRecentQueue(){
        recentAdds.display();
    }
    
    /**
     * Returns the total number of cards in the collection.
     * @return Total cards count.
     */
    public int getTotalCards() {
        return mainCollection.size();
    }

    /**
     * Calculates the total value of all cards in the inventory.
     * @return Sum of all card values.
     */
    public double getTotalInventoryValue() {
        double total = 0.0;
        for (PokeCard card : mainCollection) {
            total += card.getValue();
        }
        return total;
    }

    /**
     * Finds the most valuable card (highest value).
     * @return The most valuable PokemonCard or null if collection is empty.
     */
    public PokeCard getMostValuableCard() {
        if (mainCollection.isEmpty()) {
            return null;
        }
        PokeCard maxCard = mainCollection.getFirst();
        for (PokeCard card : mainCollection) {
            if (card.getValue() > maxCard.getValue()) {
                maxCard = card;
            }
        }
        return maxCard;
    }

    /**
     * Finds the most rare card based on rarity rank, with tie-breaker on condition rank.
     * Rarity ranks: Common=1, Uncommon=2, Rare=3, Epic=4, Legendary=5, SSS=6, SSS+=7
     * Condition ranks: Damaged=1, Heavily Played=2, Moderately Played=3, Lightly Played=4, Near Mint=5
     * @return The most rare PokemonCard or null if empty.
     */
    public PokeCard getMostRareCard() {
        if (mainCollection.isEmpty()) {
            return null;
        }
        PokeCard maxRare = mainCollection.getFirst();
        int maxRarityRank = getRarityRank(maxRare.getRarity());
        int maxConditionRank = getConditionRank(maxRare.getCondition());

        for (PokeCard card : mainCollection) {
            int currentRarityRank = getRarityRank(card.getRarity());
            int currentConditionRank = getConditionRank(card.getCondition());

            if (currentRarityRank > maxRarityRank || 
                (currentRarityRank == maxRarityRank && currentConditionRank > maxConditionRank)) {
                maxRare = card;
                maxRarityRank = currentRarityRank;
                maxConditionRank = currentConditionRank;
            }
        }
        return maxRare;
    }

    /**
     * Helper to get numeric rank for rarity.
     * @param rarity The rarity string.
     * @return Numeric rank.
     */
    private int getRarityRank(String rarity) {
        if (rarity == null) return 0;
        switch (rarity.toLowerCase()) {
            case "common": return 1;
            case "uncommon": return 2;
            case "rare": return 3;
            case "holo rare": return 4;
            case "ultra rare": return 5;
            case "legendary": return 6;
            case "legendary +": return 7;
            default: return 0;
        }
    }

    /**
     * Helper to get numeric rank for condition (Near Mint highest).
     * @param condition The condition string.
     * @return Numeric rank.
     */
    private int getConditionRank(String condition) {
        if (condition == null) return 0;
        switch (condition.toLowerCase()) {
            case "damaged": return 1;
            case "heavily played": return 2;
            case "moderately played": return 3;
            case "lightly played": return 4;
            case "near mint": return 5;
            default: return 0;
        }
    }
    
    // Sorting methods
    private static final String[] RARITIES = {"Common", "Uncommon", "Rare", "Holo Rare", "Ultra Rare", "Legendary", "Legendary +"};
    private static final String[] CONDITIONS = {"Damaged", "Heavily Played", "Moderately Played", "Lightly Played", "Near Mint"};
    
    private int getRarityIndex(String rarity) {
        for (int i = 0; i < RARITIES.length; i++) {
            if (RARITIES[i].equals(rarity)) return i;
        }
        return -1;
    }

    private int getConditionIndex(String condition) {
        for (int i = 0; i < CONDITIONS.length; i++) {
            if (CONDITIONS[i].equals(condition)) return i;
        }
        return -1;
    }
    
    /**
     * Insertion sort by value (ascending).
     * @return Sorted list.
     */
    public List<PokeCard> insertionSortByValue() {
        List<PokeCard> sorted = new ArrayList<>(mainCollection);
        for (int i = 1; i < sorted.size(); i++) {
            PokeCard key = sorted.get(i);
            int j = i - 1;
            while (j >= 0 && sorted.get(j).getValue() > key.getValue()) {
                sorted.set(j + 1, sorted.get(j));
                j--;
            }
            sorted.set(j + 1, key);
        }
        return sorted;
    }

    /**
     * Selection sort by name (alphabetical).
     * @return Sorted list.
     */
    public List<PokeCard> selectionSortByName() {
        List<PokeCard> sorted = new ArrayList<>(mainCollection);
        for (int i = 0; i < sorted.size() - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(j).getName().compareTo(sorted.get(minIdx).getName()) < 0) {
                    minIdx = j;
                }
            }
            PokeCard temp = sorted.get(minIdx);
            sorted.set(minIdx, sorted.get(i));
            sorted.set(i, temp);
        }
        return sorted;
    }

    /**
     * Merge sort by rarity (descending rarity index).
     * @return Sorted list.
     */
    public List<PokeCard> mergeSortByRarity() {
        List<PokeCard> sorted = new ArrayList<>(mainCollection);
        mergeSortHelper(sorted, 0, sorted.size() - 1);
        return sorted;
    }

    private void mergeSortHelper(List<PokeCard> list, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSortHelper(list, left, mid);
            mergeSortHelper(list, mid + 1, right);
            merge(list, left, mid, right);
        }
    }

    private void merge(List<PokeCard> list, int left, int mid, int right) {
        List<PokeCard> temp = new ArrayList<>(right - left + 1);
        int i = left, j = mid + 1;
        while (i <= mid && j <= right) {
            if (getRarityIndex(list.get(i).getRarity()) >= getRarityIndex(list.get(j).getRarity())) {
                temp.add(list.get(i++));
            } else {
                temp.add(list.get(j++));
            }
        }
        while (i <= mid) temp.add(list.get(i++));
        while (j <= right) temp.add(list.get(j++));
        for (int k = 0; k < temp.size(); k++) {
            list.set(left + k, temp.get(k));
        }
    }

    // Searching methods

    /**
     * Linear search by name (contains).
     * @param name Name substring.
     * @return List of matching cards.
     */
    public List<PokeCard> linearSearchByName(String name) {
        List<PokeCard> results = new ArrayList<>();
        for (PokeCard card : mainCollection) {
            if (card.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(card);
            }
        }
        return results;
    }

    /**
     * Binary search by value (exact, assumes sorted by value).
     * @param value Value to find.
     * @return Matching card or null.
     */
    public PokeCard binarySearchByValue(double value) {
        List<PokeCard> sorted = insertionSortByValue();
        int low = 0, high = sorted.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            double midVal = sorted.get(mid).getValue();
            if (midVal == value) return sorted.get(mid);
            else if (midVal < value) low = mid + 1;
            else high = mid - 1;
        }
        return null;
    }

    /**
     * Hash search by ID.
     * @param id ID to find.
     * @return Matching card or null.
     */
    public PokeCard hashSearchById(String id) {
        return idMap.get(id);
    }
}
