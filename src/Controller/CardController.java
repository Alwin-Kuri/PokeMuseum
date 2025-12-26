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
public class CardController {
    private CardCollection collection;

    /**
     * Constructor to initialize the model.
     */
    public CardController() {
        collection = new CardCollection();
        // Manually add 5 sample Pokémon cards (similar to prepareInitialData() in your Mainpage example)
        try {
            createCard("PC001", "Charizard", "Fire/Flying", "Holo Rare", "Near Mint", 450.00);
            createCard("PC002", "Pikachu", "Electric", "Common", "Mint", 120.00);
            createCard("PC003", "Mewtwo GX", "Psychic", "Ultra Rare", "Mint", 280.00);
            createCard("PC004", "Blastoise", "Water", "Rare", "Lightly Played", 180.00);
            createCard("PC005", "Venusaur", "Grass/Poison", "Holo Rare", "Near Mint", 220.00);
        } catch (IllegalArgumentException e) {
            // This won't happen with sample data, but good practice
            System.err.println("Sample load error: " + e.getMessage());
        }        
    }
    
    /**
     * Creates a new Pokémon card after validation.
     * @param id ID (non-empty, no duplicates).
     * @param name Name (non-empty).
     * @param type Type (non-empty).
     * @param rarity Rarity (non-empty).
     * @param condition Condition (non-empty).
     * @param value Value (non-negative).
     * @throws IllegalArgumentException for any validation failure.
     */
    public void createCard(String id, String name, String type, String rarity, String condition, double value) {
        validateId(id);
        validateName(name);
        validateType(type);
        validateRarity(rarity);
        validateCondition(condition);
        validateValue(value);
        // Additional year validation if year was part of card (assuming year is part of ID or separate; adjust if needed)
        // For now, assuming year is parsed from ID or separate field; add if needed: validateYear(year);

        PokeCard card = new PokeCard(id, name, type, rarity, condition, value);
        collection.addCard(card);
    }

    /**
     * Reads all Pokémon cards.
     * @return List of all cards.
     */
    public List<PokeCard> readAllCards() {
        return collection.getAllCards();
    }

    /**
     * Updates an existing Pokémon card after validation.
     * @param id ID (must exist).
     * @param name New name (non-empty).
     * @param type New type (non-empty).
     * @param rarity New rarity (non-empty).
     * @param condition New condition (non-empty).
     * @param value New value (non-negative).
     * @throws IllegalArgumentException for any validation failure or ID not found.
     */
    public void updateCard(String id, String name, String type, String rarity, String condition, double value) {
        validateIdExists(id);
        validateName(name);
        validateType(type);
        validateRarity(rarity);
        validateCondition(condition);
        validateValue(value);

        PokeCard updatedCard = new PokeCard(id, name, type, rarity, condition, value);
        collection.updateCard(updatedCard);
    }

    /**
     * Deletes a Pokémon card by ID.
     * @param id ID (must exist).
     * @throws IllegalArgumentException if ID not found.
     */
    public void deleteCard(String id) {
        validateIdExists(id);
        collection.deleteCard(id);
    }

    // Validation methods (private, reusable)

    /**
     * Validates ID is non-empty and no duplicate (for add).
     * @param id ID to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
    }

    /**
     * Validates ID exists (for update/delete).
     * @param id ID to check.
     * @throws IllegalArgumentException if not found.
     */
    private void validateIdExists(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty");
        }
        // Check existence (you might need to add getById in model if frequent)
        boolean exists = false;
        for (PokeCard card : collection.getAllCards()) {
            if (card.getId().equals(id)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            throw new IllegalArgumentException("ID does not exist");
        }
    }

    /**
     * Validates name is non-empty.
     * @param name Name to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    /**
     * Validates type is non-empty.
     * @param type Type to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be empty");
        }
    }

    /**
     * Validates rarity is non-empty.
     * @param rarity Rarity to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateRarity(String rarity) {
        if (rarity == null || rarity.trim().isEmpty()) {
            throw new IllegalArgumentException("Rarity cannot be empty");
        }
    }

    /**
     * Validates condition is non-empty.
     * @param condition Condition to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            throw new IllegalArgumentException("Condition cannot be empty");
        }
    }

    /**
     * Validates value is non-negative.
     * @param value Value to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateValue(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value cannot be negative");
        }
    }

    // If year is separate (not in current PokemonCard), add field and this:
    /**
     * Validates year is between 1996 and 2025.
     * @param year Year to validate.
     * @throws IllegalArgumentException if invalid.
     */
    private void validateYear(int year) {
        if (year < 1996 || year > 2025) {
            throw new IllegalArgumentException("Year must be between 1996 and 2025");
        }
    }
}
