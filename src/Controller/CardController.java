/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

/**
 *
 * @author Kuri
 */
import Model.CardCollection;
import Model.PokeCard;
import java.util.List;
/**
 * Controller class that mediates between the View (MainFrame) and Model (CardCollection).
 * Handles all business logic, validation, and delegates to the model.
 *
 * @author Kuri
 */
public class CardController {
    
    private final CardCollection collection;

    /**
     * Constructor initializes the collection and loads sample data.
     */
    public CardController() {
        collection = new CardCollection();
        try {
            createCard("PC001", "Charizard", "Fire", "Holo Rare", "Near Mint", 450.00, "/utils/charizard.png");
            createCard("PC002", "Pikachu", "Electric", "Common", "Mint", 120.00, "/utils/pikachu.png");
            createCard("PC003", "Mewtwo", "Psychic", "Ultra Rare", "Mint", 980.00, "/utils/mewtwo.png");
            createCard("PC004", "Blastoise", "Water", "Holo Rare", "Lightly Played", 480.00, "/utils/blastoise.png");
            createCard("PC005", "Mega Venusaur eX", "Grass", "Holo Rare", "Near Mint", 320.00, "/utils/venasaur.png");
            createCard("PC006", "Bulbasaur", "Grass", "Rare", "Lightly Played", 100.00, "/utils/bulbasaur.png");
            createCard("PC007", "Mega Abomasnow eX", "Water", "Ultra Rare", "Near Mint", 520.00, "/utils/megaabo.png");
            createCard("PC008", "Mega Diancie eX", "Psychic", "Holo Rare", "Mint", 720.00, "/utils/diancie.png");
            createCard("PC009", "Mega Gengar  eX", "Dark", "Holo Rare", "Near Mint", 560.00, "/utils/gengar.png");
            createCard("PC0010", "Pikachu Illustrator", "Electric", "Legendary +", "Near Mint", 1220.00, "/utils/pikilu.png");
        } catch (IllegalArgumentException e) {
            System.err.println("Error loading sample cards: " + e.getMessage());
        }
    }

    //CRUD Operations

    /**
     * Creates and adds a new card after full validation.
     */
    public void createCard(String id, String name, String type, String rarity,
                           String condition, double value, String imagePath) {
        validateIdForCreate(id);
        validateName(name);
        validateType(type);
        validateRarity(rarity);
        validateCondition(condition);
        validateValue(value);
        validateImagePath(imagePath);

        PokeCard card = new PokeCard(id, name, type, rarity, condition, value, imagePath);
        collection.addCard(card);
    }

    /**
     * Updates an existing card image path remains unchanged
     */
    public void updateCard(String id, String name, String type, String rarity,
                           String condition, double value) {
        validateIdExists(id);
        validateName(name);
        validateType(type);
        validateRarity(rarity);
        validateCondition(condition);
        validateValue(value);

        //Get current image path (image not editable during update)
        String currentImagePath = collection.getAllCards().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .map(PokeCard::getImagePath)
                .orElse("/utils/pokecard.jpg"); // fallback

        PokeCard updated = new PokeCard(id, name, type, rarity, condition, value, currentImagePath);
        collection.updateCard(updated);
    }

    /**
     * Deletes a card by ID.
     * @param id For deletion of id
     */
    public void deleteCard(String id) {
        validateIdExists(id);
        collection.deleteCard(id);
    }

    /**
     * Returns all cards (unmodifiable view).
     * @return Cards
     */
    public List<PokeCard> readAllCards() {
        return collection.getAllCards();
    }
    
    public PokeCard undoDelete(){
        return collection.undoDelete();
    }
    
    public boolean canUndoDelete() {
        return collection.canUndoDelete();
    }
    
    public PokeCard peekLastDeletedCard() {
        return collection.peekLastDeleted();
    }

    //Dashboard / Stats Methods

    public int getTotalCards() {
        return collection.getTotalCards();
    }

    public double getTotalValue() {
        return collection.getTotalInventoryValue();
    }
    

    public PokeCard getMostValuableCard() {
        return collection.getMostValuableCard();
    }

    public PokeCard getMostRareCard() {
        return collection.getMostRareCard();
    }

    public List<PokeCard> getRecentAdds() {
        return collection.getRecentAdds();
    }

    //Sorting Methods

    public List<PokeCard> getCardsSortedByValue() {
        return collection.insertionSortByValue();
    }

    public List<PokeCard> getCardsSortedByName() {
        return collection.selectionSortByName();
    }

    public List<PokeCard> getCardsSortedByRarity() {
        return collection.mergeSortByRarity();
    }

    //Searching Methods

    public List<PokeCard> searchByNameLinear(String namePart) {
        return collection.linearSearchByName(namePart);
    }

    public PokeCard searchByValueBinary(double value) {
        return collection.binarySearchByValue(value);
    }

    public PokeCard searchByIdHash(String id) {
        return collection.hashSearchById(id);
    }

    //Validation Methods

    private void validateIdForCreate(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Card ID cannot be empty");
        }
        if (collection.hashSearchById(id) != null) {
            throw new IllegalArgumentException("Card ID already exists");
        }
    }

    private void validateIdExists(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Card ID cannot be empty");
        }
        if (collection.hashSearchById(id) == null) {
            throw new IllegalArgumentException("Card ID not found");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    private void validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
    }

    private void validateRarity(String rarity) {
        if (rarity == null || rarity.trim().isEmpty()) {
            throw new IllegalArgumentException("Rarity cannot be empty");
        }
    }

    private void validateCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition cannot be empty");
        }
    }

    private void validateValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }

    private void validateImagePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Image path cannot be empty");
        }
    }
}
